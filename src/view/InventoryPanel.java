package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;
import model.GameStruct;
import model.Player;

public class InventoryPanel {

    private BufferedImage potionImg;
    private BufferedImage coinImg;

    public InventoryPanel() {
        try {
            java.io.InputStream isPotion = getClass().getResourceAsStream("/sprite/potion_red.png");
            if (isPotion != null) potionImg = ImageIO.read(isPotion);

            java.io.InputStream isCoin = getClass().getResourceAsStream("/sprite/coin_bronze.png");
            if (isCoin != null) coinImg = ImageIO.read(isCoin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, GamePanel panel, GameStruct model) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // Sfondo scuro semi-trasparente sopra il gioco
        g2.setColor(new Color(20, 20, 30, 230));
        g2.fillRect(0, 0, panelWidth, panelHeight);

        // Titolo dell'inventario
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "INVENTARIO DEL PERSONAGGIO";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (panelWidth - titleWidth) / 2, 80);

        if (model == null || model.getPlayer() == null) return;
        Player player = model.getPlayer();
        List<String> items = player.getInventory().getCollectedItems();

        // Riquadro centrale
        int boxWidth = 500;
        int boxHeight = 350;
        int boxX = (panelWidth - boxWidth) / 2;
        int boxY = 130;

        g2.setColor(new Color(40, 40, 60));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2.setColor(new Color(100, 100, 150));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Se l'inventario è vuoto
        if (items.isEmpty()) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(new Font("Arial", Font.ITALIC, 18));
            String emptyMsg = "Nessun oggetto raccolto finora.";
            int emptyWidth = g2.getFontMetrics().stringWidth(emptyMsg);
            g2.drawString(emptyMsg, (panelWidth - emptyWidth) / 2, boxY + (boxHeight / 2));
        } else {
            // Mostra gli oggetti raccolti con i relativi sprite
            int startX = boxX + 30;
            int startY = boxY + 50;
            int spacing = 35;

            for (int i = 0; i < items.size(); i++) {
                String itemName = items.get(i);
                
                // Sfondo della riga dell'oggetto
                g2.setColor(new Color(60, 60, 80));
                g2.fillRect(startX, startY + (i * spacing) - 20, boxWidth - 60, 30);

                // Determiniamo quale immagine/sprite disegnare in base al tipo di oggetto
                BufferedImage itemSprite = null;
                if (itemName.startsWith("POTION")) {
                    itemSprite = potionImg;
                } else if (itemName.startsWith("COIN")) {
                    itemSprite = coinImg;
                }

                // Se lo sprite esiste lo disegniamo a sinistra del testo
                int textOffset = startX + 15;
                if (itemSprite != null) {
                    g2.drawImage(itemSprite, startX + 10, startY + (i * spacing) - 16, 22, 22, null);
                    textOffset = startX + 40; // Spostiamo il testo a destra per fare spazio allo sprite
                }

                // Nome dell'oggetto e numero
                g2.setColor(Color.YELLOW);
                g2.setFont(new Font("Arial", Font.BOLD, 15));
                g2.drawString("• " + itemName + " #" + (i + 1), textOffset, startY + (i * spacing));
            }
        }

        // Istruzioni in basso
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String footer = "Premi I o ESC per tornare al gioco";
        int footerWidth = g2.getFontMetrics().stringWidth(footer);
        g2.drawString(footer, (panelWidth - footerWidth) / 2, boxY + boxHeight + 40);
    }
}