package jec.CM12sekine.packetflower.packet ;
import java.util.ArrayList;




import android.os.Environment;
import android.util.Log;

// singleton
public class PacketDataController implements Runnable{
	private static final String TAG = "PacketDataController" ; 
	private static PacketDataController instance ;

	public static final int STATE_NORMAL = 0 ;
	public static final int STATE_NOWLOAD = 1 ;
	public static final int STATE_EOF = 2 ;
	private int state = 0 ;
	
	private final int LOAD_TIMING_PACKET_NUMBER = 200 ;//このパケット数以下でロードする
	private final int LOAD_BUFFER_SIZE = 20000; //一度に読み込むバイト数
	
	/*
	standard 
	private final int LOAD_TIMING_PACKET_NUMBER = 2000 ;
	private final int LOAD_BUFFER_SIZE = 200000 ; 
	*/
	ArrayList<Packet> loadedPackets = new ArrayList<Packet>() ;
	
	//パケットデータとして解析できた部分から後の文字列を,次回読み込んだ文字列の頭に追加するので保存しておく、
	String breakString = new String() ;
	
	FileManager fm = null;
	boolean nowLoad = false ;
	boolean eof = false ;

	private PacketDataController(){
	}
	
	
	public static PacketDataController getInstance() {
		if(instance == null){
                instance = new PacketDataController() ;
		}
		return instance ;
	}
	public void setFile(String filePath){
		fm = new FileManager(filePath) ;
	}
	public Packet pool(){
		checkFileState(); 
		if(loadedPackets.size() < LOAD_TIMING_PACKET_NUMBER && !nowLoad && !eof){
			nowLoad = true ;
			state = PacketDataController.STATE_NOWLOAD ;
			new Thread(this).start();
		}
		
		Packet tmp = null ;
		if(loadedPackets.size() <= 0){
			if(eof==true)tmp = new EOFPacket() ;
		}else{
			tmp = loadedPackets.remove(0);
			if(tmp==null)Log.v("error", "loadedPackets[0] is null ") ;
		}
		return tmp ;
	}
	public int getLoadedSize(){
		checkFileState(); 
		return loadedPackets.size();
	}
	
	public int getState(){
		checkFileState(); 
		return state ;
	}

	public synchronized void update() throws Exception{
		checkFileState(); 

		Log.v(TAG,"update()") ;
		if(fm.getState() == FileManager.STATE_EOF){
			Log.v(TAG,"EOF") ;
			eof = true ;
			state = PacketDataController.STATE_EOF ;
			return ;
		}
		
		byte[] binToBytes = fm.read(LOAD_BUFFER_SIZE) ; 
		String hexString = breakString + HexUtil.asHex(binToBytes) ;
		ProtocolParser.parsePacket(hexString) ;
		breakString = new String() ;
		
		for(Packet p:ProtocolParser.getPackets()){
			loadedPackets.add(p) ;
		}
		int unusedIndex = ProtocolParser.getUnusedIndex() ;

		breakString=(hexString.substring(unusedIndex)) ;		
		
	}
	public void run() {
		try{
            update() ;
		}catch(Exception e){
			Log.v("update()",e.getMessage()) ;
		}
		nowLoad = false ;
		state = PacketDataController.STATE_NORMAL ;
	}
	private void checkFileState(){
		if(fm == null){
			throw new IllegalStateException() ;
		}
	}

}
