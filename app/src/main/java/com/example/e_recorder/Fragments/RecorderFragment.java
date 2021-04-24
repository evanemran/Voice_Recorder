package com.example.e_recorder.Fragments;

import android.Manifest;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chibde.visualizer.LineVisualizer;
import com.example.e_recorder.MainActivity;
import com.example.e_recorder.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecorderFragment extends Fragment {
    View view;
    Button btnRec;
    LineVisualizer visualizer;
    String filename = "e_recording.amr";
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/eRecords");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recorder, container, false);

        btnRec = view.findViewById(R.id.btnRec);
        visualizer = view.findViewById(R.id.visualizer);

        askruntimePermission();

        if (!path.exists()){
            path.mkdirs();
        }


        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(path.getAbsolutePath()+"new_e_rec.amr");

        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnRec.getText().equals("Play")){
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        btnRec.setText("Stop");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (btnRec.getText().equals("Stop")){
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    btnRec.setText("Play");
                }
            }
        });
        return view;
    }

    private void askruntimePermission() {
        Dexter.withContext(getContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(getContext(), "Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}
