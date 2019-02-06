package com.example.dilansachintha.encrypto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    Button btn_gallery;
    Button btn_decrypt;
    ImageView img_view;
    TextView txt_key;

    private static int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_gallery = (Button) findViewById(R.id.btn_gallery);
        img_view = (ImageView) findViewById(R.id.img_view);
        btn_decrypt = (Button) findViewById(R.id.btn_decrypt);
        txt_key = (TextView) findViewById(R.id.txt_key);

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
            }
        });

        btn_decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 2);
            }
        });

    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode == 1){
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    final byte[] bytes = getBytesFromBitmap(selectedImage);

                    new Thread(new Runnable() {
                        public void run() {

                            try{

                                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                                keyGenerator.init(128);
                                Key key = keyGenerator.generateKey();
                                System.out.println(key);

                                byte[] k = key.getEncoded();
                                String kk = Base64.encodeToString(k,1);

                                txt_key.setText(kk);

                                byte[] encrypted = encryptFile(key, bytes);

                                byte[] decrypted = decryptPdfFile(key, encrypted);
                                System.out.println(decrypted);

                                if(isStoragePermissionGranted()){

                                    writeToSDFile(encrypted);

                                    img_view.post(new Runnable() {
                                        public void run() {
                                            //img_view.setImageBitmap(bit);
                                        }
                                    });
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(MainActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }else if(reqCode == 2){
            Toast.makeText(MainActivity.this,"Decrypt",Toast.LENGTH_SHORT).show();

            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                final byte[] bytes = getBytesFromBitmap(selectedImage);

                new Thread(new Runnable() {
                    public void run() {

                        try {
                            byte[] byt =  Base64.decode(txt_key.getText().toString(),1);
                            Key originalKey = new SecretKeySpec(byt, 0, byt.length, "AES");

                            byte[] decrypted = decryptPdfFile(originalKey, bytes);
                            System.out.println(decrypted);

                            if (isStoragePermissionGranted()) {

                                writeToSDFile(decrypted);

                                img_view.post(new Runnable() {
                                    public void run() {
                                        //img_view.setImageBitmap(bit);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }catch(Exception e){
                Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        }




    }

    public static byte[] encryptFile(Key key, byte[] content) {
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;

    }

    public byte[] encryptByteArray(byte[] byteArr){
        byte[] tempArr = new byte[16];

        byte[] newArr = byteArr.clone();

        int id;

        int transpoArr[] = {12,5,6,9,8,3,4,14,11,1,13,2,7,10,15,0};

        int newTranspoArr[] = {15,9,11,5,6,1,2,12,4,3,13,8,0,10,7,14};

        for(int i=2000; i<newArr.length; i++){

            id = i%16;

            tempArr[id] = newArr[i];

            if(id == 15){
                for(int j=0;j<16;j++){
                    newArr[i+j-16] = tempArr[transpoArr[j]];
                }
            }
        }
        return newArr;
    }

    public String processBitMapString(String imgString){
        String[] original = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String[] transpose = {"E","V","Z","X","Q","A","T","R","M","O","B","H","C","U","P","K","G","D","F","L","S","I","N","W","Y","J"};
        String test = imgString.substring(0,6000);
        boolean ind = false;

        for(int i = 6000; i<imgString.length(); i++){
            ind = false;
            for(int j = 0; j<original.length; j++){
                if(original[j].charAt(0) == imgString.charAt(i)){
                    System.out.println(original[j]);
                    test = test + transpose[j];
                    ind = true;
                    break;
                }
            }
            if(!ind){
                test = test + imgString.charAt(i);
            }
        }
        //System.out.println(imgString);
        //System.out.println(imgString.length());
        //Toast.makeText(MainActivity.this, imgString.length(), Toast.LENGTH_LONG).show();
        //System.out.println(test);

        System.out.println(imgString.substring(6000));
        System.out.println(test.substring(6000));
        System.out.println(test.length());
        System.out.println(imgString.length());

        return test;

/*
        try{
            byte [] encodeByte=Base64.decode(test,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }*/
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            System.out.println( "Directory not created");
        }
        return file;
    }

    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public static byte[] decryptPdfFile(Key key, byte[] textCryp) {
        Cipher cipher;
        byte[] decrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(textCryp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decrypted;
    }

    private void writeToSDFile(byte[] inputStream){

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        //tv.append("\nExternal file system root: "+root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, "myDa.jpg");

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            //System.out.println(inputStream.length);

            for(int i = 0; i < inputStream.length; i ++){
                //System.out.println(inputStream[i]);
                outputStream.write(inputStream[i]);
            }
/*
            int i;
            while((i=inputStream.read())!=-1){
                outputStream.write(i);
                System.out.println("asfasf");
            }
*/
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
