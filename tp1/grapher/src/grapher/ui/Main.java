package grapher.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class Main extends JFrame implements ListSelectionListener{

	JPanel panelExpr;
	JList expressionList;
	JSplitPane spliter;
	JToolBar tbList;
	JButton bAdd, bDel;
	Grapher grapher;

	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		grapher = new Grapher();
		for(String expression : expressions) {
			grapher.add(expression);
		}

		panelExpr = new JPanel(new BorderLayout());

		expressionList = new JList(expressions);
		tbList = new JToolBar();

		bAdd = new JButton("+");
		bDel = new JButton("-");

		bAdd.addActionListener(new ActionListener()
			{
			  public void actionPerformed(ActionEvent e)
			  {
					JPanel optionPanel = new JPanel(new GridLayout(0, 1));
					JTextField funField = new JTextField("x");
	        optionPanel.add(new JLabel("Nouvelle Expression"));
	        optionPanel.add(funField);
					int newFunction = JOptionPane.showConfirmDialog(null, optionPanel, "Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);;
			  }
			});

		tbList.add(bAdd);
		tbList.add(bDel);

		panelExpr.add(expressionList, BorderLayout.CENTER);
		panelExpr.add(tbList, BorderLayout.SOUTH);

		spliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panelExpr,grapher);
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

			int[] indices = expressionList.getSelectedIndices();

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
