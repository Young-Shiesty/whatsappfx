package com.mcnz.sql.whatsappfx.serveur;


import java.io.*;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    //la fonction callbak pour avoir celui qui envoie le mess et ce quil a envoye
    private BiConsumer<String, String> MessageRecu;
    //la fonction callbak pour verifier si le user c connecter
    private Consumer<String> UserConnecter;
    private Consumer<String> UserDeConnecter;

    public void MessageRecu(BiConsumer<String, String> callback) {
        this.MessageRecu = callback;
    }

    public void UserConnecter(Consumer<String> callback) {
        this.UserConnecter = callback;
    }

    public void UserDeConnecter(Consumer<String> callback) {
        this.UserDeConnecter = callback;
    }

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeTt();
        }
    }

    public boolean login(String password) {
        try {
            // Envoyer username et password
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            bufferedWriter.write(password);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String con = bufferedReader.readLine();

            if (con != null && con.startsWith("OK")) {
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public  String getUsername(){
        return username;
    }

    private boolean rester;

    public void EcouterMessage() {
        //pour ecouter en arriere plan
        new Thread(() -> {
            while (rester && socket!=null && !socket.isClosed()) {
                try {
                    String envoyeur = bufferedReader.readLine();
                    String contenu = bufferedReader.readLine();

                    if (envoyeur == null || contenu == null) break;
                    if (envoyeur.equals("SERVEUR") && contenu.contains("est en ligne")) {
                        String username = contenu.replace(" est en ligne", "").trim();
                        if (UserConnecter != null) UserConnecter.accept(username);
                    } else if (envoyeur.equals("SERVEUR") && contenu.contains("est hors ligne")) {
                        String username = contenu.replace(" est hors ligne", "").trim();
                        if (UserDeConnecter != null) UserDeConnecter.accept(username);
                    } else {
                        if (MessageRecu != null) MessageRecu.accept(envoyeur, contenu);
                    }

                } catch (IOException e) {
                    System.out.println("Connexion perdue !");
                    break;
                }
                if(rester==false){
                    break;
                }
            }
        }).start();
    }

    public void envoieMessage(String destinataire, String contenu) {
        try {
            bufferedWriter.write(destinataire);
            bufferedWriter.newLine();
            bufferedWriter.write(contenu);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeTt();
        }
    }

    public void closeTt(){
        rester = false;
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}