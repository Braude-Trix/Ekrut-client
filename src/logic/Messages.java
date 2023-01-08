package logic;

import client.Client;
import client.ClientUI;
import models.Method;
import models.Request;
import models.ResponseCode;

import java.util.ArrayList;
import java.util.List;

public class Messages {
    public static void writeNewMsgToDB(String msg, Integer fromCustomerId, Integer toCustomerId) {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/postMsg");
        request.setMethod(Method.POST);
        paramList.add(msg);
        paramList.add(fromCustomerId);
        paramList.add(toCustomerId);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        if (Client.resFromServer.getCode() != ResponseCode.OK) {
            System.out.println("Some error occurred");
        }
    }
}
