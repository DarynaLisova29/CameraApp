package com.example.cameraapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.cameraapp.databinding.ActivityMainBinding;

import java.io.File;
import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_CODE=1;
    ActivityMainBinding mainBinding;
    ActivityResultLauncher<Uri>takePictureLauncher;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        imageUri=createURI();
        registerPictureLauncher();

        mainBinding.btnTakePicture.setOnClickListener(view -> {
            checkCameraPermissionAndOpenCamera();
        });
    }
    private Uri createURI(){
        File imageFile=new File(getApplicationContext().getFilesDir(),"camera_photo.jpg");
        return FileProvider.getUriForFile(
                getApplicationContext(),
                "com/example/cameraapp.fileProvider",
                imageFile
        );
    }

    private void registerPictureLauncher(){
        takePictureLauncher=registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        try{
                            if(result){
                                mainBinding.ivUser.setImageURI(null);
                                mainBinding.ivUser.setImageURI(imageUri);
                            }
                        }catch(Exception e){
                            e.getStackTrace();
                        }
                    }
                }
        );
    }

    private void checkCameraPermissionAndOpenCamera(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }else{
            takePictureLauncher.launch(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CAMERA_PERMISSION_CODE){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                takePictureLauncher.launch(imageUri);
            }else {
                Toast.makeText(this,"Camera permission denied, please allow permission to take picture",Toast.LENGTH_SHORT).show();
            }
        }
    }
}