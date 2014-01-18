package jec.CM12sekine.packetflower.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jec.CM12sekine.packetflower.packet.Packet;
import android.util.Log;

public class SeedGenerator {
	private static String TAG = "SeedGenerator" ;
	private static SeedGenerator instance = null ;
	private SeedGenerator(){
		
	}
		
	public static SeedGenerator getInstance(){
		if(instance == null){
			instance = new SeedGenerator() ;
		}
		
		return instance ;
	}

	
	public synchronized long seedGenerate(Packet packetData){
		long seed = 0 ;
		String[] packetDatas = packetData.getHeaders() ;
		int id = Integer.parseInt(packetDatas[4],16) ;
		int protcol = Integer.parseInt(packetDatas[7],16) ;
		long sourceIP = Long.parseLong(packetDatas[9],16) ;
		long destinationIP = Long.parseLong(packetDatas[10],16) ;
		Log.v(TAG,"id:"+id + " "+  Integer.toBinaryString(id)) ;
		Log.v(TAG,"protcol:"+protcol+ " "+  Integer.toBinaryString(protcol)) ;
		Log.v(TAG,"sourceIP:"+sourceIP + " "+  Long.toBinaryString(sourceIP)) ;
		Log.v(TAG,"destinationIP:"+destinationIP + " "+  Long.toBinaryString(destinationIP)) ;
		seed = sourceIP ^ destinationIP ;
		seed = seed << 16 | id ;
		seed = seed << 8 | protcol ;
		Log.v(TAG, "seed" + seed + " " + Long.toBinaryString(seed)) ;
		return seed;
		
	}
}
