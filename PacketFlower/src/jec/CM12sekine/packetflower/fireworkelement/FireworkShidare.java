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

public class FireworkShidare extends Firework implements Runnable{
	private boolean isAfterFirstDraw = false ;
	private long seed ;
	private int lifeMilliTime = 0; //破裂後の描写時間
	private long startPacketFlowerSystemTime = -1 ;
	private long endPacketFlowerSystemTime = -1 ;
	
	private static boolean randomStarPhiRotationEnable = true ;
	private static boolean randomStarThetaRotationEnable = true ;
	private static boolean randomStarFirstSpeedEnable = true	;
	private static boolean randmoStarFlashEnable = true ;
	
	private Random randomPhi;
	private Random randomTheta;
	private Random randomSpeed;
	
	private float randomStarPhiRotationRate = 0f ;
	private float randomStarThetaRotationRate = 0f ; 
	private float randomStarFirstSpeedRate = 0f ;


	
	private int drawQuality = 0;
	public static final int QUALITY_MAX = 1 ;
	public static final int QUALITY_HIGH = 10 ;
	public static final int QUALITY_MIDDLE = 30 ;
	public static final int QUALITY_LOW = 50 ;
	
	private static final String TAG = "FireworkShidare" ;
    private FloatBuffer[] vertexBuffers;//頂点バッファ
    private static final float K = 1.4f ; //空気抵抗係数
    private static final float G = 0.098f ;

	private boolean isComplete = false ;
	
	private float [][] phis = null ;
	private float [][] thetas = null ;
	private float[][] radiusesForScene = null ; // radius 半径
	private float[] fallDistanceForScene = null ;
	private float[][] xyz = null ;
	private int sceneCount = 0 ;
	private float v = 0 ;
	private int ringSizes[] = null ;
	private int ringAngles[] = null ;
	private int allSize = 0 ;
	private float startX = 0 ;
	private float startY = 0 ;
	private float startZ = 0 ;
	
	private int[] changeColorTimes ;
	private Color[] changeColors ;
	
	public FireworkShidare(long seed, int lifeMilliTime,  int drawQuality,float v, float startX, float startY, float startZ, 
			int[] ringAngles, int[] ringSizes, int[] changeColorTimes, Color[] changeColors, int startFlashTime){
		this(seed, lifeMilliTime, drawQuality,v,startX,startY,startZ,ringAngles,ringSizes,changeColorTimes,changeColors,startFlashTime,0.1f,1,1) ;
	}
	public FireworkShidare(long seed, int lifeMilliTime, int drawQuality,float v, float startX, float startY, float startZ, 
			int[] ringAngles, int[] ringSizes, int[] changeColorTimes, Color[] changeColors, 
			int startFlashTime, float randomStarFirstSpeedRate, float randomStarPhiRotationRate, float randomStarThetaRotationRate){
		testArguments(drawQuality,v,ringAngles,ringSizes,changeColorTimes,changeColors) ;

		initSeeds(seed) ;
		this.seed = seed ;
		this.lifeMilliTime = lifeMilliTime ;
		this.drawQuality = drawQuality ;
		this.v = v ;
		this.startX = startX ;
		this.startY = startY ;
		this.startZ = startZ ;
		this.ringAngles = ringAngles ;
		this.ringSizes = ringSizes ;
		this.changeColorTimes = changeColorTimes ;
		this.changeColors = changeColors ;
		this.randomStarPhiRotationRate = randomStarPhiRotationRate ;
		this.randomStarThetaRotationRate = randomStarThetaRotationRate ;
		this.randomStarFirstSpeedRate = randomStarFirstSpeedRate ;
		
		this.sceneCount = lifeMilliTime/drawQuality; 
		for(int i=0 ; i<ringSizes.length ; i++){
			for(int j=0 ; j<ringSizes[i] ; j++){ 
				allSize++ ;
			}
		}
		
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
		Random rand = new Random(seed) ;
		randomPhi = new Random(rand.nextLong()) ;
		randomTheta = new Random(rand.nextLong()) ;
		randomSpeed = new Random(rand.nextLong()) ;
	}
	
