package jec.CM12sekine.packetflower.fireworkelement;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import jec.CM12sekine.packetflower.GLES;
import jec.CM12sekine.packetflower.util.Color;

public class FireworkTail extends Firework implements Runnable{
	private boolean isAfterFirstDraw = false ;
	private long seed ;
	private boolean isComplete = false ;
	private int sceneCount = 0 ;

	private int lifeMilliTime = 0; //あがりきるまでの描画時間
	private long startPacketFlowerSystemTime = -1 ;
	private long endPacketFlowerSystemTime = -1 ;

	private int drawQuality = 0 ;
	public static final int QUALITY_MAX = 1 ;
	public static final int QUALITY_HIGH = 10 ;
	public static final int QUALITY_MIDDLE = 30 ;
	public static final int QUALITY_LOW = 50 ;

	private static final String TAG = "FireworkBall" ;
    private FloatBuffer[] vertexBuffers;//頂点バッファ

    private int pointSize = 0 ;
    private float[][] topXYZ = null ;
	private float startX = 0 ;
	private float startY = 0 ; 
	private float startZ = 0 ;
	private float endX= 0 ;
	private float endY= 0 ; 
	private float endZ = 0 ;
	private Color baseColor = null ;
	
	private float[][] thetas = null ;
	private float[][] topDisXYZ = null ;//一番上の尾との距離
	private float[] radius = null ; // radius 半径
	private static float TOP_RADIUS = 0.01f ; // 一番上の点の半径

	private float[][] alpha = null ;

	public FireworkTail(long seed, int lifeMilliTime, int drawQuality, 
			int pointSize, float startX, float startY, float startZ, 
			float endX, float endY, float endZ, 
			Color baseColor){ 
		this.seed = seed ;
		this.lifeMilliTime = lifeMilliTime ;
		this.drawQuality = drawQuality ;
		this.startX = startX ;
		this.startY = startY ;
		this.startZ = startZ ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
		this.pointSize = pointSize ;
		this.sceneCount = lifeMilliTime/drawQuality; 
		

		new Thread(this).start();
		
	}
	public long getSeed(){
		return seed ;
	}
	public void run(){
		thetas = new float[sceneCount][] ;
		radius = new float[pointSize] ;
		topDisXYZ = new float[pointSize][] ;
		topXYZ = new float[sceneCount][] ;
		alpha = new float[sceneCount][];
		

		for(int scene=0 ; scene<sceneCount ; scene++){
			topXYZ[scene] = new float[3] ;
			topXYZ[scene][0] = startX + ((endX-startX)/sceneCount) * scene ;
			topXYZ[scene][1] = startY + ((endY-startY)/sceneCount) * scene ;
			topXYZ[scene][2] = startZ + ((endZ-startZ)/sceneCount) * scene ;
		}

		for(int scene=0 ; scene<sceneCount ; scene++){
			int screw = -scene*100 ;
			thetas[scene] = new float[pointSize] ;
			for(int i = 0 ; i<pointSize ; i++){
				thetas[scene][i] = (float) Math.toRadians(screw + i*10); 
			}
		}
		
		for(int i=0; i<pointSize ; i++){
			radius[i] = TOP_RADIUS*(pointSize-i)/pointSize ;
		}
		
		for(int i=0 ; i<pointSize ; i++){
			topDisXYZ[i] = new float[3] ;
			topDisXYZ[i][0] = -i*(endX-startX)/sceneCount ;
			topDisXYZ[i][1] = -i*(endY-startY)/sceneCount ;
			topDisXYZ[i][2] = -i*(endZ-startZ)/sceneCount ;
		}
		
		for(int scene=0 ; scene<sceneCount ; scene++){
			alpha[scene] = new float[pointSize] ;
			float topY = topXYZ[scene][1];
			float topAlpha = 0.9f*(1.0f - (float)scene/sceneCount);
			for(int i=0 ; i<pointSize ; i++){
				float y = topY + topDisXYZ[i][1] ;
				if(y>startY){
					alpha[scene][i] = topAlpha*(1.0f-(float)i/pointSize);	
				}
			}
		}

		float[][] xyz = new float[sceneCount][] ;
		for(int scene=0 ; scene<sceneCount ; scene++){
				xyz[scene] = nextVertexForScene(scene) ;
		}
		vertexBuffers = new FloatBuffer[sceneCount] ;
		for(int scene=0 ; scene<sceneCount ; scene++){
			vertexBuffers[scene] = makeFloatBuffer(xyz[scene]); 
		}
		isComplete = true ;
	}

	private float[] nextVertexForScene(int scene){
		float preBuff[] = new float[pointSize*3] ;
		float topX = topXYZ[scene][0] ;
		float topY = topXYZ[scene][1] ;
		float topZ = topXYZ[scene][2] ;
		for(int i=0 ; i<pointSize ; i++){
			float r = radius[i] ; 
			float x = topX + topDisXYZ[i][0] + r*(float) Math.cos(thetas[scene][i]) ;
			float y = topY + topDisXYZ[i][1] ;
			float z = topZ + topDisXYZ[i][2] +r*(float) Math.sin(thetas[scene][i]) ; 
			preBuff[i*3] =  x ;
			preBuff[i*3+1] = y ;
			preBuff[i*3+2] = z ; 
		}
		return preBuff;
	}
	@Override
	protected void initSeeds(long seed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return isComplete;
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
	
	@Override
	public void drawFireworks(int textureId, int time) {
		// TODO Auto-generated method stub
		int scene = time / drawQuality ;
		checkState(); 
		isAfterFirstDraw = true ;


		for(int i=0 ; i<pointSize ; i++){
			float r = 1.0f ; 
			float g = 0.5f ; 
			float b = 0.5f ; 
			float a = alpha[scene][i] ; 

			GLES20.glUniform4f(GLES.colorHandle, r, g, b, a);                
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
			GLES20.glUniform1i(GLES.texHandle,0);
        	GLES20.glVertexAttribPointer(GLES.positionHandle,3,
            GLES20.GL_FLOAT,false,0,vertexBuffers[scene]);
        	GLES20.glDrawArrays(GLES20.GL_POINTS,i,1);
		}
	}
	
	  //float配列をFloatBufferに変換
    private FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb=ByteBuffer.allocateDirect(array.length*4).order(
            ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

	private void checkState(){
		if(startPacketFlowerSystemTime==-1){
			throw new IllegalStateException("startPacketFlowerSystemTime==-1") ;
		}
	}
	@Override
	public boolean isAfterFirstDraw() {
		// TODO Auto-generated method stub
		return isAfterFirstDraw ;
	}
	
}
