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

public class FireworkHachi extends Firework implements Runnable{
	private boolean isAfterFirstDraw = false ;
	private long seed ;
	private int lifeMilliTime = 0; //破裂後の描写時間
	private long startPacketFlowerSystemTime = -1 ;
	private long endPacketFlowerSystemTime = -1 ;
	private float [][]funXYZ ; 

	private Random randomAccel ;
	
	private int drawQuality = 0;
	public static final int QUALITY_MAX = 1 ;
	public static final int QUALITY_HIGH = 10 ;
	public static final int QUALITY_MIDDLE = 30 ;
	public static final int QUALITY_LOW = 50 ;
	
	private static final String TAG = "FireworkHachi" ;
    private FloatBuffer[] vertexBuffers;//頂点バッファ

	private boolean isComplete = false ;
	
	private float[][] xyz = null ;
	private int sceneCount = 0 ;
	private int allSize = 0 ;
	private float startX = 0 ;
	private float startY = 0 ;
	private float startZ = 0 ;
	
	private int[] changeColorTimes ;
	private Color[] changeColors ;
	
	public FireworkHachi(long seed, int lifeMilliTime, int drawQuality, float startX, float startY, float startZ,int allSize, 
			 int[] changeColorTimes, Color[] changeColors ){

		initSeeds(seed) ;
		this.seed = seed ;
		this.lifeMilliTime = lifeMilliTime ;
		this.drawQuality = drawQuality ;
		this.startX = startX ;
		this.startY = startY ;
		this.startZ = startZ ;
		this.allSize = allSize ;
		this.changeColorTimes=changeColorTimes; 
		this.changeColors = changeColors ;
		
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
			
		int[] lostForceScene = new int[allSize];
		float[] accelXYZ = new float[allSize*3] ;
		for(int i=0 ; i<lostForceScene.length ;i++){
			lostForceScene[i] = -1 ;
		}
		
		funXYZ = new float[sceneCount][] ;	
		for(int i=0 ; i<sceneCount; i++){
			funXYZ[i] = new float[allSize*3] ;
			
		}

		for(int j=0 ; j<allSize ; j++){
			funXYZ[0][j*3]=0 ;
			funXYZ[0][j*3+1]=0 ;
			funXYZ[0][j*3+2]=0 ;
		}
		for(int i=1 ; i<sceneCount; i++){
			for(int j=0 ; j<allSize ; j++){
				if(lostForceScene[j]==-1){
					accelXYZ[j*3] += (randomAccel.nextFloat()-0.5f)*0.01f ;
					accelXYZ[j*3+1] += (randomAccel.nextFloat()-0.5f)*0.01f ;
					accelXYZ[j*3+2] += (randomAccel.nextFloat()-0.5f)*0.01f ;
					//if(Math.abs(accelXYZ[j*3])>=0.03f | Math.abs(accelXYZ[j*3+1])>=0.03f | Math.abs(accelXYZ[j*3+2])>=0.03f){
                    if(i >= sceneCount/2){
						lostForceScene[j] = i;
					}
				}	
				

			}
			for(int j=0 ; j<allSize ; j++){
				if(lostForceScene[j] != -1){
					int lostForce = lostForceScene[j] ;
					funXYZ[i][j*3]= funXYZ[i-1][j*3]+accelXYZ[j*3]*(float)((sceneCount-lostForce)-(i-lostForce))/(sceneCount-lostForce) ;
					funXYZ[i][j*3+1]= funXYZ[i-1][j*3+1]+accelXYZ[j*3+1]*(float)((sceneCount-lostForce)-(i-lostForce))/(sceneCount-lostForce) - 0.01f;
					funXYZ[i][j*3+2]= funXYZ[i-1][j*3+2]+accelXYZ[j*3+2]*(float)((sceneCount-lostForce)-(i-lostForce))/(sceneCount-lostForce) ;
				}else{
					funXYZ[i][j*3]= funXYZ[i-1][j*3]+accelXYZ[j*3] ;
					funXYZ[i][j*3+1]= funXYZ[i-1][j*3+1]+accelXYZ[j*3+1] ;
					funXYZ[i][j*3+2]= funXYZ[i-1][j*3+2]+accelXYZ[j*3+2] ;

				}
			}
		}

		xyz = new float[sceneCount][] ;
		for(int scene=0 ; scene<sceneCount ; scene++){
				xyz[scene] = nextVertexForScene(scene) ;
		}
		vertexBuffers = new FloatBuffer[sceneCount] ;
		for(int scene=0 ; scene<sceneCount ; scene++){
			vertexBuffers[scene] = makeFloatBuffer(xyz[scene]); 
		}
		allRelease();
		isComplete = true ;
	}
	
	private void allRelease(){
		funXYZ= null ;
		xyz = null ;	
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
		//float[] rgba = nowColor(time) ;
		int scene = time / drawQuality ;
		int bufIndex = 0 ;
		float[] rgba = nowColor(time) ;

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
	private float[] nextVertexForScene(int scene){

		float preBuff_xyz[][] = new float[allSize][] ;

		int pointIndex=0 ;
		for(int i=0 ; i<preBuff_xyz.length ; i++){
			preBuff_xyz[i] = new float [3] ;
			preBuff_xyz[i][0] = funXYZ[scene][pointIndex++] + startX ;
			preBuff_xyz[i][1] = funXYZ[scene][pointIndex++] + startY  ;
			preBuff_xyz[i][2] = funXYZ[scene][pointIndex++] + startZ ;		
		}

		
		float[] preBuff = new float[preBuff_xyz.length*3] ;
		int i=0;
		for(float[] tmp :preBuff_xyz){
			for(float t: tmp){
				preBuff[i] = t ;
				i++ ;
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
