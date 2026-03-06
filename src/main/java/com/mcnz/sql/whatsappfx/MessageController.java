package com.mcnz.sql.whatsappfx;


import com.mcnz.sql.whatsappfx.serveur.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class MessageController {

    @FXML private ListView<String> usersListView;
    @FXML private VBox messagesBox;
    @FXML private TextField messageField;
    @FXML private Label chatWithLabel;
    @FXML private ScrollPane scrollPane;

    private Client client;
    private String selectedUser;

    public void gereClient(Client client) {
        this.client = client;
        client.MessageRecu((envoyeur, contenu) -> {
            Platform.runLater(()->afficherMessage(envoyeur+":", contenu, false));
        });
        client.UserConnecter((username) -> {
            Platform.runLater(()->usersListView.getItems().add(username));
        });
        client.UserDeConnecter((username) -> {
            Platform.runLater(()->usersListView.getItems().remove(username));
        });
        // On commence à écouter les messages
        client.EcouterMessage();
    }
    @FXML
    public void initialize() {
        usersListView.setOnMouseClicked(event -> {
            String userSelectionne = usersListView.getSelectionModel().getSelectedItem();
            if (userSelectionne != null) {
                selectedUser = userSelectionne;
                chatWithLabel.setText("💬 Conversation avec : " + selectedUser);
                messagesBox.getChildren().clear();
            }
        });
    }

    @FXML
    public void gererEnvoie() {
        String contenu = messageField.getText();

        if (selectedUser == null) {
            chatWithLabel.setText("Sélectionne d'abord un utilisateur !");
            return;
        }
        if (contenu.isEmpty()) return;


        client.envoieMessage(selectedUser, contenu);


        afficherMessage("Moi", contenu, true);
        messageField.clear();
    }

    @FXML
    public void gererDeconnection() {
        client.closeTt();
        Stage stage = (Stage) messageField.getScene().getWindow();
        stage.close();
    }

    private void afficherMessage(String expediteur, String contenu, boolean estMoi) {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5));

        Label label = new Label(expediteur + "\n" + contenu);
        label.setWrapText(true);
        label.setMaxWidth(400);
        label.setPadding(new Insets(10));

        if (estMoi) {
            label.setStyle("-fx-background-color: #25d366; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-size: 13px;");
            hbox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            label.setStyle("-fx-background-color: #3e3e4e; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-size: 13px;");
            hbox.setAlignment(Pos.CENTER_LEFT);
        }

        hbox.getChildren().add(label);
        messagesBox.getChildren().add(hbox);
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }


}