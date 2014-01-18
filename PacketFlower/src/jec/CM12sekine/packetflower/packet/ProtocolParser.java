package jec.CM12sekine.packetflower.packet ;
import java.util.ArrayList;

import android.util.Log;


public class ProtocolParser {
	private static final int PACKETTYPE_ID_IPv4 = 0 ;
	private static final int PACKETTYPE_ID_IPv6 = 1 ;
	
	private static final String MARK_IPv4 = "080045" ;
	private static final String MARK_IPv6 = "XXXXX" ; //TODO ���ipv6��������m���ȕ������T��
	private static final int MARK_DIST_IPv4 = 4 ;
	private static final int MARK_DIST_IPv6 = 00000 ;
	private static boolean endParse = false ;
	
	private static ArrayList<Packet> packet = null ;
	private static int unusedIndex = 0 ;
	
	public static Packet[] getPackets(){
		Packet[] tmp = (Packet[]) packet.toArray(new Packet[packet.size()]) ;
		packet = null ;
		return tmp ;
	}
	
	public static int getUnusedIndex(){
		int tmp = unusedIndex ;
		unusedIndex = 0 ;
		return tmp ;
	}
	
	public static void parsePacket(String data){
		endParse = false ;
		if(packet != null || unusedIndex != 0){
			throw new IllegalStateException("�O��̉�͌��ʂ���o���Ă��Ȃ�") ;
		}
		packet = new ArrayList<Packet>() ;
		unusedIndex = 0 ;
		
		int firstTargetLength = data.length() ;
		
		int ipv4markIndex = -1 ;
		int ipv6markIndex = -1 ;
		while(!endParse && (ipv4markIndex = data.indexOf(MARK_IPv4)) != -1 || (ipv6markIndex = data.indexOf(MARK_IPv6)) != -1 ){
				int ipv4Index = -1 ;
				if(ipv4markIndex != -1){
					ipv4Index = ipv4markIndex+MARK_DIST_IPv4 ;
				}
				int ipv6Index = -1 ;
				if(ipv6markIndex != -1){
					ipv6Index = ipv6markIndex+MARK_DIST_IPv6 ;
				}

				int frontPacketType = askMinimamElementIndex(new int[]{ipv4Index, ipv6Index}) ;	
				switch(frontPacketType){
				case PACKETTYPE_ID_IPv4: 
                        data = parseIPv4(data, ipv4Index) ;
				break ;
				
				case PACKETTYPE_ID_IPv6: 
                        data = parseIPv6(data, ipv6Index) ;
				break ;	
				default:
					Log.v("error", "unknownPacket") ;
				}
			
		}
		unusedIndex = firstTargetLength - data.length() ;
	}
	
	private static int askMinimamElementIndex(int[] numbers){
		int[] indexes = new int[numbers.length] ;
		for(int i=0 ; i<indexes.length ; i++){
			indexes[i] = i ;
		}
        for(int i=0;i<numbers.length-1;i++){

                // 下から上に順番に比較します
                for(int j=numbers.length-1;j>i;j--){

                        // 上の方が大きいときは互いに入れ替えます
                        if(numbers[j]<numbers[j-1]){
                                int t=numbers[j];
                                numbers[j]=numbers[j-1];
                                numbers[j-1]=t;

                                int t2=indexes[j];
                                indexes[j]=indexes[j-1];
                                indexes[j-1]=t2;
                        }
                }
        }
        int result = -1 ;
        for(int i=0 ; i<numbers.length ; i++){
        	if(numbers[i]!=-1){
        		result = indexes[i] ; 
        		break; 
        	}
        }
		return result;
	}
	private static String parseIPv4(String data, int ipv4index){
		int optionAndPadding = 0 ;
		
		String ihl = data.substring(ipv4index+1,ipv4index+2) ;
		int headerLength = Integer.parseInt(ihl,16)*4*2 ;
		int headerEndIndex = ipv4index + headerLength ;

		optionAndPadding = headerLength/8 - 5;
		if(headerEndIndex > data.length()){
			endParse = true ;
			return data ;
		}
		IPv4Packet ipv4packet = new IPv4Packet() ;
		ipv4packet.addHeaderAreas(IPv4Packet.splitForArea(data.substring(ipv4index, headerEndIndex), optionAndPadding)) ;
	
		ipv4packet.setHeader(data.substring(ipv4index, headerEndIndex));

		data = data.substring(headerEndIndex) ;
		packet.add(ipv4packet) ;
		return data ; 
	}
	private static String parseIPv6(String data, int ipv6index){
		return null ;//��͌���
	}
	
	
}
