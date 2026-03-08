package com.mcnz.sql.whatsappfx;

import com.mcnz.sql.whatsappfx.entity.User;
import com.mcnz.sql.whatsappfx.repository.impl.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    private UserRepository userRepository = new UserRepository();

    @FXML
    private PasswordField password_input;

    @FXML
    private TextField username_input;


    @FXML
    void Ajouter(ActionEvent event) throws IOException{
        if (username_input.getText().isEmpty() || password_input.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.showAndWait();
            return;
        }
        User existant = userRepository.getByUsername(username_input.getText());
        if (existant != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Username déjà utilisé !");
            alert.showAndWait();
            return;
        }
        User user = new User(
                username_input.getText(),
                password_input.getText()
        );
        userRepository.insert(user);

        Stage stage = (Stage) username_input.getScene().getWindow();
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("Connexion.fxml"));
        Scene scene = new Scene(fxml.load());
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void Connexion(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("Connexion.fxml"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void FermerPage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    @FXML
    void clearInterface(ActionEvent event) {
        username_input.clear();
        password_input.clear();
    }

}


