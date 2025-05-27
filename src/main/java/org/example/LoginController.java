package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField tfUsuario;
    @FXML private PasswordField tfPassword;
    @FXML private Label lblError;

    @FXML
    protected void onLogin(ActionEvent event) {
        Usuario u = GestorCredenciales.autenticar(tfUsuario.getText(), tfPassword.getText());
        if (u != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuView.fxml"));
                Scene scene = new Scene(loader.load());
                MenuController mc = loader.getController();
                mc.setUsuario(u);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Menú Principal");
            } catch (IOException e) { e.printStackTrace(); }
        } else {
            lblError.setText("Usuario o contraseña incorrectos");
        }
    }

    @FXML
    protected void onRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegistroWizard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Registro - Paso 1");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
