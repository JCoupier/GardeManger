package com.example.android.garde_manger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 *
 * Simple Home panel to navigate between the three major activities
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button courseList = (Button) findViewById(R.id.main_course_list);
        courseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent courseListActivity = new Intent(MainActivity.this, CourseActivity.class);
                if (courseListActivity.resolveActivity(getPackageManager()) != null) {
                    startActivity(courseListActivity);
                }
            }
        });

        Button gardeManger = (Button) findViewById(R.id.main_garde_manger);
        gardeManger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent storageActivity = new Intent(MainActivity.this, StorageActivity.class);
                if (storageActivity.resolveActivity(getPackageManager()) != null) {
                    startActivity(storageActivity);
                }
            }
        });

        Button recetteList = (Button) findViewById(R.id.main_recette_list);
        recetteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recetteActivity = new Intent(MainActivity.this, RecetteActivity.class);
                if (recetteActivity.resolveActivity(getPackageManager()) != null) {
                    startActivity(recetteActivity);
                }
            }
        });
    }
}
