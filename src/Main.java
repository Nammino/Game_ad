import controller.GameController;
import controller.GameControllerImpl;
import javax.swing.SwingUtilities;
import model.GameStruct;
import model.GameStructImpl;
import view.GameFrame;
import view.GamePanel;

public class Main {

    public static void main(String[] args) {
        // 1. Inizializzazione Model
        System.out.println("=== TEST MODEL INIZIALE ===");
        GameStruct model = new GameStructImpl();
        
        if (model.getPlayer() != null) {
            System.out.println("[OK] Player istanziato correttamente.");
        } else {
            System.err.println("[KO] Player non presente!");
        }

        try {
            if (model.getCurrentWorld() != null) {
                System.out.println("[OK] Mondo corrente caricato.");
            } else {
                System.out.println("[INFO] Nessun mondo attivo caricato (verificare i file .txt dei percorsi).");
            }
        } catch (Exception e) {
            System.err.println("[WARN] Eccezione nella lettura del Mondo: " + e.getMessage());
        }
        System.out.println("===========================");

        // 2. Avvio dell'Interfaccia Grafica e del Controller
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            GamePanel panel = frame.getGamePanel();

            // Istanziazione del Controller che collega Model e View
            GameController controller = new GameControllerImpl(model, panel);

            panel.setFocusable(true);
            panel.requestFocusInWindow();
            frame.display();

            // Avvio del Game Loop dal Controller
            controller.startGame();
        });
    }
}