package org.example.testgui04;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ZdarzenieCrud {
    public UUID addZdarzenie(int liczba_maszyn){
        UUID uuid = UUID.randomUUID();
        String sql = "INSERT INTO zdarzenie (id, liczba_maszyn, data_eksperymentu) VALUES (?, ?, CURRENT_TIMESTAMP())";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, liczba_maszyn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuid;
    }

    public void addZadanie(UUID idZdarzenia, int wagaZadania){
        UUID uuid = UUID.randomUUID();

        String sql = "INSERT INTO zadanie (id, waga_zadania, id_zdarzenia) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, wagaZadania);
            pstmt.setString(3, idZdarzenia.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addZdarzenieZZadaniami(int liczba_maszyn, int liczba_zadan, int[] wagi_zadan,  List<List<Zadanie>> wynik){
        UUID uuidZdarzenie = UUID.randomUUID();
        String sqlInsertZdarzenie = "INSERT INTO zdarzenie (id, liczba_maszyn, data_eksperymentu) VALUES (?, ?, CURRENT_TIMESTAMP())";
        String sqlInsertZadanie = "INSERT INTO zadanie (id, waga_zadania, id_zdarzenia) VALUES (?, ?, ?)";
        String sqlInsertWykonanie = "INSERT INTO wykonanie(id, numer_maszyny, lp, id_zadania) values(?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement insertZdarzenie = conn.prepareStatement(sqlInsertZdarzenie);
             PreparedStatement insertZadania = conn.prepareStatement(sqlInsertZadanie);
             PreparedStatement insertWykonanie = conn.prepareStatement(sqlInsertWykonanie)
        ) {
            insertZdarzenie.setString(1, uuidZdarzenie.toString());
            insertZdarzenie.setInt(2, liczba_maszyn);
            insertZdarzenie.executeUpdate();

            for (int j=0; j<liczba_zadan;j++){
                UUID uuidZadanie = UUID.randomUUID();
                insertZadania.setString(1, uuidZadanie.toString());
                insertZadania.setInt(2, wagi_zadan[j]);
                insertZadania.setString(3, uuidZdarzenie.toString());
                insertZadania.executeUpdate();

                int lpMaszyny = 0;
                int lpWMaszynie = 0;

                for(int nrMaszyny = 0; nrMaszyny < wynik.size(); nrMaszyny++){
                    for(int nrZadaniaWMszynie = 0; nrZadaniaWMszynie< wynik.get(nrMaszyny).size(); nrZadaniaWMszynie++){
                        if(
                                wynik.get(nrMaszyny).get(nrZadaniaWMszynie).getWaga() == wagi_zadan[j]
                                && wynik.get(nrMaszyny).get(nrZadaniaWMszynie).getId() != -1
                        ){
                            lpMaszyny = nrMaszyny + 1;
                            lpWMaszynie = nrZadaniaWMszynie + 1;
                            wynik.get(nrMaszyny).get(nrZadaniaWMszynie).setId(-1);
                        }
                    }
                }

                UUID uuidWyknanie = UUID.randomUUID();
                insertWykonanie.setString(1, uuidWyknanie.toString());
                insertWykonanie.setInt(2, lpMaszyny);
                insertWykonanie.setInt(3, lpWMaszynie);
                insertWykonanie.setString(4, uuidZadanie.toString());
                insertWykonanie.executeUpdate();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportDataToCsv(String outputFile) {
        try (Connection conn = DBConnection.getConnection()) {
            // Pobranie danych z tabel
            String selectZdarzeniaQuery = "SELECT * FROM zdarzenie";
            String selectZadaniaQuery = "SELECT * FROM zadanie";

            try (PreparedStatement statementZdarzenia = conn.prepareStatement(selectZdarzeniaQuery);
                 PreparedStatement statementZadania = conn.prepareStatement(selectZadaniaQuery);
                 ResultSet resultSetZdarzenia = statementZdarzenia.executeQuery();
                 ResultSet resultSetZadania = statementZadania.executeQuery()) {

                // Zapis do pliku CSV
                try (Writer writer = new FileWriter(outputFile)) {
                    // Nagłówki dla tabel
                    writer.write("id_zdarzenia,liczba_maszyn,data_eksperymentu\n");

                    // Zapis danych z tabeli zdarzenie
                    while (resultSetZdarzenia.next()) {
                        String idZdarzenia = resultSetZdarzenia.getString("id");
                        int liczbaMaszyn = resultSetZdarzenia.getInt("liczba_maszyn");
                        String dataEksperymentu = resultSetZdarzenia.getString("data_eksperymentu");

                        writer.write(String.format("%s,%d,%s\n", idZdarzenia, liczbaMaszyn, dataEksperymentu));
                    }

                    // Zapis danych z tabeli zadanie
                    writer.write("\n");
                    writer.write("id_zadania,waga_zadania,id_zdarzenia\n");

                    while (resultSetZadania.next()) {
                        String idZadania = resultSetZadania.getString("id");
                        int wagaZadania = resultSetZadania.getInt("waga_zadania");
                        String idZdarzenia = resultSetZadania.getString("id_zdarzenia");

                        writer.write(String.format("%s,%d,%s\n", idZadania, wagaZadania, idZdarzenia));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Dane zapisane do pliku CSV: " + outputFile);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
//https://localhost/phpmyadmin/

//    CREATE TABLE zdarzenie (
//            id CHAR(36) PRIMARY KEY,
//    liczba_maszyn INT,
//    wynik INT,
//    data_eksperymentu TIMESTAMP
//);
//
//    CREATE TABLE zadanie (
//            id CHAR(36) PRIMARY KEY,
//    waga_zadania INT,
//    id_zdarzenia CHAR(36),
//    FOREIGN KEY (id_zdarzenia) REFERENCES zdarzenie(id)
//            );

//    CREATE TABLE wykonanie (
//            id CHAR(36) PRIMARY KEY,
//            numer_maszyny INT,
//            lp INT,
//            id_zadania CHAR(36),
//    FOREIGN KEY (id_zadania) REFERENCES zadanie(id)
//            );

//    INSERT INTO zdarzenie (id, liczba_maszyn, data_eksperymentu)
//    VALUES (UUID(), 100, CURRENT_TIMESTAMP());

//select
//    *
//from zdarzenie z
//left join zadanie zad on z.id = zad.id_zdarzenia
//;

//delete from zadania;
//delete from zdarzenie;
