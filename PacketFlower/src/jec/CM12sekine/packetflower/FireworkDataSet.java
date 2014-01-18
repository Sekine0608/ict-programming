package jec.CM12sekine.packetflower;

import jec.CM12sekine.packetflower.fireworkelement.Firework;
import jec.CM12sekine.packetflower.util.Color;

public class FireworkDataSet {
	private Firework firework = null ;
	private int margin = 0;
	private String parseString = null ;
	public void setFirework(Firework firework){
		this.firework = firework;
	}
	public void setMargin(int margin){
		this.margin = margin;
	}
	public void setParseString(String parseString){
		this.parseString= parseString;
	}
	
	public Firework getFirework(){
		return firework; 
	}
	public int getMargin(){
		return margin; 
	}
	public String getParseString(){
		return parseString ;
	}
	
}
