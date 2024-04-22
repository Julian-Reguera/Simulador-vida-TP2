package simulator.model;

import simulator.misc.Vector2D;
import simulator.model.Animal.State;

//estos metodos como indica el nombre solo sirven para leer variablese y en ningun caso para modificarlas
public interface AnimalInfo extends JSONable { // Note that it extends JSONable

	public State get_state();

	public Vector2D get_position();

	public String get_genetic_code();

	public Diet get_diet();

	public double get_speed();

	public double get_sight_range();

	public double get_energy();

	public double get_age();

	public Vector2D get_destination();

	public boolean is_pregnant();

}