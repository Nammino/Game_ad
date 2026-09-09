package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;
import model.GameStruct;
import model.ItemInstance;
import model.Player;

public class InventoryPanel {

    private BufferedImage potionImg;
    private BufferedImage coinImg;
    private BufferedImage swordImg;
    private BufferedImage bowImg;
    
    private int scrollOffset = 0;

    private final Rectangle[] inventoryItemBounds = new Rectangle[50];
    private final Rectangle[] hotbarSlotBounds = new Rectangle[5];     
    
    private int selectedInventoryItemIndex = -1; 

    public InventoryPanel() {
        try {
            java.io.InputStream isPotion = getClass().getResourceAsStream("/sprite/potion_red.png");
            if (isPotion != null) potionImg = ImageIO.read(isPotion);

            java.io.InputStream isCoin = getClass().getResourceAsStream("/sprite/coin_bronze.png");
            if (isCoin != null) coinImg = ImageIO.read(isCoin);

            java.io.InputStream isSword = getClass().getResourceAsStream("/sprite/sword_right.png");
            if (isSword != null) swordImg = ImageIO.read(isSword);

            java.io.InputStream isBow = getClass().getResourceAsStream("/sprite/bow_right.png");
            if (isBow != null) bowImg = ImageIO.read(isBow);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (int i = 0; i < inventoryItemBounds.length; i++) inventoryItemBounds[i] = new Rectangle();
        for (int i = 0; i < hotbarSlotBounds.length; i++) hotbarSlotBounds[i] = new Rectangle();
    }

    public void draw(Graphics2D g2, GamePanel panel, GameStruct model) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        g2.setColor(new Color(20, 20, 30, 230));
        g2.fillRect(0, 0, panelWidth, panelHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "INVENTARIO DEL PERSONAGGIO";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (panelWidth - titleWidth) / 2, 50);

        if (model == null || model.getPlayer() == null) return;
        Player player = model.getPlayer();
        List<ItemInstance> allItems = player.getInventory().getItems();

        int boxWidth = 520;
        int boxHeight = 420;
        int boxX = (panelWidth - boxWidth) / 2;
        int boxY = 70;

        g2.setColor(new Color(40, 40, 60));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2.setColor(new Color(100, 100, 150));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        int listWidth = boxWidth - 40;
        int listHeight = boxHeight - 130;
        int listX = boxX + 20;
        int listY = boxY + 20;

        g2.setColor(new Color(30, 30, 45));
        g2.fillRoundRect(listX, listY, listWidth, listHeight, 10, 10);

        if (allItems.isEmpty()) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            String emptyMsg = "Nessun oggetto raccolto finora.";
            int emptyWidth = g2.getFontMetrics().stringWidth(emptyMsg);
            g2.drawString(emptyMsg, listX + (listWidth - emptyWidth) / 2, listY + (listHeight / 2));
        } else {
            int startX = listX + 15;
            int startY = listY + 35;
            int spacing = 40; 
            
            int totalContentHeight = allItems.size() * spacing;
            int visibleAreaHeight = listHeight - 20;
            int maxScroll = Math.max(0, totalContentHeight - visibleAreaHeight);

            if (scrollOffset > maxScroll) scrollOffset = maxScroll;
            if (scrollOffset < 0) scrollOffset = 0;

            java.awt.Shape oldClip = g2.getClip();
            g2.setClip(listX, listY, listWidth, listHeight);

            for (int i = 0; i < allItems.size(); i++) {
                ItemInstance item = allItems.get(i);
                int currentY = startY + (i * spacing) - scrollOffset;

                if (i < inventoryItemBounds.length) {
                    inventoryItemBounds[i].setBounds(startX, currentY - 5, listWidth - 30, 32);
                }

                if (currentY + 30 >= listY && currentY <= listY + listHeight) {
                    if (selectedInventoryItemIndex == i) {
                        g2.setColor(new Color(100, 100, 180));
                    } else {
                        g2.setColor(new Color(55, 55, 75));
                    }
                    g2.fillRoundRect(startX, currentY - 5, listWidth - 30, 32, 8, 8);

                    BufferedImage itemSprite = null;
                    String type = item.getType();
                    if (type.equals("POTION")) itemSprite = potionImg;
                    else if (type.equals("COIN")) itemSprite = coinImg;
                    else if (type.equals("SWORD")) itemSprite = swordImg;
                    else if (type.equals("GUN")) itemSprite = bowImg;

                    int textOffset = startX + 15;
                    if (itemSprite != null) {
                        g2.drawImage(itemSprite, startX + 8, currentY, 24, 24, null);
                        textOffset = startX + 40; 
                    }

                    g2.setColor(Color.YELLOW);
                    g2.setFont(new Font("Arial", Font.BOLD, 14));
                    String label = "• " + type + " #" + (i + 1);
                    if (!type.equals("POTION") && !type.equals("COIN")) {
                        label += " (Usi: " + item.getUsesLeft() + ")";
                    }
                    g2.drawString(label, textOffset, currentY + 17);
                } else {
                    if (i < inventoryItemBounds.length) {
                        inventoryItemBounds[i].setBounds(0, 0, 0, 0); 
                    }
                }
            }
            g2.setClip(oldClip);
        }

