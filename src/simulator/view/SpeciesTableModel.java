package simulator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.Animal.State;;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	private List<Object[]> _filas;
	private String[] _columnas;
	private Map<State, Integer> _nombreColumnas;
	private Map<String, Object[]> _nombreFilas;

	SpeciesTableModel(Controller ctrl) {

		State[] estados = State.values();
		_filas = new ArrayList<Object[]>();
		_nombreColumnas = new HashMap<>();
		_nombreFilas = new HashMap<>();
		_columnas = new String[1 + estados.length];

		_columnas[0] = "Species";

		for (int i = 0; i < estados.length; i++) {
			_columnas[i + 1] = estados[i].toString();
			_nombreColumnas.put(estados[i], i + 1);
		}

		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return _filas.size();
	}

	@Override
	public int getColumnCount() {
		return _columnas.length;
	}

	@Override
	public String getColumnName(int column) {
		return _columnas[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return _filas.get(rowIndex)[columnIndex];
	}

	/* METODOS DE LA INTERFAZ ECOSYSOBSERVER */
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			actualizaAnimales(animals);
			fireTableDataChanged();
		});
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			actualizaAnimales(animals);
			fireTableDataChanged();
		});
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		SwingUtilities.invokeLater(() -> {
			anadirAnimal(a);
			fireTableDataChanged();
		});
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(() -> {
			actualizaAnimales(animals); // reinicio todo porque no se que animales han muerto y cuales no
			fireTableDataChanged();
		});
	}

	private void actualizaAnimales(List<AnimalInfo> animals) {
		// reinicio todas las columnas
		for (Object[] o : _filas) {
			for (int i = 1; i < o.length; i++) {
				o[i] = 0;
			}
		}

		for (AnimalInfo a : animals) {
			anadirAnimal(a);
		}
	}

	private void anadirAnimal(AnimalInfo a) {
		if (!_nombreFilas.containsKey(a.get_genetic_code())) {
			Object[] fila = new Object[1 + State.values().length];
			fila[0] = a.get_genetic_code();

			for (int i = 0; i < State.values().length; i++) {
				fila[i + 1] = 0;
			}

			_filas.add(fila);
			_nombreFilas.put(a.get_genetic_code(), fila);
		}

		Object[] fila = _nombreFilas.get(a.get_genetic_code());
		int col = _nombreColumnas.get(a.get_state());
		fila[col] = (Integer) fila[col] + 1;
	}

}
