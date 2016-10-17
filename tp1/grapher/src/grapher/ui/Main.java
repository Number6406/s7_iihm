package grapher.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.JSplitPane;

public class Main extends JFrame {

	JList panelList;
	JSplitPane spliter;

	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Grapher grapher = new Grapher();
		for(String expression : expressions) {
			grapher.add(expression);
		}

		panelList = new JList(expressions);
		spliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panelList,grapher);
		spliter.setDividerLocation(100);
		this.add(spliter);

		pack();
	}

	public static void main(String[] argv) {
		final String[] expressions = argv;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main("grapher", expressions).setVisible(true);
			}
		});
	}
}
