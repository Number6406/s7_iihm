
public class Main extends JFrame{
	
	public static void main(String argv[]) {
		for(String url: argv) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new Main("Download", url).setVisible(true);
				}
			});
		}
	}

	Main(String title, String[] url) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
