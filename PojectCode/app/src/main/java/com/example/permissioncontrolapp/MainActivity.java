package com.example.permissioncontrolapp;
import androidx.annotation.NonNull;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.SmsManager;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.provider.Settings.Secure;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.widget.DatePicker;
import android.content.ContentResolver;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.io.File;
public class MainActivity extends Activity {
    Button b1,b2,b3,b4,b5;
    static DBConnect dbc;
    static String android_id;
    ImageView showimage;
    private static final int request_camera = 1888;
    private static final int camera_permission = 100;
    String msg;
    TextView tv1,tv2,tv3;
    TextToSpeech textToSpeech;
    public void initSpeaker(){
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                }
            }
        });
    }

    public String getDeviceName() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();
        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return capitalize(parts[0]);
        }
        return null;

    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }

    }


    private String getContactNo(String input) {
        String num = "none";
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                name = name.toLowerCase().trim();
                if (name.equals(input)) {
                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            num = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break;
                        }
                        pCur.close();
                    }
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return num;
    }
    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String permissions[] = {Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(!hasPermissions(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, 42);
        }
        /**if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},79);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},2);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},3);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, camera_permission);
        }
        boolean hasPermission = (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1200);
        }
        else {

            //you have permission, create your file
        }**/
        dbc = new DBConnect(this);
        setContentView(R.layout.activity_main);
        android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        showimage = (ImageView)this.findViewById(R.id.show_image);
        tv1 = (TextView)this.findViewById(R.id.textView2);
        tv2 = (TextView)this.findViewById(R.id.textView3);
        tv3 = (TextView)this.findViewById(R.id.textView4);
        int deny = dbc.getDenyStatistics();
        int maybe = dbc.getMaybe();
        String UserName = getDeviceName();
        tv1.setText("Denied Statistics : "+deny);
        tv2.setText("May Be : "+maybe);
        tv3.setText("Welcome "+UserName);
        b1 = findViewById(R.id.rp);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = dbc.getStatus("Camera");
                if(msg.equals("none")) {
                    displayCameraPermission();
                }
                else if(msg.equals("Denied")) {
                    Toast.makeText(MainActivity.this, "You already denied permission", Toast.LENGTH_LONG).show();
                    displayCameraPermission();
                }
                else if(msg.equals("Allow")) {
                    Intent cameraview = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraview, request_camera);
                }
                else if(msg.equals("Not Expired")) {
                    Intent cameraview = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraview, request_camera);
                }
                else if(msg.equals("Expired")) {
                    Toast.makeText(MainActivity.this, "Permission time period expired. Please reset now", Toast.LENGTH_LONG).show();
                    displayCameraPermission();
                }

            }
        });

        b2 = findViewById(R.id.shareimage);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = dbc.getStatus("Storage");
                if(msg.equals("none")) {
                    displayStoragePermission();
                }
                else if(msg.equals("Denied")) {
                    Toast.makeText(MainActivity.this, "You already denied permission", Toast.LENGTH_LONG).show();
                    displayStoragePermission();
                }
                else if(msg.equals("Allow")) {
                    shareImage();
                }
                else if(msg.equals("Not Expired")) {
                    shareImage();
                }
                else if(msg.equals("Expired")) {
                    Toast.makeText(MainActivity.this, "Permission time period expired. Please reset now", Toast.LENGTH_LONG).show();
                    displayStoragePermission();
                }

            }
        });


        b3 = findViewById(R.id.call);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = dbc.getStatus("Call");
                if(msg.equals("none")) {
                    displayCallPermission();
                }
                else if(msg.equals("Denied")) {
                    Toast.makeText(MainActivity.this, "You already denied permission", Toast.LENGTH_LONG).show();
                    displayCallPermission();
                }
                else if(msg.equals("Allow")) {
                    readContactCall();
                }
                else if(msg.equals("Not Expired")) {
                    readContactCall();
                }
                else if(msg.equals("Expired")) {
                    Toast.makeText(MainActivity.this, "Permission time period expired. Please reset now", Toast.LENGTH_LONG).show();
                    displayCallPermission();
                }

            }
        });

        b4 = findViewById(R.id.sms);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = dbc.getStatus("Sms");
                if(msg.equals("none")) {
                    displaySmsPermission();
                }
                else if(msg.equals("Denied")) {
                    Toast.makeText(MainActivity.this, "You already denied permission", Toast.LENGTH_LONG).show();
                    displaySmsPermission();
                }
                else if(msg.equals("Allow")) {
                    readContactSms();
                }
                else if(msg.equals("Not Expired")) {
                    readContactSms();
                }
                else if(msg.equals("Expired")) {
                    Toast.makeText(MainActivity.this, "Permission time period expired. Please reset now", Toast.LENGTH_LONG).show();
                    displaySmsPermission();
                }

            }
        });


        initSpeaker();
    }
    public void shareImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 200);
    }

    public void displayCameraPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose desire type to access permission");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("ALLOW SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    dbc.register("Camera", "Allow", Long.toString(dd.getTime()));
                    Intent cameraview = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraview, request_camera);
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    dbc.updatePermission("Camera", Long.toString(dd.getTime()),"Allow");
                    Intent cameraview = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraview, request_camera);
                }
                Toast.makeText(MainActivity.this, "Permission Allow applied", Toast.LENGTH_LONG).show();
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Deny SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    dbc.register("Camera", "Denied", Long.toString(dd.getTime()));
                    dbc.updateStatistics("Deny");
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    dbc.updatePermission("Camera", Long.toString(dd.getTime()),"Denied");
                    dbc.updateStatistics("Deny");
                }
                Toast.makeText(MainActivity.this, "Permission Denied applied", Toast.LENGTH_LONG).show();
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("May Be",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("May be SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    //dbc.register(android_id, "Allow", Long.toString(dd.getTime()));
                    Intent intent = new Intent(MainActivity.this, DateSelection.class);
                    intent.putExtra("device", "Camera");
                    intent.putExtra("status", "insert");
                    startActivity(intent);
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    //dbc.updatePermission(android_id, Long.toString(dd.getTime()),"Allow");
                    Intent intent = new Intent(MainActivity.this, DateSelection.class);
                    intent.putExtra("device", "Camera");
                    intent.putExtra("status", "update");
                    startActivity(intent);
                }
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /**if (requestCode == camera_permission) {
            if (grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, request_camera);
                System.out.println("storage permission granted");
            }}
        }
        else if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("storage permission granted");
            }
            else {
               System.out.println("storage permission denied");
            }
        }
        else if (requestCode == 1200) {
            if (grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("storage permission granted");
            }
            else {
                System.out.println("storage permission denied");
            }
        }}**/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_camera && resultCode == Activity.RESULT_OK) {
            Bitmap capture_photo = (Bitmap) data.getExtras().get("data");
            showimage.setImageBitmap(capture_photo);
            MediaStore.Images.Media.insertImage(getContentResolver(), capture_photo, "test.png" , "test");
            //saveImage(capture_photo);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    File file = new File(selectedImageUri.getPath());
                    System.out.println(file.getPath()+"====================");
                    showimage.setImageURI(selectedImageUri);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, selectedImageUri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(Intent.createChooser(intent, "Share Image"));

                }
            }
        }
        if (requestCode == 10) {
            ArrayList<String> datavalue3 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            System.out.println(datavalue3);
            if (datavalue3.size() > 0) {
                msg = datavalue3.get(0);
                msg = msg.toLowerCase().trim();
                String person_name = msg;
                String contact = getContactNo(msg);
                if (!contact.equals("none")) {
                    System.out.println(contact + "===========");
                    //editText.setText(contact);
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + contact));
                    startActivity(callIntent);
                } else {
                    textToSpeech.speak("invalid contact name try again", TextToSpeech.QUEUE_FLUSH, null);
                    //editText.setText("invalid contact name");
                    getOptionValue();
                }
            }
        }
        if (requestCode == 20) {
            ArrayList<String> datavalue3 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            System.out.println(datavalue3);
            if (datavalue3.size() > 0) {
                msg = datavalue3.get(0);
                msg = msg.toLowerCase().trim();
                String person_name = msg;
                String contact = getContactNo(msg);
                if (!contact.equals("none")) {
                    System.out.println(contact + "===========");
                    //editText.setText(contact);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contact, null, "hello", null, null);
                    textToSpeech.speak("sms sent to " + person_name, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    textToSpeech.speak("invalid contact name try again", TextToSpeech.QUEUE_FLUSH, null);
                    //editText.setText("invalid contact name");
                    getSmsOption();
                }
            }
        }
    }

    public static void saveStaistics() {
        String data = dbc.getDetails();
        byte stat[] = data.getBytes();
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File textFile = new File(path, "statistics.txt"); // Imagename.png
        try{
            FileOutputStream out = new FileOutputStream(textFile);
            out.write(stat,0,stat.length); // Compress Image
            out.flush();
            out.close();
            //Toast.makeText(MainActivity.this, "Statistics file saved as 'statistics.txt' file inside Downloads folder", Toast.LENGTH_LONG).show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void saveImage(Bitmap bm)  {
        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //Creates app specific folder
        //path.mkdirs();
        File imageFile = new File(path, "test.png"); // Imagename.png
        try{
            FileOutputStream out = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(getApplicationContext(),new String[] { imageFile.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void displayStoragePermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose desire type to access permission");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("ALLOW SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    dbc.register("Storage", "Allow", Long.toString(dd.getTime()));
                    shareImage();
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    dbc.updatePermission("Storage", Long.toString(dd.getTime()),"Allow");
                    shareImage();
                }
                Toast.makeText(MainActivity.this, "Permission Allow applied", Toast.LENGTH_LONG).show();
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Deny SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    dbc.register("Storage", "Denied", Long.toString(dd.getTime()));
                    dbc.updateStatistics("Deny");
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    dbc.updatePermission("Storage", Long.toString(dd.getTime()),"Denied");
                    dbc.updateStatistics("Deny");
                }
                Toast.makeText(MainActivity.this, "Permission Denied applied", Toast.LENGTH_LONG).show();
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("May Be",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("May be SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    //dbc.register(android_id, "Allow", Long.toString(dd.getTime()));
                    Intent intent = new Intent(MainActivity.this, DateSelection.class);
                    intent.putExtra("device", "Storage");
                    intent.putExtra("status", "insert");
                    startActivity(intent);
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    //dbc.updatePermission(android_id, Long.toString(dd.getTime()),"Allow");
                    Intent intent = new Intent(MainActivity.this, DateSelection.class);
                    intent.putExtra("device", "Storage");
                    intent.putExtra("status", "update");
                    startActivity(intent);
                }
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



    public void displayCallPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose desire type to access permission");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("ALLOW SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    dbc.register("Call", "Allow", Long.toString(dd.getTime()));
                    readContactCall();
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    dbc.updatePermission("Call", Long.toString(dd.getTime()),"Allow");
                    readContactCall();
                }
                Toast.makeText(MainActivity.this, "Permission Allow applied", Toast.LENGTH_LONG).show();
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Deny SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    dbc.register("Call", "Denied", Long.toString(dd.getTime()));
                    dbc.updateStatistics("Deny");
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    dbc.updatePermission("Call", Long.toString(dd.getTime()),"Denied");
                    dbc.updateStatistics("Deny");
                }
                Toast.makeText(MainActivity.this, "Permission Denied applied", Toast.LENGTH_LONG).show();
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("May Be",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("May be SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    //dbc.register(android_id, "Allow", Long.toString(dd.getTime()));
                    Intent intent = new Intent(MainActivity.this, DateSelection.class);
                    intent.putExtra("device", "Call");
                    intent.putExtra("status", "insert");
                    startActivity(intent);
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    //dbc.updatePermission(android_id, Long.toString(dd.getTime()),"Allow");
                    Intent intent = new Intent(MainActivity.this, DateSelection.class);
                    intent.putExtra("device", "Call");
                    intent.putExtra("status", "update");
                    startActivity(intent);
                }
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void readContactCall() {
        int speechStatus = textToSpeech.speak("Speak Contact name to call", TextToSpeech.QUEUE_FLUSH, null);
        System.out.println(speechStatus + " " + TextToSpeech.SUCCESS);
        if (speechStatus == TextToSpeech.SUCCESS) {
            getOptionValue();
        }
    }
    public void getOptionValue() {
        try{
            Thread.sleep(3000);
        } catch(Exception e) {
            e.printStackTrace();
        }
        getOption();
    }
    public void getOption(){
        msg = "none";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(intent, 10);
    }

    public void displaySmsPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose desire type to access permission");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("ALLOW SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    dbc.register("Sms", "Allow", Long.toString(dd.getTime()));
                    readContactSms();
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    dbc.updatePermission("Sms", Long.toString(dd.getTime()),"Allow");
                    readContactSms();
                }
                Toast.makeText(MainActivity.this, "Permission Allow applied", Toast.LENGTH_LONG).show();
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Deny SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    dbc.register("Sms", "Denied", Long.toString(dd.getTime()));
                    dbc.updateStatistics("Deny");
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    dbc.updatePermission("Sms", Long.toString(dd.getTime()),"Denied");
                    dbc.updateStatistics("Deny");
                }
                Toast.makeText(MainActivity.this, "Permission Denied applied", Toast.LENGTH_LONG).show();
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("May Be",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("May be SELECTED==================");
                java.util.Date dd = new java.util.Date();
                if(msg.equals("none")) {
                    //dbc.register(android_id, "Allow", Long.toString(dd.getTime()));
                    Intent intent = new Intent(MainActivity.this, DateSelection.class);
                    intent.putExtra("device", "Sms");
                    intent.putExtra("status", "insert");
                    startActivity(intent);
                }
                if(msg.equals("Expired") || msg.equals("Denied")) {
                    //dbc.updatePermission(android_id, Long.toString(dd.getTime()),"Allow");
                    Intent intent = new Intent(MainActivity.this, DateSelection.class);
                    intent.putExtra("device", "Sms");
                    intent.putExtra("status", "update");
                    startActivity(intent);
                }
                int deny = dbc.getDenyStatistics();
                int maybe = dbc.getMaybe();
                tv1.setText("Denied Statistics : "+deny);
                tv2.setText("May Be : "+maybe);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void readContactSms() {
        int speechStatus = textToSpeech.speak("Speak Contact name to send sms", TextToSpeech.QUEUE_FLUSH, null);
        System.out.println(speechStatus + " " + TextToSpeech.SUCCESS);
        if (speechStatus == TextToSpeech.SUCCESS) {
            getSmsOption();
        }
    }
    public void getSmsOption() {
        try{
            Thread.sleep(3000);
        } catch(Exception e) {
            e.printStackTrace();
        }
        getSms();
    }
    public void getSms(){
        msg = "none";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(intent, 20);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        System.out.println("call destroy=======================");
    }
}
