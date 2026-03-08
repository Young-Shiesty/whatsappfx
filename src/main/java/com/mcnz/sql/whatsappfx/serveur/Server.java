package com.mcnz.sql.whatsappfx.serveur;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public void startServer(){
        //pour le serveur roule non-stop
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("Nouveau client");
                //pour gerer les clients
                ClientHandler clientHandler = new ClientHandler(socket);
                //pour gerer les threads
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
