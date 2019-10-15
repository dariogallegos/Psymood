package com.example.psymood.Fragments;


import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.psymood.Activities.FirebaseInteractor;
import com.example.psymood.Helpers.CountUpTimer;
import com.example.psymood.Preferences.ApplicationPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.psymood.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AudioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AudioFragment extends Fragment {

    private String[] sentencesUser = new String[]{
            "Todos los que crecimos fuimos niños, pero pocos lo recuerdan",
            "Facilita la identificacion de especies desconocidas",
            "Cuántos más palos me da el mundo... ¡más limonadas que me tomo!",
            "Todo depende de lo que este dispuesto a dar.",
            "No es mío, ya me gustaría a mi que lo fuera."
    };


    private static final String KEY_COUNTER = "COUNTER";
    private static final String KEY_NUM_AUDIO = "NUM_AUDIO";


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private OnFragmentInteractionListener mListener;
    private ImageButton playButton;
    private Button confirmButton;
    private LottieAnimationView animationView;
    private TextView ramdomSentence, timeAudio;
    private ProgressBar progressBarAudio;

    //Boolean controll
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;

    //record and play audio
    private MediaRecorder mRecorder;
    private MediaPlayer player;
    private String mFileName = null;
    private static final String LOG_TAG = "Record_log";
    private CountUpTimer timer;

    //Firebase storage audio
    private StorageReference mStorage;
    private FirebaseUser currentUser;


    public AudioFragment() {
        // Required empty public constructor
    }


    //Cada vez que inflamos un fragment pasa por el metodo onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        //Select a sentence to show at user.
        final String sentence = selectSentenceToShow();
        ramdomSentence = view.findViewById(R.id.ramdomSentence);

        //animation of record audio
        animationView = view.findViewById(R.id.animation_view);


        //Firabase storage ini
        mStorage = FirebaseStorage.getInstance().getReference();
        //Firebase user authentication
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Nombre del archivo
        try {
            mFileName = createAudioFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        releaseAudioRecorder();

        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("AudioFragment", "clicked");

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        ramdomSentence.setText(sentence);
                        ramdomSentence.setTypeface(ramdomSentence.getTypeface(), Typeface.BOLD);

                        onRecord(mStartRecording);
                        setMicrophoneBackground(mStartRecording);
                        mStartRecording = !mStartRecording;

                    } else {
                        checkRequestPermission();
                    }
                } else {
                    onRecord(mStartRecording);
                    mStartRecording = !mStartRecording;
                }
            }
        });
        return view;
    }

    private void releaseAudioRecorder() {
        try {
            if (mRecorder != null) {
                mRecorder.reset();
                mRecorder.release();
            }
        } catch (Exception ignore) {
        } finally {
            mRecorder = null;
        }

        Log.e("AudioFragment", "he vuelto a entrar en le fragmnet");
    }

    private String selectSentenceToShow() {

        Random random = new Random();
        int randomIndex = random.nextInt(sentencesUser.length);
        return sentencesUser[randomIndex];
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    //check permission to record audio in the user mobile
    private void checkRequestPermission() {
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


    //Dialog of confirm to send audio or discard the record.
    private void showDialogConfirm() {

        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());

        dialog.setContentView(R.layout.dialog_preferences);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Elements to dialog
        playButton = dialog.findViewById(R.id.playButton);
        confirmButton = dialog.findViewById(R.id.confirmButton);
        timeAudio = dialog.findViewById(R.id.timeAudio);
        progressBarAudio = dialog.findViewById(R.id.progressBarAudio);


        final Long millis = durationAudio();

        int maxValueProgress = (int) (millis / 1);
        progressBarAudio.setMax(maxValueProgress);
        progressBarAudio.setProgress(0);

        String time = convertLongToTimeString(millis);
        timeAudio.setText(time);


        //Boton de play
        //TODO si le doy una vez se ejecuta, si le vuelvo a dar lo pauso.
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playButtonBackground();
                onPlay(mStartPlaying);
                updateProgressAudio(millis);
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

    private void playButtonBackground() {
        if (mStartPlaying)
            playButton.setBackgroundResource(R.drawable.ic_round_pause);
        else
            playButton.setBackgroundResource(R.drawable.ic_round_play_arrow);
    }

    private void updateProgressAudio(final Long millis) {
        if (mStartPlaying) {
            timer = new CountUpTimer(millis, 1) {
                public void onTick(int second) {
                    progressBarAudio.setProgress(second);
                }
            };
            timer.start();
        } else {
            timer.cancel();
        }
        // --> pause the timer

    }

    private Long durationAudio() {

        // load data file
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(mFileName);

        // convert duration to minute:seconds
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.e("time", duration);
        long millis = Long.parseLong(duration);

        // close object
        metaRetriever.release();

        return millis;
    }

    private String convertLongToTimeString(Long millis) {
        String time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return time;
    }

    //Controla el estado de framgent audio cuando se cierra el dialogo. Deja todos lo flags correctamente asignados para la proxima grabacion.
    private void controlAudioWhenDismissDialog() {
        if (player != null && player.isPlaying()) {
            stopPlaying();
            progressBarAudio.setProgress(0);
            timer.cancel();
        }
        //lo dejamos como nuevo para la siguiente vez que inice no haya problema
        mStartPlaying = true;
    }


    //Change background of microphone. it depends of her state
    private void setMicrophoneBackground(boolean mStartRecording) {
        if (mStartRecording) {
            animationView.playAnimation();

        } else {
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
        } catch (IllegalStateException | IOException e) {
            Log.e(LOG_TAG, "Error al preparar el recorder,reseteamos todos los valores");
        }
        mRecorder.start();

    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        showDialogConfirm();
    }

    private void uploadAudio() {

        String audioStamp = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        audioStamp+=".3gp";

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/3gp")
                .build();

        File audio =  new File(mFileName);

        Uri uri = Uri.fromFile(audio);

        final StorageReference filepath = mStorage.child("daily_user_audios").child(currentUser.getUid()).child(audioStamp);


        filepath.putFile(uri,metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        updateCounterAudio();
                        FirebaseInteractor.saveAudioInDatabase(uri.toString());
                        mListener.showMessageFragmentInHome("El audio se ha subido correctamente");
                    }
                });

            }
        });


    }

    private String createAudioFile() throws IOException {

        String file =  getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath();
        file += "/recorded_audio.3gp";
        return file;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void updateCounterAudio() {

        int numAudios = ApplicationPreferences.loadNumState(KEY_NUM_AUDIO);
        if (numAudios < 1) {
            int cont = ApplicationPreferences.loadNumState(KEY_COUNTER) + 3;
            ApplicationPreferences.saveNumState(KEY_NUM_AUDIO, 1);
            ApplicationPreferences.saveNumState(KEY_COUNTER, cont);
        }
    }


    //Play audio
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

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

    private void finishedPlaying() {

        //TODO Error al intentar reproducir un audio que ya esta en ejecucion.
        if (!mStartPlaying) {
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mStartPlaying = true;
                    playButton.setBackgroundResource(R.drawable.ic_round_play_arrow);
                }
            });
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

        } catch (NullPointerException | IllegalStateException e) {
            Log.e(LOG_TAG, "se ha producido al querer cerrar el audio cuando ya ha sido cerrado");
        }
    }

    //Implements function fragment
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void showMessageFragmentInHome(String message);
    }
}
