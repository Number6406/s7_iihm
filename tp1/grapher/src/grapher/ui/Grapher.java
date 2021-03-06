package grapher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.Cursor;
import javax.swing.SwingUtilities;

import java.awt.Point;

import java.util.Vector;

import static java.lang.Math.*;
import static java.lang.System.*;

import grapher.fc.*;

// Implémentation des MouseListener et MouseMotionListener pour gérer leur utilisation
public class Grapher extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	
	// Enumération des états de la machine à états
	enum State {
		IDLE, LEFT_CLICK, RIGHT_CLICK, SELECT_ZONE, DRAG, MIDDLE_CLICK, ZOOM_MIDDLE
	};
	
	// Etat courant de la machine
	protected State state = State.IDLE;
	
	// Définition de points pour les sauvegarder
	protected Point anchor_point;
	protected Point mouse_point;
	protected Point previous_point;
	
	// Constantes
	static final int MARGIN = 40;
	static final int STEP = 5;
	
	// Définition du trait pour le tracé
	static final BasicStroke dash = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.f, new float[] { 4.f, 4.f }, 0.f);
	
	
	protected int selectWidth;
	protected int selectHeight;
	protected int translateX;
	protected int translateY;
	protected double dist;

	protected int W = 400;
	protected int H = 300;

	protected double xmin, xmax;
	protected double ymin, ymax;

	// Liste des fonctions (c'est une defaultListModel pour pouvoir l'utiliser aussi dans le repaint)
	protected DefaultListModel<Function> functions;
	// Vecteur contenant un entier correspondant à si la courbe est affichée en gras ou non
	protected Vector<Integer> font;
	// Vecteur des couleurs correspondant à chaque fonction (pour extension)
	protected Vector<Color> colors;

	/** Lors de la création du grapher on instancie la liste des fonctions
	 * la liste des polices (gras ou pas pour la selection)
	 * et on ajoute les listener pour l'interaction 
	 */
	public Grapher() {
		xmin = -PI/2.; xmax = 3*PI/2;
		ymin = -1.5;   ymax = 1.5;

		functions = new DefaultListModel<Function>();
		font = new Vector<Integer>();
		colors = new Vector<Color>();
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	/**
	 * ajoute une fonction dans le grapher à partir de la chaine de caractères
	 * passée en paramètre
	 */
	public void add(String expression) {
		add(FunctionFactory.createFunction(expression));
	}
	
	/**
	 * Ajoute une fonction au tableau des fonctions en mettant sa couleur à noir
	 */
	public void add(Function function) {
		functions.addElement(function);
		font.add(0);
		colors.add(Color.BLACK);
		repaint();
	}

	public Dimension getPreferredSize() { return new Dimension(W, H); }
	
	/**
	 * Méthode paintComponent qui est appelée lorsque l'on appele repaint()
	 * Elle permet notamment d'afficher le rectangle en pointillès lors
	 * du drag avec le clic droit
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
		W = getWidth();
		H = getHeight();

		Graphics2D g2 = (Graphics2D)g;

		// background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, W, H);

		g2.setColor(Color.BLACK);

		// box
		g2.translate(MARGIN, MARGIN);
		W -= 2*MARGIN;
		H -= 2*MARGIN;
		if(W < 0 || H < 0) {
			return;
		}

		g2.drawRect(0, 0, W, H);

		g2.drawString("x", W, H+10);
		g2.drawString("y", -10, 0);


		// plot
		g2.clipRect(0, 0, W, H);
		g2.translate(-MARGIN, -MARGIN);

		// x values
		final int N = W/STEP + 1;
		final double dx = dx(STEP);
		double xs[] = new double[N];
		int    Xs[] = new int[N];
		for(int i = 0; i < N; i++) {
			double x = xmin + i*dx;
			xs[i] = x;
			Xs[i] = X(x);
		}

		for(int j = 0; j < functions.getSize();j++) {
			Function f = functions.get(j);
			// y values
			int Ys[] = new int[N];
			for(int i = 0; i < N; i++) {
				Ys[i] = Y(f.y(xs[i]));
			}
			
			/* si font = 1 alors mettre le trait en gras */
			if(font.get(j)==1){
				g2.setStroke(new BasicStroke(3));
			} else {
				g2.setStroke(new BasicStroke(1));
			}
			
			// Mettre la couleur
			g2.setPaint(colors.get(j));
			// Dessiner
			g2.drawPolyline(Xs, Ys, N);
			
		}
	
		// Remettre les paramètres pour les axes
		g2.setStroke(new BasicStroke(1));
		g2.setPaint(Color.BLACK);
		
		g2.setClip(null);

		// axes
		drawXTick(g2, 0);
		drawYTick(g2, 0);

		double xstep = unit((xmax-xmin)/10);
		double ystep = unit((ymax-ymin)/10);

		g2.setStroke(dash);
		for(double x = xstep; x < xmax; x += xstep)  { drawXTick(g2, x); }
		for(double x = -xstep; x > xmin; x -= xstep) { drawXTick(g2, x); }
		for(double y = ystep; y < ymax; y += ystep)  { drawYTick(g2, y); }
		for(double y = -ystep; y > ymin; y -= ystep) { drawYTick(g2, y); }
	
		// Dessin du rectangle pointillé si necessaire
		if(state == State.SELECT_ZONE) {
			Point debut = new Point();
			Point fin = new Point();
			if(mouse_point.x < anchor_point.x) {
				debut.x = mouse_point.x;
				fin.x = anchor_point.x;
			} else {
				debut.x = anchor_point.x;
				fin.x = mouse_point.x;
			}
			if(mouse_point.y < anchor_point.y) {
				debut.y = mouse_point.y;
				fin.y = anchor_point.y;
			} else {
				debut.y = anchor_point.y;
				fin.y = mouse_point.y;
			}

			selectWidth = fin.x - debut.x;
			selectHeight = fin.y - debut.y;

			g2.drawRect(debut.x, debut.y, selectWidth, selectHeight);
		}
	}

	protected double dx(int dX) { return  (double)((xmax-xmin)*dX/W); }
	protected double dy(int dY) { return -(double)((ymax-ymin)*dY/H); }

	protected double x(int X) { return xmin+dx(X-MARGIN); }
	protected double y(int Y) { return ymin+dy((Y-MARGIN)-H); }

	protected int X(double x) {
		int Xs = (int)round((x-xmin)/(xmax-xmin)*W);
		return Xs + MARGIN;
	}
	protected int Y(double y) {
		int Ys = (int)round((y-ymin)/(ymax-ymin)*H);
		return (H - Ys) + MARGIN;
	}

	protected void drawXTick(Graphics2D g2, double x) {
		if(x > xmin && x < xmax) {
			final int X0 = X(x);
			g2.drawLine(X0, MARGIN, X0, H+MARGIN);
			g2.drawString((new Double(x)).toString(), X0, H+MARGIN+15);
		}
	}

	protected void drawYTick(Graphics2D g2, double y) {
		if(y > ymin && y < ymax) {
			final int Y0 = Y(y);
			g2.drawLine(0+MARGIN, Y0, W+MARGIN, Y0);
			g2.drawString((new Double(y)).toString(), 5, Y0);
		}
	}

	protected static double unit(double w) {
		double scale = pow(10, floor(log10(w)));
		w /= scale;
		if(w < 2)      { w = 2; }
		else if(w < 5) { w = 5; }
		else           { w = 10; }
		return w * scale;
	}


	protected void translate(int dX, int dY) {
		double dx = dx(dX);
		double dy = dy(dY);
		xmin -= dx; xmax -= dx;
		ymin -= dy; ymax -= dy;
		repaint();
	}

	protected void zoom(Point center, int dz) {
		double x = x(center.x);
		double y = y(center.y);
		double ds = exp(dz*.01);
		xmin = x + (xmin-x)/ds; xmax = x + (xmax-x)/ds;
		ymin = y + (ymin-y)/ds; ymax = y + (ymax-y)/ds;
		repaint();
	}

	protected void zoom(Point p0, Point p1) {
		double x0 = x(p0.x);
		double y0 = y(p0.y);
		double x1 = x(p1.x);
		double y1 = y(p1.y);
		xmin = min(x0, x1); xmax = max(x0, x1);
		ymin = min(y0, y1); ymax = max(y0, y1);
		repaint();
	}
	
	// LISTENERS
	
	/**
	 * Lors d'un clic avec la souris on regarde l'état dans lequel on 
	 * se trouve, puis on regarde quel boutton de la souris a été pressé
	 * pour savoir quelle action effectuer et dasn quel prochain état aller
	 */
	public void mousePressed(MouseEvent e) {	
		switch(state){
			case IDLE :
				if(SwingUtilities.isLeftMouseButton(e)) {
					state = State.LEFT_CLICK;
				}
				if(SwingUtilities.isRightMouseButton(e)) {
					state = State.RIGHT_CLICK;
				}
				if(SwingUtilities.isMiddleMouseButton(e)) {
					state = State.MIDDLE_CLICK;
				}
				anchor_point = e.getPoint();
				break;
			case LEFT_CLICK :
				break;
			case RIGHT_CLICK :
				break;
			case MIDDLE_CLICK :
				break;
			case SELECT_ZONE :
				break;
			case DRAG :
				break;
			case ZOOM_MIDDLE :
				break;
			default : System.out.println("Evenement inattendu... lol");// Traiter erreurs
		}
	}
	
	/**
	 * Lorsqu'un boutton est relaché, on regarde l'état dans lequel on se trouve
	 * puis selon le bouton correspondant à l'événement on effectue la transition
	 * 
	 * Ici on doit utiliser getbutton() et MouseEvent.BUTTON1,2 ou 3
	 * Car isLeftButton et le autres methode peuvent renvoyer vrai si
	 * le boutton est encore pressé et non pas relaché.
	 */
	public void mouseReleased(MouseEvent e) {
		switch(state){
			case IDLE :
				break;
			case LEFT_CLICK :
				if(e.getButton()==MouseEvent.BUTTON1) {
					zoom(anchor_point,5);
					state = State.IDLE;
				}
				break;
			case RIGHT_CLICK :
				if(e.getButton()==MouseEvent.BUTTON3) {
					zoom(anchor_point,-5);
					state = State.IDLE;
				}
				break;
			case MIDDLE_CLICK :
				if(e.getButton()==MouseEvent.BUTTON2) {
					state = State.IDLE;
				}
				break;
			case SELECT_ZONE :
				if(e.getButton()==MouseEvent.BUTTON3) {
					zoom(anchor_point,mouse_point);
					state = State.IDLE;
				}
				break;
			case DRAG :
				if(e.getButton()==MouseEvent.BUTTON1) {
					this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					state = State.IDLE;
				}
				break;
			case ZOOM_MIDDLE :
				if(e.getButton()==MouseEvent.BUTTON2) {
					state = State.IDLE;
				}
				break;
			default : System.out.println("Evenement inattendu... lol");// Traiter erreurs
		}
		repaint();
	}

	// Fonction non utilisée
	public void mouseClicked(MouseEvent e) {

	}

	// Fonction non utilisée
	public void mouseMoved(MouseEvent e) {

	}
	
	/**
	 * Lorsque la souris bouge et selon l'état dans lequel on est
	 * on effectue l'action et le changement d'état adéquat
	 */
	public void mouseDragged(MouseEvent e) {
		switch(state){
			case IDLE :
				break;
			case LEFT_CLICK :
				mouse_point = e.getPoint();
				translateX = (int) (mouse_point.getX() - anchor_point.getX());
				translateY = (int) (mouse_point.getY() - anchor_point.getY());
				anchor_point = mouse_point;
				translate(translateX, translateY);
				this.setCursor(new Cursor(Cursor.HAND_CURSOR));
				state = State.DRAG;
				break;
			case RIGHT_CLICK :
				mouse_point = e.getPoint();
				state = State.SELECT_ZONE;
				repaint();
				break;
			case MIDDLE_CLICK :
				mouse_point = e.getPoint();
				dist = mouse_point.getX() - anchor_point.getX() + mouse_point.getY() - anchor_point.getY();
				zoom(anchor_point,(int) dist);
				previous_point = mouse_point;
				state = State.ZOOM_MIDDLE;
				break;
			case SELECT_ZONE :
				mouse_point = e.getPoint();
				repaint();
				break;
			case DRAG :
				mouse_point = e.getPoint();
				translateX = (int) (mouse_point.getX() - anchor_point.getX());
				translateY = (int) (mouse_point.getY() - anchor_point.getY());
				anchor_point = mouse_point;
				translate(translateX, translateY);
				break;
			case ZOOM_MIDDLE :
				mouse_point = e.getPoint();
				dist = mouse_point.getX() - previous_point.getX() + mouse_point.getY() - previous_point.getY();
				zoom(anchor_point,(int) dist);
				previous_point = mouse_point;
				break;
			default : System.out.println("Evenement inattendu... lol");// Traiter erreurs
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}
	
	/**
	 * Zoomer en cas de roulement de la molette
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		switch(state){
			case IDLE :
				int tours = e.getWheelRotation();
				zoom(e.getPoint(),-5*tours);
				break;
			case LEFT_CLICK :
				break;
			case RIGHT_CLICK :
				break;
			case MIDDLE_CLICK :
				break;
			case SELECT_ZONE :
				break;
			case DRAG :
				break;
			case ZOOM_MIDDLE :
				break;
			default : System.out.println("Evenement inattendu... lol");// Traiter erreurs
		}
	}
	
	// GETTEURS ET SETTEURS
	
	public DefaultListModel<Function> getFunctions(){
		return functions;
	}
	
	public void changeFont(int i){
		int currentFont = this.font.get(i);
		if(currentFont==1){
			this.font.set(i,0);
		} else if(currentFont==0){
			this.font.set(i,1);
		}
	}

	public void setFont(int i,int f){
		this.font.set(i,f);
	}
	
	public void setColor(int i, Color c){
		this.colors.set(i,c);
	}
	
	public void setColor(int i, int r, int g, int b){
		this.colors.set(i,new Color(r,g,b));
	}

}
