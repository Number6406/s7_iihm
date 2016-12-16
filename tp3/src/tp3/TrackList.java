/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp3;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author bonhourg
 */
public class TrackList extends JPanel {
    
    JScrollPane p_tracks;
    JTable tracks;
    JPanel p_bottom;
    JPanel p_track_option;
    JButton b_plus, b_rand, b_loop; 
    JLabel l_nb_tracks;
    JTextField tf_search;
    
    public TrackList() {
        
        this.setLayout(new BorderLayout());
        
        /*Modèle de la JTable (qui ne s'affiche pas)*/
        String[] columnNames = {"Nom",
                        "Auteur",
                        "Durée"};
        Object[][] data = {};
        /*Fin du modèle de la JTable*/
        
        p_tracks = new JScrollPane(); //ScrollPane pour la JTable des musiques
        tracks = new JTable(data, columnNames);
        p_tracks.add(tracks);
        this.add(p_tracks, BorderLayout.CENTER);
        
        /*Barre horizontale du bas de la fenetre*/
        p_bottom = new JPanel(new BorderLayout());
        this.add(p_bottom, BorderLayout.SOUTH);
        
        /*Boutons d'option de lecture*/
        p_track_option = new JPanel(new GridLayout(1,3));
        p_bottom.add(p_track_option, BorderLayout.WEST);
        
        b_plus = new JButton("+");
        b_rand = new JButton("[rand]");
        b_loop = new JButton("[loop]");
        
       p_track_option.add(b_plus);
       p_track_option.add(b_rand);
       p_track_option.add(b_loop);
       
       l_nb_tracks = new JLabel("0 titres", SwingConstants.CENTER);
       p_bottom.add(l_nb_tracks, BorderLayout.CENTER);
       
       tf_search = new JTextField();
       tf_search.setPreferredSize(new Dimension(160, 20));
       p_bottom.add(tf_search, BorderLayout.EAST);
    }
    
}
