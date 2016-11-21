package downloader.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import downloader.fc.Downloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main extends JFrame{
	
	static List<JProgressBar> progs;
	static List<Downloader> dls;
	
	JProgressBar prog;
	static Downloader dl;
	
	public static void main(String argv[]) {
		final String[] urls = argv;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main("Download", urls).setVisible(true);
				begin_download();
			}
		});
	}

	Main(String title, String[] urls) {
		
		super(title);
        this.setSize(400, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		dls = new ArrayList<Downloader>();
		progs = new ArrayList<JProgressBar>();
		
		for(String url : urls){
			try {
				dl = new Downloader(url);
			}
			catch(RuntimeException e) {
				System.err.format("skipping %s %s\n", url, e);
				continue;
			}
			
			dls.add(dl);
			

			//final JProgressBar prog;
			prog = new JProgressBar(0, 100);
			prog.setValue(dl.getProgress());
			prog.setStringPainted(true);
			this.add(prog);
			progs.add(prog);
                        
			dl.addPropertyChangeListener(new  PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
                        System.out.print("." + dl.getProgress());				
                        prog.setValue(dl.getProgress());
				}
			});
			
			revalidate();
		}
	}

	private static void begin_download() {
		String filename = "Erreur de téléchargement";
		/*for( int i = 1; i < dls.size(); i++) { */
	        dl.execute();
	        try {
	            filename = dl.get();    
	        } catch(InterruptedException e) {
	            System.err.println(e);
	        } catch(ExecutionException e) {
	            System.err.println(e);
	        }
	        
	        System.out.format("Downloaded into %s\n", filename);
		/*}*/
		
	}
}
