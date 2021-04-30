package com.example.e_recorder.Fragments;

import android.Manifest;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.chibde.visualizer.LineVisualizer;
import com.example.e_recorder.MainActivity;
import com.example.e_recorder.R;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class RecorderFragment extends Fragment {
    View view;
    ImageButton btnRec, btnPlay;
    TextView txtRecStatus;
    Chronometer timeRec;
    BlobVisualizer visualizer;
    LineVisualizer lineVisualizer;
    GifImageView gifImageView;
    String filename = "e_recording.amr";
    private static String fileName;
    private MediaRecorder recorder;
    boolean isRecording;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/eRecords");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recorder, container, false);

        btnRec = view.findViewById(R.id.btnRec);
        btnPlay = view.findViewById(R.id.btnPlay);
        txtRecStatus = view.findViewById(R.id.txtRecStatus);
        timeRec = view.findViewById(R.id.timeRec);
        visualizer = view.findViewById(R.id.blob);
        lineVisualizer = view.findViewById(R.id.visualizer);
        gifImageView = view.findViewById(R.id.gifView);

        lineVisualizer.setColor(ContextCompat.getColor(getContext(), R.color.red));
        lineVisualizer.setStrokeWidth(1);

        isRecording = false;

        askruntimePermission();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());
        fileName =path + "/recording_"+date+".amr";

        if (!path.exists()){
            path.mkdirs();
        }

        btnRec.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (!isRecording){
                    try {
                        startRecording();
                        gifImageView.setVisibility(View.VISIBLE);
                        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                        int audioSessionId = audioManager.generateAudioSessionId();
                        if (audioSessionId != -1)
                            lineVisualizer.setPlayer(audioSessionId);
//                            visualizer.setAudioSessionId(audioSessionId);
                        timeRec.setBase(SystemClock.elapsedRealtime());
                        timeRec.start();
                        txtRecStatus.setText("Recording...");
                        btnRec.setImageResource(R.drawable.ic_stop);
                        isRecording = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (isRecording){
                    stopRecording();
                    gifImageView.setVisibility(View.GONE);
                    timeRec.setBase(SystemClock.elapsedRealtime());
                    timeRec.stop();
                    txtRecStatus.setText("");
                    btnRec.setImageResource(R.drawable.ic_record);
                    isRecording = false;
                }
            }
        });
        return view;
    }
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
    }
    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void askruntimePermission() {
        Dexter.withContext(getContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}
