package jec.CM12sekine.packetflower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;




import android.sax.Element;
import android.util.Log;
import android.widget.TextView;
import jec.CM12sekine.packetflower.fireworkelement.*;
import jec.CM12sekine.packetflower.packet.Packet;
import jec.CM12sekine.packetflower.packet.PacketDataController;
import jec.CM12sekine.packetflower.sounds.SoundPoolManager;
import jec.CM12sekine.packetflower.util.Color;
import jec.CM12sekine.packetflower.util.SeedGenerator;

public class FireworksController implements Runnable {
	private int opusNumer = 1 ;
	
	private ArrayList<String> showingInfo = new ArrayList<String>(); 
	private static final int PREPARE_MAX = 3 ;
	private static final String TAG = "FireworksController" ;
	private MessageBoard messageBoard ;
    private boolean halt;
	private long packetFlowerSystemTime = 0 ;
	private long pauseJavaSystemTime = 0 ;
	private long resumeJavaSystemTime = -1 ;
	private long blankTime = 0 ;
	private Thread packetDataThread = null ;
	private PacketDataController packetDataController = PacketDataController.getInstance() ;
	
	private ArrayList <Firework>drawingFireworks = new ArrayList<Firework>() ;
	private ArrayList <Firework>prepareFireworks = new ArrayList<Firework>() ;
	private ArrayList <Integer>drawMargin = new ArrayList<Integer>() ; //描画時刻をこの分遅くする。
	private ArrayList <String>packetParseString = new ArrayList<String>() ;
	private static FireworksController instance ;
	private FireworksController(){
	}
		
	public static FireworksController getInstance(){
		if(instance == null){
			instance = new FireworksController() ;
		}
		return instance ;
	}
	
	public void setMessageBoard(MessageBoard messageBoard){
		this.messageBoard = messageBoard ;
	}
	
	public void run() {
		//段階１ 解析して、花火のインスタンスを生成
		//段階２ FireworkクラスisCompleteメソッドから花火の生成が完了した事を知り、drawingFireworksに追加する。
		
        while (!halt) {
        	prepareFireworks() ;
        	drawingRegist() ;

        	
        }
    }
	int poolCount  ;
	private void prepareFireworks(){
		if(prepareFireworks.size() <= 0 && drawingFireworks.size() <= 0){
			showingInfo = new ArrayList<String>(); 
			int ttlSum = 0 ;
			ArrayList<Packet> p = new ArrayList<Packet>() ;
			boolean emptyFlag = false ;
			for(int i=0 ; i<PREPARE_MAX ; i++){
				if(ttlSum>=100){
					break; 
				}

				
				
				Packet packet = packetDataController.pool(); 
				poolCount++ ;
    			Log.v(TAG, "pool"+poolCount) ; 
				
				if(packet == null || poolCount==32){
					emptyFlag = true;
				}else{
					ttlSum += Integer.parseInt(packet.getHeaders()[6],16) ;
					p.add(packet) ;
				}
				
			}

    		if(!emptyFlag){

    			//FireworkGenerator.generateForPacket(Packet)メソッドはパケットクラスを受け取り
    			Packet[] addPacket = p.toArray(new Packet[0]) ;
    			FireworkDataSet[] fds = (FireworkDataSet[]) FireworkGenerator.generateForPacket(addPacket) ;

    			for(int i=0 ; i<fds.length ; i++){
    				prepareFireworks.add(fds[i].getFirework()) ;
    				drawMargin.add(fds[i].getMargin());
    				packetParseString.add(fds[i].getParseString()) ;
    			}
            }
    	}
    	
	}
	
	private void drawingRegist(){
    	if(drawingFireworks.size()<=0 && prepareFireworks.size()>=1){
    		int completeCount = 0 ;
    		for(int i=0 ; i<prepareFireworks.size() ; i++){
        		Firework f = prepareFireworks.get(i) ;
        		if(f.isComplete()){
        			completeCount++ ;
        		}
        	}
    		if(completeCount==prepareFireworks.size()){ // 描画中の花火が無く、生成が全て完了していたらdrawingFireworksに追加する。
    			String[] parseString = new String[packetParseString.size()] ;
    			long currentPFSystemTime = currentPacketFlowerSystemtime() ;
        		for(int i=prepareFireworks.size()-1 ; i>=0 ; i--){
        			int margin = drawMargin.remove(i) ;
        			long startPFSystemTime = currentPFSystemTime + margin ;
        			Firework fb = prepareFireworks.remove(i) ;
        			fb.setStartAndEndPacketFlowerSystemTime(startPFSystemTime);
        			String packetInfo = packetParseString.remove(i) ;
        			if(showingInfo.indexOf(packetInfo)==-1){
        				showingInfo.add(packetInfo) ;
        			}
            		drawingFireworks.add(fb) ;
            	}
        		String showingMes = "" ;
            	for(int i=showingInfo.size()-1 ; i>= 0; i--	){
            		showingMes +=   "\n作品No." + String.format("%1$03d", opusNumer) +   
            				" " + showingInfo.get(i) + "------------------";
            		opusNumer++ ;
            	}
            	messageBoard.setPacketInfo(showingMes) ;
    		}
    	}
	}
	
	public void drawFireworks(int textureId){
			
			
			long t = currentPacketFlowerSystemtime() ;
			for(int i=drawingFireworks.size()-1 ; i>=0 ; i--){
				Firework ball = drawingFireworks.get(i) ;
				long endTime = ball.getEndPacketFlowerSystemTime() ;
				long startTime = ball.getStartPacketFlowerSystemTime() ;
				if(t >= endTime){
					drawingFireworks.remove(i) ;
				}else if(t >= startTime){
					if(!ball.isAfterFirstDraw()){
						//TODO 最初に描画された事を判定し音を再生
						if(ball instanceof FireworkTail){
							SoundPoolManager.getInstance().playSound(SoundPoolManager.SOUND_TAIL);
						}else{
							SoundPoolManager.getInstance().playSound(SoundPoolManager.SOUND_BOMB);
						}
						Log.v(TAG,"first") ;
					}
					ball.drawFireworks(textureId, (int)(t - ball.getStartPacketFlowerSystemTime()));
				}
				
			}
				
	}
	
	private void halt() {
        halt = true;
        packetDataThread.interrupt();
    }
	
	public boolean isHalt(){
		return halt ;
	}

	public void stop(){
		pauseJavaSystemTime = System.currentTimeMillis() ;
		halt() ;
	}
	
	public void start(){
		halt = false;
		resumeJavaSystemTime = System.currentTimeMillis() ;
			blankTime += resumeJavaSystemTime - pauseJavaSystemTime ;
		packetDataThread = new Thread(this) ;
		packetDataThread.start() ;
	}

	private long currentPacketFlowerSystemtime(){
		if(!halt){
                packetFlowerSystemTime = System.currentTimeMillis() - blankTime;
		}
		return packetFlowerSystemTime ;
	}
	
	
}
