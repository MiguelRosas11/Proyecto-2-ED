package org.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;

public class RegistroWizardController implements Initializable {
    @FXML private VBox paneCredentials, paneGenres, paneActors, panePlatforms, paneMovies;
    @FXML private TextField tfNewUsuario, tfNewPassword, tfSearchGenres, tfSearchActors, tfSearchPlatforms, tfSearchMovies;
    @FXML private VBox vboxGenres, vboxActors, vboxPlatforms, vboxMovies;
    @FXML private Button btnBack, btnNext;

    private List<VBox> panes;
    private int currentStep = 0;
    private List<CheckBox> cbGenres, cbActors, cbPlatforms, cbMovies;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        panes = Arrays.asList(paneCredentials, paneGenres, paneActors, panePlatforms, paneMovies);
        cbGenres     = load("MATCH (g:Género) RETURN g.nombre AS nom", vboxGenres);
        cbActors     = load("MATCH (a:Actor) RETURN a.nombre AS nom", vboxActors);
        cbPlatforms  = load("MATCH (p:Plataforma) RETURN p.nombre AS nom", vboxPlatforms);
        cbMovies     = load("MATCH (m:Película) RETURN m.nombre AS nom", vboxMovies);

        addFilter(tfSearchGenres,    cbGenres,    vboxGenres);
        addFilter(tfSearchActors,    cbActors,    vboxActors);
        addFilter(tfSearchPlatforms, cbPlatforms, vboxPlatforms);
        addFilter(tfSearchMovies,    cbMovies,    vboxMovies);

        btnBack.setDisable(true);
    }

    private List<CheckBox> load(String query, VBox container) {
        System.out.println("Ejecutando consulta: " + query);
        List<CheckBox> list = new ArrayList<>();
        try (Session s = ConexionNeo4j.getInstance().getSession()) {
            for (Record r : s.run(query).list()) {
                CheckBox cb = new CheckBox(r.get("nom").asString());
                list.add(cb);
                container.getChildren().add(cb);
            }
        }
        return list;
    }

    private void addFilter(TextField tf, List<CheckBox> list, VBox container) {
        tf.textProperty().addListener((o,old,n) -> {
            container.getChildren().setAll(
                    list.stream()
                            .filter(cb -> cb.getText().toLowerCase().contains(n.toLowerCase()))
                            .collect(Collectors.toList())
            );
        });
    }

    @FXML
    protected void onNext(ActionEvent event) {
        if (currentStep < panes.size() - 1) {
            panes.get(currentStep).setVisible(false);
            currentStep++;
            panes.get(currentStep).setVisible(true);
            btnBack.setDisable(false);
            if (currentStep == panes.size() - 1) btnNext.setText("Finalizar");
        } else {
            finish();
            goLogin(event);
        }
    }

    @FXML
    protected void onBack(ActionEvent event) {
        panes.get(currentStep).setVisible(false);
        currentStep--;
        panes.get(currentStep).setVisible(true);
        btnNext.setText("Siguiente");
        if (currentStep == 0) btnBack.setDisable(true);
    }

    private void finish() {
        String user = tfNewUsuario.getText(), pass = tfNewPassword.getText();
        GestorCredenciales.registrar(user, pass);
        try (Session session = ConexionNeo4j.getInstance().getSession()) {
            session.writeTransaction(tx -> tx.run(
                    "MERGE (u:Usuario {nombre:$n,password:$p})",
                    Values.parameters("n", user, "p", pass)
            ));
            cbGenres.stream().filter(CheckBox::isSelected).forEach(cb ->
                    session.writeTransaction(tx -> tx.run(
                            "MATCH (u:Usuario {nombre:$n}), (g:Género {nombre:$g}) MERGE (u)-[:PREFIERE_GÉNERO]->(g)",
                            Values.parameters("n", user, "g", cb.getText())
                    ))
            );
            cbActors.stream().filter(CheckBox::isSelected).forEach(cb ->
                    session.writeTransaction(tx -> tx.run(
                            "MATCH (u:Usuario {nombre:$n}), (a:Actor {nombre:$g}) MERGE (u)-[:PREFIERE_ACTOR]->(a)",
                            Values.parameters("n", user, "g", cb.getText())
                    ))
            );
            cbPlatforms.stream().filter(CheckBox::isSelected).forEach(cb ->
                    session.writeTransaction(tx -> tx.run(
                            "MATCH (u:Usuario {nombre:$n}), (p:Plataforma {nombre:$g}) MERGE (u)-[:USA_PLATAFORMA]->(p)",
                            Values.parameters("n", user, "g", cb.getText())
                    ))
            );
            cbMovies.stream().filter(CheckBox::isSelected).forEach(cb ->
                    session.writeTransaction(tx -> tx.run(
                            "MATCH (u:Usuario {nombre:$n}), (m:Película {nombre:$g}) MERGE (u)-[:VIÓ]->(m)",
                            Values.parameters("n", user, "g", cb.getText())
                    ))
            );
        }
    }

    private void goLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}