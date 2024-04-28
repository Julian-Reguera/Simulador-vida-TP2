package simulator.view;

import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	private static final String TEXTO_AYUDA1 = "h: toggle help";
	private static final String TEXTO_AYUDA2 = "s: show animals of a specific state";

	// Una clase auxilar para almacenar información sobre una especie
	private static class SpeciesInfo {
		private Integer _count;
		private Color _color;

		SpeciesInfo(Color color) {
			_count = 0;
			_color = color;
		}
	}

	// el tamano del simulador es igual al tamano del componente
	private int _width;
	private int _height;
	private int _rows;
	private int _cols;
	int _rwidth; // regionWidth
	int _rheight;
	Animal.State _currState; // solo se muestran animales de este estado. si es null se muestran todos
	private int _numCurrentState;
	volatile private Collection<AnimalInfo> _objs; // lista de animales
	volatile private Double _time; // tiempo para dibujarlo
	Map<String, SpeciesInfo> _kindsInfo = new HashMap<>(); // Un mapa para la información sobre las especies
	private Font _font = new Font("Arial", Font.BOLD, 12); // El font que usamos para dibujar texto
	private boolean _showHelp; // Indica si mostramos el texto la ayuda o no

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		_numCurrentState = 0;
		_currState = null; // Por defecto mostramos todos los animales
		_showHelp = true; // Por defecto mostramos el texto de ayuda

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'h':
					_showHelp = !_showHelp;
					repaint();
					break;
				case 's':
					Animal.State[] estados = Animal.State.values();
					if (_numCurrentState == estados.length)
						_currState = null;
					else
						_currState = estados[_numCurrentState];

					_numCurrentState++;
					if (_numCurrentState > estados.length)
						_numCurrentState = 0;

					repaint();
				default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus(); // pide el foco para recibir eventos del teclado
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setFont(_font);// cambio de fuente

		// Dibujar fondo blanco
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, _width, _height);

		// Dibujar los animales, el tiempo, etc.
		if (_objs != null)
			drawObjects(gr, _objs, _time);

		if (_showHelp == true) {
			g.setColor(Color.RED);
			g.drawString(TEXTO_AYUDA1, 10, 15);
			g.drawString(TEXTO_AYUDA2, 10, 30);
		}
	}

	private boolean visible(AnimalInfo a) {
		return _currState == null ? true : _currState == a.get_state();
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {

		g.setColor(Color.lightGray);

		for (int y = 0; y < _height; y = y + _rheight) {
			for (int x = 0; x < _width; x = x + _rwidth) {
				g.drawRect(x, y, _rwidth, _rheight);
			}
		}

		// Dibujar los animales
		for (AnimalInfo a : animals) {
			if (!visible(a))
				continue; // Si no es visible saltamos la iteración
			SpeciesInfo esp_info = _kindsInfo.get(a.get_genetic_code()); // La información sobre la especie de 'a'

			if (esp_info == null) {
				esp_info = new SpeciesInfo(ViewUtils.get_color(a.get_genetic_code()));
				_kindsInfo.put(a.get_genetic_code(), esp_info);
			}

			esp_info._count++;

			double tamano = (a.get_age() / 2) + 2;
			g.setColor(esp_info._color);
			g.fillRect((int) a.get_position().getX(), (int) a.get_position().getY(), (int) tamano, (int) tamano);
		}

		printStates(g);
		for (Entry<String, SpeciesInfo> e : _kindsInfo.entrySet()) {
			e.getValue()._count = 0;
		}
	}

	// Un método que dibujar un texto con un rectángulo y devuelve la altura
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		_objs = objs;
		_time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		_width = map.get_width();
		_height = map.get_height();
		_rows = map.get_rows();
		_cols = map.get_cols();
		_rwidth = map.get_width() / _cols;
		_rheight = map.get_height() / _rows;

		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset
		setPreferredSize(new Dimension(map.get_width(), map.get_height()));

		// Dibuja el estado
		update(animals, time);
	}

	private void printStates(Graphics2D g) {
		double y = _height - 30;
		if (_currState != null) {
			g.setColor(Color.BLUE);
			String s = "State: " + _currState.toString();
			drawStringWithRect(g, 30, (int) y, s);
			y = y - (int) g.getFontMetrics().getStringBounds(s, g).getHeight() - 5;
		}

		g.setColor(Color.MAGENTA);
		String t = "Time: " + String.format("%.3f", _time);
		drawStringWithRect(g, 30, (int) y, t);
		y = y - (int) g.getFontMetrics().getStringBounds(t, g).getHeight() - 5;

		for (Entry<String, SpeciesInfo> e : _kindsInfo.entrySet()) {
			g.setColor(e.getValue()._color);
			String aux = e.getKey() + ": " + e.getValue()._count;
			drawStringWithRect(g, 30, (int) y, aux);
			y = y - (int) g.getFontMetrics().getStringBounds(aux, g).getHeight() - 5;
		}
	}
}
