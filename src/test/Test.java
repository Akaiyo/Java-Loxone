package test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import loxone.ConnectionCloseEvent;
import loxone.ConnectionCloseHandler;
import loxone.ErrorHandler;
import loxone.LoxoneConnection;
import loxone.StatusUpdate;
import loxone.StatusUpdateListener;
import loxone.request.Request;
public class Test {
	public static void main(String[] args) throws URISyntaxException, InterruptedException{
		
		/*
		 * Your loxone server
		 */
		URI uri = new URI("ws://10.2.2.222/ws");
		
		/*
		 * Basic auth 
		 * user credentials
		 */
		String user = "user";
		String password = "password";
				
		
		
		LoxoneConnection lc = new LoxoneConnection(uri, user, password);
		
		
		
		/*
		 * A normal Listener that gets
		 * triggered on every StatusUpdate
		 * (You could also use lambdas)
		 */
		lc.addStatusUpdateListener(new StatusUpdateListener() {
			@Override
			public void onStatusUpdate(StatusUpdate statusUpdate) {
				System.out.println("StatusUpdateEvent: " + statusUpdate.toString());
			}
		});
		
		
		
		/*
		 * A special Listener that only triggers 
		 * on changes for uuid 0c438bf0-01bc-0adb-ffff-01223344556
		 * (You could also use lambdas)
		 */
		lc.addStatusUpdateListener(new StatusUpdateListener() {
			
			@Override
			public void onStatusUpdate(StatusUpdate statusUpdate) {
				System.out.println("I am a special snowflake!! ^.^");
			}
		}, UUID.fromString("0c438bf0-01bc-0adb-ffff-012233445566"));
		
		
		
		/*
		 * ErrorHandler that gets called when there occures an exception in the connection
		 */
		lc.setErrorHandler(System.err :: println);
		/*
		lc.setErrorHandler(exception -> System.err.println(exception));
		*/
		
		/*
		lc.setErrorHandler(new ErrorHandler() {
			@Override
			public void onError(Exception exception) {
				System.err.println(exception);
			}
		});
		*/
		
		/*
		 * CloseHandler that gets called when the connection is Closed
		 */
		lc.setConnectionCloseHandler(event -> System.out.println(event.toString()));
		
		
		
		/*
		 * Open the connection to the loxone miniserver
		 */
		lc.connect();
		

		/*
		 * Your normal application logic here
		 */
		Thread.sleep(200);
		lc.sendRequest(Request.State);
		
		/*
		 * Connection open for 60 seconds
		 */
		Thread.sleep(60000);
		lc.disconnect();
	}
}
