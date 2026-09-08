package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.SwingUtilities;

public class SettingsPanel {

    private final String[] tabs = {"Audio", "Grafica", "Controlli"};
    private int selectedTab = 0;

    private int activeFocusArea = 1;
    private int selectedOptionIndex = 0;

    private String statusMessage = "";
    private long statusMessageTime = 0;

    private final Rectangle[] tabBounds = new Rectangle[tabs.length];
    private final Rectangle[] optionBounds = new Rectangle[10];
    private final Rectangle applyButtonBounds = new Rectangle();
    private final Rectangle backButtonBounds = new Rectangle();

    private int tempMusicVol = 80, tempSfxVol = 100;
    private boolean tempMuted = false;
    private int tempResIdx = 0, tempFpsIdx = 1;
    private boolean tempFullScreen = false, tempVSync = true;

    private int tempJumpKey = KeyEvent.VK_SPACE;
    private int tempLeftKey = KeyEvent.VK_LEFT;
    private int tempRightKey = KeyEvent.VK_RIGHT;

    private int appliedMusicVol = 80, appliedSfxVol = 100;
    private boolean appliedMuted = false;
    private int appliedResIdx = 0, appliedFpsIdx = 1;
    private boolean appliedFullScreen = false, appliedVSync = true;
    
    private int appliedJumpKey = KeyEvent.VK_SPACE;
    private int appliedLeftKey = KeyEvent.VK_LEFT;
    private int appliedRightKey = KeyEvent.VK_RIGHT;

    private boolean waitingForKey = false;
    private int controlIndexToRebind = -1;

    private final String[] resolutions = {"800x600", "1280x720", "1920x1080"};
    private final String[] fpsLimits = {"30 FPS", "60 FPS", "120 FPS", "Illimitati"};

    public SettingsPanel() {
        for (int i = 0; i < tabBounds.length; i++) tabBounds[i] = new Rectangle();
        for (int i = 0; i < optionBounds.length; i++) optionBounds[i] = new Rectangle();
        
        loadSettingsFromFile();
    }

    public int getSelectedTab() { return selectedTab; }
    public int getSelectedOptionIndex() { return selectedOptionIndex; }
    public int getSettingsOptionsCount() { return getCurrentOptionsCount(); }
    public boolean isWaitingForKey() { return waitingForKey; }

