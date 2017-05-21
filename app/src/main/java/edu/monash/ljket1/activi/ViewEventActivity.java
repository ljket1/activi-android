package edu.monash.ljket1.activi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.parceler.Parcels;

import edu.monash.ljket1.activi.models.Event;

public class ViewEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        String eventId = getIntent().getStringExtra("id");
        System.out.println(eventId);
        Event event = Parcels.unwrap(getIntent().getParcelableExtra("event"));
        System.out.println(event.title);
    }
}
