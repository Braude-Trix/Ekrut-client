package logic;

import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import models.IRequest;
import models.Method;
import models.Request;

public class SubscriberController {
	

	public static List<Subscriber> getAllSubscribers()
	{
		List<Subscriber> arr = new ArrayList<Subscriber>();
		arr.add(new Subscriber("sss","xxx","sss","333", "asdasdas", "aaaa", "sssss"));
		arr.add(new Subscriber("ss3223s","x232xx","s232ss","331113", "asd22asdas", "aaa33a", "ssstgss"));
		return arr;
		
	}
	
	public static void editSubscriber(Subscriber NewSub)
	{ 
		List<Object> lst = new ArrayList<>();
		lst.add(NewSub);
		Request request = new Request();
		request.setPath("/UpdateSubscriber");
		request.setMethod(Method.PUT);
		request.setBody(lst);
		ClientUI.chat.accept(request);


	}

}
