package edu.monash.ljket1.activi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import edu.monash.ljket1.activi.models.Event;

public class ViewEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        String eventId = getIntent().getStringExtra("id");
        System.out.println(eventId);
        final Event event = Parcels.unwrap(getIntent().getParcelableExtra("event"));
        System.out.println(event.title);

        TextView host = (TextView) findViewById(R.id.host);
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        TextView dateTime = (TextView) findViewById(R.id.dateTime);
        ImageView location = (ImageView) findViewById(R.id.location);
        String url = "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=" + event.longitude + "," + event.latitude +
                "&zoom=18" +
                "&size=512x288" +
                "&scale=2" +
                "&markers=color:red%7C" + event.longitude + "," + event.latitude +
                "&key=AIzaSyCg35ph81jkmUZRZ6r6HLc5ldQTUIWW3GY";
        Picasso.with(this).load(url).into(location);

        title.setText(event.title);
        description.setText(event.description);
        dateTime.setText(event.startTime + " " + event.startDate + " - " + event.endTime + " " + event.endDate);

        Button contactButton = (Button) findViewById(R.id.contactButton);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), ViewProfileActivity.class);
//                intent.putExtra("id", event.host);
//                startActivity(intent);
            }
        });
    }
}
