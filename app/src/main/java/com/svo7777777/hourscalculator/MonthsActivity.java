package com.svo7777777.hourscalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MonthsActivity extends AppCompatActivity {
    private int yearId = -1;
    private int year = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_months);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        Intent intent = getIntent();
        yearId = intent.getIntExtra("yearId", -1);
        year = intent.getIntExtra("year", -1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.months), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Replace 'menu_main' with your menu resource name
        return true;
    }

    ActivityResultLauncher<Intent> mStartForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK){
                Intent intent = result.getData();
                long monthId = intent.getLongExtra("monthId", -1);
            }
            else{

            }
        }
    });

    public void januaryClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 0);
        mStartForResult.launch(intent);
    }

    public void februaryClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 1);
        mStartForResult.launch(intent);
    }

    public void marchClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 2);
        mStartForResult.launch(intent);
    }

    public void aprilClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 3);
        mStartForResult.launch(intent);
    }

    public void mayClickHandler(View viewe) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 4);
        mStartForResult.launch(intent);
    }

    public void juneClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 5);
        mStartForResult.launch(intent);
    }

    public void julyClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 6);
        mStartForResult.launch(intent);
    }

    public void augustClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 7);
        mStartForResult.launch(intent);
    }

    public void septemberClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 8);
        mStartForResult.launch(intent);
    }

    public void octoberClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 9);
        mStartForResult.launch(intent);
    }

    public void novemberClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 10);
        mStartForResult.launch(intent);
    }

    public void decemberClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 11);
        mStartForResult.launch(intent);
    }
}