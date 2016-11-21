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
				new Main("Download", urls).setVisible(true);
			}
		});
	}

	Main(String title, String[] urls) {
		
		super(title);
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
			prog.setValue(0);
			prog.setStringPainted(true);
			this.add(prog);
			
			dl.addPropertyChangeListener(new  PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					prog.setValue(dl.getProgress());
				}
			});
			
			pack();
		}
	}
}
