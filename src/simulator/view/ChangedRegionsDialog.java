package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.json.JSONObject;
import org.json.JSONArray;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
	private static final String TEXTO_AYUDA = "<html>Select a region type, thre rows/cols interval, and provide values for the parametes in the <b>Value column</b> (default values are used for parametes with no value).</html>";

	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;

	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;

	private String[] _headers = { "Key", "Value", "Description" };

	private JComboBox<String> _regionsBox;
	private JComboBox<String> _fromRowBox;
	private JComboBox<String> _toRowBox;
	private JComboBox<String> _fromColBox;
	private JComboBox<String> _toColBox;

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		_ctrl = ctrl;
		initGUI();
		ctrl.addObserver(this);
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		iniciaMoledos();

		JTable tabla = new JTable(_dataTableModel);
		JPanel pTexto = new JPanel(new BorderLayout());
		JScrollPane pTabla = new JScrollPane(tabla);
		JPanel pComboBoxes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));

		TableColumnModel modeloCols = tabla.getColumnModel();
		modeloCols.getColumn(0).setResizable(false);
		modeloCols.getColumn(0).setPreferredWidth(200);
		modeloCols.getColumn(0).setMaxWidth(200);
		modeloCols.getColumn(1).setResizable(false);
		modeloCols.getColumn(1).setPreferredWidth(150);
		modeloCols.getColumn(1).setMaxWidth(150);
		modeloCols.getColumn(2).setResizable(true);

		pTexto.add(new JLabel(TEXTO_AYUDA), BorderLayout.NORTH);

		_regionsBox = new JComboBox<>(_regionsModel);
		_regionsBox.addActionListener((a) -> {
			actualizaTabla();
		});
		_fromRowBox = new JComboBox<>(_fromRowModel);
		_toRowBox = new JComboBox<>(_toRowModel);
		_fromColBox = new JComboBox<>(_fromColModel);
		_toColBox = new JComboBox<>(_toColModel);

		pComboBoxes.add(new JLabel("Region type:"));
		pComboBoxes.add(_regionsBox);
		pComboBoxes.add(new JLabel("Row from/to:"));
		pComboBoxes.add(_fromRowBox);
		pComboBoxes.add(_toRowBox);
		pComboBoxes.add(new JLabel("Column from/to:"));
		pComboBoxes.add(_fromColBox);
		pComboBoxes.add(_toColBox);

		pTexto.setMinimumSize(new Dimension(0, 0));
		pTexto.setPreferredSize(new Dimension(new Dimension(Integer.MAX_VALUE, 75)));
		pTexto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
		pTabla.setMinimumSize(new Dimension(0, 0));
		pTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JButton cancel = new JButton("Cancel");
		JButton ok = new JButton("OK");

		cancel.addActionListener((a) -> {
			SwingUtilities.invokeLater(() -> {
				this.setVisible(false);
			});
		});
		ok.addActionListener((a) -> {
			hacerCambiosModelo();
		});

		pBotones.add(cancel);
		pBotones.add(ok);

		mainPanel.add(pTexto);
		mainPanel.add(pTabla);
		mainPanel.add(pComboBoxes);
		mainPanel.add(pBotones);
		mainPanel.add(Box.createVerticalStrut(30));

		actualizaTabla();

		setContentPane(mainPanel);
		setPreferredSize(new Dimension(700, 400));
		this.setMinimumSize(new Dimension(600, 400));
		pack();
	}

	private void hacerCambiosModelo() {
		if (_fromColModel.getSelectedItem() != null && _toColModel.getSelectedItem() != null
				&& _fromRowModel.getSelectedItem() != null && _toRowModel.getSelectedItem() != null) {
			JSONObject regions = new JSONObject();
			JSONArray array = new JSONArray();
			JSONObject region = new JSONObject();
			JSONObject spec = new JSONObject();
			JSONObject data = new JSONObject();
			JSONArray col = new JSONArray();
			JSONArray row = new JSONArray();

			for (int i = 0; i < _dataTableModel.getRowCount(); i++) {
				data.put((String) _dataTableModel.getValueAt(i, 0), _dataTableModel.getValueAt(i, 1));
			}

			spec.put("type", (String) _regionsModel.getSelectedItem());
			spec.put("data", data);

			int colInicio = Integer.parseInt((String) _fromColModel.getSelectedItem());
			int colFinal = Integer.parseInt((String) _toColModel.getSelectedItem());
			int rowInicio = Integer.parseInt((String) _fromRowModel.getSelectedItem());
			int rowFinal = Integer.parseInt((String) _toRowModel.getSelectedItem());

			col.put(colInicio);
			col.put(colFinal);
			row.put(rowInicio);
			row.put(rowFinal);

			region.put("col", col);
			region.put("row", row);
			region.put("spec", spec);

			array.put(region);

			regions.put("regions", array);

			try {
				_ctrl.set_regions(regions);
			} catch (Exception e) {
				SwingUtilities.invokeLater(() -> {
					ViewUtils.showErrorMsg(e.getMessage());
				});
			}

		} else {
			SwingUtilities.invokeLater(() -> {
				ViewUtils.showErrorMsg("Error faltan valores por introducir");
			});
		}
	}

	private void iniciaMoledos() {
		_regionsInfo = Main._facRegiones.get_info();

		_dataTableModel = new DefaultTableModel(_headers, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};

		_regionsModel = new DefaultComboBoxModel<>();

		for (JSONObject o : _regionsInfo) {
			if (o.has("type")) {
				_regionsModel.addElement(o.getString("type"));
			}
		}

		_fromRowModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();
		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();
	}

	public void open(Frame parent) {
		setLocation(//
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			actualizaCB(map);
		});
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			actualizaCB(map);
		});
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// mejor no pongoque se actualizen las cosas porque serían muchas
		// actualizaciones innecesarias

	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// mejor no pongo que se actualizen las cosas porque serían muchas
		// actualizaciones innecesarias.

	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// mejor no pongo que se actualizen las cosas porque serían muchas
		// actualizaciones
	}

	private void actualizaCB(MapInfo map) {
		_fromRowModel.removeAllElements();
		_toRowModel.removeAllElements();
		_fromColModel.removeAllElements();
		_toColModel.removeAllElements();

		int cols = map.get_cols();
		int rows = map.get_rows();
		List<String> oCols = new ArrayList<>();
		List<String> oRows = new ArrayList<>();

		for (int i = 0; i < cols; i++) {
			oCols.add(Integer.toString(i));
		}

		for (int i = 0; i < rows; i++) {
			oRows.add(Integer.toString(i));
		}

		_fromRowModel.addAll(oRows);
		_toRowModel.addAll(oRows);
		_fromColModel.addAll(oCols);
		_toColModel.addAll(oCols);
	}

	private void actualizaTabla() {
		String valor = (String) _regionsBox.getSelectedItem();
		JSONObject infoSeleccion = null;

		for (JSONObject o : _regionsInfo) {
			if (o.has("type") && o.getString("type").equals(valor)) {
				if (o.has("data"))
					infoSeleccion = o.getJSONObject("data");
				break;
			}
		}

		int filas = _dataTableModel.getRowCount();
		for (int i = filas; i > 0; i--) {
			_dataTableModel.removeRow(i - 1);
		}

		if (infoSeleccion != null) {
			Collection<String> keys = infoSeleccion.keySet();

			for (String clave : keys) {
				Object[] row = { clave, null, infoSeleccion.getString(clave) };
				_dataTableModel.addRow(row);
			}
		}
	}
}
