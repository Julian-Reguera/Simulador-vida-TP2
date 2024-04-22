package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.launcher.Main;

class ControlPanel extends JPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	
	private JButton _openButton;
	private JButton _viewerButton;
	private JButton _regionsButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JButton _quitButton;
	
	private JLabel _labelSpiner;
	private JSpinner _spiner;
	private JLabel _labelDt;
	private JTextField _textoDt;
	
	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		_changeRegionsDialog  = new ChangeRegionsDialog(_ctrl);
		initGUI();
	}
	
	private void initGUI(){
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);
		initButtons();
		
		_fc = new JFileChooser();
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		_changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
	}
	

	private void initButtons()
	{
		_openButton = new JButton();
		_viewerButton = new JButton();
		_regionsButton = new JButton();
		_runButton = new JButton();
		_stopButton = new JButton();
		_quitButton = new JButton();
		
		_openButton.setToolTipText("Load an input file into the simulator");
		_viewerButton.setToolTipText("Map Viewer");
		_regionsButton.setToolTipText("Change Regions");
		_runButton.setToolTipText("Run the simulator");
		_stopButton.setToolTipText("Stop the simulator");
		_quitButton.setToolTipText("Quit");
		
		_openButton.setIcon( new ImageIcon("resources/icons/open.png")); 
		_viewerButton.setIcon( new ImageIcon("resources/icons/viewer.png")); 
		_regionsButton.setIcon( new ImageIcon("resources/icons/regions.png")); 
		_runButton.setIcon( new ImageIcon("resources/icons/run.png")); 
		_stopButton.setIcon( new ImageIcon("resources/icons/stop.png") ); 
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		
		_openButton.addActionListener((e)->{
			int result = _fc.showOpenDialog(ViewUtils.getWindow(this));
			if(result == JFileChooser.APPROVE_OPTION) //compruebo que se ha abierto un archivo
			{
				
				try(InputStream selectedFile = new FileInputStream(_fc.getSelectedFile());) {
					JSONObject in = new JSONObject(new JSONTokener(selectedFile));
					_ctrl.reset(in.getInt("cols"), in.getInt("rows"), in.getInt("width"),in.getInt("height"));
					_ctrl.load_data(in);
					
				} catch (IOException e1) {
					ViewUtils.showErrorMsg("error al cargar el archivo");
				} catch(Exception e1)
				{
					ViewUtils.showErrorMsg(e1.getMessage());
				}
			}
		});
		
		_viewerButton.addActionListener((e)->{
			MapWindow ventana = new MapWindow(null,_ctrl);
			SwingUtilities.invokeLater(() -> {ventana.setVisible(true);});
		});
		
		_regionsButton.addActionListener((e)->{
			SwingUtilities.invokeLater(() -> {_changeRegionsDialog.setVisible(true);});
		});
		
		_runButton.addActionListener((e)->{
			setEnableButtons(false);
			_stopped = false;
			int n = (Integer)_spiner.getValue();
			double dt = Double.parseDouble(_textoDt.getText());
			SwingUtilities.invokeLater(() -> run_sim(n,dt));
		});
		
		_stopButton.addActionListener((e)->{
			_stopped = true;
		});
		
		_quitButton.addActionListener((e) -> ViewUtils.quit(this));
		
		_toolaBar.add(_openButton);
		_toolaBar.addSeparator();
		_toolaBar.add(_viewerButton);
		_toolaBar.add(_regionsButton);
		_toolaBar.addSeparator();
		_toolaBar.add(_runButton);
		_toolaBar.add(_stopButton);
		creaSpinner();
		crearTextField();
		_toolaBar.add(Box.createGlue()); // this aligns the button to the right
		_toolaBar.addSeparator();
		_toolaBar.add(_quitButton);
	}
	
	private void creaSpinner()
	{
		_labelSpiner = new JLabel("Steps: ");
		_spiner = new JSpinner(new SpinnerNumberModel(10000,0,100000,100));
        
        _toolaBar.add(_labelSpiner);
        _toolaBar.add(_spiner);
	}
	
	private void crearTextField()
	{
		_textoDt = new JTextField(10);
		_textoDt.setText(Main._dt.toString());
		_labelDt = new JLabel("Delta-Time:");
		_textoDt.setMaximumSize(new Dimension(50,100));
		
		_toolaBar.add(_labelDt);
		_toolaBar.add(_textoDt);
		
	}
	
	private void run_sim(int n, double dt) {
		if (n > 0 && !_stopped) {
			try {
				_ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			} catch (Exception e) {
				ViewUtils.showErrorMsg(e.getMessage());
				setEnableButtons(true);
				_stopped = true;
			}
		} else {
			setEnableButtons(true);
			_stopped = true;
		}
	}
	
	private void setEnableButtons(boolean val)
	{
		_openButton.setEnabled(val);
		_viewerButton.setEnabled(val);
		_regionsButton.setEnabled(val);
		_runButton.setEnabled(val);
		_spiner.setEnabled(val);
		_labelDt.setEnabled(val);
		_labelDt.setEnabled(val);
		_quitButton.setEnabled(val);
	}


}
