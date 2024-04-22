package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {
	String _title;
	TableModel _tableModel;
	JTable _table;
	JScrollPane _scrollPanel;
	
	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLACK, 2),_title));
		_table = new JTable(_tableModel);
		_scrollPanel = new JScrollPane(_table);
		_scrollPanel.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		this.add(_scrollPanel);
	}
}