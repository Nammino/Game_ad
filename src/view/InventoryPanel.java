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
    
    // Gestione dello scorrimento in pixel
    private int scrollOffset = 0;

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
            int startX = boxX + 30;
            int startY = boxY + 45;
            int spacing = 40; // Spazio verticale per ogni riga
            
            // Calcolo altezza totale del contenuto e limiti di scorrimento in pixel
            int totalContentHeight = items.size() * spacing;
            int visibleAreaHeight = boxHeight - 60;
            int maxScroll = Math.max(0, totalContentHeight - visibleAreaHeight);

            // Validazione dei limiti dello scroll
            if (scrollOffset > maxScroll) scrollOffset = maxScroll;
            if (scrollOffset < 0) scrollOffset = 0;

            // --- AREA DI CLIP: Taglia tutto ciò che esce dal box ---
            java.awt.Shape oldClip = g2.getClip();
            g2.setClip(boxX + 10, boxY + 10, boxWidth - 20, boxHeight - 20);

            for (int i = 0; i < items.size(); i++) {
                String itemName = items.get(i);
                
                // Posizione Y calcolata sottraendo i pixel dello scroll
                int currentY = startY + (i * spacing) - scrollOffset;

                // Disegna la riga solo se rientra nell'area visibile del riquadro
                if (currentY + 30 >= boxY + 15 && currentY <= boxY + boxHeight - 15) {
                    // Sfondo della riga dell'oggetto
                    g2.setColor(new Color(60, 60, 80));
                    g2.fillRect(startX, currentY, boxWidth - 60, 32);

                    BufferedImage itemSprite = null;
                    if (itemName.startsWith("POTION")) {
                        itemSprite = potionImg;
                    } else if (itemName.startsWith("COIN")) {
                        itemSprite = coinImg;
                    }

                    int textOffset = startX + 15;
                    if (itemSprite != null) {
                        g2.drawImage(itemSprite, startX + 10, currentY + 4, 24, 24, null);
                        textOffset = startX + 45; 
                    }

                    g2.setColor(Color.YELLOW);
                    g2.setFont(new Font("Arial", Font.BOLD, 15));
                    g2.drawString("• " + itemName + " #" + (i + 1), textOffset, currentY + 22);
                }
            }

            // Ripristina il clip grafico originale
            g2.setClip(oldClip);

            // --- DISEGNO DELLA BARRA DI SCORRIMENTO (SCROLLBAR) ---
            if (totalContentHeight > visibleAreaHeight) {
                int scrollBarWidth = 6;
                int scrollBarHeight = boxHeight - 40;
                int scrollBarX = boxX + boxWidth - 15;
                int scrollBarY = boxY + 20;

                g2.setColor(new Color(30, 30, 40));
                g2.fillRect(scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight);

                int thumbHeight = Math.max(30, (scrollBarHeight * visibleAreaHeight) / totalContentHeight);
                int maxThumbTravel = scrollBarHeight - thumbHeight;
                int thumbY = scrollBarY + (maxScroll > 0 ? (scrollOffset * maxThumbTravel) / maxScroll : 0);

                g2.setColor(new Color(150, 150, 200));
                g2.fillRoundRect(scrollBarX, thumbY, scrollBarWidth, thumbHeight, 4, 4);
            }
        }

        // Istruzioni in basso
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String footer = "Usa la Rotellina per scorrere | Premi I o ESC per tornare al gioco";
        int footerWidth = g2.getFontMetrics().stringWidth(footer);
        g2.drawString(footer, (panelWidth - footerWidth) / 2, boxY + boxHeight + 40);
    }

    public void handleWheel(int delta) {
        scrollOffset += delta;
    }
}