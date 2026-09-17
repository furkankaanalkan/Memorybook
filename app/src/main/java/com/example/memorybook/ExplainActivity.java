package com.example.memorybook;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memorybook.databinding.ActivityExplainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExplainActivity extends AppCompatActivity {
    private ActivityExplainBinding binding;

    Bitmap selectedImage;
    SQLiteDatabase database;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityExplainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.dateText.addTextChangedListener(new android.text.TextWatcher() {
            boolean isUpdating = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    return;
                }
                String currentText = s.toString().replace("/", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < currentText.length(); i++) {
                    formatted.append(currentText.charAt(i));
                    if ((i == 1 || i == 3) && i != currentText.length() - 1) {
                        formatted.append("/");
                    }
                }
                isUpdating = true;
                binding.dateText.setText(formatted.toString());
                binding.dateText.setSelection(formatted.length());
                isUpdating = false;}
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        binding.dateText.setText(currentDate);


        registerLauncher();

        database = this.openOrCreateDatabase("Memories",MODE_PRIVATE,null);



        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.matches("new")) {
            binding.nameText.setText("");
            binding.dateText.setText("");
            binding.extraText.setText("");
            binding.newButton.setVisibility(View.VISIBLE);

            Bitmap selectImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.selectimage);
            binding.imageView.setImageBitmap(selectImage);


        } else {
            int memoryId = intent.getIntExtra("memoryId",1);
            binding.newButton.setVisibility(View.INVISIBLE);

            try {

                Cursor cursor = database.rawQuery("SELECT * FROM Memories WHERE id = ?",new String[] {String.valueOf(memoryId)});

                int nameIx = cursor.getColumnIndex("memoryName");
                int dateIx = cursor.getColumnIndex("date");
                int extraIx = cursor.getColumnIndex("extra");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()) {

                    binding.nameText.setText(cursor.getString(nameIx));
                    binding.dateText.setText(cursor.getString(dateIx));
                    binding.extraText.setText(cursor.getString(extraIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);


                }

                cursor.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void selectImage(View view) {
        //Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        Uri imageData = intentFromResult.getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(ExplainActivity.this.getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            } else {
                                selectedImage = MediaStore.Images.Media.getBitmap(  ExplainActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                } else {
                    //permission denied
                    Toast.makeText(ExplainActivity.this,"Permisson needed!",Toast.LENGTH_LONG).show();
                }
            }

        });
    }


    public void save(View view) {

        String name = binding.nameText.getText().toString();
        String date = binding.dateText.getText().toString();
        String extra = binding.extraText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {

            database = this.openOrCreateDatabase("Memories",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS Memories (id INTEGER PRIMARY KEY,memoryName VARCHAR, date VARCHAR, extra VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO Memories (memoryName, date, extra, image) VALUES (?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,date);
            sqLiteStatement.bindString(3,extra);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ExplainActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        //finish();

    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }
}