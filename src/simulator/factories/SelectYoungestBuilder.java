package simulator.factories;

import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import simulator.model.Animal;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {

	public SelectYoungestBuilder() {
		super("youngest", "Describe algo no se sabe el que");
	}

	@Override
	protected void fill_in_data(JSONObject o) {
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) throws IllegalArgumentException {
		SelectionStrategy estrategia = null;

		if (data.isEmpty()) {
			estrategia = new SelectYoungest();
		} else
			throw new IllegalArgumentException();

		return estrategia;
	}

	static public class SelectYoungest implements SelectionStrategy {

		@Override
		public Animal select(Animal a, List<Animal> as) {
			Animal seleccionado = null;
			double edad, aux;

			if (as.size() != 0) {
				Iterator<Animal> iterador = as.iterator();
				seleccionado = iterador.next();
				edad = seleccionado.get_age();

				while (iterador.hasNext()) {
					Animal intermedio = iterador.next();
					aux = seleccionado.get_age();
					if (aux < edad) {
						edad = aux;
						seleccionado = intermedio;
					}
				}
			}

			return seleccionado;
		}

	}

}
