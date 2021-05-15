package com.bham.fsd.assignments.jabberserver.controller;

import com.bham.fsd.assignments.jabberserver.JabberMessage;
import com.bham.fsd.assignments.jabberserver.server.JabberDatabase;

public class OutMessageController
{
    JabberDatabase jdb;

    private static final String[] INCOME_MESSAGES =
            {
                    "signin",
                    "register",
                    "signout",
                    "timeline"
            };

    public void isValidMessage(JabberMessage jMsg)
    {
        String valid = "[SERVER]: NOT valid message!";
        for (String msg : OutMessageController.INCOME_MESSAGES)
        {
            if (jMsg.getMessage().split(" ")[0].equals(msg)) {
                valid = "[SERVER]: Valid message!";
                System.out.println("[CLIENT]: " + jMsg.getMessage());
            }
        }
        System.out.println(valid);
        if (valid.equals("[SERVER]: Valid message!"))
        {
            sendRequestToDatabase(jMsg);
        }
    }

    public void sendRequestToDatabase(JabberMessage jMsg)
    {
        jdb = new JabberDatabase();
        int response = jdb.getUserID(jMsg.getMessage().split(" ")[1]);
        if(response < 0)
        {
            System.out.println("[SERVER]: User is NOT valid!!! ");
        }
        else
            System.out.println("[SERVER]: User is VALID in Database!!! ");
    }
}