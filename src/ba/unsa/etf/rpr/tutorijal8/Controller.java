package ba.unsa.etf.rpr.tutorijal8;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Controller {
    public SimpleStringProperty text;
    public SimpleListProperty<String> lista;
    public TextField searchField;
    public Button searchBtn;
    public ListView list;
    public List<String> result1;
    public Button stopButton;
    public ProgressBar progressBar;
    public boolean prekidanje = false;
    NoviController noviController;
    private Thread thread1, thread2;

    public Controller() {
        text = new SimpleStringProperty("");
        lista = new SimpleListProperty<>();
        result1 = Collections.synchronizedList(new ArrayList<>());
        progressBar = new ProgressBar(0);
    }

    @FXML
    public void initialize() {
        searchField.textProperty().bindBidirectional(text);
        list.itemsProperty().bindBidirectional(lista);
        lista.set(FXCollections.observableArrayList(result1));
        stopButton.setDisable(true);
        prekidanje = false;
        progressBar.setProgress(0);
    }

    public void dajFajloveKojiSePodudaraju(File f) {
        if (prekidanje) return;
        try {
            File[] files = f.listFiles();
            if (files == null) return;
            for (File file : files) {
                if (file.isDirectory()) {
                    dajFajloveKojiSePodudaraju(file);
                } else {
                    if (file.getCanonicalPath().toLowerCase().contains(searchField.getText().toLowerCase())) {
                        String result = file.getCanonicalPath();
                        Platform.runLater(() -> {
                            list.getItems().add(result);
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickOnSearchBtn(ActionEvent actionEvent) {
        initialize();
        Runnable r1 = () -> {
            searchBtn.setDisable(true);
            searchField.setDisable(true);
            stopButton.setDisable(false);
            dajFajloveKojiSePodudaraju(new File(System.getProperty("user.home")));
        };
        Runnable r2 = () -> {
            for (int i = 1; i <= 1000; i++) {
                if (prekidanje) {
                    progressBar.setProgress(1000);
                    break;
                }
                progressBar.setProgress(i / 1000.0);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread1 = new Thread(r1);
        thread2 = new Thread(r2);
        thread1.start();
        thread2.start();
    }

    public void clickOnStopButton(ActionEvent actionEvent) {
        if (thread1 != null || thread2 != null) {
            prekidanje = true;
            searchBtn.setDisable(false);
            searchField.setDisable(false);
            stopButton.setDisable(true);
        }
    }

    public void clickonListView(MouseEvent mouseEvent) {
        Parent root = null;
        try {
            Stage myStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("noviprozor.fxml"));
            loader.load();
            noviController = loader.getController();
            myStage.setTitle("Slanje datoteke");
            myStage.setScene(new Scene(loader.getRoot(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            myStage.setResizable(false);
            myStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}