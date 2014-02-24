package jec.CM12sekine.packetflower.sounds;


import jec.CM12sekine.packetflower.R;
import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class SoundPoolManager implements OnLoadCompleteListener{
	private static SoundPoolManager instance = null ;
	public static SoundPoolManager getInstance(){
		if(instance == null){
			instance = new SoundPoolManager() ;
		}
		return instance ;
	}
	
	private boolean isComplete_bomb ;
	private boolean isComplete_tail ;
	
	private int soundId_bomb ;
	private int soundId_tail ;
	public static final int SOUND_BOMB = 1;
	public static final int SOUND_TAIL = 2;
	
	private SoundPool soundPool = null ;
	private SoundPoolManager(){
	}
	public void initSounds(Activity activity){
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		soundId_bomb = soundPool.load(activity, R.raw.bomb4, 1);
		soundId_tail = soundPool.load(activity, R.raw.tail3, 1);
		soundPool.setOnLoadCompleteListener(this);
	}
	public void playSound(int sound){
		if(sound == SOUND_BOMB && isComplete_bomb){
                soundPool.play(soundId_bomb, 1.0f, 1.0f, 1, 0, 1.0f);
		}else if(sound == SOUND_TAIL && isComplete_tail){
                soundPool.play(soundId_tail, 0.2f, 0.2f, 1, 0, 1.0f);
		}
	}
	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		if(sampleId == SOUND_BOMB){
			isComplete_bomb = true ;
		}else if(sampleId == SOUND_TAIL){
			isComplete_tail = true ;
		}
	}
}
