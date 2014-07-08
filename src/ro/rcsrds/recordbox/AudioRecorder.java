package ro.rcsrds.recordbox;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
//Pentru a incepe o inregistrare apelati startRecording, pentru a o incheia apelati stopRecording.
//Aveti grija sa nu apelati starRecording in timp ce inregistreaza(Ex:dati disable la butonu de record si renable la stop)
@SuppressLint("SimpleDateFormat")
public class AudioRecorder {
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private String lastFile=null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    
    private boolean canRecord=true;
    private boolean canPlay=false;
    private boolean canPause=false;
    private boolean canRecPause=false;
    private boolean isRecording=false;
    private boolean isPlaying=false;
    private boolean isPlayPaused=false;
    private boolean isRecPaused=false;
    private int playingPausedAt;
    private int recstoped;
    
    
    //Setam directorul unde se salveaza fisierul
    public AudioRecorder() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();        
    } 
    
	public void startRecording() {
		if((canRecord)&&(!isRecording)){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String currentDateandTime = df.format(c.getTime());
        lastFile=mFileName+"/"+currentDateandTime+".3gp";
        
        mRecorder.setOutputFile(lastFile);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
        canRecord=false;
        canPlay=false;
        canRecPause=true;
        isRecording=true;
		}
		if(isRecording&&canRecPause){
			///In Developement
		}
		
    }
	
	/*public void pauseRecording(){
		if(isRecording)
	}
	
	public void resumeRecording(){
		
	}*/
	
	private void mergeAudio(String file1,String file2,String finalFile)
	{
		
	}
	
    public void stopRecording() {
    	if((!canRecord)&&(isRecording)){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        canRecord=true;
        canPlay=true;
        isRecording=false;
    	}
    }
    
    public void cancelRecording(){
    	if((!canRecord)&&(isRecording))
    	{
    		stopRecording();
    		File temp = new File(lastFile);
    		temp.delete();
    		lastFile=null;
    		canPlay=false;
    	}
    }
    
    public void startPlaying() {
    	if((canPlay)&&(!isPlaying)){
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(lastFile);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        canPause=true;
        canPlay=false;
        canRecord=false;
        isPlaying=true;
        
    	}
    	else if((canPause)&&(isPlaying)){
    		mPlayer.pause();
    		playingPausedAt = mPlayer.getCurrentPosition();
    		canPause=false;
    		isPlaying=false;
    		isPlayPaused=true;
    	}
    	else if(isPlayPaused){
    		mPlayer.seekTo(playingPausedAt);
    		mPlayer.start();
    		canPause=true;
    		isPlayPaused=false;
    		isPlaying=true;
    	}    
    }
    
    public void stopPlaying() {
    	if((isPlaying)||(isPlayPaused)){
        mPlayer.release();
        mPlayer = null;
        canPlay=true;
        canPause=false;
        canRecord=true;
        isPlaying=false;
        isPlayPaused=false;
    	}
    }
    
    /*public void pausePlaying(){
    	if((canPause)&&(isPlaying)){
    		mPlayer.pause();
    		playingPausedAt = mPlayer.getCurrentPosition();
    		canPause=false;
    		isPlaying=false;
    		isPlayPaused=true;
    	}
    }
    
    public void resumePlaying(){
    	if(isPlayPaused){
    		mPlayer.seekTo(playingPausedAt);
    		mPlayer.start();
    		canPause=true;
    		isPlayPaused=false;
    		isPlaying=true;
    	}
    }//*/
    
}
