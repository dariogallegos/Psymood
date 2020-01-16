package com.example.psymood.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psymood.Activities.FirebaseInteractor;
import com.example.psymood.Preferences.ApplicationPreferences;
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

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final String KEY_COUNTER = "COUNTER";
    private static final String KEY_NUM_PHOTO = "NUM_PHOTO";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView imgPhoto;
    private ImageButton btn_camera;
    private TextView textPicture;
    private Button savePhoto;

    private StorageReference mStorage;
    private FirebaseUser currentUser;
    private int counterOfPhoto;


    private String mCurrentPhotoPath;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        //photo proof is taken

        //init firebase storage reference
        mStorage = FirebaseStorage.getInstance().getReference();
        //Firebase user authentication
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        counterOfPhoto = ApplicationPreferences.loadNumState(KEY_NUM_PHOTO);

        imgPhoto = view.findViewById(R.id.imgFoto);
        btn_camera = view.findViewById(R.id.btn_camera);

        textPicture = view.findViewById(R.id.textPicture);
        savePhoto = view.findViewById(R.id.savePhoto);

        if(counterOfPhoto >= 1){
            Log.e("Dario","ya tiene una foto");
            textPicture.setText(R.string.selfie_alredy_exist);
            savePhoto.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.reg_btn_grey_light_style));
            savePhoto.setTextColor(ContextCompat.getColor(getContext(),R.color.GreyMedium));
            savePhoto.setEnabled(false);
            btn_camera.setVisibility(View.INVISIBLE);
        }else{
            Log.e("foto","Aun no he subido ninguna foto");
            openCameraToTakePhotos();
            btn_camera.setVisibility(View.VISIBLE);
            btn_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCameraToTakePhotos();
                }
            });
        }

        controlToShowMessageAndUploadPhoto();
        return view;
    }

    private void controlToShowMessageAndUploadPhoto() {

        if (counterOfPhoto < 1) {
            textPicture.setText(R.string.selfie_necesary);
            savePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imgPhoto != null && counterOfPhoto < 1) {
                        //Comprobamos si ya se ha subido una foto o no.
                        textPicture.setText(R.string.selfie_task_completed);
                        uploadPhoto();
                        savePhoto.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.reg_btn_grey_light_style));
                        savePhoto.setTextColor(ContextCompat.getColor(getContext(),R.color.GreyMedium));
                        savePhoto.setEnabled(false);
                    }
                }
            });
        } else {
            textPicture.setText(R.string.selfie_alredy_exist);
            savePhoto.setEnabled(false);
        }
    }

    private void uploadPhoto() {
        textPicture.setText("Se esta subiendo ...");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        try {
            final StorageReference filepath = mStorage.child("daily_user_photos").child(currentUser.getUid()).child(currentDateandTime);
            File file = new File(mCurrentPhotoPath);
            Log.e("Camera fragment", mCurrentPhotoPath);
            Uri uri = Uri.fromFile(file);
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            textPicture.setText(R.string.selfie_task_completed);
                            updateCounterPhoto();
                            FirebaseInteractor.savePhotoInDatabase(uri.toString());

                            try{
                                mListener.showMessageFragmentInHome("La imagen se ha subido correctamente");
                            }catch (Exception e){
                                Log.e("TAG","Cambio de contexto, no se puede pintar el mensaje");
                            }
                        }
                    });
                }
            });
        }catch (Exception e){
            mListener.showMessageFragmentInHome("No se ha podido subir la imagen. Intentalo de nuevo :)");
        }
    }

    private void updateCounterPhoto() {

        int numAudios = ApplicationPreferences.loadNumState(KEY_NUM_PHOTO);
        if (numAudios < 1) {
            int cont = ApplicationPreferences.loadNumState(KEY_COUNTER) + 3;
            ApplicationPreferences.saveNumState(KEY_NUM_PHOTO, 1);
            ApplicationPreferences.saveNumState(KEY_COUNTER, cont);
        }
    }

    private void openCameraToTakePhotos() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Es una versión mayor que la 6.0
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                dispatchTakePictureIntent();
            }
        }
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Foto Fragment", "error");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.psymood.fileprovider", photoFile);
                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permisos de la cámara concedidos", Toast.LENGTH_LONG).show();

                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getContext(), "Permisos de la cámara denegados", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            rotationImageToShowInFragment(mCurrentPhotoPath, bitmap);
                        }
                    }
                    break;
                }
            }

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void rotationImageToShowInFragment(String mCurrentPhotoPath, Bitmap bitmap) throws IOException {

        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
                break;

            default:
                rotatedBitmap = bitmap;
        }


        imgPhoto.setImageBitmap(rotatedBitmap);
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void showMessageFragmentInHome(String message);
    }
}
