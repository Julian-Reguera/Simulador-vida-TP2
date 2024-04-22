package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.MapInfo.RegionData;

public class RegionManager implements AnimalMapView{
	private Map<Animal, Region> _animal_region;
	private int _width;
	private int _height;
	private int _cols;
	private int _rows;
	private int _regionWidth;
	private int _regionHeight;
	private Region[][] _regions;

	public RegionManager(int cols, int rows, int width, int height) {
		if (cols < 0 || rows < 0 || width < 0 || height < 0)
			throw new IllegalArgumentException(
					"tanto las dimensiones del mapa como el numero de filas y columnas deben ser positivos");

		_width = width;
		_height = height;
		_cols = cols;
		_rows = rows;
		_regionWidth = width / cols + (width % cols != 0 ? 1 : 0);
		_regionHeight = height / rows + (height % rows != 0 ? 1 : 0);
		_regions = new Region[rows][cols];
		_animal_region = new HashMap<Animal, Region>();

		for (int i = 0; i < rows; i++) {
			for (int e = 0; e < cols; e++) {
				_regions[i][e] = new DefaultRegion();
			}
		}
	}

	public void set_region(int row, int col, Region r) {
		List<Animal> animales = _regions[row][col].getAnimals();

		for (Animal a : animales) {
			r.add_animal(a);
			_animal_region.put(a, r);
		}

		_regions[row][col] = r;
	}

	public void register_animal(Animal a) {
		a.init(this);

		Vector2D pos = a.get_position();
		int aCol = (int) pos.getX() / _regionWidth, aRow = (int) pos.getY() / _regionHeight;

		_regions[aRow][aCol].add_animal(a);
		_animal_region.put(a, _regions[aRow][aCol]);
	}

	public void unregister_animal(Animal a) {
		_animal_region.get(a).remove_animal(a);
		_animal_region.remove(a);
	}

	public void update_animal_region(Animal a) {
		Vector2D pos = a.get_position();
		int aCol = (int) pos.getX() / _regionWidth, aRow = (int) pos.getY() / _regionHeight;

		if (_regions[aRow][aCol] != _animal_region.get(a)) {
			_animal_region.get(a).remove_animal(a);
			_regions[aRow][aCol].add_animal(a);
			_animal_region.put(a, _regions[aRow][aCol]);
		}
	}

	public void update_all_regions(double dt) {
		for (Region[] arrayReg : _regions) {
			for (Region reg : arrayReg) {
				reg.update(dt);
			}
		}
	}

	@Override
	public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter) {
		List<Animal> salida = new ArrayList<Animal>();
		int x_ini, x_fin, y_ini, y_fin;
		Vector2D aux = new Vector2D(a.get_sight_range(), a.get_sight_range());
		Vector2D limSup = a.get_position().minus(aux), limInf = a.get_position().plus(aux);

		x_ini = Math.max(0, (int) (limSup.getX() / _regionWidth));
		x_fin = Math.min(_cols - 1, (int) (limInf.getX() / _regionWidth));
		y_ini = Math.max(0, (int) (limSup.getY() / _regionHeight));
		y_fin = Math.min(_rows - 1, (int) (limInf.getY() / _regionHeight));

		for (int y = y_ini; y <= y_fin; y++) {
			for (int x = x_ini; x <= x_fin; x++) {
				List<Animal> animales = _regions[y][x].getAnimals();
				for (Animal animal : animales) {
					if (animal.get_position().distanceTo(a.get_position()) <= a.get_sight_range() && !animal.equals(a)
							&& filter.test(animal)) {
						salida.add(animal);
					}
				}
			}
		}

		return salida;
	}

	@Override
	public double get_food(Animal a, double dt) {
		return _animal_region.get(a).get_food(a, dt);
	}

	@Override
	public int get_cols() {
		return _cols;
	}

	@Override
	public int get_rows() {
		return _rows;
	}

	@Override
	public int get_width() {
		return _width;
	}

	@Override
	public int get_height() {
		return _height;
	}

	@Override
	public int get_region_width() {
		return _regionWidth;
	}

	@Override
	public int get_region_height() {
		return _regionHeight;
	}

	public JSONObject as_JSON() {
		JSONObject o = new JSONObject();
		JSONArray a = new JSONArray();

		for (int row = 0; row < _regions.length; row++) {
			for (int col = 0; col < _regions[row].length; col++) {
				JSONObject region = new JSONObject();

				region.put("row", row);
				region.put("col", col);
				region.put("data", _regions[row][col].as_JSON());

				a.put(region);
			}
		}
		o.put("regiones", a);

		return o;
	}

	@Override
	public Iterator<RegionData> iterator() {
		return new RegionIterator();
	}
	
	private class RegionIterator implements Iterator<RegionData>{
		private int _x;
		private int _y;
		
		public RegionIterator()
		{
			_x = -1;
			_y = 0;
		}
		
		@Override
		public boolean hasNext() {
			return _x+1 < _cols || _y+1 < _rows;
		}

		@Override
		public RegionData next() {
			_x++;
			if(_x >= _cols)
			{
				_x = 0;
				_y++;
			}
			return new RegionData(_y,_x,_regions[_y][_x]);
		}
	}
}