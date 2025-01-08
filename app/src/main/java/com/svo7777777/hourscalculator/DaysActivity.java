package com.svo7777777.hourscalculator;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class DaysActivity extends AppCompatActivity {

    private int yearId = -1;
    private int year = -1;
    private int month = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_days);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        Intent intent = getIntent();
        yearId = intent.getIntExtra("yearId", -1);
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);

        Calendar cldr = Calendar.getInstance(Locale.ROOT);
        cldr.set(Calendar.YEAR, year);
        cldr.set(Calendar.MONTH, month);
        int daysInMonth = cldr.getActualMaximum(Calendar.DAY_OF_MONTH);

        GridLayout dc = findViewById(R.id.days_container);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        float itemWidthDp = 70; // Example item width in dp
        int numColumns = (int) (screenWidthDp / itemWidthDp);
        itemWidthDp = screenWidthDp/numColumns;

        // Convert itemWidthDp to pixels for layout parameters
        int itemWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                itemWidthDp,
                getResources().getDisplayMetrics()
        );

        dc.setColumnCount(numColumns);

        for(int i = 1; i <= daysInMonth; i++) {
            // Create a Button programmatically
            Button button = new Button(DaysActivity.this);
            button.setText(String.valueOf(i));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec((i-1)/numColumns); // Row index
            params.columnSpec = GridLayout.spec((i-1)%numColumns); // Column index
            params.width = itemWidthPx;
            params.height = itemWidthPx;
            button.setLayoutParams(params);

            dc.addView(button);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.days), (v, insets) -> {
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
}