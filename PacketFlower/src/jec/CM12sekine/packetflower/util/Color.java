package jec.CM12sekine.packetflower.util;

public class Color {
	public static Color START_WHITE = new Color(1f,1f,1f,1f) ;
	public static Color END_BLACK = new Color(0f,0f,0f,0f) ;

	public static Color RED = new Color(1f,0.5f,0.5f,0.9f) ;
	public static Color LIGHT_RED = new Color(1f,0.8f,0.8f,0.9f) ;
	public static Color CLEAR_RED = new Color(1f,0.5f,0.5f,1f) ;

	public static Color GREEN = new Color(0.5f,1f,0.5f,0.9f) ;
	public static Color LIGHT_GREEN = new Color(0.8f,1.0f,0.8f,0.9f) ;
	public static Color CLEAR_GREEN = new Color(0.5f,1f,0.5f,1f) ;

	public static Color BLUE = new Color(0.5f,0.5f,1.0f,0.9f) ;
	public static Color LIGHT_BLUE = new Color(0.8f,0.8f,1.0f,0.9f) ;
	public static Color CLEAR_BLUE = new Color(0.5f,0.5f,1f,1f) ;

	public static Color YELLOW = new Color(1f,1f,0.5f,0.9f) ;
	public static Color LIGHT_YELLOW = new Color(1f,1f,0.8f,0.9f) ;

	public static Color PINK = new Color(1f,0.5f,1.0f,0.9f) ;
	public static Color LIGHT_PINK = new Color(1f,0.8f,1.0f,0.9f) ;

	public static Color SKYBLUE = new Color(0.5f,1.0f,1.0f,0.9f) ;
	public static Color LIGHT_SKYBLUE = new Color(0.8f,1.0f,1.0f,0.9f) ;
	
	private int value = 0xFF << 24 ;
	public Color(int r, int g, int b, int a){
		value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
	}
	public Color(int r, int g, int b){
		this(r,g,b,255) ;
	}
	
	public Color(float r, float g, float b, float a){
		this((int)(r*255), (int)(g*255), (int)(b*255), (int)(a*255));
	}
	public Color(float r, float g, float b){
		this(r,g,b,1f) ;
	}
		
	public float[] getValueAsFloatArray(){
		return new float[]{(float)((value >> 16) & 0xFF)/(float)255,(float)((value >> 8) & 0xFF)/(float)255,(float)((value >> 0) & 0xFF)/(float)255,(float)((value >> 24) & 0xFF)/(float)255} ;
	}
	public int[] getValueAsIntArray(){
		return new int[]{(value >> 16) & 0xFF,(value >> 8) & 0xFF,(value >> 0) & 0xFF,(value >> 24) & 0xFF} ;
	}
	public String toString(){
		return "R:" + ((value >> 16) & 0xFF) + " G:" + ((value >> 8) & 0xFF) + " B:" + ((value >> 0) & 0xFF) + " A:" + ((value >> 24) & 0xFF) ;
	}
	
}