    public void saveSettingsToFile() {
        try (FileWriter writer = new FileWriter("settings.txt")) {
            writer.write("JUMP=" + appliedJumpKey + "\n");
            writer.write("LEFT=" + appliedLeftKey + "\n");
            writer.write("RIGHT=" + appliedRightKey + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSettingsFromFile() {
        File file = new File("settings.txt");
        if (!file.exists()) {
            return; 
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    int value = Integer.parseInt(parts[1]);

                    switch (key) {
                        case "JUMP" -> { appliedJumpKey = value; tempJumpKey = value; }
                        case "LEFT" -> { appliedLeftKey = value; tempLeftKey = value; }
                        case "RIGHT" -> { appliedRightKey = value; tempRightKey = value; }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void handleKeyRebind(int keyCode) {
        if (!waitingForKey) return;
        
        boolean alreadyUsed = false;
        if (controlIndexToRebind != 0 && tempJumpKey == keyCode) alreadyUsed = true;
        if (controlIndexToRebind != 1 && tempLeftKey == keyCode) alreadyUsed = true;
        if (controlIndexToRebind != 2 && tempRightKey == keyCode) alreadyUsed = true;

        if (alreadyUsed) {
            showStatusMessage("Errore: Tasto già in uso!");
            waitingForKey = false;
            controlIndexToRebind = -1;
            return;
        }
        
        switch (controlIndexToRebind) {
            case 0 -> tempJumpKey = keyCode;
            case 1 -> tempLeftKey = keyCode;
            case 2 -> tempRightKey = keyCode;
        }
        waitingForKey = false;
        controlIndexToRebind = -1;
        showStatusMessage("Tasto aggiornato! Premi APPLICA per salvare.");
    }

    public void navigateVertical(int direction) {
        if (waitingForKey) return;
        if (activeFocusArea == 0) {
            if (direction > 0) activeFocusArea = 1;
            return;
        }
        int totalSelectables = getCurrentOptionsCount() + 1;
        selectedOptionIndex = (selectedOptionIndex + direction + totalSelectables) % totalSelectables;
    }

    public void navigateHorizontal(int direction) {
        if (waitingForKey) return;
        if (activeFocusArea == 0) {
            selectedTab = (selectedTab + direction + tabs.length) % tabs.length;
            selectedOptionIndex = 0;
            return;
        }

        if (selectedOptionIndex == getCurrentOptionsCount()) return;

        switch (selectedTab) {
            case 0 -> {
                if (selectedOptionIndex == 0) tempMusicVol = Math.min(100, Math.max(0, tempMusicVol + (direction * 10)));
                else if (selectedOptionIndex == 1) tempSfxVol = Math.min(100, Math.max(0, tempSfxVol + (direction * 10)));
                else if (selectedOptionIndex == 2) tempMuted = !tempMuted;
            }
            case 1 -> {
                if (selectedOptionIndex == 0) {
                    if (!tempFullScreen) {
                        tempResIdx = (tempResIdx + direction + resolutions.length) % resolutions.length;
                    }
                }
                else if (selectedOptionIndex == 1) tempFpsIdx = (tempFpsIdx + direction + fpsLimits.length) % fpsLimits.length;
                else if (selectedOptionIndex == 2) {
                    tempFullScreen = !tempFullScreen;
                    if (tempFullScreen) {
                        tempResIdx = 2;
                    }
                }
                else if (selectedOptionIndex == 3) tempVSync = !tempVSync;
            }
            case 2 -> {
                if (selectedOptionIndex >= 0 && selectedOptionIndex <= 2) {
                    waitingForKey = true;
                    controlIndexToRebind = selectedOptionIndex;
                    showStatusMessage("Premi un tasto sulla tastiera...");
                }
            }
        }
    }

    public void switchFocusArea() {
        if (waitingForKey) return;
        activeFocusArea = (activeFocusArea == 0) ? 1 : 0;
    }

    public void applyCurrentTabSettings(GamePanel panel) {
        GameFrame frame = (GameFrame) SwingUtilities.getWindowAncestor(panel);

        switch (selectedTab) {
            case 0 -> {
                appliedMusicVol = tempMusicVol;
                appliedSfxVol = tempSfxVol;
                appliedMuted = tempMuted;
            }
            case 1 -> {
                boolean isFullScreenChanged = (appliedFullScreen != tempFullScreen);
                appliedResIdx = tempResIdx;
                appliedFpsIdx = tempFpsIdx;
                appliedFullScreen = tempFullScreen;
                appliedVSync = tempVSync;

                if (frame != null) {
                    if (isFullScreenChanged) {
                        frame.setFullScreen(appliedFullScreen);
                    }
                    if (!appliedFullScreen) {
                        String[] res = resolutions[appliedResIdx].split("x");
                        frame.setWindowSize(Integer.parseInt(res[0]), Integer.parseInt(res[1]));
                    }
                }
            }
            case 2 -> {
                appliedJumpKey = tempJumpKey;
                appliedLeftKey = tempLeftKey;
                appliedRightKey = tempRightKey;
                saveSettingsToFile();
            }
        }
        showStatusMessage("Modifiche applicate per " + tabs[selectedTab] + "!");
    }

    private int getCurrentOptionsCount() {
        return switch (selectedTab) {
            case 0 -> 3; 
            case 1 -> 4; 
            case 2 -> 5; 
            default -> 0;
        };
    }

    private void showStatusMessage(String msg) {
        this.statusMessage = msg;
        this.statusMessageTime = System.currentTimeMillis();
    }

    public void handleMouseMove(Point mousePoint) {
        if (waitingForKey) return;
        for (int i = 0; i < tabs.length; i++) {
            if (tabBounds[i].contains(mousePoint)) {
                selectedTab = i;
                activeFocusArea = 0;
                return;
            }
        }
        for (int i = 0; i < getCurrentOptionsCount(); i++) {
            if (optionBounds[i].contains(mousePoint)) {
                selectedOptionIndex = i;
                activeFocusArea = 1;
                return;
            }
        }
        if (applyButtonBounds.contains(mousePoint)) {
            selectedOptionIndex = getCurrentOptionsCount();
            activeFocusArea = 1;
        }
    }

    public boolean handleMouseClick(Point mousePoint, GamePanel panel) {
        if (waitingForKey) return false;
        if (backButtonBounds.contains(mousePoint)) {
            panel.setCurrentState(GameState.MENU);
            return true;
        }
        for (int i = 0; i < tabs.length; i++) {
            if (tabBounds[i].contains(mousePoint)) {
                selectedTab = i;
                selectedOptionIndex = 0;
                activeFocusArea = 1;
                return true;
            }
        }
        for (int i = 0; i < getCurrentOptionsCount(); i++) {
            if (optionBounds[i].contains(mousePoint)) {
                selectedOptionIndex = i;
                activeFocusArea = 1;

                if (selectedTab == 0 && (i == 0 || i == 1)) {
                    Rectangle rect = optionBounds[i];
                    double clickX = mousePoint.getX() - rect.getX();
                    int newVol = Math.min(100, Math.max(0, (int) ((clickX / rect.getWidth()) * 100)));
                    if (i == 0) tempMusicVol = newVol;
                    else tempSfxVol = newVol;
                } else if (selectedTab == 2 && i >= 3) {
                    return true;
                } else {
                    if (!(selectedTab == 1 && i == 0 && tempFullScreen)) {
                        navigateHorizontal(1);
                    }
                }
                return true;
            }
        }
        if (applyButtonBounds.contains(mousePoint)) {
            applyCurrentTabSettings(panel);
            return true;
        }
        return false;
    }

    public void handleMouseDrag(Point mousePoint) {
        if (selectedTab != 0 || waitingForKey) return;

        for (int i = 0; i <= 1; i++) {
            Rectangle rect = optionBounds[i];
            if (mousePoint.getY() >= rect.getY() - 10 && mousePoint.getY() <= rect.getY() + rect.getHeight() + 10) {
                selectedOptionIndex = i;
                activeFocusArea = 1;

                double clickX = mousePoint.getX() - rect.getX();
                int newVol = Math.min(100, Math.max(0, (int) ((clickX / rect.getWidth()) * 100)));

                if (i == 0) tempMusicVol = newVol;
                else tempSfxVol = newVol;
                return;
            }
        }
    }

    public void draw(Graphics2D g2, GamePanel panel) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.YELLOW);
        String title = "IMPOSTAZIONI";
        g2.drawString(title, getCenteredX(g2, title, panelWidth), 60);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics tabMetrics = g2.getFontMetrics();
        int totalTabsWidth = tabs.length * 140;
        int startX = (panelWidth - totalTabsWidth) / 2;

        for (int i = 0; i < tabs.length; i++) {
            int textWidth = tabMetrics.stringWidth(tabs[i]);
            int x = startX + (i * 140) + (140 - textWidth) / 2;
            int y = 110;

            tabBounds[i].setBounds(x - 10, y - 22, textWidth + 20, 30);

            if (i == selectedTab) {
                g2.setColor(activeFocusArea == 0 ? Color.RED : Color.YELLOW);
                g2.drawRect(x - 8, y - 20, textWidth + 16, 26);
            } else {
                g2.setColor(Color.GRAY);
            }
            g2.drawString(tabs[i], x, y);
        }

        g2.setColor(Color.WHITE);
        g2.drawLine(40, 130, panelWidth - 40, 130);

        drawTabContent(g2, panelWidth);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        String backText = "< Torna al Menu Principale >";
        int backX = getCenteredX(g2, backText, panelWidth);
        int backY = panelHeight - 90;
        backButtonBounds.setBounds(backX, backY - 20, g2.getFontMetrics().stringWidth(backText), 30);
        g2.setColor(Color.CYAN);
        g2.drawString(backText, backX, backY);

        if (System.currentTimeMillis() - statusMessageTime < 2500 && !statusMessage.isEmpty()) {
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(statusMessage.contains("Errore") ? Color.RED : Color.GREEN);
            g2.drawString(statusMessage, getCenteredX(g2, statusMessage, panelWidth), panelHeight - 130);
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.LIGHT_GRAY);
        String hint = waitingForKey ? "PREMI UN TASTO SULLA TASTIERA..." : "TAB per Schede | FRECCE per muoverti | ENTER/CLICK per applicare o modificare";
        g2.drawString(hint, getCenteredX(g2, hint, panelWidth), panelHeight - 40);
    }

    private void drawTabContent(Graphics2D g2, int panelWidth) {
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = g2.getFontMetrics();
        int startY = 170;

        if (selectedTab == 0) {
            drawVolumeBar(g2, "Volume Musica", tempMusicVol, 0, startY, panelWidth);
            drawVolumeBar(g2, "Volume Effetti", tempSfxVol, 1, startY + 55, panelWidth);

            String muteText = "Mute Audio: " + (tempMuted ? "[SI]" : "[NO]");
            int textWidth = metrics.stringWidth(muteText);
            int x = (panelWidth - textWidth) / 2;
            int y = startY + 110;
            optionBounds[2].setBounds(x, y - metrics.getAscent(), textWidth, metrics.getHeight());

            if (selectedOptionIndex == 2 && activeFocusArea == 1) {
                g2.setColor(Color.RED);
                g2.drawString("> " + muteText + " <", x - 30, y);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(muteText, x, y);
            }
        } else {
            String resLabel = tempFullScreen ? "Risoluzione: 1920x1080 (Bloccata)" : "Risoluzione: < " + resolutions[tempResIdx] + " >";
            
            String[] labels = switch (selectedTab) {
                case 1 -> new String[]{
                    resLabel,
                    "Limite FPS: < " + fpsLimits[tempFpsIdx] + " >",
                    "Schermo Intero: " + (tempFullScreen ? "[ATTIVO]" : "[DISATTIVO]"),
                    "V-Sync: " + (tempVSync ? "[ATTIVO]" : "[DISATTIVO]")
                };
                case 2 -> new String[]{
                    "Salto: " + KeyEvent.getKeyText(tempJumpKey),
                    "Muovi a Sinistra: " + KeyEvent.getKeyText(tempLeftKey),
                    "Muovi a Destra: " + KeyEvent.getKeyText(tempRightKey),
                    "Uso oggetto: Click Sinistro Mouse",
                    "Apri/Chiudi Inventario: Tasto I"
                };
                default -> new String[0];
            };

            for (int i = 0; i < labels.length; i++) {
                int textWidth = metrics.stringWidth(labels[i]);
                int x = (panelWidth - textWidth) / 2;
                int y = startY + (i * 40);

                optionBounds[i].setBounds(x, y - metrics.getAscent(), textWidth, metrics.getHeight());

                boolean isCurrent = (i == selectedOptionIndex && activeFocusArea == 1);
                if (isCurrent) {
                    if (selectedTab == 1 && i == 0 && tempFullScreen) {
                        g2.setColor(Color.DARK_GRAY);
                        g2.drawString(labels[i], x, y);
                    } else {
                        g2.setColor(waitingForKey && controlIndexToRebind == i ? Color.ORANGE : Color.RED);
                        g2.drawString("> " + labels[i] + " <", x - 30, y);
                    }
                } else {
                    g2.setColor((selectedTab == 1 && i == 0 && tempFullScreen) ? Color.DARK_GRAY : Color.WHITE);
                    g2.drawString(labels[i], x, y);
                }
            }
        }

        int applyIndex = getCurrentOptionsCount();
        String applyText = "[ APPLICA MODIFICHE ]";
        int applyWidth = metrics.stringWidth(applyText);
        int applyX = (panelWidth - applyWidth) / 2;
        int applyY = startY + (applyIndex * 40) + 15;

        applyButtonBounds.setBounds(applyX, applyY - metrics.getAscent(), applyWidth, metrics.getHeight());

        if (selectedOptionIndex == applyIndex && activeFocusArea == 1) {
            g2.setColor(Color.GREEN);
            g2.drawString("> " + applyText + " <", applyX - 25, applyY);
        } else {
            g2.setColor(Color.ORANGE);
            g2.drawString(applyText, applyX, applyY);
        }
    }

    private void drawVolumeBar(Graphics2D g2, String label, int value, int optionIdx, int y, int panelWidth) {
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);

        String title = label + ": " + value + "%";
        g2.drawString(title, (panelWidth / 2) - 150, y);

        int barWidth = 300;
        int barHeight = 16;
        int barX = (panelWidth - barWidth) / 2;
        int barY = y + 8;

        optionBounds[optionIdx].setBounds(barX, barY, barWidth, barHeight);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        int fillWidth = (int) (barWidth * (value / 100.0));
        g2.setColor((optionIdx == selectedOptionIndex && activeFocusArea == 1) ? Color.RED : Color.CYAN);
        g2.fillRect(barX, barY, fillWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);
    }

    private int getCenteredX(Graphics2D g2, String text, int panelWidth) {
        FontMetrics metrics = g2.getFontMetrics();
        return (panelWidth - metrics.stringWidth(text)) / 2;
    }
    
    public int getAppliedJumpKey() { return appliedJumpKey; }
    public int getAppliedLeftKey() { return appliedLeftKey; }
    public int getAppliedRightKey() { return appliedRightKey; }
}