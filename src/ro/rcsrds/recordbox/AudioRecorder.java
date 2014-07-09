package ro.rcsrds.recordbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String filePath = null;
    private String lastFile=null;
    private ArrayList<String> tempAudio = new ArrayList<String>();
    
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    
    private boolean canRecord=true;
    private boolean canPlay=false;
    private boolean canPause=false;
    private boolean canRecPause=false;
    private boolean isRecording=false;
    private boolean isPlaying=false;
    private boolean isPlayPaused=false;
    private boolean isRecPaused=false;
    private int playingPausedAt;
    private int recPaused = 0;
    private int recMerged = 0;
    
    
    //Setam directorul unde se salveaza fisierul
    public AudioRecorder() {
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath();        
    } 
    
	public void startRecording() {
		if((canRecord)&&(!isRecording)){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String currentDateandTime = df.format(c.getTime());
        lastFile=filePath+"/"+currentDateandTime+".3gp";
        
        tempAudio.add(filePath+"/"+"temp("+recPaused+").3gp");
        
        mRecorder.setOutputFile(tempAudio.get(0));
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
		//PauseRecording
		else if((isRecording)&(canRecPause)){
			mRecorder.stop();
			mRecorder.release();
			recPaused++;
			mRecorder = null;
			isRecording = false;
			canRecPause = false;
			isRecPaused = true;			
		}
		//ResumeRecording
		else if(isRecPaused){
			if(recPaused==1){
			mRecorder = new MediaRecorder();
	        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	        
	        tempAudio.add(filePath+"/"+"temp("+recPaused+").3gp");
	        
	        mRecorder.setOutputFile(tempAudio.get(recPaused));
	        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	        try {
	            mRecorder.prepare();
	        } catch (IOException e) {
	            Log.e(LOG_TAG, "prepare() failed");
	        }

	        mRecorder.start();
	        isRecording = true;
	        canRecPause = true;
	        isRecPaused = false;
			}
			else if(recPaused==2){
				try {
					String tempMerge=mergeAudio(tempAudio.get(0), tempAudio.get(1));
					recMerged++;
					tempAudio = new ArrayList<String>();
					tempAudio.add(tempMerge);
					recPaused--;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mRecorder = new MediaRecorder();
		        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		        
		        tempAudio.add(filePath+"/"+"temp("+recPaused+").3gp");
		        
		        mRecorder.setOutputFile(tempAudio.get(recPaused));
		        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		        try {
		            mRecorder.prepare();
		        } catch (IOException e) {
		            Log.e(LOG_TAG, "prepare() failed");
		        }

		        mRecorder.start();
		        isRecording = true;
		        canRecPause = true;
		        isRecPaused = false;
			}
	        
	        
		}
		
    }
	
    public void stopRecording() {
    	if((isRecording)||(isRecPaused)){
        mRecorder.stop();
        mRecorder.release();
        
        if(tempAudio.size()==1)
        {
        	renameFile(tempAudio.get(0),lastFile);
        	tempAudio=new ArrayList<String>();
        }
        else if(tempAudio.size()==2)
        {        	
        	try {
				String tempMerge=mergeAudio(tempAudio.get(0), tempAudio.get(1));
				deleteFile(tempAudio.get(0));
				deleteFile(tempAudio.get(1));
				recMerged++;
				tempAudio = new ArrayList<String>();
				tempAudio.add(tempMerge);
				recPaused--;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	renameFile(tempAudio.get(0),lastFile);
        	tempAudio=new ArrayList<String>();
        	tempAudio=new ArrayList<String>();
        }
        
        mRecorder = null;
        canRecord=true;
        canPlay=true;
        isRecording=false;
        recPaused=0;
        recMerged=0;
    	}
    }
    
    public void cancelRecording(){
    	if((isRecording)||(isRecPaused))
    	{
    		mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            canRecord=true;
            isRecording=false;
            recPaused=0;
            recMerged=0;
    		for(int i=0;i<tempAudio.size();i++){
    			deleteFile(tempAudio.get(i));
    		}
    		tempAudio=new ArrayList<String>();
    		lastFile=null;
    		canPlay=false;
    	}
    }
    
    private void deleteFile(String filePath){
    	File temp = new File(filePath);
		temp.delete();
    }

	private void renameFile(String originalName,String newName)
	{
		File file = new File(originalName);
		File file2 = new File(newName);
		file.renameTo(file2);
	}
    
	private String mergeAudio(String file1,String file2) throws IOException
	{
		FileInputStream fistream1 = new FileInputStream(file1);  // first source file
        FileInputStream fistream2 = new FileInputStream(file2);//second source file
        SequenceInputStream sistream = new SequenceInputStream(fistream1, fistream2);
        FileOutputStream fostream = new FileOutputStream(filePath+"/"+"merge("+recMerged+").3gp");//destinationfile

        int temp;

        while( ( temp = sistream.read() ) != -1)
        {
            // System.out.print( (char) temp ); // to print at DOS prompt
            fostream.write(temp);   // to write to file
        }
        fostream.close();
        sistream.close();
        fistream1.close();
        fistream2.close();
		return filePath+"/"+"merge("+recMerged+").3gp";
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
