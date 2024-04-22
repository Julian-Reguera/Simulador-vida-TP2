package simulator.control;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.model.Simulator;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {
	private Simulator _sim;

	public Controller(Simulator sim) {
		if (sim == null)
			throw new IllegalArgumentException("el simulador no puede ser nulo");
		_sim = sim;
	}

	public void load_data(JSONObject data) {
		set_regions(data);
		JSONArray animales = data.getJSONArray("animals");
		Iterator<Object> it = animales.iterator();

		while (it.hasNext()) {
			JSONObject o = (JSONObject) it.next();
			for (int i = o.getInt("amount"); i > 0; i--) {
				_sim.add_animal(o.getJSONObject("spec"));
			}
		}
	}

	public void run(double t, double dt, boolean sv, OutputStream out) {
		JSONObject o = new JSONObject();
		o.put("in", _sim.as_JSON());

		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(), m.get_cols(), m.get_rows());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		while (_sim.get_time() < t) {
			_sim.advance(dt);
			if (sv)
				view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		o.put("out", _sim.as_JSON());
		if (sv)
			view.close();

		try {
			out.write(o.toString(3).getBytes());
		} catch (Exception e) {
		}
	}
	
	public void reset(int cols, int rows, int width, int height)
	{
		_sim.reset(cols, rows, width, height);
	}
	
	public void set_regions(JSONObject rs)
	{
		if (rs.has("regions"))
		{
			JSONArray regiones = rs.getJSONArray("regions");
			Iterator<Object> i = regiones.iterator();

			while (i.hasNext()) {
				JSONObject o = (JSONObject) i.next();
				JSONArray jCol = o.getJSONArray("col");
				JSONArray jRow = o.getJSONArray("row");

				for (int col = jCol.getInt(0); col <= jCol.getInt(1); col++) {
					for (int row = jRow.getInt(0); row <= jRow.getInt(1); row++) {
						_sim.setRegion(row, col, o.getJSONObject("spec"));
					}
				}
			}
		}
	}
	
	public void advance(double dt)
	{
		_sim.advance(dt);
	}
	
	public void addObserver(EcoSysObserver o)
	{
		_sim.addObserver(o);
	}
	
	public void removeObserver(EcoSysObserver o)
	{
		_sim.removeObserver(o);
	}

	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.get_genetic_code(), (int) a.get_position().getX(), (int) a.get_position().getY(),
					(int) Math.round(a.get_age()) + 2));
		return ol;
	}

}
