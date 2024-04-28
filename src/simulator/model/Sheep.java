package simulator.model;

import java.util.Objects;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {
	private static final double INIT_VISIONRANGE = 40;
	private static final double INIT_SPEED = 35;
	private static final double DESIRE_FACTOR = 40;
	private static final double ENERGY_FACTOR = 20;
	private static final double DESSIRE_MATE = 65;
	private static final double CHILD_PROB = 0.9;
	private static final double NEAR_DISTANCE = 8.0;
	private static final double MAX_AGE = 8;

	private Animal _danger_source; // animal peligroso
	private SelectionStrategy _danger_strategy; // elige el animal peligroso en el campo visual

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, INIT_VISIONRANGE, INIT_SPEED, mate_strategy, pos);

		if (danger_strategy != null) {
			_danger_strategy = danger_strategy;
			_danger_source = null;
		} else
			throw new IllegalArgumentException("danger_strategy no puede ser null");

	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		_danger_source = null;
		_danger_strategy = p1._danger_strategy;
	}

	@Override
	public void update(double dt) {
		if (_state != State.DEAD) {
			switch (_state) {
			case NORMAL:
				actualizacionNormal(dt);
				break;
			case DANGER:
				actualizacionDanger(dt);
				break;
			case MATE:
				actualizacionMate(dt);
				break;
			default:
				break;
			}
			cambioDeEstado();

			if (fueraTablero()) {
				ajustarPosicion();
				_state = State.NORMAL;
			}

			if (get_energy() == 0 || _age >= MAX_AGE)
				_state = State.DEAD;
			if (_state != State.DEAD)
				add_energy(_region_mngr.get_food(this, dt));
		}
	}

	/** METODOS PARA ACTUALIZAR AL ANIMAL EN FUNCION DE SU ESTADO **/

	private void actualizacionNormal(double dt) {
		if (_dest.distanceTo(get_position()) < NEAR_DISTANCE) {
			_dest = randomPos();
		}

		move(get_speed() * dt * Math.exp((get_energy() - 100.0) * 0.007));

		_age += dt;
		add_energy(-ENERGY_FACTOR * dt);
		add_desire(DESIRE_FACTOR * dt);
	}

	private void actualizacionDanger(double dt) {
		if (_danger_source == null || _danger_source.get_state() == State.DEAD) {
			_danger_source = null;
			actualizacionNormal(dt);
		} else {
			_dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction());
			move(2.0 * _speed * dt * Math.exp((get_energy() - 100.0) * 0.007));
			_age += dt;
			add_energy(-ENERGY_FACTOR * 1.2 * dt);
			add_desire(DESIRE_FACTOR * dt);
		}
	}

	private void actualizacionMate(double dt) {

		if (_mate_target == null || _mate_target.get_state() == State.DEAD) {
			_mate_target = _mate_strategy.select(this,
					_region_mngr.get_animals_in_range(this, a -> a.get_genetic_code().equals(_genetic_code)));
		}

		if (_mate_target == null)
			actualizacionNormal(dt);
		else {
			_dest = _mate_target.get_position();
			move(2.0 * _speed * dt * Math.exp((get_energy() - 100.0) * 0.007));

			_age += dt;
			add_energy(-ENERGY_FACTOR * 1.2 * dt);
			add_desire(DESIRE_FACTOR * dt);

			// apareamiento
			if (_mate_target.get_position().distanceTo(_pos) < NEAR_DISTANCE) {
				_mate_target.set_desire(0);
				set_desire(0);

				if (_baby == null && Utils._rand.nextDouble(1) <= CHILD_PROB) {
					_baby = new Sheep(this, _mate_target);
				}
				_mate_target = null;
			}
		}
	}

	private void cambioDeEstado() {

		switch (_state) {
		case NORMAL:
			if (_danger_source == null)
				_danger_source = _danger_strategy.select(this,
						_region_mngr.get_animals_in_range(this, a -> a.get_diet() == Diet.CARNIVORE));
			if (_danger_source != null)
				_state = State.DANGER;
			else if (get_desire() >= DESSIRE_MATE)
				_state = State.MATE;
			break;
		case DANGER:
			if (_danger_source == null || _danger_source.get_position().distanceTo(_pos) > _sight_range)
				_danger_source = _danger_strategy.select(this,
						_region_mngr.get_animals_in_range(this, a -> a.get_diet() == Diet.CARNIVORE));
			if (_danger_source == null) {
				if (get_desire() >= DESSIRE_MATE)
					_state = State.MATE;
				else
					_state = State.NORMAL;
			}
			break;
		case MATE:
			if (_danger_source == null)
				_danger_source = _danger_strategy.select(this,
						_region_mngr.get_animals_in_range(this, a -> a.get_diet() == Diet.CARNIVORE));
			if (_danger_source != null)
				_state = State.DANGER;
			else if (get_desire() < DESSIRE_MATE)
				_state = State.NORMAL;
			break;
		default:
			break;
		}
	}
}
