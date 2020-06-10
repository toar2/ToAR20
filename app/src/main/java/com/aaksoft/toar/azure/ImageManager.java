package com.aaksoft.toar.azure;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.aaksoft.toar.activities.MapsActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;




public class ImageManager {


    public static String UploadImage(InputStream image, int imageLength, String uniqueFileName, ImagesData data) throws Exception {

        String ownerId = data.owner_id
                , fileName = data.unique_filename;
        String storageString = "userImages/" + ownerId+"/"+fileName;
        StorageReference reference = FirebaseStorage.getInstance().getReference(storageString);

        UploadTask task = reference.putStream(image);
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // If image is successfully uploaded, also
                // increment the count of the number of images online by 1.



            }
        });
        return uniqueFileName;

    }

    public static void downloadImage(String unique_filename, String unique_userId){


    Log.d("FileName", unique_filename);
    Log.d("uID", unique_userId);
    String rootPath = Environment.DIRECTORY_PICTURES + File.separator + "ToAR/" + unique_userId;

    File localFile = new File("Pictures/ToAR", unique_filename);



    String storageString = "userImages/" + unique_userId + "/" + unique_filename;
    StorageReference reference = FirebaseStorage.getInstance().getReference(storageString);
    byte[] returnBuff;
    reference.getBytes(1024*1024 * 10).addOnSuccessListener(new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            try{
                saveBitmapToDiskFromCloud(bitmap, unique_filename, unique_userId);
                Log.d("Success", "Success");
            }
            catch(IOException e) {
                e.printStackTrace();
            }

    }}
    );



    }



    public static void saveBitmapToDiskFromCloud(Bitmap bitmap, String filename, String uid) throws IOException {
        boolean status;
        String localFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ToAR/" + uid + "/" + filename;

        File out = new File(localFileName);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(localFileName);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException ex) {

            throw new IOException("Failed to save bitmap to disk", ex);
        }

    }


}
