package jec.CM12sekine.packetflower;

import java.util.Random;

import android.os.IInterface;
import android.util.Log;
import jec.CM12sekine.packetflower.fireworkelement.FireworkBotan;
import jec.CM12sekine.packetflower.fireworkelement.FireworkHachi;
import jec.CM12sekine.packetflower.fireworkelement.FireworkShidare;
import jec.CM12sekine.packetflower.fireworkelement.FireworkTail;
import jec.CM12sekine.packetflower.util.Color;

public class DataSetFactory {
	private static final float TTL_AVERAGE = 100f ;
	private static final float ETC_TTL_AVERAGE = 250f ;
	private static final String TAG = "DataSetFactory" ;
	private static final int TAIL_LIFETIME = 3000;
	private static final float TCP_LENGTH_AVERAGE = 1500f ;
	private static final Color[] normalColors = {Color.RED, Color.GREEN, Color.BLUE, Color.PINK, Color.SKYBLUE} ;
	private static final Color[] lightColors = {Color.LIGHT_RED, Color.LIGHT_GREEN, Color.LIGHT_BLUE, Color.LIGHT_PINK, Color.LIGHT_SKYBLUE} ;

		
	static int culcLifeTime(float standardLifeTime, float minLifeTime, int ttl, float AverageTTL ){
		int lifeTime = (int)(minLifeTime + (standardLifeTime-minLifeTime)*(ttl/AverageTTL)) ;

		lifeTime = lifeTime - (lifeTime % 150) ;
		return lifeTime;
	}

	private static int[] culcChangeColorTimes(float lifeTimeRate, int[] changeColorTimes){
		int[] result = new int[changeColorTimes.length];
		for(int i=0 ;i<changeColorTimes.length ; i++){
			result[i] = (int)(changeColorTimes[i]*lifeTimeRate) ;
		}
		return result;
	}
	

