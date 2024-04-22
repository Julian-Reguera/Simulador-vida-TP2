package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {

	Factory<SelectionStrategy> _strategyBuilder;

	public WolfBuilder(Factory<SelectionStrategy> strategyBuilder) {
		super("wolf", "genera objetos de tipo wolf");
		if (strategyBuilder == null)
			throw new IllegalArgumentException("strategyBuilder no puede ser null");
		_strategyBuilder = strategyBuilder;
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		JSONObject posicion = new JSONObject();
		posicion.put("x_range", new JSONArray().put(100.0).put(200.0));
		posicion.put("y_range", new JSONArray().put(100.0).put(200.0));

		// o.put("mate_strategy", _strategyBuilder.get_info());
		// o.put("hunt_strategy", _strategyBuilder.get_info());
		o.put("pos", posicion);
	}

	@Override
	protected Animal create_instance(JSONObject data) {
		int contador = 0; // va a contar el numero de keys que se usan
		Vector2D posicion = null;
		SelectionStrategy mateStrategy = new SelectFirstBuilder.SelectFirst();
		SelectionStrategy huntStrategy = new SelectFirstBuilder.SelectFirst();

		if (data.has("pos")) {
			posicion = getPosition(data.getJSONObject("pos"));
			contador++;
		}

		if (data.has("mate_strategy")) {
			mateStrategy = _strategyBuilder.create_instance(data.getJSONObject("mate_strategy"));
			contador++;
		}

		if (data.has("hunt_strategy")) {
			huntStrategy = _strategyBuilder.create_instance(data.getJSONObject("hunt_strategy"));
			contador++;
		}

		if (data.length() != contador)
			throw new IllegalArgumentException(); // si hay mas cosas de las que hace falta

		return new Wolf(mateStrategy, huntStrategy, posicion);
	}

	private static Vector2D getPosition(JSONObject o) {
		JSONArray json_x_range = o.getJSONArray("x_range");
		JSONArray json_y_range = o.getJSONArray("y_range");
		double x_range[] = new double[2];
		double y_range[] = new double[2];

		// si los rangos no son arrays de 2 elementos
		if (json_x_range.length() != 2 || json_y_range.length() != 2)
			throw new IllegalArgumentException("Los rangos para crear la posicion deben tener 2 elementos");

		// si dentro de los arrays en vez de haber double hay otro tipo de objeto
		try {
			x_range[0] = json_x_range.getDouble(0);
			x_range[1] = json_x_range.getDouble(1);
			y_range[0] = json_y_range.getDouble(0);
			y_range[1] = json_y_range.getDouble(1);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		Vector2D resultado = Vector2D.randomPoint(Math.max(x_range[0], x_range[1]), Math.max(y_range[0], y_range[1]));
		resultado = resultado.plus(new Vector2D(-Math.min(x_range[0], x_range[1]), -Math.min(y_range[0], y_range[1])));

		return resultado;
	}

}
