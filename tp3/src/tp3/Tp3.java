/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author bonhourg
 */
public class Tp3 extends JFrame {
    
    public static String title = "Lecteur multimédia";
    
    public JPanel p_player;
    public TrackList p_track_list;
    
    public JPanel p_buttons, p_infos;

    public JButton b_previous, b_play, b_next, b_begin, b_stop, b_end;
    
    public JPanel p_sound;
    public JPanel p_track;
    public JPanel p_toggleB;
    
    public JButton b_sound_down, b_sound_up;
    public JSlider sl_sound_volume;
    
    public JSlider sl_player;
    public JLabel lb_track_title, lb_track_timer;
    
    public JToggleButton tb_options, tb_track_list;
    
    int music_timer = 360;
    
    int min_width = 580;
    int min_height = 100;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String argv[]) {
        final String[] urls = argv;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Tp3(title).setVisible(true);
            }
        });
    }

    private Tp3(String title) {
        super(title);
        
        this.setMinimumSize(new Dimension(min_width,min_height));
        
        this.setLayout(new BorderLayout());
        
        p_player = new JPanel(new BorderLayout());
        this.add(p_player, BorderLayout.NORTH);
        
        /*Boutons de lecture, musique suivante ect.*/
        p_buttons = new JPanel(new GridLayout(2, 3));
        
        p_player.add(p_buttons, BorderLayout.WEST);
        
        b_previous = new JButton("<<");
        b_play = new JButton("|>");
        b_next = new JButton(">>");
        b_begin = new JButton("|<<");
        b_stop = new JButton("■");
        b_end = new JButton(">>|");
        
        p_buttons.add(b_previous);
        p_buttons.add(b_play);
        p_buttons.add(b_next);
        p_buttons.add(b_begin);
        p_buttons.add(b_stop);
        p_buttons.add(b_end);
        
        /*Panel d'information*/
        p_infos = new JPanel(new BorderLayout());
        p_player.add(p_infos, BorderLayout.CENTER);
        
        /*Gestion du volume*/
        p_sound = new JPanel(new BorderLayout());
        p_infos.add(p_sound, BorderLayout.WEST);
        
        b_sound_down = new JButton("< )"); // baisser le volume
        sl_sound_volume = new JSlider(0, 100); // intensité du volume
        b_sound_up = new JButton("< )))"); // augmenter le volume
        
        p_sound.add(b_sound_down, BorderLayout.WEST);
        p_sound.add(sl_sound_volume, BorderLayout.CENTER);
        p_sound.add(b_sound_up, BorderLayout.EAST);
        
        /*Informations sur la lecture en cours*/
        p_track = new JPanel(new BorderLayout());
        p_infos.add(p_track, BorderLayout.NORTH);
        
        lb_track_title = new JLabel("Titre de la musique");
        lb_track_timer = new JLabel("00:00");
        sl_player = new JSlider(0, music_timer);
        
        /*Mise à jour du timer en fonction du slider*/
        sl_player.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                lb_track_timer.setText(String.valueOf(sl_player.getValue()/60) + ":" + String.valueOf(sl_player.getValue()%60));
            }
        });
        
        p_track.add(lb_track_title, BorderLayout.WEST);
        p_track.add(lb_track_timer, BorderLayout.EAST);
        p_track.add(sl_player, BorderLayout.SOUTH);

        /*Boutons d'option et d'affichage de la liste des musiques*/
        p_toggleB = new JPanel(new GridLayout(1,2));
        p_infos.add(p_toggleB, BorderLayout.EAST);
        
        tb_options = new JToggleButton("|o|");// options
        tb_track_list = new JToggleButton("=");// liste
        
        p_toggleB.add(tb_options);
        p_toggleB.add(tb_track_list);
        
        /*Liste de lecture*/
        p_track_list = new TrackList();
        this.add(p_track_list, BorderLayout.CENTER);
        p_track_list.setVisible(false); // non visible par défaut : on ouvre une petite fenêtre
        
        /*Gestion de l'affichage de la liste selon l'état du ToggleButton correspondant*/
        tb_track_list.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                ButtonModel b = tb_track_list.getModel();
                if(b.isSelected()) {
                    showTrackList();
                } else {
                    hideTrackList();
                }
            }
        });
        
        /*Gestion de l'affichage de la liste au resize de la fenetre*/
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                if(c.getSize().height > min_height + 50) {
                    p_track_list.setVisible(true);
                } else {
                    p_track_list.setVisible(false);
                }
            }
        });
        
        this.pack();
        
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    
    /**
     * Pour masquer la liste
     */
    public void hideTrackList() {
        this.setSize(this.getWidth(), min_height);
    }
    
    /**
     * pour afficher la liste
     */
    public void showTrackList() {
        this.setSize(this.getWidth(), min_height + 500);
    }
    
    
}
