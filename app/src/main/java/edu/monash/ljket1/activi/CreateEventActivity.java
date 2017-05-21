package edu.monash.ljket1.activi;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class CreateEventActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public final static int LOCATION_REQUEST_CODE = 1;
    private final static String START = "start";
    private final static String END = "end";
    private String currentTimePicker;
    private String currentDatePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Button startDateButton = (Button) findViewById(R.id.startDateButton);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDatePicker = START;
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Button startTimeButton = (Button) findViewById(R.id.startTimeButton);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimePicker = START;
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getFragmentManager(), "startTimePicker");
            }
        });

        Button endDateButton = (Button) findViewById(R.id.endDateButton);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDatePicker = END;
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Button endTimeButton = (Button) findViewById(R.id.endTimeButton);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimePicker = END;
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getFragmentManager(), "endTimePicker");
            }
        });

        Button setLocationButton = (Button) findViewById(R.id.eventSetLocationButton);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SetLocationActivity.class);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
            }
        });

        Button createEventButton = (Button) findViewById(R.id.eventCreateButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        switch (currentDatePicker) {
            case START:
                TextView startDateTextView = (TextView) findViewById(R.id.startDateText);
                startDateTextView.setText(day + "/" + (month+1) + "/" + year );
                break;
            case END:
                TextView endDateTextView = (TextView) findViewById(R.id.endDateText);
                endDateTextView.setText(day + "/" + (month+1) + "/" + year );
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        switch (currentTimePicker) {
            case START:
                TextView startTimeTextView = (TextView) findViewById(R.id.startTimeText);
                startTimeTextView.setText(hour + ":" + minute);
                break;
            case END:
                TextView endTimeTextView = (TextView) findViewById(R.id.endTimeText);
                endTimeTextView.setText(hour + ":" + minute);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String latitude = data.getStringExtra("latitude");
                    String longitude = data.getStringExtra("longitude");
                    TextView textView = (TextView) findViewById(R.id.locationText);
                    textView.setText("Longitude: " + longitude + " Latitude: " + latitude);
                }
                break;
        }
    }
}
