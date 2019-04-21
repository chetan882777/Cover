package com.chetan.projects.cover;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.SetWallpaperEntry;

import java.util.List;

public class EntrieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrie);


        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase instance = AppDatabase.getInstance(getApplicationContext());

                final List<SetWallpaperEntry> entry = instance.setWallpaperDao().loadAllWallpaperEntries();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView listView = findViewById(R.id.entries_listView);
                        ArrayAdapter adapter =
                                new ArrayAdapter(EntrieActivity.this , android.R.layout.simple_list_item_1 , entry);
                        listView.setAdapter(adapter);
                    }
                });
            }
        });
        newThread.run();

    }
}
