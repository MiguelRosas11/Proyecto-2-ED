package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuController {
    @FXML private Button btnRecomendaciones, btnListaDeseos, btnSalir, btnAgregarPelicula, btnEliminarPelicula;
    private Usuario usuario;

    public void setUsuario(Usuario u) {
        this.usuario = u;
        boolean isAdmin = u.esAdmin();
        btnAgregarPelicula.setVisible(isAdmin);
        btnEliminarPelicula.setVisible(isAdmin);
    }

    @FXML
    private void onRecomendaciones(ActionEvent e) {
        System.out.println("Click en Recomendaciones");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecommendationsView.fxml"));
            Scene scene = new Scene(loader.load());
            RecommendationsController rc = loader.getController();
            rc.setUsuario(usuario);
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Recomendaciones");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML private void onListaDeseos(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListView.fxml"));
            Scene scene = new Scene(loader.load());
            ListController lc = loader.getController();
            lc.setUsuario(usuario);
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Lista de Deseos");
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void onSalir(ActionEvent e) {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML private void onAgregarPelicula(ActionEvent e) { /* TODO */ }
    @FXML private void onEliminarPelicula(ActionEvent e) { /* TODO */ }
}
