package simulator.model;

public class DefaultRegion extends Region {

	public DefaultRegion() {
		super();
	}

	@Override
	public double get_food(Animal a, double dt) {
		double comida;

		if (a.get_diet() == Diet.HERBIVORE) {
			comida = 60.0 * Math.exp(-Math.max(0, count((an) -> an.get_diet() == Diet.HERBIVORE) - 5.0) * 2.0) * dt;
		} else
			comida = 0;

		return comida;
	}

	@Override
	public void update(double dt) {
	}

	@Override
	public String toString() {
		return "Default region";
	}

}
