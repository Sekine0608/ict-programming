package jec.CM12sekine.packetflower.packet ;

import java.io.* ;

import android.util.Log;
public class FileManager{
	private int state = 0 ;
	public static final int STATE_NORMAL = 0 ;
	public static final int STATE_EOF = -1 ;
	private long offset ;
	RandomAccessFile raf ;
	
	private long currentReadByte = 0 ;
	private long currentProcessByte = 0 ;
	FileManager(String fileName){
		try{
			raf = new RandomAccessFile(new File(fileName), "r");
		}catch(IOException e){
			System.out.println("IOException") ;
		}
	}
	
	public void setOffset(long offset){
		this.state = STATE_NORMAL ;
		this.offset = offset ;
		try {	
			raf.seek(offset) ;
		} catch (IOException e){
		}
	}
	public byte[] read(int readByte){
		currentReadByte = readByte ;
		byte[] bytes = new byte[readByte];
		try {	
			for(int i=0 ; i<readByte ; i++){
				currentProcessByte = i+1 ;
				bytes[i] = raf.readByte();
			}
			offset += readByte ;
			raf.seek(offset) ;
		} catch (EOFException e) {
			System.out.println("[EOF]");
			state = STATE_EOF ;
		} catch (IOException e){
		}
		currentReadByte = -1 ;
		currentProcessByte = -1 ;
		return bytes ;
	}

	public float currentProcess(){
		if(currentReadByte == -1 || currentProcessByte == -1){
			return -1 ;
		}else{
			return (float)currentProcessByte/currentReadByte ;
		}
	}
	
	public int getState(){
		return state ;
	}
	public void close(){
		try {	
			raf.close() ;
		} catch (IOException e){
		}
	}
}