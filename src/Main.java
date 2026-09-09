
import controller.GameControllerImpl;
import model.GameStruct;
import model.GameStructImpl;
import view.GameFrame;
import view.GamePanel;

public class Main {
    public static void main(String[] args) {
    	GameStruct model = new GameStructImpl();
    	GameFrame frame = new GameFrame(); // Inizializza frame e panel interno
        GamePanel panel = frame.getGamePanel();
        
        panel.setModel(model);
        GameControllerImpl controller = new GameControllerImpl(model, panel);

        frame.display(); // Rende visibile la finestra
    }
}