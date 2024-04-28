package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Region implements FoodSupplier, RegionInfo, Entity {

	protected List<Animal> _animales;

	public Region() {
		_animales = new LinkedList<Animal>();
	}

	final void add_animal(Animal a) {
		_animales.add(a);
	}

	final void remove_animal(Animal a) {
		_animales.remove(a);
	}

	final List<Animal> getAnimals() {
		return Collections.unmodifiableList(_animales);
	}

	public JSONObject as_JSON() {
		JSONObject o = new JSONObject();
		JSONArray a = new JSONArray();

		for (Animal animal : _animales)
			a.put(animal.as_JSON());

		o.put("animals", a);

		return o;
	}

	public List<AnimalInfo> getAnimalsInfo() {
		return new ArrayList<>(_animales);
	}

	/* METODOS PROTECTED AÑADIDOS POR EL ALUMNO */
	protected int count(Predicate<Animal> pred) {
		int i = 0;

		for (Animal a : _animales)
			if (pred.test(a))
				i++;

		return i;
	}

}
