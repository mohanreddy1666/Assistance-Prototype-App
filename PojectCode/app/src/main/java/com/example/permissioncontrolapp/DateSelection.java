package com.example.permissioncontrolapp;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateSelection extends Activity implements View.OnClickListener {
    EditText date;
    DatePickerDialog start_date;
    Button submit;
    SimpleDateFormat format;
    String device;
    long stime;
    DBConnect db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_layout);
        format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        db = new DBConnect(this);
        date = (EditText) findViewById(R.id.sdate);
        date.setInputType(InputType.TYPE_NULL);

        submit =(Button) findViewById(R.id.viewbar);
        submit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try{
                    apply();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        setDateTimeField();
    }
    public void apply(){
        String device = getIntent().getExtras().getString("device");
        String status = getIntent().getExtras().getString("status");
        if(status.equals("insert")) {
            db.register(device, "May Be", Long.toString(stime));
            db.updateStatistics("May Be");
        }
        if(status.equals("update")) {
            db.updatePermission(device, Long.toString(stime), "May Be");
            db.updateStatistics("May Be");
        }
        Toast.makeText(DateSelection.this, "Permission time period successfully set", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(DateSelection.this, MainActivity.class);
        startActivity(intent);
    }
    private void setDateTimeField() {
        date.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        start_date = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                stime = newDate.getTime().getTime();
                date.setText(format.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }
    @Override
    public void onClick(View view) {
        if(view == date) {
            start_date.show();
        }
    }
}
