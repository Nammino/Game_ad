import javax.swing.SwingUtilities;
import view.GameFrame;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
        });
		}

}
