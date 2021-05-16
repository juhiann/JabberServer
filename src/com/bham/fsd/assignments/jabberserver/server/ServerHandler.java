package com.bham.fsd.assignments.jabberserver.server;

import com.bham.fsd.assignments.jabberserver.JabberMessage;
import com.bham.fsd.assignments.jabberserver.controller.JabberController;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ListIterator;

/**
 * @author Juhi Jose
 */
public class ServerHandler implements Runnable
{
    private Server server;
    private Socket socket;
    private boolean shouldRun = true;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int clientID;
    private String username;

    /**
     * Constructor
     *
     * @param socket
     * @param server
     */
    public ServerHandler(Socket socket, Server server, int id)
    {
        this.socket = socket;
        this.server = server;
        this.clientID = id;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @param jmessage
     */
    private void sendResponseJabberMessageToClient(JabberMessage jmessage)
    {
        System.out.println("[SERVER]: " + jmessage.getMessage());
        try {
            out.flush();
            out.writeObject(jmessage);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param jmessage
     */
    public void sendResponseJabberMessageToAllClients(JabberMessage jmessage)
    {
        for (ServerHandler i : server.getClientThreadList())
        {
            System.out.println("Client ID: "+ i.clientID);
            i.sendResponseJabberMessageToClient(jmessage);
        }
    }

    /**
     *  A thread for each client
     */
    public void run()
    {
        int count = 1;
        JabberMessage inMsg = null;
        JabberMessage outMsg = null;

        try{
            while(shouldRun && in.available()==0)
            {
                try {
                    inMsg = (JabberMessage)in.readObject();
                } catch (SocketException | EOFException e)
                {
                    System.out.println("[SERVER]: Client " + this.clientID + " Disconnected");
                    break;
                } catch (IOException | ClassNotFoundException ee) {
                    ee.printStackTrace();
                    break;
                } catch (Exception allE) {
                    break;
                }

                System.out.println("[SERVER]: Message: " + count);
                count++;

                JabberController.processRequest(inMsg, getUsername());

                if (!inMsg.getMessage().equals("signout"))
                {
                    outMsg = JabberController.getResponseJabberMessage();
                    if (!outMsg.getMessage().equals("no_response"))
                    {
                        sendResponseJabberMessageToAllClients(outMsg);
                    }
                }
                else {
                    ListIterator<ServerHandler> iter = server.getClientThreadList().listIterator();
                    while(iter.hasNext()){
                        if(iter.next() == this){
                            iter.remove();
                        }
                    }
                    closeServerConnection();
                }
                System.out.println("***********************************\n");
            }
            closeServerConnection();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            closeServerConnection();
        }
    }

    public void closeServerConnection()
    {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}