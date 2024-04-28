package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame {
	private Controller _ctrl;
	private ControlPanel _control;
	private StatusBar _barraEstado;
	private InfoTable _tablaEspecies;
	private InfoTable _tablaRegiones;

	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.setContentPane(mainPanel);

		_control = new ControlPanel(_ctrl);
		mainPanel.add(_control, BorderLayout.PAGE_START);
		_barraEstado = new StatusBar(_ctrl);
		mainPanel.add(_barraEstado, BorderLayout.PAGE_END);

		// Definición del panel de tablas (usa un BoxLayout vertical)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridLayout(2, 1, 10, 10)); // 5 filas, 2 columnas, 3 de espacip, 3 de espacio
		mainPanel.add(contentPanel, BorderLayout.CENTER);

		_tablaEspecies = new InfoTable("Species", new SpeciesTableModel(_ctrl));
		_tablaEspecies.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(_tablaEspecies);

		_tablaRegiones = new InfoTable("Regions", new RegionsTableModel(_ctrl));
		_tablaRegiones.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(_tablaRegiones);

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				ViewUtils.quit(MainWindow.this);
			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}

}