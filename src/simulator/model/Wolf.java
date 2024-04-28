package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {
	private static final double INIT_VISIONRANGE = 50;
	private static final double INIT_SPEED = 60;
	private static final double DESIRE_FACTOR = 30;
	private static final double ENERGY_FACTOR = 18;
	private static final double DESIRE_MATE = 65;
	private static final double ENERGY_HUNGUER = 50;
	private static final double CHILD_PROB = 0.9;
	private static final double NEAR_DISTANCE = 8.0;
	private static final double MAX_AGE = 14;

	private Animal _hunt_target; // animal que está buscando
	private SelectionStrategy _hunting_strategy; // estrategia para elegir animal que comer

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("Wolf", Diet.CARNIVORE, INIT_VISIONRANGE, INIT_SPEED, mate_strategy, pos);

		if (hunting_strategy != null) {
			_hunting_strategy = hunting_strategy;
			_hunt_target = null;
		} else {
			throw new IllegalArgumentException("hunting_strategy debe ser diferente que null");
		}

	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		_hunt_target = null;
		_hunting_strategy = p1._hunting_strategy;
	}

	@Override
	public void update(double dt) {
		if (_state != State.DEAD) {
			switch (_state) {
			case NORMAL:
				actualizaNormal(dt);
				break;
			case HUNGER:
				actualizaHunger(dt);
				break;
			case MATE:
				actualizaMate(dt);
			default:
				break;
			}
			cambioEstado();

			if (fueraTablero()) {
				ajustarPosicion();
				_state = State.NORMAL;
			}

			if (get_energy() == 0 || _age > MAX_AGE)
				_state = State.DEAD;
			if (_state != State.DEAD)
				add_energy(_region_mngr.get_food(this, dt));
		}
	}

	/** METODOS PARA ACTUALIZAR EL ANIMAL SEGÚN EL ESTADO **/

	private void actualizaNormal(double dt) {
		if (_dest.distanceTo(_pos) < NEAR_DISTANCE) {
			_dest = randomPos();
		}

		move(get_speed() * dt * Math.exp((get_energy() - 100.0) * 0.007));

		_age += dt;
		add_energy(-ENERGY_FACTOR * dt);
		add_desire(DESIRE_FACTOR * dt);
	}

	private void actualizaHunger(double dt) {

		if (_hunt_target == null || _hunt_target.get_state() == State.DEAD
				|| _hunt_target.get_position().distanceTo(_pos) > _sight_range) {
			_hunt_target = _hunting_strategy.select(this,
					_region_mngr.get_animals_in_range(this, a -> a.get_diet() == Diet.HERBIVORE));
		}

		if (_hunt_target == null)
			actualizaNormal(dt);
		else {
			_dest = _hunt_target.get_position();
			move(3.0 * _speed * dt * Math.exp((get_energy() - 100.0) * 0.007));

			_age += dt;
			add_energy(-ENERGY_FACTOR * 1.2 * dt);
			add_desire(DESIRE_FACTOR * dt);

			if (_hunt_target.get_position().distanceTo(_pos) < NEAR_DISTANCE) {
				_hunt_target._state = State.DEAD;
				_hunt_target = null;
				add_energy(50);
			}
		}
	}

	private void actualizaMate(double dt) {
		if (_mate_target == null || _mate_target.get_state() == State.DEAD
				|| _mate_target.get_position().distanceTo(_pos) > _sight_range) {
			_mate_target = _mate_strategy.select(this,
					_region_mngr.get_animals_in_range(this, a -> a.get_genetic_code().equals(_genetic_code)));
		}

		if (_mate_target == null)
			actualizaNormal(dt);
		else {
			_dest = _mate_target.get_position();
			move(3.0 * _speed * dt * Math.exp((get_energy() - 100.0) * 0.007));

			_age += dt;
			add_energy(-ENERGY_FACTOR * 1.2 * dt);
			add_desire(DESIRE_FACTOR * dt);

			// apareamiento
			if (_mate_target.get_position().distanceTo(_pos) < NEAR_DISTANCE) {
				_mate_target.set_desire(0);
				set_desire(0);

				if (_baby == null && Utils._rand.nextDouble(1) <= CHILD_PROB) {
					_baby = new Wolf(this, _mate_target);
				}

				add_energy(-10);
				_mate_target = null;
			}
		}
	}

	private void cambioEstado() {
		switch (_state) {
		case NORMAL:
			if (get_energy() < ENERGY_HUNGUER)
				_state = State.HUNGER;
			else if (get_desire() > DESIRE_MATE)
				_state = State.MATE;
			break;
		case HUNGER:
			if (get_energy() > ENERGY_HUNGUER) {
				if (get_desire() < DESIRE_MATE)
					_state = State.NORMAL;
				else
					_state = State.MATE;
			}
			break;
		case MATE:
			if (get_energy() < ENERGY_HUNGUER)
				_state = State.HUNGER;
			else if (get_desire() < DESIRE_MATE)
				_state = State.NORMAL;
			break;
		default:
			break;
		}
	}
}
