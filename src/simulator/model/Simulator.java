package simulator.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.model.Animal.State;

public class Simulator implements JSONable, Observable<EcoSysObserver> {
	private Factory<Animal> _animals_factory;
	private Factory<Region> _regions_factory;
	private RegionManager _gestor;
	private List<Animal> _animales;
	private List<Animal> _aux_animales;
	private boolean _enForEach; // cuando esto esté a true se está dentro de un for each
	private int _cols, _rows, _width, _height; // raro guardarlo si no se usa pero ya lo tiene regionManager
	private double _time;

	private List<EcoSysObserver> _observers;
	
	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {

		_observers = new LinkedList<EcoSysObserver>();
		_animals_factory = animals_factory;
		_regions_factory = regions_factory;
		reset(cols, rows, width, height);
	}

	private void set_region(int row, int col, Region r) {
		_gestor.set_region(row, col, r);
		notify_on_regionSet( row, col, r);
	}

	public void setRegion(int row, int col, JSONObject r_json) {
		set_region(row, col, _regions_factory.create_instance(r_json));
	}

	private void add_animal(Animal a) {
		if (_enForEach)
			_aux_animales.add(a);
		else {
			_animales.add(a);
			_gestor.register_animal(a);
		}
		
		notify_on_animalAdded(a);
	}

	public void add_animal(JSONObject a_json) {
		add_animal(_animals_factory.create_instance(a_json));
	}

	public MapInfo get_map_info() {
		return _gestor;
	}

	public List<? extends AnimalInfo> get_animals() {
		return Collections.unmodifiableList(_animales);
	}

	public double get_time() {
		return _time;
	}

	public void advance(double dt) {
		_time += dt;
		eliminarCadaveres();

		for (Animal a : _animales) {
			a.update(dt);
			_gestor.update_animal_region(a);
		}

		_gestor.update_all_regions(dt);

		setEnForEach(true); // para poder anadir un animal en mitad del for each
		for (Animal a : _animales)
			if (a.is_pregnant())
				add_animal(a.deliver_baby());
		setEnForEach(false);
		
		notify_on_advance(dt);
	}

	@Override
	public JSONObject as_JSON() {
		JSONObject resultado = new JSONObject();
		resultado.put("time", _time);
		resultado.put("state", _gestor.as_JSON());

		return resultado;
	}

	public void reset(int cols, int rows, int width, int height) {
		_time = 0.0;
		_cols = cols;
		_rows = rows;
		_width = width;
		_height = height;
		_animales = new LinkedList<Animal>();
		_aux_animales = new LinkedList<Animal>(); // para poder añadir en un forEach
		_enForEach = false;
		_gestor = new RegionManager(cols, rows, width, height);
		
		notify_on_reset();
	}

	/* FUNCIONES PRIVADAS HECHAS POR EL ALUMNO */

	// para poder anadir obejas en mitad de un bucle
	private void setEnForEach(boolean entrada) {
		if (!entrada && _enForEach) // paso los elementos de la lista auxiliar a la lista de animales
		{
			Iterator<Animal> iterador = _aux_animales.iterator();
			while (iterador.hasNext()) {
				Animal a = iterador.next();
				_animales.add(a);
				_gestor.register_animal(a);
				iterador.remove();
			}
		}

		_enForEach = entrada;
	}

	private void eliminarCadaveres() {
		Iterator<Animal> i = _animales.iterator();

		while (i.hasNext()) {
			Animal a = i.next();

			if (a.get_state() == State.DEAD) {
				_gestor.unregister_animal(a);
				i.remove();
			}
		}
	}

	@Override
	public void addObserver(EcoSysObserver o) {
		_observers.add(o);
		o.onRegister(_time, _gestor, new ArrayList<>(_animales));
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		_observers.remove(o);
	}
	
	private void notify_on_reset()
	{
		List<AnimalInfo> animales = new ArrayList<>(_animales);
		
		for(EcoSysObserver o: _observers)
		{
			o.onReset(_time, _gestor,animales);
		}
	}
	
	private void notify_on_animalAdded(Animal a)
	{
		List<AnimalInfo> animales = new ArrayList<>(_animales);
		
		for(EcoSysObserver o: _observers)
		{
			o.onAnimalAdded(_time, _gestor, animales, a);
		}
	}
	
	private void notify_on_regionSet(int row, int col, Region r)
	{
		for(EcoSysObserver o: _observers)
		{
			o.onRegionSet(row, col, _gestor, r);
		}
	}
	
	private void notify_on_advance(double dt)
	{
		List<AnimalInfo> animales = new ArrayList<>(_animales);
		
		for(EcoSysObserver o: _observers)
		{
			o.onAvanced(_time, _gestor, animales, dt);
		}
	}
}