	public void run(){
		radiusesForScene = new float[sceneCount][allSize] ;
		fallDistanceForScene = new float[sceneCount] ;
		phis = new float[ringSizes.length][] ;
		thetas = new float[ringSizes.length][] ;
		float[] starVs = new float[allSize] ;

		for(int i=0 ; i<ringSizes.length ; i++){
			phis[i] = new float[ringSizes[i]] ;
			thetas[i] = new float[ringSizes[i]] ;
		}
		if(randomStarFirstSpeedEnable){
			for(int i=0 ; i<allSize ; i++){
				starVs[i] = v*(float)(1+(1-2*randomSpeed.nextFloat())*randomStarFirstSpeedRate);
			}
		}
		for(int scene=0 ; scene<sceneCount ; scene++){
			float firstSpeed = v; 
			float e = 1-(float)Math.pow(Math.E, -K*1/1000*scene*drawQuality) ;
			for(int i=0 ; i<allSize ; i++){
				if(randomStarFirstSpeedEnable){
					firstSpeed = starVs[i] ;
				}
				radiusesForScene[scene][i] =  firstSpeed/K*e; 
			}
			fallDistanceForScene[scene] = G/K*1/1000*scene*drawQuality-G/K/K*e ;
		}
		
	
		
		for(int i=0 ; i<ringSizes.length ; i++){
			float splitCircle = (float)360/ringSizes[i] ;
			float angle = ringAngles[i];
			float[] tmpPhis = new float[ringSizes[i]] ;
			float[] tmpTheta = new float[ringSizes[i]] ;
			
			float nearThetaDist = 0 ;
			if(randomStarThetaRotationEnable){
				float higherTheta = 0 ;
				float lowerTheta = 180 ;
				if(i!=0){
					higherTheta = ringAngles[i-1] ;
				}
				if(i!=ringSizes.length-1){
					lowerTheta = ringAngles[i+1] ;
				}
				float higherDist = angle - higherTheta ;
				float lowerDist = lowerTheta - angle ;
				
				nearThetaDist = higherDist ;
				if(higherDist>lowerDist){
					nearThetaDist = lowerDist ;
				}
			}
			for(int j=0 ; j<ringSizes[i] ; j++){
				float phisRotationRandomDist = 0 ;
				float thetasRotationRandomDist = 0 ;
				
				if(randomStarPhiRotationEnable){
					phisRotationRandomDist =  (-splitCircle/2 + randomPhi.nextFloat()*splitCircle) * randomStarPhiRotationRate ;
				}
				if(randomStarThetaRotationEnable){
					thetasRotationRandomDist = (-nearThetaDist/2 + randomTheta.nextFloat()*nearThetaDist) * randomStarThetaRotationRate ;
				}

				tmpPhis[j] = (float)Math.toRadians(splitCircle*j+phisRotationRandomDist) ;
				tmpTheta[j] = (float)Math.toRadians(angle + thetasRotationRandomDist) ;
			}
			phis[i] =tmpPhis ;
			thetas[i] = tmpTheta ;
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

		phis = null ;
		thetas = null ;
		radiusesForScene = null ; // radius 半径
		fallDistanceForScene = null ;
		xyz = null ;
		sceneCount = 0 ;
		v = 0 ;
	}	
	
	
	public static void setRandomStarPhiRotationEnable(boolean b){
		randomStarPhiRotationEnable = b ;
	}
	public static void setRandomStarThetaRotationEnable(boolean b){
		randomStarThetaRotationEnable = b ;
	}
	public static void setRandomStarFirstSpeedEnable(boolean b){
		randomStarFirstSpeedEnable = b ;
	}
	public static void setRandomStarFlashEnable(boolean b){
		randmoStarFlashEnable = b ; 
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
		int tailCount = 10 ;
		if(scene<=tailCount){
			tailCount = scene-1 ;
		}
		int bufIndex = 0 ;
		for(int s=0 ; s<tailCount+1 ; s++){
			for(int i=0 ; i<ringSizes.length ; i++){
				int size = ringSizes[i] ;
				for(int j=0 ; j<size ; j++){ 
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
		int tailCount = 10 ;
		if(scene<=tailCount){
			tailCount = scene-1 ;
		}

		float preBuff_xyz[] = new float[allSize*(tailCount+1)*3] ;
		float fallDistance = fallDistanceForScene[scene] ;
		int bufCount = 0 ;

		for(int s=scene-1 ; s>=scene-tailCount-1 ; s--){
			int starIndex = 0 ;
			for(int i=0 ; i<ringSizes.length ;i++){
				for(int j=0 ; j<ringSizes[i] ; j++){
					float r = radiusesForScene[s][starIndex] ;

					float x = (float) (r*Math.sin(thetas[i][j])*Math.sin(phis[i][j])) ;
					float y = (float) (r*Math.cos(thetas[i][j])) ;
					float z = (float) (r*Math.sin(thetas[i][j])*Math.cos(phis[i][j])) ;
					preBuff_xyz[bufCount] = x + startX ;
					preBuff_xyz[bufCount+1] = y + startY - fallDistance;
					preBuff_xyz[bufCount+2] = z + startZ ;
					bufCount+=3 ;
					starIndex++ ;
				}
			}
		}
		
		return preBuff_xyz ;
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
