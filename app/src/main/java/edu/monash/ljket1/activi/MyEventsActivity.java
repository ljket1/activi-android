package edu.monash.ljket1.activi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import edu.monash.ljket1.activi.models.Event;

public class MyEventsActivity extends AppCompatActivity {

    private ArrayList<EventInfo> mEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        String userId = getIntent().getStringExtra("id");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("events");
        Query query =  mDatabase.orderByChild("host").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    final Event event = eventSnapshot.getValue(Event.class);
                    mEvents.add(new EventInfo(eventSnapshot.getKey(), event));

                    EventAdapter eventAdapter = new EventAdapter(getBaseContext(), mEvents);
                    ListView listView = (ListView) findViewById(R.id.myEventsList);
                    listView.setAdapter(eventAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            EventInfo itemEvent = (EventInfo) adapterView.getItemAtPosition(position);

                            Intent intent = new Intent(getBaseContext(), ViewEventActivity.class);
                            intent.putExtra("id", itemEvent.getKey());
                            intent.putExtra("event", Parcels.wrap(itemEvent.getEvent()));
                            startActivity(intent);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
