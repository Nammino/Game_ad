package model;

import java.util.ArrayList;

public interface Level {

    String getPath();

    void build();

    ArrayList<String> getMap();

    ArrayList<Entity> getEntities();

    boolean isCompleted(); 

    void setCompleted(boolean completed); 

    void resetLevel(); // <-- AGGIUNGI QUESTO
}