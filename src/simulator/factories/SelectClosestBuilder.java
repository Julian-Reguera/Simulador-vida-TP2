package simulator.factories;

import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import simulator.model.Animal;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {

	public SelectClosestBuilder() {
		super("closest", "Devuelve una Selection Strategy de SelectFirstBuilder");
	}

	@Override
	protected void fill_in_data(JSONObject o) {
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) throws IllegalArgumentException {
		SelectionStrategy estrategia = null;

		if (data.isEmpty()) {
			estrategia = new SelectClosest();
		} else
			throw new IllegalArgumentException();

		return estrategia;
	}

	static public class SelectClosest implements SelectionStrategy {

		@Override
		public Animal select(Animal a, List<Animal> as) {
			Animal seleccionado = null;
			double distancia, aux;

			if (as.size() != 0) {
				Iterator<Animal> iterador = as.iterator();
				seleccionado = iterador.next();
				distancia = a.get_position().distanceTo(seleccionado.get_position());

				while (iterador.hasNext()) {
					Animal intermedio = iterador.next();
					aux = intermedio.get_position().distanceTo(a.get_position());
					if (aux < distancia) {
						distancia = aux;
						seleccionado = intermedio;
					}
				}
			}

			return seleccionado;
		}
	}

}
