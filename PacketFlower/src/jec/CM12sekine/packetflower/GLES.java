package jec.CM12sekine.packetflower;
import android.opengl.GLES20;
import android.opengl.Matrix;

//シェーダ操作
public class GLES {
    //頂点シェーダのコード
    private final static String VERTEX_CODE=
        "uniform mat4 u_MMatrix;"+//(A)
        "uniform mat4 u_PMatrix;"+//(A)
        "attribute vec4 a_Position;"+
        "void main(){"+
            "gl_Position=u_PMatrix*u_MMatrix*a_Position;"+
            "gl_PointSize = 100.0/gl_Position.w;"+
        "}";
    
    //フラグメントシェーダのコード
    private final static String FRAGMENT_CODE= 
        "precision mediump float;"+
        "uniform sampler2D u_Tex;"+
        "uniform vec4 u_Color;"+

        "void main(){"+
        	"gl_FragColor=texture2D(u_Tex,gl_PointCoord)* vec4(u_Color);"+//(C)
        "}";
    
    //システム
    private static int program;//プログラムオブジェクト
    
    //ハンドル
    public static int mMatrixHandle; //モデルビュー行列ハンドル
    public static int pMatrixHandle; //射影行列ハンドル
    public static int positionHandle;//位置ハンドル
    public static int colorHandle;   //色ハンドル 
    public static int texHandle;      //テクスチャハンドル    

    //行列
    public static float[] mMatrix=new float[16];//モデルビュー行列
    public static float[] pMatrix=new float[16];//射影行列
    
    //プログラムの生成
    public static void makeProgram() {
        //シェーダーオブジェクトの生成
        int vertexShader=loadShader(GLES20.GL_VERTEX_SHADER,VERTEX_CODE);
        int fragmentShader=loadShader(GLES20.GL_FRAGMENT_SHADER,FRAGMENT_CODE);
        
        //プログラムオブジェクトの生成
        program=GLES20.glCreateProgram();
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);
        GLES20.glLinkProgram(program);
        
        //ハンドルの取得
        mMatrixHandle=GLES20.glGetUniformLocation(program,"u_MMatrix");//(B)
        pMatrixHandle=GLES20.glGetUniformLocation(program,"u_PMatrix");//(B)
        positionHandle=GLES20.glGetAttribLocation(program,"a_Position");
        colorHandle=GLES20.glGetUniformLocation(program,"u_Color");
        texHandle=GLES20.glGetUniformLocation(program,"u_Tex");

        //プログラムオブジェクトの利用開始
        GLES20.glUseProgram(program);    
    }
    
    //シェーダーオブジェクトの生成
    private static int loadShader(int type,String shaderCode) {
        int shader=GLES20.glCreateShader(type); 
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    
    //行列をシェーダに指定
    public static void updateMatrix() {
        //モデルビュー行列をシェーダに指定
        GLES20.glUniformMatrix4fv(mMatrixHandle,1,false,mMatrix,0);//(B)
        
        //射影行列をシェーダに指定
        GLES20.glUniformMatrix4fv(pMatrixHandle,1,false,pMatrix,0);//(B)
    }
    
    //透視変換の指定(C)
    public static void gluPerspective(float[] m,
        float angle,float aspect,float near,float far) {
        float top=near*(float)Math.tan(angle*(Math.PI/360.0));
        float bottom=-top;
        float left=bottom*aspect;
        float right=top*aspect;
        float[] frustumM=new float[16];
        Matrix.frustumM(frustumM,0,left,right,bottom,top,near,far);
    	multiplyMM(m,frustumM);
    }
    
    //ビュー変換の指定(D)
    public static void gluLookAt(float[] m,
        float eyeX,float eyeY,float eyeZ,
        float focusX,float focusY,float focusZ,
        float upX,float upY,float upZ) {
        float[] lookAtM=new float[16];
        Matrix.setLookAtM(lookAtM,0,
            eyeX,eyeY,eyeZ,focusX,focusY,focusZ,upX,upY,upZ);
    	multiplyMM(m,lookAtM);
    }
    
    //行列同士の乗算
    public static void multiplyMM(float[] m0,float[] m1) {
        float[] resultM=new float[16];
        Matrix.multiplyMM(resultM,0,m0,0,m1,0);
        System.arraycopy(resultM,0,m0,0,16);
    }
}