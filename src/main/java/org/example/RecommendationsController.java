package org.example;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.List;

public class RecommendationsController {
    @FXML private VBox vboxRecs;
    @FXML private Button btnRefrescar, btnAgregar, btnBack;
    private Usuario user;

    public void setUsuario(Usuario u) {
        this.user = u;
        loadRecs();
    }

    private void loadRecs() {
        vboxRecs.getChildren().clear();
        List<String> recs = new AlgoritmoRecomendacion().recomendar(user.getNombre());
        for (String pel : recs) vboxRecs.getChildren().add(new CheckBox(pel));
    }

    @FXML private void onRefrescar(ActionEvent e) { loadRecs(); }

    @FXML private void onAgregar(ActionEvent e) {
        for (var node : vboxRecs.getChildren()) {
            CheckBox cb = (CheckBox) node;
            if (cb.isSelected()) WishlistManager.add(user.getNombre(), cb.getText());
        }
    }

    @FXML private void onBack(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuView.fxml"));
            Scene scene = new Scene(loader.load());
            MenuController mc = loader.getController();
            mc.setUsuario(user);
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Menú Principal");
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}
