package edu.monash.ljket1.activi.activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.adapters.NotificationAdapter;
import edu.monash.ljket1.activi.models.Notification;
import edu.monash.ljket1.activi.models.domain.NotificationInfo;

public class NotificationsActivity extends AppCompatActivity {

    private ArrayList<NotificationInfo> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load Notifications from DB
        DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("notifications");
        notificationsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                    NotificationInfo notification = new NotificationInfo(notificationSnapshot.getKey(), notificationSnapshot.getValue(Notification.class));
                    notifications.add(notification);
                }

                final NotificationAdapter adapter = new NotificationAdapter(getBaseContext(), notifications);
                ListView listView = (ListView) findViewById(R.id.notificationsListView);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        NotificationInfo notification = (NotificationInfo) adapterView.getItemAtPosition(position);

                        Intent intent = new Intent(getBaseContext(), RateActivity.class);
                        intent.putExtra("notificationId", notification.getKey());
                        intent.putExtra("profileId", notification.getNotification().userId);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