	static FireworkDataSet[] etc(String parseString,long seed, int totalLength, int ttl, int[] sourceIps, int[] destinationIps, int index){
		int backMargin = index*3700 ;
		FireworkDataSet[] fireworkDataSet = new FireworkDataSet[2] ;
	
		float lengthRate = 0.1f + (float)totalLength / TCP_LENGTH_AVERAGE;
		float firstSpeedRate = 0.1f + (float)totalLength / TCP_LENGTH_AVERAGE;
		float lifeTimeRate = 0.3f + ttl /ETC_TTL_AVERAGE*0.7f ;
		
		int lifeTime = culcLifeTime(6000, 1000, ttl, ETC_TTL_AVERAGE) ;
        
		int[] innerChangeColorTimes =culcChangeColorTimes(lifeTimeRate,new int[]{300,1000,4000,6000} );

		int[] ringsNums = new int[]{(int)(32*lengthRate), (int)(28*lengthRate), (int)(60*lengthRate),(int)(80*lengthRate), (int)(60*lengthRate),(int)(28*lengthRate), (int)(32*lengthRate)};
		
		float startX = ((sourceIps[0] + sourceIps[1])-255) / 100 ;
		float startY = 0 ;
		float startZ = ((sourceIps[2] + sourceIps[3])-255) / 100 ;
		float endX = (destinationIps[0]-127) / 50 ;
		float endY = 3 + ((destinationIps[1]+destinationIps[2])-255) / 300 ;
		float endZ = (destinationIps[3]-127) / 50 ; 
		
		Random rand = new Random(seed); 
		int colorIndex = rand.nextInt()%5 ;
		colorIndex = Math.abs(colorIndex) ;
		Color color = normalColors[colorIndex];
		int lightColorIndex = rand.nextInt()%5 ;
		lightColorIndex = Math.abs(colorIndex) ;
		Color lightColor= lightColors[lightColorIndex] ; 
		fireworkDataSet[0] = new FireworkDataSet() ;
		fireworkDataSet[0].setFirework(new FireworkTail(seed,TAIL_LIFETIME, FireworkTail.QUALITY_MIDDLE,50, startX, startY, startZ, endX,endY,endZ, Color.YELLOW)) ;
		fireworkDataSet[0].setMargin(0+backMargin);
		fireworkDataSet[0].setParseString("牡丹" + "\n" + parseString+"seed:" + seed + "\n");
		

		fireworkDataSet[1] = new FireworkDataSet() ;
		fireworkDataSet[1] .setFirework(new FireworkBotan(seed,lifeTime,FireworkBotan.QUALITY_MIDDLE,1.4f*firstSpeedRate, endX,endY,endZ, new int[]{30, 50, 70, 90, 110, 130, 150},ringsNums,innerChangeColorTimes,new Color[]{Color.START_WHITE, lightColor, color, Color.END_BLACK},0,0.05f,1f,1f)) ;
		fireworkDataSet[1] .setMargin(TAIL_LIFETIME+backMargin);
		fireworkDataSet[1] .setParseString("牡丹" + "\n" + parseString+"seed:" + seed + "\n");
		return fireworkDataSet ;
	}
	
	
	static FireworkDataSet[] tcp(String parseString,long seed, int totalLength, int ttl, int[] sourceIps, int[] destinationIps, int index){
		int backMargin = index*3700 ;
		FireworkDataSet[] fireworkDataSet = new FireworkDataSet[3] ;
	
		float lengthRate = 0.1f + (float)totalLength / TCP_LENGTH_AVERAGE;
		float firstSpeedRate = 0.1f + (float)totalLength / TCP_LENGTH_AVERAGE;
		float lifeTimeRate = 0.3f + ttl /TTL_AVERAGE*0.7f ;
		
		int lifeTime = culcLifeTime(6000, 1000, ttl, TTL_AVERAGE) ;
		int[] outerChangeColorTimes =culcChangeColorTimes(lifeTimeRate, new int[]{300,500,1500,1800,4000,6000});
        
		int[] innerChangeColorTimes =culcChangeColorTimes(lifeTimeRate,new int[]{300,1000,4000,6000} );

		int[] ringsNums = new int[]{(int)(32*lengthRate), (int)(28*lengthRate), (int)(60*lengthRate),(int)(80*lengthRate), (int)(60*lengthRate),(int)(28*lengthRate), (int)(32*lengthRate)};
		
		float startX = ((sourceIps[0] + sourceIps[1])-255) / 100 ;
		float startY = 0 ;
		float startZ = ((sourceIps[2] + sourceIps[3])-255) / 100 ;
		float endX = (destinationIps[0]-127) / 50 ;
		float endY = 3 + ((destinationIps[1]+destinationIps[2])-255) / 300 ;
		float endZ = (destinationIps[3]-127) / 50 ; 

		Color innerColor = Color.BLUE ;
		Color innerLightColor= Color.BLUE ;
		Color outerColor = Color.RED ;
		fireworkDataSet[0] = new FireworkDataSet() ;
		fireworkDataSet[0].setFirework(new FireworkTail(seed,TAIL_LIFETIME, FireworkTail.QUALITY_MIDDLE,50, startX, startY, startZ, endX,endY,endZ, Color.YELLOW)) ;
		fireworkDataSet[0].setMargin(0+backMargin);
		fireworkDataSet[0].setParseString("牡丹" + "\n" + parseString+"seed:" + seed + "\n");
		
		fireworkDataSet[1] = new FireworkDataSet() ;
		fireworkDataSet[1].setFirework(new FireworkBotan(seed, lifeTime, FireworkBotan.QUALITY_MIDDLE,2.6f*firstSpeedRate, endX,endY,endZ, new int[]{30, 50, 70, 90, 110, 130, 150},ringsNums,outerChangeColorTimes,new Color[]{Color.START_WHITE, Color.LIGHT_YELLOW,Color.YELLOW, outerColor,outerColor, Color.END_BLACK},0,0.05f,1f,1f)) ;
		fireworkDataSet[1].setMargin(TAIL_LIFETIME+backMargin);
		fireworkDataSet[1].setParseString("牡丹" + "\n" + parseString+"seed:" + seed + "\n");

		fireworkDataSet[2] = new FireworkDataSet() ;
		fireworkDataSet[2] .setFirework(new FireworkBotan(seed,lifeTime,FireworkBotan.QUALITY_MIDDLE,1.4f*firstSpeedRate, endX,endY,endZ, new int[]{30, 50, 70, 90, 110, 130, 150},ringsNums,innerChangeColorTimes,new Color[]{Color.START_WHITE, innerLightColor, innerColor, Color.END_BLACK},0,0.05f,1f,1f)) ;
		fireworkDataSet[2] .setMargin(TAIL_LIFETIME+backMargin);
		fireworkDataSet[2] .setParseString("牡丹" + "\n" + parseString+"seed:" + seed + "\n");
		return fireworkDataSet ;
	}
	
	
	
