package com.example.magentastreaming.Activities;

import static com.example.magentastreaming.Activities.AppHolder.mainProfileImg;
import static com.example.magentastreaming.Activities.Login.SHARED_PREFS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.magentastreaming.Models.User;
import com.example.magentastreaming.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditProfile extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView profilePic;

    User appUser;
    Button update_Button,cancel_Button, logoutButton;
    TextView changeProfilePic;
    Uri selectedImageUri;

    StorageReference storageReference;
    ActivityResultLauncher<Intent> imagePickLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profilePic = findViewById(R.id.profile_image);
        Glide.with(getApplicationContext()).asBitmap()
                .load(R.drawable.sample_user)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePic);

        //getting user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        appUser = new User(user.getEmail(),user.getUid());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        storageReference = FirebaseStorage.getInstance().getReference("profile_pic/"+appUser.getUserID());

        try {
            File localFile = File.createTempFile("tempFile", ".jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                    if (bitmap!= null) {
                        Glide.with(getApplicationContext()).asBitmap()
                                .load(bitmap)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePic);
                    }
                    else {
                        Glide.with(getApplicationContext())
                                .load(R.drawable.sample_user)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePic);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            setProfilePic(getApplicationContext(),selectedImageUri,profilePic);
                        }
                    }
                }
                );
        update_Button = findViewById(R.id.update_button);
        cancel_Button = findViewById(R.id.cancel_button);
        changeProfilePic = findViewById(R.id.change_profile_btn);

        update_Button.setOnClickListener((v -> {

            if(selectedImageUri!=null){
                try {
                    getCurrentProfilePicStorage().putFile(selectedImageUri);

                    RequestOptions requestOptions1 = new RequestOptions();
                    requestOptions1 = requestOptions1.transforms(new CenterCrop(), new RoundedCorners(16));
                    Glide.with(getApplicationContext()).asBitmap()
                            .load(selectedImageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mainProfileImg);
                    Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Add a file.", Toast.LENGTH_SHORT).show();
            }

        }));

        profilePic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        cancel_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name","");
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).asBitmap()
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePic);
    }

    public StorageReference getCurrentProfilePicStorage(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(appUser.getUserID());
    }
}