package jec.CM12sekine.packetflower;

import java.util.ArrayList;

import android.util.Log;
import jec.CM12sekine.packetflower.fireworkelement.Firework;
import jec.CM12sekine.packetflower.fireworkelement.FireworkBotan;
import jec.CM12sekine.packetflower.fireworkelement.FireworkShidare;
import jec.CM12sekine.packetflower.fireworkelement.FireworkTail;
import jec.CM12sekine.packetflower.packet.IPv4Packet;
import jec.CM12sekine.packetflower.packet.Packet;
import jec.CM12sekine.packetflower.packet.ProtcolName;
import jec.CM12sekine.packetflower.util.Color;
import jec.CM12sekine.packetflower.util.SeedGenerator;

public class FireworkGenerator {
	private static SeedGenerator seedGenerator = SeedGenerator.getInstance() ;
	private static final String TAG = "FireworkGenerator" ; 

	public static FireworkDataSet[] generateForPacket(Packet[] packet){
		
		ArrayList<FireworkDataSet> arrayList = new ArrayList<FireworkDataSet>() ;
		for(int i=0 ; i<packet.length ; i++){
			Log.v(TAG, "" + packet[i]) ;
			FireworkDataSet[] fireworkDataSets = createFireworkForProtocolNumber(packet[i], i) ;
			if(fireworkDataSets != null && fireworkDataSets.length != 0){
				
				for(int j=0 ; j<fireworkDataSets.length ; j++){
					arrayList.add(fireworkDataSets[j]) ;
				}
			}
		}
		
		return (FireworkDataSet[])arrayList.toArray(new FireworkDataSet[0]) ;
	}
	
	private static FireworkDataSet[] createFireworkForProtocolNumber(Packet packet, int index){
		String parseString = iPv4String(packet) ;
		long seed = seedGenerator.seedGenerate(packet) ;
		
		String[] headers = packet.getHeaders() ;
		
		int totalLength = Integer.parseInt(headers[3],16) ;
		int ttl = Integer.parseInt(headers[6],16) ;
		int protcol = Integer.parseInt(headers[7],16); 
		int[] sourceIps = ipSplit(headers[9]) ;
		int[] destinationIps = ipSplit(headers[10]) ;
		
		FireworkDataSet[] fireworkDataSet = null ;
		switch(protcol){
		case 1: //ICMP
			fireworkDataSet = DataSetFactory.icmp(parseString, seed, totalLength, ttl, sourceIps, destinationIps, index) ;
		break ;

		case 6: //TCP
			fireworkDataSet = DataSetFactory.tcp(parseString, seed, totalLength, ttl, sourceIps, destinationIps, index) ;
		break ;

		case 17: //UDP
			fireworkDataSet = DataSetFactory.udp(parseString, seed, totalLength, ttl, sourceIps, destinationIps, index) ;
		break ;

		default: //etc...
			fireworkDataSet = DataSetFactory.etc(parseString, seed, totalLength, ttl, sourceIps, destinationIps, index) ;
		break ;
		
		}
		return fireworkDataSet ;
	}
	
	
	
	private static String iPv4String(Packet p){
		StringBuffer sb = new StringBuffer() ;
		String header = p.getHeader() ;
		String headers[] = p.getHeaders() ;
		for(int j=0 ; j<headers.length ; j++){
			String name = IPv4Packet.headerName[j]  ;
			String h = headers[j] ;
			switch(j){
			case 0: //version
				h = "" + Integer.parseInt(h,16)  ;
				break ;
			case 3: // total length
				h = "" + Integer.parseInt(h,16)  ;
				break ;
			case 4: //identification
				h = "" + Integer.parseInt(h,16) ;
				break ;
			case 6: //ttl
				h = "" + Integer.parseInt(h,16) ;
				break ;
			case 7: //protcol
				int pNum = Integer.parseInt(h,16) ;
				String pName = "" ; 
				if(pNum<=ProtcolName.protcol.length){
					pName = ProtcolName.protcol[pNum];   
				}
				if(!pName.equals("")){
					h = pNum + "(" + pName + ")" ;
				}else{
					h = "" +  pNum ;
				}
					
				break ;
			case 9: // source Address
				int[] sourceIps = ipSplit(h) ;
				h = sourceIps[0] + ". " + sourceIps[1] + ". " + sourceIps[2] + ". " + sourceIps[3]  ; 
				break ;
			case 10: // destination Address
				int[] destinationIps = ipSplit(h) ;
				h = destinationIps[0] + ". " + destinationIps[1] + ". " + destinationIps[2] + ". " + destinationIps[3]  ; 
				break ;
			default:
				name = "" ;
				h = "" ;
			}
			if(name!=null && name!=""){
				sb.append(name + ":" + h + "\n") ;
			}
		}
		return sb.toString() ;
	}
	private static int[] ipSplit(String ipHex){
		int[] result ; 
		int r1 = Integer.parseInt(ipHex.substring(0,2),16) ;
		int r2 = Integer.parseInt(ipHex.substring(2,4),16) ;
		int r3 = Integer.parseInt(ipHex.substring(4,6),16) ;
		int r4 = Integer.parseInt(ipHex.substring(6,8),16) ;
		result = new int[]{r1,r2,r3,r4} ;
		return result ; 
	}
	
}
