package com.svo7777777.hourscalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.svo7777777.hc_database.AppDatabaseClient;
import com.svo7777777.hc_database.DayEntity;
import com.svo7777777.hc_database.SettingsEntity;
import com.svo7777777.utils.DatabaseHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHandler dbh = null;

    private SettingsEntity settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_menu);

        dbh = new DatabaseHandler(getApplicationContext());

        settings = dbh.getSettings();

        if(settings == null) {
            settings = new SettingsEntity();
            settings.hours = 8;
            settings.price = 0;
            dbh.writeSettings(settings);
        }

        EditText defaultHours = findViewById(R.id.defaultHours);
        EditText defaultPrice = findViewById(R.id.defaultPrice);

        defaultHours.setText(String.valueOf(settings.hours));
        defaultPrice.setText(String.valueOf(settings.price));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void saveClickHandler(View view) {
        EditText defaultHours = findViewById(R.id.defaultHours);
        EditText defaultPrice = findViewById(R.id.defaultPrice);
        settings.hours = Double.parseDouble(defaultHours.getText().toString());
        settings.price = Double.parseDouble(defaultPrice.getText().toString());

        dbh.updateSettings(settings);
    }

}