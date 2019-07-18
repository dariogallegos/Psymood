package com.example.psymood.Fragments;


import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.psymood.Activities.FirebaseInteractor;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.psymood.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AudioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AudioFragment extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private OnFragmentInteractionListener mListener;
    private ImageButton playButton;
    private Button confirmButton;
    private LottieAnimationView animationView;

    //Boolean controll
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;

    //record and play audio
    private MediaRecorder mRecorder;
    private MediaPlayer player;
    private String mFileName = null;
    private static final String LOG_TAG = "Record_log";

    //Firebase storage audio
    private StorageReference mStorage;
    private FirebaseUser currentUser;


    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        //animation of record audio
        animationView = view.findViewById(R.id.animation_view);


        //Firabase storage ini
        mStorage = FirebaseStorage.getInstance().getReference();
        //Firebase user authentication
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Nombre del archivo
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";


        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("AudioFragment","clicked");
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        onRecord(mStartRecording);
                        setMicrophoneBackground(mStartRecording);
                        mStartRecording = !mStartRecording;

                    } else {
                        checkRequestPermission();
                    }
                }
                else {
                    onRecord(mStartRecording);
                    mStartRecording = !mStartRecording;
                }
            }
        });

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
       /* if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        */
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    //check permission to record audio in the user mobile
    private  void  checkRequestPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    onRecord(mStartRecording);
                    mStartRecording = !mStartRecording;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    //Dialog of confirm
    private void showDialogConfirm() {

        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());

        dialog.setContentView(R.layout.dialog_preferences);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        playButton =  dialog.findViewById(R.id.playButton);
        confirmButton = dialog.findViewById(R.id.confirmButton);


        //Boton de play
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                mStartPlaying = !mStartPlaying;
                finishedPlaying();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAudio();
                dialog.dismiss();
            }
        });

        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                controlAudioWhenDismissDialog();
            }
        });


    }

    //Controla el estado de framgent audio cuando se cierra el dialogo. Deja todos lo flags correctamente asignados para la proxima grabacion.
    private void controlAudioWhenDismissDialog() {
        if(player != null && player.isPlaying()){
            stopPlaying();
        }
        //lo dejamos como nuevo para la siguiente vez que inice no haya problema
        mStartPlaying = true;
    }


    //Change background of microphone. it depends of her state
    private void setMicrophoneBackground(boolean mStartRecording) {
        if(mStartRecording){
            animationView.playAnimation();

        }else {
            animationView.cancelAnimation();
            animationView.setFrame(0);
        }
    }


    //Record audio
    private void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error al preparar el recorder");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        //TODO ANTES DE ENVIAR EL AUDIO A FIRENASE DEBE APARECER UN DIALOG DE CONFIRMACION
        showDialogConfirm();
    }

    private void uploadAudio() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());


        //TODO AUDIO: ID_AUDIO CON EL ID DE USER
        final StorageReference filepath = mStorage.child("users_audios").child(currentUser.getUid()).child(currentDateandTime);
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseInteractor.saveAudioInDatabase(uri.toString());
                        Toast.makeText(getContext(),"Upload successfull",Toast.LENGTH_SHORT).show();
                        //mListener.onFragmentInteraction(uri.toString());

                    }
                });

            }
        });


    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }


    //Play audio
    private void startPlaying() {

        player = new MediaPlayer();
        try {
            player.setDataSource(mFileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error al ejecutar el media player prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void finishedPlaying(){

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mStartPlaying = true;
            }
        });

    }


    //Implements function fragment
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String urlAudio);
    }
}
