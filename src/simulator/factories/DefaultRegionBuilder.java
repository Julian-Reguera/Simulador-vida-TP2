package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {

	public DefaultRegionBuilder() {
		super("default", "Infinite food supply");
	}

	@Override
	protected void fill_in_data(JSONObject o) {
	}

	@Override
	protected Region create_instance(JSONObject data) throws IllegalArgumentException {
		if (data.length() != 0)
			throw new IllegalArgumentException();

		return new DefaultRegion();
	}

}
