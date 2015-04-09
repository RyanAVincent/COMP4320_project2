package sr;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class SimpleWebServer {

	
	public static final int PACKET_SIZE = 512; //bytes
	public static final int HEADER_SIZE = 8; //bytes
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		byte[] receiveData = new byte[1024]; 
		
		try {
			//Set up server socket to listen on port 10038
			DatagramSocket serverSocket = new DatagramSocket(10038);
				
			//Listen for request packets...
			DatagramPacket receivePacket = 
					new DatagramPacket(receiveData, receiveData.length); 
			
			//(blocks until received packet)
			serverSocket.receive(receivePacket);
			
			//Process request packet
			processHTTPRequest(receivePacket); //TODO -- Ryan
			
			//TODO -- Ryan
			byte[] buffer = {}; //some buffer with file's contents...
			
			//BEGINNING TRANSPORT LAYER PROTOCOL ------------------------------
			//Create the SAR unit to split into packets that include header
			//items for the sequence number as well as length
			SegmentReassembleUnit sar = new SegmentReassembleUnit(
									PACKET_SIZE, HEADER_SIZE);
			ArrayList<Packet> packets = sar.segment(buffer);
			
			//Create Error detection unit and generate checksums to be added
			//to the headers of all the packets
			ErrorDetectionUnit edu = new ErrorDetectionUnit();
			packets = edu.generateCheckSum(packets); //TODO -- Jared
			
			//Create our selective repeat unit and begin transporting packets
			//to destination
			SelectiveRepeatUnit sru = new SelectiveRepeatUnit();
			//sru.send(packets); //TODO -- Adam
			
			//-----------------------------------------------------------------
			serverSocket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void processHTTPRequest(DatagramPacket req)
	{
		//TODO
	}

}
