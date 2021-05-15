package com.bham.fsd.assignments.jabberserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
    private ServerSocket listener;
    private final int PORT = 44444;
    private ArrayList<ServerHandler> clientThreadList = new ArrayList<>();
    private boolean shouldRun = true;
    private int clientID;

    public Server()
    {
        clientID = 1;
        try {
            listener = new ServerSocket(PORT);
//            listener.setSoTimeout(300);
            int clientCount = 0;
            System.out.println("[SERVER]: Waiting for connections... ");
            while (shouldRun)
            {
                Socket socket = listener.accept();
                clientCount++;
                System.out.println("[SERVER]: Client [" + clientCount + "] connected! ");
                ServerHandler sh = new ServerHandler(socket, this, clientID);
                new Thread(sh).start();
                clientThreadList.add(sh);
                clientID++;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ServerHandler> getClientThreadList() {
        return this.clientThreadList;
    }

}
