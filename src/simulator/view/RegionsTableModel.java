package simulator.view;

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
import simulator.model.Diet;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	private Object[][] _filas;
    private String[] _columnas;
    private Map<Diet, Integer> _nombreColumnas;
	
	RegionsTableModel(Controller ctrl) {
		Diet[] dietas = Diet.values();
		_columnas = new String[dietas.length + 3];
		_filas = new Object[0][0];
		_nombreColumnas = new HashMap<>();
		
		_columnas[0] = "Row";
		_columnas[1] = "Col";
		_columnas[2] = "Desc.";
		
		for(int i = 0; i< dietas.length;i++)
		{
			_columnas[i+3] = dietas[i].toString();
			_nombreColumnas.put(dietas[i], i+3);
		}
		
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return _filas.length;
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
		return _filas[rowIndex][columnIndex];
	}

	/*METODOS DE LA INTERFAZ ECOSYSOBSERVER*/
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(()->{
			_filas = new Object[map.get_cols()*map.get_rows()][_columnas.length];
			actualizaFilas(map);
			fireTableDataChanged();
		});
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(()->{
			_filas = new Object[map.get_cols()*map.get_rows()][_columnas.length];
			actualizaFilas(map);
			fireTableDataChanged();
		});
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		SwingUtilities.invokeLater(()->{
			actualizaFilas(map);
			fireTableDataChanged();
		});
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		SwingUtilities.invokeLater(()->{
			_filas[row*map.get_cols() + col][2] = r.toString();
			fireTableCellUpdated(row*map.get_cols() + col,2);
		});
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(()->{
			actualizaFilas(map);
			fireTableDataChanged();
		});
	}
	
	private void actualizaFilas( MapInfo map)
	{
		int cont = 0;
		for(MapInfo.RegionData r : map)
		{
			Object[] fila = _filas[cont];
			fila[0] = r.row();
			fila[1] = r.col();
			fila[2] = r.r().toString();
			
			//reinicio el contador de cada tipo de animal para la region
			for(int i = 0; i< Diet.values().length;i++)
			{
				fila[i+3] = 0;
			}
			
			//voy contando
			for(AnimalInfo a: r.r().getAnimalsInfo())
			{
				int col = _nombreColumnas.get(a.get_diet());
				fila[col] = (Integer)fila[col]+1;
			}
			cont++;
		}
	}
}
