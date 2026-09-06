package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LevelBuilderImpl implements LevelBuilder {

    @Override
    public ArrayList<String> build(String path) {
        ArrayList<String> mapLines = new ArrayList<>();

        if (path == null || path.isEmpty()) {
            return mapLines;
        }

        // Legge il file di testo della mappa e inserisce ogni riga nella lista
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                mapLines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura della mappa dal percorso: " + path);
        }

        return mapLines;
    }
}