	static FireworkDataSet[] icmp(String parseString,long seed, int totalLength, int ttl, int[] sourceIps, int[] destinationIps, int index){
		int backMargin = index*3700 ;
		float firstSpeedRate = 0.3f + (float)totalLength / 40 ;

		float startX = ((sourceIps[0] + sourceIps[1])-255) / 100 ;
		float startY = 0 ;
		float startZ = ((sourceIps[2] + sourceIps[3])-255) / 100 ;
		float endX = (destinationIps[0]-127) / 50 ;
		float endY = 3 + ((destinationIps[1]+destinationIps[2])-255) / 300 ;
		float endZ = (destinationIps[3]-127) / 50 ; 
		int lifeTime = culcLifeTime(5000, 1000, ttl, TTL_AVERAGE) ;
		lifeTime = lifeTime>6000?6000:lifeTime ;
		float lifeTimeRate = (float)lifeTime / 5000 ; 

		int[] innerChangeColorTimes = culcChangeColorTimes(lifeTimeRate, new int[]{300,500,3000}) ;
		int[] outerChangeColorTimes = culcChangeColorTimes(lifeTimeRate, new int[]{300,500,1500,1800,4000,5000}) ;
		FireworkDataSet[] fireworkDataSet = new FireworkDataSet[3] ;
		fireworkDataSet[0] = new FireworkDataSet() ;
		fireworkDataSet[0].setFirework(new FireworkTail(seed, 3000, FireworkTail.QUALITY_MIDDLE, 50,startX,startY,startZ, endX,endY,endZ, Color.YELLOW)) ;
		fireworkDataSet[0].setMargin(0+backMargin);
		fireworkDataSet[0].setParseString("菊" + "\n" + parseString+"seed:" + seed + "\n");
		
		fireworkDataSet[1] = new FireworkDataSet() ;
		fireworkDataSet[1].setFirework(new FireworkBotan(seed,lifeTime, FireworkBotan.QUALITY_MIDDLE,1.4f*firstSpeedRate, endX, endY, endZ, new int[]{30, 50},new int[]{16*2, 14*2 },outerChangeColorTimes,new Color[]{Color.START_WHITE, Color.LIGHT_YELLOW,Color.YELLOW, Color.RED,Color.RED, Color.END_BLACK},0,0.05f,1f,1f)) ;
		fireworkDataSet[1].setMargin(3000+backMargin);
		fireworkDataSet[1].setParseString("菊" + "\n" + parseString+"seed:" + seed + "\n");

		
		fireworkDataSet[2] = new FireworkDataSet() ;
		fireworkDataSet[2] .setFirework(new FireworkShidare(seed, lifeTime, FireworkShidare.QUALITY_MIDDLE,1.4f*firstSpeedRate, endX, endY, endZ, new int[]{30, 50},new int[]{8, 7},innerChangeColorTimes,new Color[]{Color.START_WHITE, Color.LIGHT_YELLOW, Color.END_BLACK},0,0.05f,1f,1f)) ;
		fireworkDataSet[2] .setMargin(3000+backMargin);
		fireworkDataSet[2] .setParseString("菊" + "\n" + parseString+"seed:" + seed + "\n");
		return fireworkDataSet ;
	}
	static FireworkDataSet[] udp(String parseString,long seed, int totalLength, int ttl, int[] sourceIps, int[] destinationIps, int index){
		int backMargin = index*3700 ;
		FireworkDataSet[] fireworkDataSet = new FireworkDataSet[4] ;
	
		
		float startX = ((sourceIps[0] + sourceIps[1])-255) / 100 ;
		float startY = 0 ;
		float startZ = ((sourceIps[2] + sourceIps[3])-255) / 100 ;
		float endX = (destinationIps[0]-127) / 50 ;
		float endY = 3 + ((destinationIps[1]+destinationIps[2])-255) / 300 ;
		float endZ = (destinationIps[3]-127) / 50 ; 

		int lifeTime = culcLifeTime(4000, 2000, ttl, TTL_AVERAGE) ;
		float lifeTimeRate = (float)lifeTime/4000 ; 
		int[] outerChangeColorTimes = culcChangeColorTimes(lifeTimeRate, new int[]{300,800,1200,3000,4000}) ;
		int[] innerChangeColorTimes1 = culcChangeColorTimes(lifeTimeRate, new int[]{300,800,1200,3000,4000}) ;
		int[] innerChangeColorTimes2 = culcChangeColorTimes(lifeTimeRate, new int[]{300,800,1200,3000,4000}) ;

	 	fireworkDataSet[0] = new FireworkDataSet() ;
		fireworkDataSet[0].setFirework(new FireworkTail(seed, 3000, FireworkTail.QUALITY_MIDDLE,50,startX, startY, startZ, endX,endY,endZ, Color.YELLOW)) ;
		fireworkDataSet[0].setMargin(0+backMargin);
		fireworkDataSet[0].setParseString("蜂" + "\n" + parseString+"seed:" + seed + "\n");
		
		fireworkDataSet[1] = new FireworkDataSet() ;
		fireworkDataSet[1] .setFirework(new FireworkHachi(seed,lifeTime,FireworkHachi.QUALITY_MIDDLE, endX,endY,endZ, 100,outerChangeColorTimes,new Color[]{Color.START_WHITE, Color.LIGHT_YELLOW, Color.CLEAR_RED, Color.CLEAR_RED,Color.END_BLACK})) ;
		fireworkDataSet[1].setMargin(3000+backMargin);
		fireworkDataSet[1].setParseString("蜂" + "\n" + parseString+"seed:" + seed + "\n");
		
		fireworkDataSet[2] = new FireworkDataSet() ;
		fireworkDataSet[2] .setFirework(new FireworkHachi(seed+1,lifeTime,FireworkHachi.QUALITY_MIDDLE, endX,endY,endZ, 100,innerChangeColorTimes1,new Color[]{Color.START_WHITE, Color.LIGHT_YELLOW, Color.CLEAR_GREEN,Color.CLEAR_GREEN, Color.END_BLACK})) ;
		fireworkDataSet[2].setMargin(3500+backMargin);
		fireworkDataSet[2].setParseString("蜂" + "\n" + parseString+"seed:" + seed + "\n");

		
		fireworkDataSet[3] = new FireworkDataSet() ;
		fireworkDataSet[3] .setFirework(new FireworkHachi(seed+2,lifeTime,FireworkHachi.QUALITY_MIDDLE, endX,endY,endZ, 100,innerChangeColorTimes2,new Color[]{Color.START_WHITE, Color.LIGHT_YELLOW, Color.CLEAR_BLUE, Color.CLEAR_BLUE, Color.END_BLACK})) ;
		fireworkDataSet[3].setMargin(4000+backMargin);
		fireworkDataSet[3].setParseString("蜂" + "\n" + parseString+"seed:" + seed + "\n");
		return fireworkDataSet ;
	}
}
