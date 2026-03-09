package com.mcnz.sql.whatsappfx;
import com.mcnz.sql.whatsappfx.entity.Message;
import com.mcnz.sql.whatsappfx.entity.User;
import com.mcnz.sql.whatsappfx.repository.impl.MessageRepository;
import com.mcnz.sql.whatsappfx.serveur.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;


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
            Platform.runLater(() -> {
                usersListView.getItems().remove(username);
                usersListView.getItems().remove(username + "🟢");
                usersListView.getItems().add(username + "🟢");
            });
        });
        client.UserDeConnecter((username) -> {
            Platform.runLater(() -> {
                usersListView.getItems().remove(username + "🟢");
                usersListView.getItems().add(username);
            });
        });

        List<User> tousLesUsers = messageRepository.getTousLesUsers();
        for (User u : tousLesUsers) {
            if (!u.getUsername().equals(client.getUsername())) {
                if (!usersListView.getItems().contains(u.getUsername())) {
                    usersListView.getItems().add(u.getUsername());
                }
            }
        }
        configurerListView();
        // On commence a ecouter les messages
        client.EcouterMessage();
    }
    @FXML
    public void initialize() {
        usersListView.setOnMouseClicked(event -> {
            String userSelectionne = usersListView.getSelectionModel().getSelectedItem();
            if (userSelectionne != null) {
                selectedUser = userSelectionne.replace("🟢", "").trim();
                chatWithLabel.setText("💬 Conversation avec : " + selectedUser);
                messagesBox.getChildren().clear();
                //pour les message qui sont dans  le bd
                User moi = messageRepository.getUserByUsername(client.getUsername());
                User ami = messageRepository.getUserByUsername(selectedUser);
                if (moi != null && ami != null) {
                    List<Message> historique = messageRepository.getHistorique(moi, ami);
                    for (Message m : historique) {
                        if (m.getSender().getUsername().equals(client.getUsername())) {
                            afficherMessage("Moi:", m.getContenu(), true);
                        } else {
                            afficherMessage(m.getSender().getUsername(), m.getContenu(), false);
                        }
                    }
                }
            }
        });
    }

    private MessageRepository messageRepository = new MessageRepository();

    @FXML
    public void gererEnvoie() {
        String contenu = messageField.getText();

        if (selectedUser == null) {
            chatWithLabel.setText("Sélectionne d'abord un utilisateur !");
            return;
        }
        if (contenu.isEmpty()) return;


        client.envoieMessage(selectedUser, contenu);


        afficherMessage("Moi:", contenu, true);
        messageField.clear();
    }


    @FXML
    public void gererDeconnection() {
        new Thread(() -> {
            client.closeTt();
        Platform.runLater(()->{
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Connexion.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) messageField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
                        });
        }).start();
    }

    //pour avoir le client en ligne ou bien en hors ligne
    private void configurerListView() {
        usersListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> lv) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String username, boolean empty) {
                        super.updateItem(username, empty);
                        if (empty || username == null) {
                            setText(null);
                            setGraphic(null);
                            setStyle("-fx-background-color: transparent;");
                        }  else if (username.contains("🟢")) {
                            setText("●  " + username.replace("🟢", "").trim());
                            setStyle("-fx-background-color: #2e2e3e; -fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10;");
                        } else {
                            setText("●  " + username.trim());
                            setStyle("-fx-background-color: #2e2e3e; -fx-text-fill: gray; -fx-font-size: 14px; -fx-padding: 10;");
                        }

                    }};}})
        ;}
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