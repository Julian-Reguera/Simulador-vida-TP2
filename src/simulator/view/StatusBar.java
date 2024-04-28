package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {
	
	private static final String TIME_FORMAT = "Time: %.3f";
	private static final String ANIMALS_FORMAT = "Total Animals: %d";
	private static final String DIMENSION_FORMAT = "Dimension: %dx%d %dx%d";
	
	private static final double INIT_TIME = 0;
	private static final int INIT_ANIMALS = 0;
	private static final int INIT_WIDTH = 0;
	private static final int INIT_HEIGHT = 0;
	private static final int INIT_COLS = 0;
	private static final int INIT_ROWS = 0;
	
	
	private JLabel _tiempo;
	private JLabel _animales;
	private JLabel _dimensiones;
	
	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}
	
	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));

		iniciaTextos();
	}
	
	private void iniciaTextos()
	{
		_tiempo = new JLabel(String.format(TIME_FORMAT, INIT_TIME));
		_animales = new JLabel(String.format(ANIMALS_FORMAT, INIT_ANIMALS));
		_dimensiones = new JLabel(String.format(DIMENSION_FORMAT,INIT_WIDTH, INIT_HEIGHT, INIT_COLS,INIT_ROWS));
		
		this.add(_tiempo);
		
		JSeparator s1 = new JSeparator(JSeparator.VERTICAL);
		s1.setPreferredSize(new Dimension(10, 20));
		this.add(s1);
		
		this.add(_animales);
		
		JSeparator s2 = new JSeparator(JSeparator.VERTICAL);
		s2.setPreferredSize(new Dimension(10, 20));
		this.add(s2);
		
		this.add(_dimensiones);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(()->{
			_tiempo.setText(String.format(TIME_FORMAT, time));
			_animales.setText(String.format(ANIMALS_FORMAT, animals.size()));
			_dimensiones.setText(String.format(DIMENSION_FORMAT,map.get_width(), map.get_height(), map.get_cols(),map.get_rows()));
		});
	}
	
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(()->{
			_tiempo.setText(String.format(TIME_FORMAT, time));
			_animales.setText(String.format(ANIMALS_FORMAT, animals.size()));
			_dimensiones.setText(String.format(DIMENSION_FORMAT,map.get_width(), map.get_height(), map.get_cols(),map.get_rows()));
		});
	}
	
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		SwingUtilities.invokeLater(()->{
			_animales.setText(String.format(ANIMALS_FORMAT, animals.size()));
		});
	}
	
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		SwingUtilities.invokeLater(()->{
			_dimensiones.setText(String.format(DIMENSION_FORMAT,map.get_width(), map.get_height(), map.get_cols(),map.get_rows()));
		});
	}
	
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(()->{
			_tiempo.setText(String.format(TIME_FORMAT, time));
		});
	}
}
