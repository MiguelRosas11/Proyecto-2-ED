package org.example;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.neo4j.driver.Values;

import java.io.IOException;

public class ListController {
    @FXML private VBox vboxWishlist;
    private Usuario user;

    public void setUsuario(Usuario u) {
        this.user = u;
        loadList();
    }

    private void loadList() {
        vboxWishlist.getChildren().clear();
        for (String pel : WishlistManager.getWishlist(user.getNombre())) {
            HBox row = new HBox(10);
            CheckBox cb = new CheckBox(pel);
            Button btnVista = new Button("Vista");
            Button btnEliminar = new Button("Eliminar");
            btnVista.setOnAction(e -> {
                try (var ses = ConexionNeo4j.getInstance().getSession()) {
                    ses.writeTransaction(tx -> tx.run(
                        "MATCH (u:Usuario {nombre:$n}), (m:Película {nombre:$p}) MERGE (u)-[:VIÓ]->(m)",
                        Values.parameters("n", user.getNombre(), "p", pel)
                    ));
                }
                WishlistManager.remove(user.getNombre(), pel);
                loadList();
            });
            btnEliminar.setOnAction(e -> {
                WishlistManager.remove(user.getNombre(), pel);
                loadList();
            });
            row.getChildren().addAll(cb, btnVista, btnEliminar);
            vboxWishlist.getChildren().add(row);
        }
    }

    @FXML private void onBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuView.fxml"));
            Scene scene = new Scene(loader.load());
            MenuController mc = loader.getController();
            mc.setUsuario(user);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Menú Principal");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
