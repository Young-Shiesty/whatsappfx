package com.mcnz.sql.whatsappfx.serveur;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            //ce que le client va utiliser pour  envoyee les messages
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //ce que le client va utiliser pour  recuperer les messages
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.clientUsername = bufferedReader.readLine();
            String password = bufferedReader.readLine();

            for (ClientHandler c : clientHandlers) {
                if (c.clientUsername.equals(this.clientUsername)) {
                    bufferedWriter.write("ERREUR :Ce User est deja connectee");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    closeTt();
                }
            }
            clientHandlers.add(this);
            System.out.println("[CONNEXION] " + clientUsername + " s'est connecté");
            bufferedWriter.write("OK");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            broadcastMessage("SERVEUR", clientUsername + " est en ligne");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
            String messageFromClient;
            while (socket.isConnected()) {
                try {
                    String destinataire = bufferedReader.readLine();
                    String contenu = bufferedReader.readLine();
                    if (destinataire == null || contenu == null) break;

                    if (contenu.isEmpty()) {
                        envoieMess("ERREUR", "Le message ne peut pas etre vide");
                        continue;
                    }
                    if (contenu.length() > 1000) {
                        envoieMess("ERREUR", "Message trop long");
                        continue;
                    }

                    System.out.println("[MESSAGE] " + clientUsername + " → " + destinataire + " : " + contenu);

                    boolean trouver = false;
                    for (ClientHandler c : clientHandlers) {
                        if (c.clientUsername.equals(destinataire)) {
                            c.envoieMess(clientUsername, contenu);
                            trouver = true;
                            break;
                        }
                    }
                    if (!trouver) {
                        envoieMess("SERVEUR", destinataire + " est hors ligne");
                    }
                } catch (IOException e) {
                    closeTt();
                    break;
                }
            }
        }
    public void envoieMess(String destinataire, String contenu) {
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

    public void broadcastMessage(String destinataire,String message) {
        for (ClientHandler c : clientHandlers) {
            if (!c.clientUsername.equals(clientUsername)) {
                c.envoieMess(destinataire,message);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        System.out.println("[DECONNEXION] " + clientUsername + " est deconnecte");
        broadcastMessage(clientUsername , " est hors ligne");
    }

    public void closeTt() {
        removeClientHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}