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
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JTable;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class Main extends JFrame implements ListSelectionListener{

	JPanel panelExpr; // Panel gauche, où est affichée la liste des expressions, et les boutons d'ajout et suppression
	JList expressionList; // Liste des expressions

	JSplitPane spliter; // Séparation pour faire varier la taille du panel gauche et du grapher
	JToolBar tbList; // La "toolbar" où seront ajoutés les boutons d'actions sur les expressions
	JButton bAdd, bDel; // boutons d'ajout et suppresion d'expression
	Grapher grapher; // L'affichage du graphe

	public static void main(String[] argv) {
		final String[] expressions = argv;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main("grapher", expressions).setVisible(true);
			}
		});
	}

	/**
	 * Créateur de la fenêtre de l'application
	 */
	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Création des divers éléments de menu
		JMenuBar menuBar = new JMenuBar();
		JMenu menuExpr = new JMenu("Expression");
		JMenuItem addM = new JMenuItem("Add");
		JMenuItem delM = new JMenuItem("Delete");

		// Imbrication des éléments de menu :
		menuExpr.add(addM);
		// Ici le menu d'ajout d'expression est ajouté dans l'onglet "menuExpr" de la barre de menu
		addM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuExpr.add(delM);
		delM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));

		menuBar.add(menuExpr);
		// Ajout de la barre de menu à la fenêtre
		setJMenuBar(menuBar);

		grapher = new Grapher();
		for(String expression : expressions) {
			grapher.add(expression);
		}

		// Création du panel gauche, avec un BorderLayout pour placer les éléments
		panelExpr = new JPanel(new BorderLayout());

		tbList = new JToolBar();

		bAdd = new JButton("+");
		bDel = new JButton("-");

		// Création des listener sur les boutons et appels à fonctions
		bAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { addFun(); }
		});

		bDel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) { delFun(); }
		});

		addM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addFun(); }
		});

		delM.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { delFun(); }
		});

		// Ajout des boutons à la toolbar
		tbList.add(bAdd);
		tbList.add(bDel);


		expressionList = new JList(grapher.getFunctions());
		expressionList.addListSelectionListener(this);

		// Ajout de la liste d'expressions au "centre" afin de prendre toute la place disponible
		panelExpr.add(expressionList, BorderLayout.CENTER);
		// Ajout de la toolbar en dessous de la liste d'expressions, qui prend le minimum de place
		panelExpr.add(tbList, BorderLayout.SOUTH);

		// Ajout de la division horizontale entre le panel d'expressions et le graphe
		spliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panelExpr,grapher);
		spliter.setDividerLocation(100);
		this.add(spliter);

		pack();
	}

	/**
	 * Fonction d'ajout d'une expression
	 */
	public void addFun() {
			JPanel optionPanel = new JPanel(new GridLayout(0, 1));
			JTextField funField = new JTextField("x");
			optionPanel.add(new JLabel("Nouvelle Expression"));
			optionPanel.add(funField);
			// Affichage d'un JDialog et récupération de la réponse
			int result = JOptionPane.showConfirmDialog(null, optionPanel, "Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
					// Ajout si la réponse est "OK"
					grapher.add(funField.getText());
			} else {
					// Sinon quitter sans rien faire
					System.out.println("Cancelled");
			}
	}

	/**
	* Fonction de suppression d'une expression
	*/
	public void delFun() {
			DefaultListModel a_suppr = new DefaultListModel();

			// Récupération de toutes les expression à supprimer
			int[] indices = expressionList.getSelectedIndices();
			DefaultListModel l = grapher.getFunctions();
			for(int i : indices){
				a_suppr.addElement(l.getElementAt(i));
			}

			// Si la liste des expression à supprimer est vide, on affiche un message d'erreur / information
			if(a_suppr.getSize() == 0) {
				JOptionPane.showMessageDialog(null, "Choisissez une fonction afin de la supprimer !", "Erreur", JOptionPane.ERROR_MESSAGE);
			} else {
				// Si la liste des expression existe, on supprime toutes ces listes
				JPanel nomDesFonctions = new JPanel();
				JLabel afficher = new JLabel(a_suppr.toString());
				nomDesFonctions.add(afficher);
				int result = JOptionPane.showConfirmDialog(null, nomDesFonctions, "Supprimer la/les fonction ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

				if(result == JOptionPane.OK_OPTION) {
					//fonction de suppression
					supprimer(a_suppr,l);
					repaint();
				}
			}
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

	public void supprimer(DefaultListModel a_suppr,DefaultListModel l){
		for(int i = 0; i < a_suppr.getSize(); i++){
			l.removeElement(a_suppr.getElementAt(i));
		}

	}
}
