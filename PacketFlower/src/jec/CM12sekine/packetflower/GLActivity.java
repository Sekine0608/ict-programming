package jec.CM12sekine.packetflower;

import jec.CM12sekine.packetflower.fireworkelement.FireworkBotan;
import jec.CM12sekine.packetflower.sounds.SoundPoolManager;
import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

//カメラの設定
public class GLActivity extends Activity implements Runnable , View.OnTouchListener {
	FireworksController fireworksController = FireworksController.getInstance() ;
	private static final int MENU1 = 1 ;
	private static final int PIX_WIGHT = 20 ;
	private static final int MIN_THETA = 5 ;
	private static final int MAX_THETA = 90 ;
	private static final int MIN_PHI = 0 ; 
	private static final int MAX_PHI = 360 ;
	private static final float WALK_SPEED = 0.1f ;
	private static final String TAG = "GLActivity" ;
	private static final int UI_DEFAULT_WIDTH = 240 ;
	private static final int UI_DEFAULT_HEIGHT = 240 ;
    private GLSurfaceView glView;
    private boolean nowTouchUI = false; 
    private boolean nowTouchScreen = false ;
    private float nowTouchX = 0 ; 
    private float nowTouchY= 0 ;
    private Thread thread = null ;
    private GLRenderer renderer ;
    private RelativeLayout ui ;
    private RelativeLayout ui_onoff ;
    private Button button_StartAndStop ;
    private Button button_debugTextOnOrOff ;
    private MessageBoard messageBoard ;
    //アクティビティ生成時に呼ばれる
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                // ダイアログ表示など特定の処理を行いたい場合はここに記述
                // 親クラスのdispatchKeyEvent()を呼び出さずにtrueを返す
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SoundPoolManager.getInstance().initSounds(this) ;
        
        FrameLayout fl = new FrameLayout(this);
        setContentView(fl);
        TextView message = new TextView(this) ;
        messageBoard = new MessageBoard(message) ;
        //GLサーフェイスビュー
        glView=new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        renderer = new GLRenderer(this, messageBoard) ;
        glView.setRenderer(renderer);
        glView.setOnTouchListener(this);
        fl.addView(glView);
        fl.addView(message);
        
