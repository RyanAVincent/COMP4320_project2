package sr;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Packet {

	//Members ---------------------------------------------------------------------
		
		private static int DEFAULT_DATA_SIZE = 256;
		
		public static enum HEADER_ELEMENTS
		{
			SEQUENCE_NUMBER,
			CHECKSUM,
			LENGTH,
			FLAGS
		}
		
		//DATA ----------------------------------------------------------
			//Data held by this packet; 
			//not to exceed (packet size - header size)
			private byte[] m_data;
			
		//HEADER --------------------------------------------------------
		//IMPORTANT! To add an header item:
		//	Create constant string to represent in map
		//  Add item into enumeration above
		//  Add switch statements to get/set below
		//  !UPDATE HEADER SIZE BASED ON # OF BYTES!
			
			//Header is a map from strings to strings
			private Map<String, String> m_header;
			
			//Private constant to map segment number
			private static final String HEADER_SEGMENT_NUM = "seqNum";
			private static final String HEADER_CHECKSUM = "checkSum";
			private static final String HEADER_LENGTH = "length";
			private static final String HEADER_FLAGS = "flags";
			
	//General Methods ---------------------------------------------------------------------
		
		//Constructor for a Packet that initializes members
		public Packet() 
		{
			//Initialize data array
			m_data = new byte[DEFAULT_DATA_SIZE];
			
			//Initialize Map (using HashMap because it's fun)
			m_header = new HashMap<String, String>();
		}
		
		//Function simply displays all of the key-value pairs in the map
		//as well as all of the bytes of data (as numerical values from
		//0 - 255).
		public void display()
		{
			System.out.println("HEADER------------------------------");
			Iterator<Entry<String, String>> it = m_header.entrySet().iterator();
			while (it.hasNext())
			{
				Entry<String,String> n = it.next();
				System.out.println((n.getKey()) + " : " + (n.getValue()));
			}
			System.out.println("DATA--------------------------------");
			for (int i = 0; i < m_data.length; i++)
				System.out.print(m_data[i] + " ");
			System.out.println("");
		}
		
		//Increases data of the array by adding the default data size
		//to what is already allocated.
		private void grow()
		{
			byte[] temp = m_data;
			m_data = new byte[m_data.length + DEFAULT_DATA_SIZE];
			for (int i = 0; i < temp.length; i++)
				m_data[i] = temp[i];
		}

	//Header Methods --------------------------------------------------------------
		
		//Sets a key-value pair in the header of this packet. The keys are
		//governed by our enumeration, HEADER_ELEMENTS, such that the
		//packets all have the exact same headers.
		public void setHeaderValue(HEADER_ELEMENTS ele, String value)
		{
			switch (ele)
			{
				case SEQUENCE_NUMBER:
					m_header.put(HEADER_SEGMENT_NUM, value);
					break;
				case CHECKSUM:
					m_header.put(HEADER_CHECKSUM, value);
					break;
				case LENGTH:
					m_header.put(HEADER_LENGTH, value);
					break;
				case FLAGS:
					m_header.put(HEADER_FLAGS, value);
					break;
				default: //should never get here...
					throw new IllegalArgumentException("HOW DID THIS HAPPEN!?");
			}
		}
		
		//Gets one of the values given the key governed by our
		//enumeration, HEADER_ELEMENTS.
		public String getHeaderValue(HEADER_ELEMENTS ele)
		{
			switch (ele)
			{
				case SEQUENCE_NUMBER:
					return m_header.get(HEADER_SEGMENT_NUM);
				case CHECKSUM:
					return m_header.get(HEADER_CHECKSUM);
				case LENGTH:
					return m_header.get(HEADER_LENGTH);
				case FLAGS:
					return m_header.get(HEADER_FLAGS);
				default: //should never get here...
					throw new IllegalArgumentException("HOW DID THIS HAPPEN!?");
			}
		}

	//Data Methods ----------------------------------------------------------------
		
		//Resets the data array to default size and values
		public void resetData()
		{
			m_data = new byte[DEFAULT_DATA_SIZE];
		}
		
		//Allows user to set a singular element in the data
		//array, given that the index is within bounds.
		//Throws IndexOutOfBoundsException if not.
		public void setData(int index, byte val)
		{
			if (index >= 0)
			{
				while (index > m_data.length)
					grow();
				m_data[index] = val;
			}
			else
				throw new IndexOutOfBoundsException(
					"PACKET -- SET DATA; index = " + index);
		}
		
		//Takes an array of bytes to be set as the data segment.
		//If the Packet contains data already, the data is overwritten.
		//Throws IllegalArgumentException if the size of toSet does not
		//conform with the size of the data segment in the packet.
		public void setData(byte[] toSet) throws IllegalArgumentException
		{
			int argSize = toSet.length;
			if (argSize > 0)
			{
				m_data = new byte[argSize];
				for (int i = 0; i < m_data.length; i++)
					m_data[i] = toSet[i];
			}
			else
				throw new IllegalArgumentException(
					"PACKET -- SET DATA; toSet.length = " + toSet.length);
		}
		
		public byte getData(int index)
		{
			if (index >= 0 && index < m_data.length)
				return m_data[index];
			throw new IndexOutOfBoundsException(
					"PACKET -- GET DATA; index = " + index);
		}
		
		public byte[] getData()
		{
			return m_data;
		}
		
		public int getDataSize()
		{
			return m_data.length;
		}
		
		/**
		 * Returns the contents of this packet as a DatagramPacket
		 * @param i the IP Address to give to the DatagramPacket
		 * @param port the port number to give to the DatagramPacket
		 * @return returns the DatagramPacket fully ready to be sent
		 */
		public DatagramPacket getDatagramPacket(InetAddress i, int port)
		{
			int length = Integer.parseInt(m_header.get(HEADER_LENGTH));
			byte[] setData = ByteBuffer.allocate(length)
					.putShort(Short.parseShort(m_header.get(HEADER_SEGMENT_NUM)))
					.putShort(Short.parseShort(m_header.get(HEADER_CHECKSUM)))
					.putShort(Short.parseShort(m_header.get(HEADER_LENGTH)))
					.putShort(Short.parseShort(m_header.get(HEADER_FLAGS)))
					.put(m_data)
					.array();
			
			return new DatagramPacket(setData, setData.length, i, port);
		}
		
		public static Packet createPacket(DatagramPacket in)
		{
			Packet newPacket = new Packet();
			ByteBuffer bb = ByteBuffer.wrap(in.getData());
			newPacket.setHeaderValue(HEADER_ELEMENTS.SEQUENCE_NUMBER, bb.getShort()+"");
			newPacket.setHeaderValue(HEADER_ELEMENTS.CHECKSUM, bb.getShort()+"");
			newPacket.setHeaderValue(HEADER_ELEMENTS.LENGTH, bb.getShort()+"");
			newPacket.setHeaderValue(HEADER_ELEMENTS.FLAGS, bb.getShort()+"");
			
			byte[] inData = in.getData();
			byte[] remaining = new byte[inData.length - bb.position()];
			for (int i = 0; i < remaining.length; i++)
				remaining[i] = inData[i+bb.position()];
			newPacket.setData(remaining);
			return newPacket;
		}
		
	}