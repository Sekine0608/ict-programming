package jec.CM12sekine.packetflower.fireworkelement;

import java.nio.ByteBuffer; 
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Currency;
import java.util.Random;

import jec.CM12sekine.packetflower.GLES;
import jec.CM12sekine.packetflower.util.Color;
import android.opengl.GLES20;
import android.util.Log;

public class FireworkCrean extends Firework implements Runnable{
	private boolean isAfterFirstDraw = false;
	
	private long seed ;
	private int lifeMilliTime = 0; //破裂後の描写時間
	private long startPacketFlowerSystemTime = -1 ;
	private long endPacketFlowerSystemTime = -1 ;
	
	private Random randomAccel ;
	
	private int drawQuality = 0;
	public static final int QUALITY_MAX = 1 ;
	public static final int QUALITY_HIGH = 10 ;
	public static final int QUALITY_MIDDLE = 30 ;
	public static final int QUALITY_LOW = 50 ;
	
	private static final String TAG = "FireworkCrean" ;
    private FloatBuffer[] vertexBuffers;//頂点バッファ

	private boolean isComplete = false ;
	
	private float[][] xyz = null ;
	private int startFlashTime = 0 ;
	private int sceneCount = 0 ;
	private int allSize = 0 ;
	private float startX = 0 ;
	private float startY = 0 ;
	private float startZ = 0 ;
	
	private int[] changeColorTimes ;
	private Color[] changeColors ;
	
	public FireworkCrean(long seed, int lifeMilliTime,  int drawQuality,float startX, float startY, float startZ, 
			int allSize, int[] changeColorTimes, Color[] changeColors, int startFlashTime){
		this(seed, lifeMilliTime, drawQuality,startX,startY,startZ,allSize,changeColorTimes,changeColors,startFlashTime,0.1f,1,1) ;
	}
	public FireworkCrean(long seed, int lifeMilliTime, int drawQuality, float startX, float startY, float startZ,int allSize, 
			 int[] changeColorTimes, Color[] changeColors, 
			int startFlashTime, float randomStarFirstSpeedRate, float randomStarPhiRotationRate, float randomStarThetaRotationRate){

		initSeeds(seed) ;
		this.seed = seed ;
		this.lifeMilliTime = lifeMilliTime ;
		this.drawQuality = drawQuality ;
		this.startX = startX ;
		this.startY = startY ;
		this.startZ = startZ ;
		this.allSize = allSize ;
		
		this.changeColorTimes = changeColorTimes ;
		this.changeColors = changeColors ;
		this.startFlashTime = startFlashTime; 
		
		this.sceneCount = lifeMilliTime/drawQuality; 
		
		new Thread(this).start();
	}
	
	public void setStartAndEndPacketFlowerSystemTime(long startPacketFlowerSystemTime){
		this.startPacketFlowerSystemTime = startPacketFlowerSystemTime ;
		this.endPacketFlowerSystemTime = startPacketFlowerSystemTime + lifeMilliTime ; 
	}
	
	public long getStartPacketFlowerSystemTime(){
		return startPacketFlowerSystemTime ;
	}
	
	public long getEndPacketFlowerSystemTime(){
		return endPacketFlowerSystemTime ;
	}
	
	protected void initSeeds(long seed){
		randomAccel= new Random(seed) ;
	}
	
	public void run(){
		
		xyz = new float[sceneCount][] ;
		for(int scene=0 ; scene<sceneCount ; scene++){
				xyz[scene] = nextVertexForScene(scene) ;
		}
		vertexBuffers = new FloatBuffer[sceneCount] ;
		for(int scene=0 ; scene<sceneCount ; scene++){
			vertexBuffers[scene] = makeFloatBuffer(xyz[scene]); 
		}
		isComplete = true ;
	}

	public boolean isComplete(){
		return isComplete ;
	}
	
	private void checkState(){
		if(startPacketFlowerSystemTime==-1){
			throw new IllegalStateException("startPacketFlowerSystemTime==-1") ;
		}
	}
	public void drawFireworks(int textureId, int time){
		checkState(); 
		isAfterFirstDraw = true ;
		float[] rgba = nowColor(time) ;
		int scene = time / drawQuality ;
		int bufIndex = 0 ;
		for(int i=0 ; i<allSize ; i++){
				float r = rgba[0] ;
				float g = rgba[1] ;
				float b = rgba[2] ;
				float a = rgba[3] ;
	        	GLES20.glUniform4f(GLES.colorHandle, r, g, b, a);                
	            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
	            GLES20.glUniform1i(GLES.texHandle,0);
	            GLES20.glVertexAttribPointer(GLES.positionHandle,3,
	                GLES20.GL_FLOAT,false,0,vertexBuffers[scene]);
	            GLES20.glDrawArrays(GLES20.GL_POINTS,bufIndex,1);
	            bufIndex++ ;
		}
	}
	
