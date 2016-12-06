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
    
    public JPanel p_player, p_track_list;
    
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
    
    int music_timer = 3600;
    
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
        
        this.setMinimumSize(new Dimension(700,200));
        
        this.setLayout(new BorderLayout());
        
        p_player = new JPanel(new BorderLayout());
        this.add(p_player, BorderLayout.NORTH);
        
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
        
        p_infos = new JPanel(new BorderLayout());
        p_player.add(p_infos, BorderLayout.CENTER);
        
        p_sound = new JPanel(new BorderLayout());
        p_infos.add(p_sound, BorderLayout.WEST);
        
        b_sound_down = new JButton("< )");
        sl_sound_volume = new JSlider(0, 100);
        b_sound_up = new JButton("< )))");
        
        p_sound.add(b_sound_down, BorderLayout.WEST);
        p_sound.add(sl_sound_volume, BorderLayout.CENTER);
        p_sound.add(b_sound_up, BorderLayout.EAST);
        
        p_track = new JPanel(new BorderLayout());
        p_infos.add(p_track, BorderLayout.NORTH);
        
        lb_track_title = new JLabel("Titre de la musique");
        lb_track_timer = new JLabel("00:00");
        sl_player = new JSlider(0, music_timer);
        
        sl_player.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                lb_track_timer.setText(String.valueOf(sl_player.getValue()/60) + ":" + String.valueOf(sl_player.getValue()%60));
            }
        });
        
        p_track.add(lb_track_title, BorderLayout.WEST);
        p_track.add(lb_track_timer, BorderLayout.EAST);
        p_track.add(sl_player, BorderLayout.SOUTH);
        
        p_toggleB = new JPanel(new GridLayout(2,1));
        p_infos.add(p_toggleB, BorderLayout.EAST);
        
        p_track_list = new JPanel();
        this.add(p_track_list, BorderLayout.CENTER);
        
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        this.pack();
    }
    
    
}
