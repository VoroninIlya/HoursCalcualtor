package com.svilvo.hourscalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

import com.svilvo.hc_database.EmployeeEntity;
import com.svilvo.utils.DatabaseHandler;
import com.svilvo.views.ItemButton;

public class MonthsActivity extends AppCompatActivity {

    private DatabaseHandler dbh = null;
    private int employeeId = -1;
    private int yearId = -1;
    private int year = -1;
    private ItemButton[] buttons = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_months);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        Intent intent = getIntent();
        employeeId = intent.getIntExtra("employeeId", -1);
        yearId = intent.getIntExtra("yearId", -1);
        year = intent.getIntExtra("year", -1);

        dbh = new DatabaseHandler(getApplicationContext());

        TextView path = findViewById(R.id.path);

        EmployeeEntity ee = dbh.getEmployee((int)employeeId);

        path.setText(">" + ee.lastName + "_" + ee.firstName.substring(0,1) +
                "_" + ee.age + ">" + year + ">");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.months), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] months = getResources().getStringArray(R.array.months);
        buttons = new ItemButton[]{
                findViewById(R.id.january_btn),
                findViewById(R.id.february_btn),
                findViewById(R.id.march_btn),
                findViewById(R.id.april_btn),
                findViewById(R.id.may_btn),
                findViewById(R.id.june_btn),
                findViewById(R.id.july_btn),
                findViewById(R.id.august_btn),
                findViewById(R.id.september_btn),
                findViewById(R.id.october_btn),
                findViewById(R.id.november_btn),
                findViewById(R.id.december_btn)};

        for(int i = 0; i < 12; i++) {
            double monthHours = dbh.getHours(yearId, i);
            double monthSalary = dbh.getSalary(yearId, i);

            buttons[i].setText(String.format("%.2f", monthHours));
            //buttons[i].setTopLeftText(months[i]);
            buttons[i].setTopRightText(String.valueOf(year));
            buttons[i].setBottomRightText(String.format("%.2f", monthSalary));
            buttons[i].setSubTextSize(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                            this.getResources().getDisplayMetrics()));
            buttons[i].setTextSize(18);

            int month = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
                    intent.putExtra("employeeId", employeeId);
                    intent.putExtra("yearId", yearId);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    startActivityForResult.launch(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Replace 'menu_main' with your menu resource name
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_settings == id) {
            // Handle settings action
            Intent intent = new Intent(MonthsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.action_about == id) {
            // Handle about action
            Intent intent = new Intent(MonthsActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> startActivityForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                int month = intent.getIntExtra("month", -1);

                String[] months = getResources().getStringArray(R.array.months);

                double hours = dbh.getHours(yearId, month);
                double salary = dbh.getSalary(yearId, month);
                buttons[month].setText(String.format("%.2f", hours));
                //buttons[month].setTopLeftText(months[month]);
                buttons[month].setTopRightText(String.valueOf(year));
                buttons[month].setBottomRightText(String.format("%.2f", salary));

                Intent resintent = new Intent();
                setResult(RESULT_OK, resintent);
            }
        }
    });
}