package downloader.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import downloader.fc.Downloader;

public class Main extends JFrame{
	
	JProgressBar prog;
	
	public static void main(String argv[]) {
		final String[] urls = argv;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main("Download", urls);
			}
		});
	}

	Main(String title, String[] urls) {
		
		super(title);
                this.setVisible(true);
                this.setSize(400, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		for(String url : urls){
			final Downloader dl;
			try {
				dl = new Downloader(url);
			}
			catch(RuntimeException e) {
				System.err.format("skipping %s %s\n", url, e);
				continue;
			}
			
			prog = new JProgressBar(0, 100);
			prog.setValue(dl.getProgress());
			prog.setStringPainted(true);
			this.add(prog);
                        
			dl.addPropertyChangeListener(new  PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
                                    System.out.print("." + dl.getProgress());				
                                    prog.setValue(dl.getProgress());
				}
			});
                        
                        String filename;
                        DownloadThread dt = new DownloadThread(dl);
                        dt.start();
                        filename = dt.getFileName();
			System.out.format("Downloaded into %s\n", filename);
		}
	}
}
