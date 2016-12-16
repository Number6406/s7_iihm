package downloader.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import downloader.fc.Downloader;
import java.awt.BorderLayout;
//import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Main extends JFrame{
    
    static List<Downloader> dls;
    //
    static JPanel panel_dl;
    JPanel panel_url;
    JTextField tf_url;
    JButton button_dl;
    
    @SuppressWarnings("Convert2Lambda")
    public static void main(String argv[]) {
        final String[] urls = argv;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main("Download", urls).setVisible(true);
                for(String url : urls){
                    add_download(url);
                }
            }
        });
    }
    
    @SuppressWarnings("Convert2Lambda")
    Main(String title, String[] urls) {

        super(title);
        this.setLayout(new BorderLayout());
        this.setSize(400,300);

        panel_dl = new JPanel(new StackLayout());
        this.add(panel_dl, BorderLayout.CENTER);

        panel_url = new JPanel(new BorderLayout());
        this.add(panel_url, BorderLayout.SOUTH);

        tf_url = new JTextField();
        panel_url.add(tf_url, BorderLayout.CENTER);

        button_dl = new JButton("GO !");
        panel_url.add(button_dl, BorderLayout.EAST);

        /*Au clic, on lance le download de l'URL présente dans le textfield*/
        button_dl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!tf_url.getText().isEmpty()) {
                    add_download(tf_url.getText());
                    tf_url.setText("");
                }
            }
        });
        
        /*fonctionne également à l'appuis sur la touche [ENTER]*/
        tf_url.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!tf_url.getText().isEmpty()) {
                    add_download(tf_url.getText());
                    tf_url.setText("");
                }
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        dls = new ArrayList<>();
    }
    
    /*Créé un nouveau downloader à partir d'une URL et lance le téléchargement*/
    @SuppressWarnings("Convert2Lambda")
    private static void add_download(String url) {
        Downloader dl;

        try {
            dl = new Downloader(url);
            dls.add(dl);
            
            /*Création d'une nouvelle ligne à ajouter au downloader*/
            JPanel ligne;
            ligne = new JPanel(new BorderLayout());
            JPanel boutons = new JPanel();
            JPanel download = new JPanel(new BorderLayout());

            ligne.add(boutons,BorderLayout.EAST);
            ligne.add(download,BorderLayout.CENTER);


            JLabel label_url = new JLabel(url);
            JProgressBar prog;
            prog = new JProgressBar(0, 100);
            prog.setStringPainted(true);

            //▯▯▷
            JButton play_pause = new JButton("▯▯");
            play_pause.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if("▯▯".equals(play_pause.getText())) {
                        dl.pause(); // mise en pause du téléchargement
                        play_pause.setText("▷");
                    } else {
                        dl.play(); // reprise du téléchargement
                        play_pause.setText("▯▯");
                    }
                }
            });
            
            /*Suppresion d'un téléchargement*/
            JButton suppr = new JButton("X");
            suppr.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    panel_dl.remove(ligne);
                    panel_dl.revalidate();
                    panel_dl.repaint();
                }
            });
            
            /*Arret d'un téléchargement (qu'on ne pourra donc pas reprendre)*/
            JButton stop = new JButton("◻");
            stop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dl.cancel(true);
                    prog.setString("Annulé");
                    prog.setValue(0);
                    boutons.remove(play_pause);
                    boutons.remove(stop);
                    boutons.add(suppr);
                    boutons.revalidate();
                }
            });
            boutons.add(play_pause);
            boutons.add(stop);


            download.add(label_url,BorderLayout.NORTH);
            download.add(prog,BorderLayout.SOUTH);

            panel_dl.add(ligne);
            panel_dl.revalidate();

            /*Lors de la terminaison d'un téléchargement, on change l'état des boutons et on grise la progressbar (retour visuel)*/
            dl.addPropertyChangeListener(new  PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    System.out.print("." + dl.getProgress());
                    prog.setValue(dl.getProgress());
                    if(dl.getProgress()==100){
                        boutons.remove(play_pause);
                        boutons.remove(stop);
                        boutons.add(suppr);
                        boutons.revalidate();
                        try {
                            String filename = dl.get();
                            System.out.println("downloaded into " + filename);
                        } catch(InterruptedException | ExecutionException e) {
                            System.err.println(e);
                        }
                    }
                }
            });

            dl.execute();
        }
        catch(RuntimeException e) {
            System.err.format("skipping %s %s\n", url, e);
            JOptionPane.showMessageDialog(panel_dl, "Url incorrecte : "+url,"Erreur url",JOptionPane.ERROR_MESSAGE);
        }
    }
}
