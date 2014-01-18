package jec.CM12sekine.packetflower;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jec.CM12sekine.packetflower.fireworkelement.FireworkBotan;
import jec.CM12sekine.packetflower.util.Color;
import jec.CM12sekine.packetflower.util.SeedGenerator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

//レンダラー
public class GLRenderer implements 
    GLSurfaceView.Renderer{
	//左上に表示するメッセージボード
	private MessageBoard messageBoard =null ;
	
    //システム
    private float aspect;//アスペクト比
    
    private float angle = 0  ; //46 ballY = 500 が適
    
    //バッファ
    private FloatBuffer[] vertexBuffers;//頂点バッファ
    int time = 0 ;
    
    private int textureId;//テクスチャID

    private Activity activity ;
    
    private FireworksController fireworksController ;
    
    private float userX = 0 ;
    private float userY = 0 ;
    private float userZ = -8 ;
    private float userTheta = 90 ;
    private float userPhi = 0 ;
    
    public GLRenderer(Activity activity, MessageBoard messageBoard) {
        this.activity=activity;
        this.messageBoard = messageBoard ;
    }
    //サーフェイス生成時に呼ばれる
    @Override
    public void onSurfaceCreated(GL10 gl10,EGLConfig eglConfig) {
        //プログラムの生成
        GLES.makeProgram();
        
        //頂点配列の有効化
        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(false); //デプスバッファを読み込み専用にする
        
        //テクスチャの有効化
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        //テクスチャの生成
        Bitmap bmp=BitmapFactory.decodeResource(
            activity.getResources(),R.drawable.particle128);
        textureId=makeTexture(bmp);
        
        //ブレンドの指定
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE); 
        //GLES20.GL_ONEは色を加算 通常はGLES20.GL_ONE_MINUS_SRC_ALPHA
        
        FireworkBotan.setRandomStarFirstSpeedEnable(true);
        FireworkBotan.setRandomStarPhiRotationEnable(true);
        FireworkBotan.setRandomStarThetaRotationEnable(true);
        FireworkBotan.setRandomStarFlashEnable(true);
        SeedGenerator seedGenerator = SeedGenerator.getInstance() ;
        
        
        fireworksController = FireworksController.getInstance() ;
        if(!fireworksController.isHalt()){
        	fireworksController.start(); 
        }
    }
   
    public void setUserX(float userX){
    	this.userX = userX ;
    }
    public void setUserY(float userY){
    	this.userY = userY ;
    }
    public void setUserZ(float userZ){
    	this.userZ = userZ ;
    }
    public void setUserTheta(float userTheta){
    	this.userTheta = userTheta ;
    }
    public void setUserPhi(float userPhi){
    	this.userPhi= userPhi;
    }
    public float getUserX(){
    	return userX ;
    }
    public float getUserY(){
    	return userY ;
    }
    public float getUserZ(){
    	return userZ ;
    }
    public float getUserTheta(){
    	return userTheta ;
    }
    public float getUserPhi(){
    	return userPhi;
    }
    // 定期処理
    public void onTick(){
    	String userXYZ = "X:" + userX + " Y:" + userY + " Z:" + userZ ;
    	String userRot = "Theta:" + userTheta +  " Phi:" + userPhi ; 
    	messageBoard.setLocation(userXYZ + "\n" + userRot + "\n");
    }
    //画面サイズ変更時に呼ばれる
    @Override
    public void onSurfaceChanged(GL10 gl10,int w,int h) {
        //ビューポート変換(4)
        GLES20.glViewport(0,0,w,h);
        aspect=(float)w/(float)h;
    }
    
    //毎フレーム描画時に呼ばれる

    @Override
    public void onDrawFrame(GL10 gl10) {
        
        //画面のクリア
        GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);  
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        //射影変換(3)
        Matrix.setIdentityM(GLES.pMatrix,0);
        GLES.gluPerspective(GLES.pMatrix,
            45.0f,  //Y方向の画角
            aspect, //アスペクト比
            1.0f,   //ニアクリップ
            100.0f);//ファークリップ
        
        
        float userThetaRad = (float) Math.toRadians(userTheta) ;
        float userPhiRad = (float) Math.toRadians(userPhi) ;
        float x = (float) (Math.sin(userThetaRad)*Math.sin(userPhiRad)) ;
		float y = (float) (Math.cos(userThetaRad)) ;
		float z = (float) (Math.sin(userThetaRad)*Math.cos(userPhiRad)) ;
     
       
		float eyeX = userX ;
        float eyeY = userY ;
        float eyeZ = userZ ;
        float focusX = userX+10*x ;
        float focusY = userY+10*y ;
        float focusZ = userZ+10*z ;
        
        //ビュー変換(2)
        Matrix.setIdentityM(GLES.mMatrix,0);
        GLES.gluLookAt(GLES.mMatrix,
            eyeX,eyeY,eyeZ,//カメラの視点
            focusX,focusY,focusZ, //カメラの焦点
            0.0f,1.0f,0.0f);//カメラの上方向

        //モデル変換(1)
        Matrix.translateM(GLES.mMatrix,0,0.0f,0.0f,0.0f);
        
        //行列をシェーダに指定
        GLES.updateMatrix();
        
        fireworksController.drawFireworks(textureId);

        time++ ;
    }
    
  //テクスチャの生成
    private int makeTexture(Bitmap bmp) {
        //テクスチャメモリの確保
        int[] textureIds=new int[1];
        GLES20.glGenTextures(1,textureIds,0);
        
        //テクスチャへのビットマップ指定
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureIds[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bmp,0);
        
        //テクスチャフィルタの指定
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
        return textureIds[0];
    }
    
    //float配列をFloatBufferに変換
    private FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb=ByteBuffer.allocateDirect(array.length*4).order(
            ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }
}