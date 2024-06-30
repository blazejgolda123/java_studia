package org.example.testgui04;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class HelloController {
    @FXML
    private Label label;
    @FXML
    private Stage stage;
    @FXML
    private static String podaj_algorytm;
    @FXML
    private TextField tliczba_maszyn_pole;
    @FXML
    private TextField tliczba_zadan_pole;
    @FXML
    private TextField tpodaj_algorytm;
    @FXML
    private TextField twaga;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private static int liczba_maszyn;
    @FXML
    private static int liczba_zadan;
    @FXML
    private static int []wagi_zadan;
    public static int i=0;

    @FXML
    private void Nowe_okienko(ActionEvent event) {
        try {
            HelloController.liczba_maszyn = Integer.parseInt(tliczba_maszyn_pole.getText());
            HelloController.liczba_zadan = Integer.parseInt(tliczba_zadan_pole.getText());
            HelloController.podaj_algorytm = tpodaj_algorytm.getText();
            HelloController.wagi_zadan = new int[liczba_zadan];
            //Load the second scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scene2.fxml"));
            root = loader.load();
            // Get the current stage
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            // Set the new scene
            stage.setScene(scene);
            stage.setTitle("Ustaw wagę dla zadania: " + String.valueOf(i + 1));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            dialog_bledu("Wystapil błąd: " + e.getMessage());
            Platform.exit();

        }
    }

    @FXML
    private void wczytaj_i_wroc_albo_znowu_okienko_do_wczytania(ActionEvent event) throws IOException {
        try {
            String waga = twaga.getText();
            HelloController.wagi_zadan[i] = Integer.parseInt(waga);
            HelloController.i++;
            System.out.println("i = " + HelloController.i);
            System.out.println("HelloController.liczba_zadan = " + HelloController.liczba_zadan);
            if (HelloController.i < HelloController.liczba_zadan) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("scene2.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Ustaw wagę dla zadania: " + String.valueOf(i + 1));
                stage.show();
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("scene3.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Informator");
                stage.setScene(scene);
                stage.show();
            }
        }catch(Exception e){
            e.printStackTrace();
            dialog_bledu("Wystapil błąd: "+e.getMessage());
            Platform.exit();
        }
    }
    @FXML
    private void oblicz_algorytmem(ActionEvent event ){
        List<List<Zadanie>> wynik = obliczWynik(HelloController.podaj_algorytm, HelloController.liczba_maszyn, HelloController.liczba_zadan, HelloController.wagi_zadan);
        ZdarzenieCrud z = new ZdarzenieCrud();
        z.addZdarzenieZZadaniami(HelloController.liczba_maszyn, HelloController.liczba_zadan, HelloController.wagi_zadan, wynik);

        label.setText(dajWynikJakoString(wynik));
        //z.exportDataToCsv("C:\\Users\\Blazej\\Desktop\\exported_data.csv");
    }


    private List<List<Zadanie>> obliczWynik(String algorytm, int liczba_maszyn, int liczba_zadan, int[] wagi_zadan){
        if("LPT".equalsIgnoreCase(algorytm)){
            List<List<Zadanie>> lpt = LPT(liczba_maszyn, liczba_zadan, wagi_zadan);
            wyswietlWynik(lpt);
            return lpt;
        } else if("BPP".equalsIgnoreCase(algorytm)){
            List<List<Zadanie>> bpp = BPP(liczba_maszyn, liczba_zadan, wagi_zadan);
            wyswietlWynik(bpp);
            return bpp;
        } else {
            //zły algorytm
        }
        return null;
    }
    private void dialog_bledu(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText("Wystąpił błąd");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static List<List<Zadanie>> LPT(int liczbaMaszyn, int liczbaZadan, int[] wagiZadan) {
        // Tworzenie listy zadań
        List<Zadanie> zadania = new ArrayList<>();
        for (int i = 0; i < liczbaZadan; i++) {
            zadania.add(new Zadanie(i + 1, wagiZadan[i]));
        }

        // Sortowanie zadań malejąco według wagi
        Collections.sort(zadania);

        // Tworzenie kolejki priorytetowej dla maszyn (min-heap)
        PriorityQueue<Maszyna> maszyny = new PriorityQueue<>();
        for (int i = 0; i < liczbaMaszyn; i++) {
            maszyny.add(new Maszyna(i + 1));
        }

        // Przypisywanie zadań do maszyn
        for (Zadanie zad : zadania) {
            Maszyna maszyna = maszyny.poll(); // Pobranie maszyny o najniższym obciążeniu
            maszyna.addZadanie(zad); // Dodanie zadania do maszyny
            maszyny.add(maszyna); // Ponowne dodanie maszyny do kolejki
        }

        // Konwersja wyniku na listę list
        List<List<Zadanie>> wynik = new ArrayList<>();
        for (Maszyna mach : maszyny) {
            wynik.add(mach.getZadania());
        }

        return wynik;
    }

    public static List<List<Zadanie>> BPP(int liczbaMaszyn, int liczbaZadan, int[] wagiZadan) {
        // Tworzenie listy zadań
        List<Zadanie> zadania = new ArrayList<>();
        for (int i = 0; i < liczbaZadan; i++) {
            zadania.add(new Zadanie(i + 1, wagiZadan[i]));
        }

        // Sortowanie zadań malejąco według wagi
        Collections.sort(zadania);

        // Lista maszyn
        List<Maszyna> maszyny = new ArrayList<>();
        for (int i = 0; i < liczbaMaszyn; i++) {
            maszyny.add(new Maszyna(i + 1));
        }

        // Przypisywanie zadań do maszyn
        for (Zadanie zad : zadania) {
            // Znajdujemy maszynę z najmniejszym obciążeniem
            Collections.sort(maszyny);
            maszyny.get(0).addZadanie(zad);
        }

        // Przekształcenie wyniku do List<List<Zadanie>>
        List<List<Zadanie>> wynik = new ArrayList<>();
        for (Maszyna mach : maszyny) {
            wynik.add(mach.getZadania());
        }

        return wynik;
    }

    private static void wyswietlWynik(List<List<Zadanie>> wynik) {
        for (int i = 0; i < wynik.size(); i++) {
            System.out.println("Maszyna " + (i + 1) + ":");
            for (Zadanie zad : wynik.get(i)) {
                System.out.println("  Zadanie z wagą: " + zad.getWaga());
            }
        }
    }

    private static String dajWynikJakoString(List<List<Zadanie>> wynik) {
        String ret = "";
        for (int i = 0; i < wynik.size(); i++) {
            ret = ret + "Maszyna " + (i + 1) + ":\n";
            for (Zadanie zad : wynik.get(i)) {
                ret = ret + "  Zadanie z wagą: " + zad.getWaga() + "\n";
            }
        }
        return ret;
    }

//    Program do szeregowania zadań na maszynach równoległych z wykorzystaniem różnych algorytmów(LPT, BPP).
//    Program pozwala na zadanie ilości maszyn, zadań oraz pozwala na wizualizację wyniku


}