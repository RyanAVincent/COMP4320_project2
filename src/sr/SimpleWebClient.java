package sr;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class SimpleWebClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			InetAddress dest = InetAddress.getByName("");
			sendHTTPRequest(dest);
		} catch (UnknownHostException e) {
			System.out.println(e.toString());
		}
		
		//...Gremlin stuff over here as well TODO -- Jared
		
		//...
		ByteBuffer webpage = ByteBuffer.allocate(0);
		
		displayWebpage(webpage);
	}
	
	private static void sendHTTPRequest(InetAddress dest)
	{
		//TODO
	}
	
	private static void displayWebpage(byte[] webpage)
	{
		//TODO
	}
	
	private static void displayWebpage(ByteBuffer webpage)
	{ displayWebpage(webpage.array()); }

}
