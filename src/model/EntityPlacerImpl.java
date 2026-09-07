package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EntityPlacerImpl implements EntityPlacer {

    @Override
    public ArrayList<Entity> place(String path) {
        ArrayList<Entity> entities = new ArrayList<>();

        if (path == null || path.isEmpty()) {
            return entities;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String type = parts[0].trim();
                    double x = Double.parseDouble(parts[1].trim());
                    double y = Double.parseDouble(parts[2].trim());

                }
            }
        } catch (IOException e) {
            System.err.println("Nessun file entità trovato in: " + path + " (verrà caricato un livello senza entità dinamiche)");
        } catch (NumberFormatException e) {
            System.err.println("Errore di formato coordinate nel file entità: " + path);
        }

        return entities;
    }
}