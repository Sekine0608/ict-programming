package jec.CM12sekine.packetflower.packet ;
import java.util.ArrayList;

public abstract class Packet {
	public static final int EOF = -1 ;
	public static final int IPv4 = 1 ;
	public static final int IPv6 = 2 ;
	
	protected ArrayList<String> headers = new ArrayList<String>() ;
	protected String header = new String() ;
	public String[] getHeaders(){
		return (String[]) headers.toArray(new String[headers.size()]) ;
	}
	public String getHeader(){
		return header ;
	}
	public int getType(){
		return 0 ;
	}
}
