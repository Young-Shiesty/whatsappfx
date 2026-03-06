
package com.mcnz.sql.whatsappfx;

import com.mcnz.sql.whatsappfx.entity.User;
import com.mcnz.sql.whatsappfx.repository.impl.UserRepository;
import com.mcnz.sql.whatsappfx.serveur.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class ConnexionController {
    private UserRepository userRepository = new UserRepository();

    @FXML
    private TextField password_input;

    @FXML
    private TextField username_input;

    @FXML
    void SeConnecter(ActionEvent event) throws IOException {
        User user = userRepository.connecter(
                username_input.getText(),
                password_input.getText()
        );
        if (user != null) {
            Socket socket = new Socket("localhost", 1235);
            Client client = new Client(socket, username_input.getText());
            client.login(password_input.getText());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("message.fxml"));
            Scene scene = new Scene(loader.load());
            MessageController messageController = loader.getController();
            messageController.gereClient(client);

            Stage stage = (Stage) username_input.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Identifiant ou mot de passe incorrect !");
            alert.showAndWait();
        }
    }
    @FXML
    private void Connexion(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("Connexion.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}






