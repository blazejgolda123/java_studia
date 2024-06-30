package org.example.testgui04;

public class Zadanie implements Comparable<Zadanie> {
    private int waga;



    private int id;

    public Zadanie(int id, int waga) {
        this.id = id;
        this.waga = waga;
    }

    public int getWaga() {
        return waga;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Zadanie other) {
        // Sortowanie malejąco według wagi
        return Integer.compare(other.waga, this.waga);
    }
}
