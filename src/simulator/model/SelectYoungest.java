package simulator.model;

import java.util.Iterator;
import java.util.List;

public class SelectYoungest implements SelectionStrategy {

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
