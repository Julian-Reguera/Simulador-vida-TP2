package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
	private static final double INCREASE_PROB = 0.5;

	private double _food;
	private double _factor;

	public DynamicSupplyRegion(double food, double factor) {
		super();
		if (food < 0)
			throw new IllegalArgumentException("La comida debe ser un numero positivo");
		if (factor < 0)
			throw new IllegalArgumentException("El factor debe ser un numero no negativo");
		_food = food;
		_factor = factor;
	}

	@Override
	public double get_food(Animal a, double dt) {
		double comida = 0;

		if (a.get_diet() == Diet.HERBIVORE) {
			comida = Math.min(_food,
					60.0 * Math.exp(-Math.max(0, count((an) -> an.get_diet() == Diet.HERBIVORE) - 5.0) * 2.0) * dt);
		}

		_food -= comida;

		return comida;
	}

	@Override
	public void update(double dt) {
		if (Utils._rand.nextDouble(1) > INCREASE_PROB) {
			_food += dt * _factor;
		}
	}

	@Override
	public String toString() {
		return "Dynamic region";
	}
}
