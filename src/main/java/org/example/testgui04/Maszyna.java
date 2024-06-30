package org.example.testgui04;

import java.util.ArrayList;
import java.util.List;

class Maszyna implements Comparable<Maszyna> {
    private int id;
    private int wczytane;
    private List<Zadanie> zadania;

    public Maszyna(int id) {
        this.id = id;
        this.wczytane = 0;
        this.zadania = new ArrayList<>();
    }

    public void addZadanie(Zadanie zad) {
        zadania.add(zad);
        wczytane += zad.getWaga();
    }

    public int getWczytane() {
        return wczytane;
    }

    public List<Zadanie> getZadania() {
        return zadania;
    }

    @Override
    public int compareTo(Maszyna other) {
        return Integer.compare(this.wczytane, other.wczytane);
    }
}
