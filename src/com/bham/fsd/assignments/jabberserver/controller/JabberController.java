package com.bham.fsd.assignments.jabberserver.controller;

import com.bham.fsd.assignments.jabberserver.JabberMessage;
import com.bham.fsd.assignments.jabberserver.server.JabberDatabase;
import java.util.ArrayList;

public class JabberController
{
    private static JabberDatabase jdb = new JabberDatabase();
    private static JabberMessage responseJabberMessage;
    private static final String[] INCOME_MESSAGES =
            {
                    "signin",
                    "register",
                    "signout",
                    "timeline",
                    "users",
                    "post",
                    "like",
                    "follow"
            };
    private static final String[] RESPONSES =
            {
                    "signedin",
                    "unknown-user",
                    "timeline",
                    "users",
                    "posted"
            };

    /**
     * checks if message is valid
     * @param jMsg
     */
    public static boolean isValidJabberMessage(JabberMessage jMsg)
    {
        for (String msg : JabberController.INCOME_MESSAGES)
        {
            if (jMsg.getMessage().split(" ")[0].equals(msg)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param jmsg
     */
    public static void processRequest(JabberMessage jmsg, String username)
    {
        String prefix = "";
        String suffix = "";
        StringBuilder jab = new StringBuilder("");

        try {
            prefix = jmsg.getMessage().split(" ")[0];
            suffix = jmsg.getMessage().split(" ")[1];
        } catch (ArrayIndexOutOfBoundsException e)
        {
            prefix = jmsg.getMessage();
        }

        if(isValidJabberMessage(jmsg))
        {
            switch (prefix)
            {
                case ("signin"):
                    int response = jdb.getUserID(suffix);
                    if(response < 0) {
                        responseJabberMessage = new JabberMessage(RESPONSES[1]); //unsuccessful
                        System.out.println("[DATABASE]: User is INVALID");
                    } else {
                        responseJabberMessage = new JabberMessage(RESPONSES[0]); //successful
                        System.out.println("[DATABASE]: User is VALID");
                    }
                    break;

                case ("register"):
                    response = jdb.getUserID(suffix);
                    if(response > 0) {
                        responseJabberMessage = new JabberMessage(RESPONSES[1]); // Already exists
                        System.out.println("[DATABASE]: User already registered");
                    } else {
                        jdb.addUser(suffix, (suffix+"@email.com"));
                        responseJabberMessage = new JabberMessage(RESPONSES[0]); //user added and signedin
                        System.out.println("[DATABASE]: User added to DB");
                    }
                    break;

                case ("timeline"):
                    ArrayList<ArrayList<String>> rtimeline = jdb.getTimelineOfUserEx(username);
                    responseJabberMessage = new JabberMessage(RESPONSES[2], rtimeline);
                    // timeline of user as a response
                    break;

                case ("users"):
                    ArrayList<ArrayList<String>> rusers = jdb.getUsersNotFollowed(jdb.getUserID(username));
                    responseJabberMessage = new JabberMessage(RESPONSES[3], rusers); // timeline of user as a response
                    break;

                case ("like"):
                    jdb.addLike(jdb.getUserID(username), Integer.parseInt(suffix));
                    responseJabberMessage = new JabberMessage(RESPONSES[4]); // "posted"
                    break;

                case ("post"):
                    String[]  jabMessageParts = jmsg.getMessage().trim().split(" ");
                    if (jabMessageParts.length > 1)
                    {
                        for (int i = 1; i < jabMessageParts.length; i++)
                        {
                            jab.append(jabMessageParts[i] + " ");
                        }
                    }
                    suffix = jab.toString().trim();
                    jdb.addJab(username, suffix);
                    responseJabberMessage = new JabberMessage(RESPONSES[4]); // "posted"
                    break;

                case ("signout"):
                    responseJabberMessage = new JabberMessage("no_response");
                    break;

                default:
                    responseJabberMessage = new JabberMessage("no_response");
                    System.out.println("[SERVER]: Invalid message");
                    break;
            }
        }
    }
    public static JabberMessage getResponseJabberMessage() {
        return responseJabberMessage;
    }
}