package simulator.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders;
	private List<JSONObject> _builders_info;

	public BuilderBasedFactory() {
		_builders = new HashMap<String, Builder<T>>();
		_builders_info = new LinkedList<JSONObject>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
		for (Builder<T> b : builders)
			add_builder(b);
	}

	public void add_builder(Builder<T> b) {
		_builders.put(b.get_type_tag(), b);
		_builders_info.add(b.get_info());
	}

	@Override
	public T create_instance(JSONObject info) {
		T result = null;

		if (info == null) {
			throw new IllegalArgumentException("’info’ cannot be null");
		}

		Builder<T> builder = _builders.get(info.getString("type"));

		if (builder != null) {
			JSONObject aux = (info.has("data") ? info.getJSONObject("data") : new JSONObject());
			result = builder.create_instance(aux);
		} else
			throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());

		return result;
	}

	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	}
}
