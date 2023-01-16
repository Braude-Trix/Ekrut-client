package logic;

import client.Client;
import client.ClientUI;
import models.Method;
import models.Request;
import models.ResponseCode;

import java.util.ArrayList;
import java.util.List;

public class Messages {
    /**
     * @param msg The message content to present in window
     * @param fromUserId The userId of the sender
     * @param toUserId The userId of the receiver
     */
    public static void writeNewMsgToDB(String msg, Integer fromUserId, Integer toUserId) {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/postMsg");
        request.setMethod(Method.POST);
        paramList.add(msg);
        paramList.add(fromUserId);
        paramList.add(toUserId);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        if (Client.resFromServer.getCode() != ResponseCode.OK) {
            System.out.println(Client.resFromServer.getDescription());
        }
    }
}
