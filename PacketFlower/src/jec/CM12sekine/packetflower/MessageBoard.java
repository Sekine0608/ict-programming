package jec.CM12sekine.packetflower;

import android.os.Handler;
import android.widget.TextView;

public class MessageBoard {
	private boolean display = true ;
	private TextView textMessage ;
	private String location ;
	private String packetInfo ;
	Handler handler = new Handler();
				
	public MessageBoard(TextView textMessage) {
		this.location = "" ;
		this.packetInfo = "" ; 
		this.textMessage = textMessage ;
	}
	
	public boolean getDisplay(){
		return display ;
	}
	
	public void toggleDisplay(){
		if(display){
			
			display = false ;
			handler.post(new Runnable() {
				@Override
				public void run() {
                        textMessage.setText("") ;
				}
			}) ;		
			
		}else{
			display = true ;
			// setText(location+packetInfo) ;
			setText(packetInfo) ;
		}
	}
	public void setLocation(String location){
		if(!this.location.equals(location)){
			this.location = location ;
			// setText(location+packetInfo) ;
			setText(packetInfo) ;
		}
	}
	public void setPacketInfo(String packetInfo){
		if(!this.packetInfo.equals(packetInfo)){
			this.packetInfo = packetInfo ;

			//setText(location+packetInfo) ;
			setText(packetInfo) ;
		}
		
	}
	private void setText(final String s){
		if(!s.equals(textMessage.getText().toString())){
			handler.post(new Runnable() {
				@Override
				public void run() {
					if(display){
                        textMessage.setText(s) ;
					}
				}
			}) ;
		}
	}
}
