package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic food supply");
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		o.put("factor", "food increase factor (optional, default 2.0)");
		o.put("food", "initial amount of food (optional, default 100.0)");
	}

	@Override
	protected Region create_instance(JSONObject data) throws IllegalArgumentException {
		double factor = 2.0;
		double food = 1000.0;
		int contador = 0;

		if (data.has("factor"))
			contador++;
		factor = data.optDouble("factor", 2.0);

		if (data.has("food"))
			contador++;
		food = data.optDouble("food");

		if (data.length() != contador)
			throw new IllegalArgumentException(); // si hay mas cosas que las que debería

		return new DynamicSupplyRegion(food, factor);
	}

}
