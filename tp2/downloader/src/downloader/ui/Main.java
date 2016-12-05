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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Main extends JFrame{
	
	static List<Downloader> dls;
        // 
        static JPanel panel_dl;
        JPanel panel_url;
        JTextField tf_url;
        JButton button_dl;
        
	static Downloader dl;
	
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
                
                button_dl.addActionListener(new ActionListener() {
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

        @SuppressWarnings("Convert2Lambda")
	private static void add_download(String url) {
            try {
                dl = new Downloader(url);
            }
            catch(RuntimeException e) {
                System.err.format("skipping %s %s\n", url, e);
            }

            dls.add(dl);

            JProgressBar prog;
            prog = new JProgressBar(0, 100);
            prog.setStringPainted(true);
            panel_dl.add(prog);
            panel_dl.revalidate();
            panel_dl.repaint();

            dl.addPropertyChangeListener(new  PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        System.out.print("." + dl.getProgress());				
                        prog.setValue(dl.getProgress());
                        if(dl.getProgress()==100){
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
}
