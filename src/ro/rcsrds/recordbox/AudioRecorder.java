package ro.rcsrds.recordbox;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

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
	private RandomAccessFile randomAccessFile;
    
	//Setam directorul unde se salveaza fisierul
    public AudioRecorder() {
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DigiRecordBox";      
    } 
    
	public void startRecording() {
		if((canRecord)&&(!isRecording)){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String currentDateandTime = df.format(c.getTime());
        lastFile=filePath+"/"+currentDateandTime+".mp4";
        
        tempAudio.add(filePath+"/"+"temp("+recPaused+").mp4");
        
        mRecorder.setOutputFile(tempAudio.get(0));
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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
	        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	        
	        tempAudio.add(filePath+"/"+"temp("+recPaused+").mp4");
	        
	        mRecorder.setOutputFile(tempAudio.get(recPaused));
	        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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
				String tempMerge;
				try {
					tempMerge = mergeAudio(tempAudio.get(0), tempAudio.get(1));
					recMerged++;
					deleteFile(tempAudio.get(0));
					deleteFile(tempAudio.get(1));
					tempAudio = new ArrayList<String>();
					tempAudio.add(tempMerge);
					recPaused--;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				mRecorder = new MediaRecorder();
		        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		        
		        tempAudio.add(filePath+"/"+"temp("+recPaused+").mp4");
		        
		        mRecorder.setOutputFile(tempAudio.get(recPaused));
		        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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
    	if((isRecording)){
        mRecorder.stop();
        mRecorder.release();
        
        if(tempAudio.size()==1)
        {
        	renameFile(tempAudio.get(0),lastFile);
        	tempAudio=new ArrayList<String>();
        }
        else if(tempAudio.size()==2)
        {        	
        	String tempMerge;
			try {
				tempMerge = mergeAudio(tempAudio.get(0), tempAudio.get(1));
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
    	else if((isRecPaused)){            
            
            if(tempAudio.size()==1)
            {
            	renameFile(tempAudio.get(0),lastFile);
            	tempAudio=new ArrayList<String>();
            }
            else if(tempAudio.size()==2)
            {        	
            	String tempMerge;
				try {
					tempMerge = mergeAudio(tempAudio.get(0), tempAudio.get(1));
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
            
            canRecord=true;
            canPlay=true;
            isRecording=false;
            recPaused=0;
            recMerged=0;
            isRecPaused=false;
        	}
    }
    
    public void cancelRecording(){
    	if((isRecording))
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
    	else if((isRecPaused))
    	{    		
            canRecord=true;
            isRecording=false;
            isRecPaused=false;
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
    
	public String mergeAudio(String file1,String file2) throws IOException
	{
		String fileDestination=filePath+"/"+"merge("+recMerged+").mp4";
		
		String f1 = file1;
        String f2 = file2;
        //String f3 = AppendExample.class.getProtectionDomain().getCodeSource().getLocation().getFile() + "/1365070453555.mp4";

        Movie[] inMovies = new Movie[]{
                MovieCreator.build(f1),
                MovieCreator.build(f2)
                //MovieCreator.build(f3)
                };

        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();

        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }

        Movie result = new Movie();

        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        Container out = new DefaultMp4Builder().build(result);

        randomAccessFile = new RandomAccessFile(String.format(fileDestination), "rw");
		FileChannel fc = randomAccessFile.getChannel();
        out.writeContainer(fc);
        fc.close();
        
		return fileDestination;

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

}
