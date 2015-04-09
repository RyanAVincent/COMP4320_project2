package sr;

import java.util.ArrayList;

public class SegmentReassembleUnit {
	private int m_headersize = 0;
	private int m_datasize = 0;
	private int m_packetsize = 0;
	
	public SegmentReassembleUnit(int packetSize, int headerSize) {
		if (headerSize >= 0)
			m_headersize = headerSize;
		else
			throw new IllegalArgumentException();
		
		if (packetSize >= 0 && packetSize > headerSize)
			m_packetsize = packetSize;
		else
			throw new IllegalArgumentException();
		
		m_datasize = m_packetsize - m_headersize;
	}
	
	public ArrayList<Packet> segment(byte[] buffer) {
		
		ArrayList<Packet> ret = new ArrayList<Packet>();
		
		int length = buffer.length;
		if (length == 0)
			throw new IllegalArgumentException("Segment: File Empty");
		
		int counter = 0; //counts bytes in byte array parameter
		int segmentNumber = 0; //counts what segment number we are on
		while (counter < length)
		{
			Packet next = new Packet();
			byte[] nextsData = new byte[m_datasize];
			
			//How much can we read from the packet
			//(initially as much as possible)
			int amtToRead = m_datasize;
			//if we don't have enough for a full packet..
			if (length - counter < m_datasize)
				amtToRead = length - counter;
			//Copy over the data
			for (int i = 0; i < amtToRead; i++)
				nextsData[i] = buffer[counter + i];

			//Set data for packet, add to list, update counters
			next.setData(nextsData);
			next.setHeaderValue(Packet.HEADER_ELEMENTS.SEQUENCE_NUMBER, 
					segmentNumber+"");
			next.setHeaderValue(Packet.HEADER_ELEMENTS.LENGTH, 
					m_packetsize+"");
			ret.add(next);
			
			segmentNumber++;
			//increase counter by how much we read
			counter = counter + amtToRead;
		}
		
		return ret;
	}
	
	public byte[] reassemble(ArrayList<Packet> packets) {
    	int totalSize = 0;
    	for (int i = 0; i < packets.size(); i++)
    		totalSize += packets.get(i).getDataSize();
    	
    	byte[] ret = new byte[totalSize];
    	int retCounter = 0;
    	for (int i = 0; i < packets.size(); i++)
    	{
	    	//Do a boring linear search on the list for each packet number
	    	for (int j = 0; j < packets.size(); j++)
	    	{
	    		Packet check = packets.get(j);
	    		String segNum = check.getHeaderValue(
									Packet.HEADER_ELEMENTS.SEQUENCE_NUMBER);
	    		if (Integer.parseInt(segNum) == i)
	    		{
	    			for (int k = 0; k < check.getDataSize(); k++)
	    				ret[retCounter + k] = check.getData(k);
	    			retCounter += check.getDataSize();
	    			break;
	    		}
	    	}
    	}
    	
    	return ret;
    }

}
