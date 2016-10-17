package grapher.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class Main extends JFrame implements ListSelectionListener{

	JList panelList;
	JSplitPane spliter;
	Grapher grapher;

	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		grapher = new Grapher();
		for(String expression : expressions) {
			grapher.add(expression);
		}

		panelList = new JList(grapher.getFunctions());
		panelList.addListSelectionListener(this);
		
		spliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panelList,grapher);
		spliter.setDividerLocation(150);
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
	
	public void valueChanged(ListSelectionEvent e){
		// Mettre en gras
		if(!e.getValueIsAdjusting()){
						
			int[] indices = panelList.getSelectedIndices();
			
			for(int i=0; i < grapher.getFunctions().getSize();i++){
				grapher.setFont(i,0);
			}
			for(int i=0; i < indices.length;i++){
				grapher.setFont(indices[i],1);
			}
			grapher.repaint();
		}
	}
}