	private float[] nowColor(int time){
		float[] result = {1,1,1,1} ;
		
		int beforeTime = 0;
		int afterTime = 0;
		float[] beforeColor ;
		float[] afterColor ;
		int beforeIndex = -1 ;
		int afterIndex = changeColorTimes.length-1 ;

		for(int i=0 ; i<changeColorTimes.length ; i++){
			if(changeColorTimes[i]>time){
				afterIndex = i ;
				if(i!=0){
					beforeIndex = i-1 ;
				}else{
					beforeIndex = -1 ;
				}
				break; 
			}
		}
		
		if(beforeIndex == -1){
			beforeTime = 0 ;
			beforeColor = changeColors[0].getValueAsFloatArray();
		}else{
			beforeTime = changeColorTimes[beforeIndex] ;
			beforeColor = changeColors[beforeIndex].getValueAsFloatArray() ;
		}
			
            afterTime = changeColorTimes[afterIndex] ;
            afterColor = changeColors[afterIndex].getValueAsFloatArray() ;
		
			float differR = afterColor[0] - beforeColor[0] ;
			float differG = afterColor[1] - beforeColor[1] ;
			float differB = afterColor[2] - beforeColor[2] ;
			float differA = afterColor[3] - beforeColor[3] ;

			float differTime = afterTime - beforeTime ;
			float unitR = differR/differTime ;
			float unitG = differG/differTime ;
			float unitB = differB/differTime ;
			float unitA = differA/differTime ;
			
			result[0] = beforeColor[0] + unitR*(time-beforeTime) ;
			result[1] = beforeColor[1] + unitG*(time-beforeTime) ;
			result[2] = beforeColor[2] + unitB*(time-beforeTime) ;
			result[3] = beforeColor[3] + unitA*(time-beforeTime) ;
			for(int i=0 ; i<result.length ; i++){
				if(result[i]<0){
					result[i] = 0 ;
				}else if(result[i]>=1.0f){
					result[i] = 1.0f ;
				}
			}

/*
		Log.v(TAG,"time(" + time + ")" + "result(" + result[0] + ", " +result[1] + ", " + result[2] + ", " + result[3] +")");
		Log.v(TAG,"before(" + "time:" + beforeTime + " color:" + beforeColor[0] + ", " + beforeColor[1] + ", "+ beforeColor[2] + ", "+ beforeColor[3]  + ")" ) ; 
		Log.v(TAG, " after(" + "time:" + afterTime + " color:"  + afterColor[0] + ", " + afterColor[1] + ", "+ afterColor[2] + ", "+ afterColor[3] + ")");
*/
		return result ;
	}
	float funX = 0 ;
	float funY = 0 ;
	float funZ = 0 ;
	Random rand = new Random() ; 
	private float[] nextVertexForScene(int scene){

		float preBuff_xyz[][] = new float[allSize][] ;

		for(int i=0 ; i<preBuff_xyz.length ; i++){
			preBuff_xyz[i] = new float [3] ;
			funX += (rand.nextFloat()-0.5f)*0.01f ;
			funY += (rand.nextFloat()-0.5f)*0.01f ;
			funZ += (rand.nextFloat()-0.5f)*0.01f ;
			preBuff_xyz[i][0] = funX + startX ;
			preBuff_xyz[i][1] = funY + startY  ;
			preBuff_xyz[i][2] = funZ + startZ ;		
		}

		
		float[] preBuff = new float[preBuff_xyz.length*3] ;
		int i=0;
		for(float[] tmp :preBuff_xyz){
			for(float t: tmp){
				preBuff[i] = t ;
			}
		}
		
		return preBuff ;
	}
	
    
    //float配列をFloatBufferに変換
    private FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb=ByteBuffer.allocateDirect(array.length*4).order(
            ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }
    
    
    private void testArguments(int drawQuality,float v, int[] ringAngles, int[] ringSizes, int[] changeColorTimes, Color[] changeColors){
		if(ringAngles.length != ringSizes.length){
			throw new IllegalArgumentException("ringAngles.length != ringSizes.length") ;
		}
		if(ringAngles.length==0 || ringSizes.length==0){
			throw new IllegalArgumentException("ringAngles.length==0 || ringSizes.length==0") ;
		}
		if(ringAngles == null || ringSizes == null){
			throw new IllegalArgumentException("ringAngles == null || ringSizes == null") ;
		}
		
		if(changeColors.length != changeColorTimes.length){
			throw new IllegalArgumentException("changeColors.length != changeColorTimes.length") ;
		}
		if(changeColors.length==0 || changeColorTimes.length==0){
			throw new IllegalArgumentException("changeColors.length==0 || changeColorTimes.length==0") ;
		}
		if(changeColors == null || changeColorTimes == null){
			throw new IllegalArgumentException("changeColors == null || changeColorTimes == null") ;
		}
		
		for(int i = 1 ; i < ringAngles.length ; i++){
			if(ringAngles[i-1] > ringAngles[i]){
				throw new IllegalArgumentException("ringAngles[" + (i-1) + "] > ringAngles[" + i + "]");
			}
		}
    }
	@Override
	public long getSeed() {
		// TODO Auto-generated method stub
		return seed;
	}
	@Override
	public boolean isAfterFirstDraw() {
		// TODO Auto-generated method stub
		return isAfterFirstDraw;
	}
}
