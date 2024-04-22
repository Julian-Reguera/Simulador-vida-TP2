package simulator.factories;

import java.util.List;

import org.json.JSONObject;

import simulator.model.Animal;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	public SelectFirstBuilder() {
		super("first", "Crea un SelectionStrategy que escoge el primero de la lista.");
	}

	@Override
	protected void fill_in_data(JSONObject o) {
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) throws IllegalArgumentException {
		SelectionStrategy resultado = null;

		if (data.isEmpty()) {
			resultado = new SelectFirst();
		} else
			throw new IllegalArgumentException();

		return resultado;
	}

	static public class SelectFirst implements SelectionStrategy {

		@Override
		public Animal select(Animal a, List<Animal> as) {
			Animal seleccionado = null;

			if (as.size() != 0) {
				seleccionado = as.get(0);
			}

			return seleccionado;
		}

	}

}
