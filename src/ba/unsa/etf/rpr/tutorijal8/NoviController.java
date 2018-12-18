package ba.unsa.etf.rpr.tutorijal8;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NoviController {
    public TextField postanskiBroj;
    public Button closeBtn;
    public SimpleStringProperty text;

    public NoviController() {
        text = new SimpleStringProperty("");
    }

    public boolean validanPostanskiBroj(String s) throws Exception {
        URL url = new URL("http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + postanskiBroj.getText());
        BufferedReader ulaz = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String json = "", line = null;
        while ((line = ulaz.readLine()) != null)
            json = json + line;
        if (json.equals("OK")) return true;
        return false;
    }

    @FXML
    public void initialize() {
        Runnable r = () -> {
            postanskiBroj.textProperty().bindBidirectional(text);
            postanskiBroj.getStyleClass().add("poljeNeispravno");
            postanskiBroj.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    try {
                        if (!newValue) {
                            if (validanPostanskiBroj(postanskiBroj.getText())) {
                                postanskiBroj.getStyleClass().removeAll("poljeNeispravno");
                                postanskiBroj.getStyleClass().add("poljeIspravno");
                            } else {
                                postanskiBroj.getStyleClass().removeAll("poljeIspravno");
                                postanskiBroj.getStyleClass().add("poljeNeispravno");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    public void clickOnCloseButton(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }
}