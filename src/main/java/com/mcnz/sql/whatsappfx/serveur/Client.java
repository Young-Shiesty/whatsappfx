package com.mcnz.sql.whatsappfx.serveur;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

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

            return (con == null) ? false : true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void EcouterMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String message = bufferedReader.readLine();
                    if (message == null) break;
                    System.out.println("" + message);
                } catch (IOException e) {
                    closeTt();
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

    public void closeTt() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Username : ");
        String username = scanner.nextLine();
        System.out.print("Password : ");
        String password = scanner.nextLine();

        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);

        // Se connecter
        if (!client.login(password)) {
            System.out.println("Login ou password incorrect.");
            return;
        }
        client.EcouterMessage();

        while (socket.isConnected()) {
            System.out.println("Destinataire :");
            String destinataire = scanner.nextLine();
            System.out.println("Ton message :");
            String contenu = scanner.nextLine();
            client.envoieMessage(destinataire, contenu);
        }
    }
}
