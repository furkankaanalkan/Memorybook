package com.example.memorybook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.memorybook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Memory> memoryList;
    MainActAdapter mainActAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerMain, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        memoryList = new ArrayList<Memory>();

        binding.recyclerMain.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        mainActAdapter = new MainActAdapter(memoryList);
        binding.recyclerMain.setAdapter(mainActAdapter);
        getData();

    }

    public  void addButton(View view){
        Intent intent = new Intent(MainActivity.this, ExplainActivity.class);
        intent.putExtra("info","new");
        startActivity(intent);
    }
    public void getData() {

        try {
            SQLiteDatabase database = this.openOrCreateDatabase("Memories",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM Memories", null);
            int nameIx = cursor.getColumnIndex("memoryName");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                Memory memory = new Memory(name,id);
                memoryList.add(memory);
            }
            mainActAdapter.notifyDataSetChanged();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflater
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.item) {
            Intent intent = new Intent(MainActivity.this, ExplainActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    */
}