        ui = (RelativeLayout)this.getLayoutInflater().inflate(R.layout.ui, null);
        FrameLayout.LayoutParams ui_lp = new FrameLayout.LayoutParams(240,240);
        ui_lp.gravity= Gravity.BOTTOM|Gravity.RIGHT; 
        ui.setLayoutParams(ui_lp);
        fl.addView(ui) ;

        
        ui_onoff = (RelativeLayout)this.getLayoutInflater().inflate(R.layout.ui_onoff, null) ;
        FrameLayout.LayoutParams uionoff_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 120) ;
        uionoff_lp.gravity = Gravity.TOP|Gravity.RIGHT ;
        
        ((TextView)ui_onoff.findViewById(R.id.button_reset_xy)).setTextColor(0x50ffffff);
        ui_onoff.findViewById(R.id.button_reset_xy).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetRenderXY(); 
			}
		});
        
        ui_onoff.findViewById(R.id.button_onoff_ui).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(ui.getVisibility()){
				case View.VISIBLE:
					ui.setVisibility(View.INVISIBLE);
					button_StartAndStop.setVisibility(View.INVISIBLE);
					((TextView)ui_onoff.findViewById(R.id.button_onoff_ui)).setTextColor(0x50ffffff);
					break; 
				case View.INVISIBLE:
					ui.setVisibility(View.VISIBLE);
					button_StartAndStop.setVisibility(View.VISIBLE);
					((TextView)ui_onoff.findViewById(R.id.button_onoff_ui)).setTextColor(0xffffffff);
					break; 
				}
			}
		});

        ui_onoff.findViewById(R.id.button_onoff_text).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				messageBoard.toggleDisplay();
				if(messageBoard.getDisplay()){
					((TextView) ui_onoff.findViewById(R.id.button_onoff_text)).setTextColor(0xffffffff);
				}else{
					((TextView) ui_onoff.findViewById(R.id.button_onoff_text)).setTextColor(0x50ffffff);
				}
			}
		});
        fl.addView(ui_onoff);

        button_StartAndStop = new Button(this) ;
        LayoutParams sas_lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT) ;
        sas_lp.gravity = Gravity.BOTTOM|Gravity.LEFT; 
        button_StartAndStop.setLayoutParams(sas_lp);

        fireworksController.setMessageBoard(messageBoard);
        if(fireworksController.isHalt()){
        	button_StartAndStop.setText("再開");
   
        }else{
        	button_StartAndStop.setText("停止");
        }
        fl.addView(button_StartAndStop);
       
        button_StartAndStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fireworksController = FireworksController.getInstance() ;
				if(fireworksController.isHalt()){
					fireworksController.start(); 
					button_StartAndStop.setText("停止");
				}else{
					fireworksController.stop(); 
					button_StartAndStop.setText("再開");
				}
			}
		});
        
        ui.findViewById(R.id.right).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float phi = renderer.getUserPhi() ;
				double rad = Math.toRadians(phi); 
				float z = WALK_SPEED * (float)Math.sin(rad) ;
				float x = WALK_SPEED * (float)Math.cos(rad) ;
				onTouchUI(event, x, 0f, z);
				return false;
			}
		} );
        
        ui.findViewById(R.id.left).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float phi = renderer.getUserPhi() ;
				double rad = Math.toRadians(phi); 
				float z = -WALK_SPEED * (float)Math.sin(rad) ;
				float x = -WALK_SPEED * (float)Math.cos(rad) ;
				onTouchUI(event, x, 0f, z);
				return false;
			}
		} );
        
        ui.findViewById(R.id.front).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float phi = renderer.getUserPhi() ;
				double rad = Math.toRadians(phi); 
				float z = WALK_SPEED * (float)Math.cos(rad) ;
				float x = WALK_SPEED * -(float)Math.sin(rad) ;
				onTouchUI(event, x, 0f, z);
				return false;
			}
		} );
        
        ui.findViewById(R.id.back).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float phi = renderer.getUserPhi() ;
				double rad = Math.toRadians(phi); 
				float z = -WALK_SPEED * (float)Math.cos(rad) ;
				float x = -WALK_SPEED * -(float)Math.sin(rad) ;
				onTouchUI(event, x, 0f, z);

				return false;
			}
		} );
        
        
        thread = new Thread(this) ;
        thread.start();
    }
    
    public void resetRenderXY(){
    	renderer.setUserX(0);
    	renderer.setUserY(0);
    	renderer.setUserZ(-8);
    	renderer.setUserTheta(70);
    	renderer.setUserPhi(0);
    	    
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	// TODO Auto-generated method stub
    	
    	onTouchScreen(event);
    	return true;
    }
    
    public void onTouchScreen(MotionEvent event){
    	Log.v(TAG, "onTouchScreen" + event.getAction());
    	switch(event.getActionMasked()){
    	case MotionEvent.ACTION_POINTER_DOWN:
    	case MotionEvent.ACTION_DOWN:

    		nowTouchX = event.getX() ;
    		nowTouchY = event.getY() ;
    		break ;
    	
		case MotionEvent.ACTION_MOVE:
			
			float deltaPhi = -(event.getX() - nowTouchX)/PIX_WIGHT ;
			float deltaTheta =  (event.getY() - nowTouchY)/PIX_WIGHT ;
			renderer.setUserTheta(deltaTheta + renderer.getUserTheta());
			renderer.setUserPhi(deltaPhi + renderer.getUserPhi());

			if(renderer.getUserPhi()<=MIN_PHI){
				renderer.setUserPhi(360);
			}else if(renderer.getUserPhi()>=MAX_PHI){
				renderer.setUserPhi(0);
			}
		
			if(renderer.getUserTheta()<=MIN_THETA){
				renderer.setUserTheta(MIN_THETA);
			}else if(renderer.getUserTheta()>=MAX_THETA){
				renderer.setUserTheta(MAX_THETA);
			}
			
        	Log.v(TAG, "nX:" + nowTouchX+ " nY:" + nowTouchY);
        	nowTouchX = event.getX();
        	nowTouchY = event.getY();
 			break ;
   	
		case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
        case MotionEvent.ACTION_CANCEL:		
        default:
        	
        	nowTouchX = 0 ;
        	nowTouchY = 0 ;
    		nowTouchScreen = false ;
        	break;

    	}
    }
    
    public void onTouchUI(MotionEvent event, final float x, final float y, final float z){
    	switch (event.getActionMasked() ) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if(nowTouchUI == false){
				nowTouchUI = true ;
				new Thread(new Runnable() {
					@Override
					public void run() {
						while(nowTouchUI){
							if(x!=0){
								renderer.setUserX(renderer.getUserX()-x);
							}
							if(y!=0){
								renderer.setUserY(renderer.getUserY()+y);
							}
							if(z!=0){
								renderer.setUserZ(renderer.getUserZ()+z);
							}

							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}
					}
				}).start();
			}
		
			break;
			
		case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
        case MotionEvent.ACTION_CANCEL:					
		default:
			nowTouchUI = false ; 
			break;
		}
    }
  //スレッドの処理
    public void run() {
        while(thread!=null) {
            //定期処理
            renderer.onTick();
            //スリープ
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }        
        }
    }
    
    //アクティビティレジューム時に呼ばれる
    @Override
    public void onResume() {
        Log.v("GLActivity", "onResume()" ); 

        ui.setVisibility(View.VISIBLE);
		button_StartAndStop.setVisibility(View.VISIBLE);
		((TextView)ui_onoff.findViewById(R.id.button_onoff_ui)).setTextColor(0xffffffff);
        
		resetRenderXY(); 
        super.onResume();
        glView.onResume();
        
    }
    
    //アクティビティポーズ時に呼ばれる
    @Override
    public void onPause() {
        Log.v("GLActivity", "onPause()" ); 
        button_StartAndStop.setText("再開");
        super.onPause();
        glView.onPause();
        thread = null ;
        fireworksController.stop();
    }
    
}