package ro.rcsrds.recordbox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
//Pentru a incepe o inregistrare apelati startRecording, pentru a o incheia apelati stopRecording.
//Aveti grija sa nu apelati starRecording in timp ce inregistreaza(Ex:dati disable la butonu de record si renable la stop)
@SuppressLint("SimpleDateFormat")
public class AudioRecorder {
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;    
    private MediaRecorder mRecorder = null;
    private boolean isRecording=false;
    
    //Setam directorul unde se salveaza fisierul
    public AudioRecorder() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();        
    } 
    
	public void startRecording() {
		if(!isRecording){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String currentDateandTime = df.format(c.getTime());
        
        mRecorder.setOutputFile(mFileName+"/"+currentDateandTime+".3gp");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
        isRecording=true;
		}
    }
	
    public void stopRecording() {
    	if(isRecording){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        isRecording=false;
    	}
    }
}
