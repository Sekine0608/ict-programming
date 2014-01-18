package jec.CM12sekine.packetflower.packet ;
import java.util.ArrayList;

public class IPv4Packet extends Packet{

	public static final int VERSION = 0 ;
	public static final int IHL = 1; 
	public static final int TYPE_OF_SERVICE = 2 ;
	public static final int TOTAL_LENGTH = 3 ;
	public static final int INDENTIFICATION = 4 ;
	public static final int FLAG_AND_FRAGMENT_OFFSET = 5 ;
	public static final int TIME_TO_LIVE = 6 ;
	public static final int PROTOCOL = 7 ;
	public static final int HEADER_CHECKSUM = 8 ;
	public static final int SOURCE_ADDRESS = 9 ;
	public static final int DESTINATION_ADDRESS = 10 ;
	public static final int OPTIONS_AND_PADDING = 11 ; 
	
	public static int[] headerAreaNibbleLength ={
		1,1,2,4,4,4,2,2,4,8,8,-1
	} ;
	
	public static String[] headerName= {
		"version", "ihl", "type of service", "total length", "identification", "flag and fragment offset",
		"ttl", "protcol", "header checksum", "source addredd", "destination address", "option and padding"
	} ;
	

	public int getType(){
		return IPv4 ;
	}
	
	
	public void addHeaderAreas(String[] headerArea){
		for(String tmp: headerArea){
			headers.add(tmp) ;
		}
	}
	
	public void setHeader(String header){
		this.header = header ;
	}
	
	public static String[] splitForArea(String header, int optionsAndPadding){
		String[] headerArea = new String[12] ;
		int offset = 0 ;
		for(int i=0 ; i<IPv4Packet.headerAreaNibbleLength.length ; i++){
			int areaLength = 0 ;
			if(i!=11){
				areaLength = headerAreaNibbleLength[i] ;
			}else{ 
				areaLength = optionsAndPadding ;	
			}
			headerArea[i] = header.substring(offset, offset+areaLength) ;
			offset += headerAreaNibbleLength[i] ;
		}

		return headerArea ;
	}
	
}
