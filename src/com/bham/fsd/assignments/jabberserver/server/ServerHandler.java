package com.bham.fsd.assignments.jabberserver.server;

import com.bham.fsd.assignments.jabberserver.JabberMessage;
import com.bham.fsd.assignments.jabberserver.controller.JabberController;

import java.io.*;
import java.net.Socket;

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

    /**
     * Constructor
     *
     * @param socket
     * @param server
     */
    public ServerHandler(Socket socket, Server server)
    {
        this.socket = socket;
        this.server = server;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        JabberMessage outMsg = outMsg = new JabberMessage("signedin");
        try {
            while(shouldRun)
            {
//                while (in.available() != 0) //(JabberMessage)in.readObject() == null)
//                {
//                    System.out.println("[SERVER]: WAITING...");
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                try {
                    inMsg = (JabberMessage)in.readObject();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                catch(ClassNotFoundException ee) {
                    ee.printStackTrace();
                    break;
                }

                System.out.println("[SERVER]: Message: " + count);
                count++;

                JabberController.
                        processRequest(inMsg);
                outMsg = JabberController.getResponseJabberMessage();
                if (!outMsg.getMessage().equals("no_response"))
                {
                    sendResponseJabberMessageToAllClients(outMsg);
                }

                System.out.println("***********************************");
                System.out.println();
            }
            in.close();
            out.close();
            closeServerConnection();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerConnection()
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
