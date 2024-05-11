package Messaging;




import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;


@Startup
@Singleton
public class JMSClient {
	
	
	@Inject
	JMSContext context;
	@Resource(mappedName="java:jboss/exported/jms/queue/MyTrelloQueue")
	private Queue MyTrelloQueue;

	
	public void sendMessage(String msg)
	{
		try{JMSProducer producer=context.createProducer();
		TextMessage message = context.createTextMessage(msg);
		producer.send((Destination) MyTrelloQueue, message);
		System.out.println(msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public String getMessage()
	{
		JMSConsumer consumer = context.createConsumer((Destination) MyTrelloQueue);

		try{
		TextMessage message = (TextMessage)consumer.receive(1000);
		if(message!=null)
		{
			System.out.println("message recieved:"+message);
			return message.getBody(String.class);
		}
		else
		{
			return null;
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
			
		}
		finally
		{
			consumer.close();
		}
		
	}

}