        int slotSize = 45;
        int slotSpacing = 10;
        int totalHotbarWidth = (5 * slotSize) + (4 * slotSpacing);
        int hotbarX = boxX + (boxWidth - totalHotbarWidth) / 2;
        int hotbarY = boxY + boxHeight - 85;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Slot Rapidi (Primi 4 modificabili, il 5° è vuoto):", hotbarX, hotbarY - 10);

        List<ItemInstance> usableItems = new java.util.ArrayList<>();
        for (ItemInstance item : allItems) {
            if (!item.getType().equals("COIN")) {
                usableItems.add(item);
                if (usableItems.size() == 4) break; 
            }
        }

        for (int i = 0; i < 5; i++) {
            int currentX = hotbarX + i * (slotSize + slotSpacing);
            hotbarSlotBounds[i].setBounds(currentX, hotbarY, slotSize, slotSize);

            g2.setColor(new Color(40, 40, 50));
            g2.fillRect(currentX, hotbarY, slotSize, slotSize);

            g2.setColor(new Color(120, 120, 150));
            g2.drawRect(currentX, hotbarY, slotSize, slotSize);

            if (i < 4 && i < usableItems.size()) {
                ItemInstance itemInst = usableItems.get(i);
                String itemType = itemInst.getType();
                
                BufferedImage slotImg = null;
                if (itemType.equals("POTION")) slotImg = potionImg;
                else if (itemType.equals("SWORD")) slotImg = swordImg;
                else if (itemType.equals("GUN")) slotImg = bowImg;

                if (slotImg != null) {
                    g2.drawImage(slotImg, currentX + 6, hotbarY + 6, slotSize - 12, slotSize - 12, null);
                }
                
                if (!itemType.equals("POTION")) {
                    g2.setColor(Color.YELLOW);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.drawString("x" + itemInst.getUsesLeft(), currentX + 4, hotbarY + slotSize - 4);
                }
            }

            // Stampiamo il numero solo per i primi 4 slot
            if (i < 4) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                g2.drawString("" + (i + 1), currentX + 4, hotbarY + 12);
            }
        }

        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        String footer = "Usa la Rotellina per scorrere | Premi I o ESC per tornare al gioco";
        int footerWidth = g2.getFontMetrics().stringWidth(footer);
        g2.drawString(footer, (panelWidth - footerWidth) / 2, boxY + boxHeight + 25);
    }

    public void handleWheel(int delta) {
        scrollOffset += delta;
    }

    public boolean handleMouseClick(java.awt.Point p, Player player) {
        if (player == null) return false;
        List<ItemInstance> allItems = player.getInventory().getItems();

        for (int i = 0; i < allItems.size() && i < inventoryItemBounds.length; i++) {
            if (inventoryItemBounds[i].contains(p)) {
                selectedInventoryItemIndex = i;
                return true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (hotbarSlotBounds[i].contains(p)) {
                if (selectedInventoryItemIndex != -1 && selectedInventoryItemIndex < allItems.size()) {
                    ItemInstance selectedItem = allItems.get(selectedInventoryItemIndex);
                    
                    allItems.remove(selectedInventoryItemIndex);
                    
                    int targetIndex = Math.min(i, allItems.size());
                    allItems.add(targetIndex, selectedItem);
                    
                    selectedInventoryItemIndex = -1; 
                    return true;
                }
            }
        }
        return false;
    }
}