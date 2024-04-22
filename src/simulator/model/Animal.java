package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements AnimalInfo, Entity {
	private static final double INIT_ENERGY = 100;
	private static final double INIT_DESIRE = 0;

	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _speed;
	protected double _age;
	protected double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	protected SelectionStrategy _mate_strategy;

	private double _energy; // private nunca baja de 0
	private double _desire; // private se matiene entre 0 y 100
	
	public enum State {
		NORMAL, MATE, HUNGER, DANGER, DEAD
	}

	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed,
			SelectionStrategy mate_strategy, Vector2D pos) {
		if (genetic_code != null && genetic_code.length() > 0 && sight_range > 0 && init_speed > 0
				&& mate_strategy != null && diet != null) {
			_diet = diet;
			_genetic_code = genetic_code;
			_sight_range = sight_range;
			_speed = Utils.get_randomized_parameter(init_speed, 0.1);
			_mate_strategy = mate_strategy;
			_pos = pos;
			_age = 0;
			_state = State.NORMAL;
			_energy = INIT_ENERGY;
			_desire = INIT_DESIRE;
			_dest = null;
			_mate_target = null;
			_baby = null;
			_region_mngr = null;
			_dest = null;
		} else {
			throw new IllegalArgumentException("parametros invalidos para Animal");
		}
	}

	protected Animal(Animal p1, Animal p2) {
		_dest = null;
		_baby = null;
		_mate_target = null;
		_region_mngr = null;
		_state = State.NORMAL;
		_energy = (p1._energy + p1._energy) / 2;
		_desire = INIT_DESIRE;
		_age = 0;
		_genetic_code = p1._genetic_code;
		_diet = p1._diet;
		_pos = p1.get_position().plus(Vector2D.get_random_vector(-1, 1).scale(60.0 * (Utils._rand.nextGaussian() + 1)));
		_sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2, 0.2);
		_speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, 0.2);
		_mate_strategy = p2._mate_strategy;
	}

	/** METODOS DE IMPLEMENTACION OBLIGATORIA **/

	public Animal deliver_baby() {
		Animal baby = _baby;
		_baby = null;

		return baby;
	}

	public void init(AnimalMapView reg_mngr) {
		_region_mngr = reg_mngr;

		if (_pos == null)
			_pos = randomPos();
		else if (fueraTablero())
			ajustarPosicion();

		_dest = randomPos();
	}

	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}

	/** GUETTERS PUBLICOS HEREDADOS DE LAS INTERFACES **/

	@Override
	public State get_state() {
		return _state;
	}

	@Override
	public Vector2D get_position() {
		return _pos;
	}

	@Override
	public String get_genetic_code() {
		return _genetic_code;
	}

	@Override
	public Diet get_diet() {
		return _diet;
	}

	@Override
	public double get_speed() {
		return _speed;
	}

	@Override
	public double get_sight_range() {
		return _sight_range;
	}

	@Override
	public double get_energy() {
		return _energy;
	}

	@Override
	public double get_age() {
		return _age;
	}

	@Override
	public Vector2D get_destination() {
		return _dest;
	}

	@Override
	public boolean is_pregnant() {
		return _baby != null;
	}

	@Override
	public JSONObject as_JSON() {
		JSONObject o = new JSONObject();

		o.put("gcode", _genetic_code);
		o.put("diet", _diet.toString());
		o.put("state", _state.toString());
		o.put("pos", new JSONArray().put(_pos.getX()).put(_pos.getY()));

		return o;
	}

	/** GUETTERS Y SETTERS DE ATRIBUTOS PROTECTED **/

	protected double get_desire() {
		return _desire;
	}

	protected void add_desire(double desire) {
		_desire += desire;

		if (_desire > 100)
			_desire = 100;
		else if (_desire < 0)
			_desire = 0;
	}

	protected void set_desire(double desire) {
		if (desire > 100)
			desire = 100;
		else if (desire < 0)
			desire = 0;

		_desire = desire;
	}

	protected void add_energy(double energy) {
		_energy += energy;

		if (_energy < 0)
			_energy = 0;
		else if (_energy > 100)
			_energy = 100;
	}

	protected void set_energy(double energy) {
		if (energy < 0)
			energy = 0;
		else if (energy > 100)
			energy = 100;

		_energy = energy;
	}

	/** FUNCIONES PROTEGIDAS AÑADIDAS POR EL ALUMNO **/

	// devuelve true si el animal está fuera del tablero
	protected boolean fueraTablero() {
		double x = _pos.getX(), y = _pos.getY();
		return x < 0 || y < 0 || y >= _region_mngr.get_height() || x >= _region_mngr.get_width();
	}

	// ajusta la posicion de manera que si sale por la derecha del tablero aparece
	// por la hizquierda
	protected void ajustarPosicion() {
		_pos = _pos.ajustarPosicion(_region_mngr.get_width(), _region_mngr.get_region_height());
	}

	// genera una posicion aleatoria dentro del tablero
	protected Vector2D randomPos() {
		return Vector2D.randomPoint(_region_mngr.get_width(), _region_mngr.get_height());
	}

}
