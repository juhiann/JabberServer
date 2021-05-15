package com.bham.fsd.assignments.jabberserver.controller;

import com.bham.fsd.assignments.jabberserver.JabberMessage;
import com.bham.fsd.assignments.jabberserver.server.JabberDatabase;

public class IncomeMessageController
{
    private static JabberDatabase jdb = new JabberDatabase();

    private static final String[] INCOME_MESSAGES =
            {
                    "signin",
                    "register",
                    "signout",
                    "timeline"
            };

    public static void isValidMessage(JabberMessage jMsg)
    {
        String valid = "[SERVER]: NOT valid message!";
        for (String msg : IncomeMessageController.INCOME_MESSAGES)
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

    public static void sendRequestToDatabase(JabberMessage jMsg)
    {
        int response = jdb.getUserID(jMsg.getMessage().split(" ")[1]);
        if(response < 0)
        {
            System.out.println("[DATABASE]: User is INVALID");
        }
        else
            System.out.println("[DATABASE]: User is VALID");
    }
}