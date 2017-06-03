package edu.monash.ljket1.activi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.Objects;

import edu.monash.ljket1.activi.models.Event;
import edu.monash.ljket1.activi.models.Profile;

public class ViewEventActivity extends AppCompatActivity {

    private String eventId;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Event Details");
        }

        eventId = getIntent().getStringExtra("id");
        event = Parcels.unwrap(getIntent().getParcelableExtra("event"));

        ImageView location = (ImageView) findViewById(R.id.location);
        String url = "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=" + event.longitude + "," + event.latitude +
                "&zoom=18" +
                "&size=512x288" +
                "&scale=2" +
                "&markers=color:red%7C" + event.longitude + "," + event.latitude +
                "&key=AIzaSyCg35ph81jkmUZRZ6r6HLc5ldQTUIWW3GY";
        Picasso.with(this).load(url).into(location);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(event.title);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(event.description);

        TextView dateTime = (TextView) findViewById(R.id.dateTime);
        dateTime.setText(event.startTime + " " + event.startDate + " - " + event.endTime + " " + event.endDate);

        final TextView host = (TextView) findViewById(R.id.host);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(event.host);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Profile profile = dataSnapshot.getValue(Profile.class);
                host.setText("Hosted by: " + profile.name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        Button actionButton = (Button) findViewById(R.id.eventActionButton);

        if (Objects.equals(FirebaseAuth.getInstance().getCurrentUser().getUid(), event.host)) {
            actionButton.setText("Scan");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new IntentIntegrator(ViewEventActivity.this).initiateScan();
                }
            });
        } else {
            actionButton.setText("Contact");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), ViewProfileActivity.class);
                    intent.putExtra("id", event.host);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    // TODO: Handle users that don't exist or already attending
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("events").child(eventId).child("attend").child(result.getContents());
                    mDatabase.setValue("true");
                }
                break;
        }
    }
}
