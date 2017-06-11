package edu.monash.ljket1.activi.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.adapters.AttendAdapter;
import edu.monash.ljket1.activi.models.Event;
import edu.monash.ljket1.activi.models.Notification;
import edu.monash.ljket1.activi.models.Profile;
import edu.monash.ljket1.activi.models.domain.ProfileInfo;

public class ViewEventActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private Event event;
    private String eventId;
    private ArrayList<ProfileInfo> attendees = new ArrayList<>();
    private static final String IMAGE_URL = "gs://activi-86191.appspot.com/events";

    @BindView(R.id.viewEventTitleTextView)
    TextView title;

    @BindView(R.id.viewEventDateTimeTextView)
    TextView date;

    @BindView(R.id.viewEventImageView)
    ImageView image;

    @BindView(R.id.viewEventDescriptionTextView)
    TextView description;

    @BindView(R.id.viewEventCategoryTextView)
    TextView category;

    @BindView(R.id.viewEventAttendeeListView)
    ListView list;

    @BindView(R.id.viewEventHostTextView)
    TextView host;

    @BindView(R.id.viewEventActionButton)
    Button action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Event Details");
        }

        event = Parcels.unwrap(getIntent().getParcelableExtra("event"));
        eventId = getIntent().getStringExtra("id");

        title.setText(event.title);
        date.setText(getDateTime());

        // If there is no image, set it to a Google Map Preview
        if (event.image.isEmpty()) {
            setGoogleMapImage();
        } else {
            setEventImage();
        }

        description.setText(event.description);
        category.setText(event.category);

        loadAttendees();
        loadHost();
        configureButton();
    }

    private void configureButton() {
        if (Objects.equals(FirebaseAuth.getInstance().getCurrentUser().getUid(), event.host)) {
            action.setText(R.string.scan);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkPermission(getBaseContext())) {
                        requestPermission(ViewEventActivity.this, PERMISSION_REQUEST_CODE);
                    } else {
                        new IntentIntegrator(ViewEventActivity.this).initiateScan();
                    }
                }
            });
        } else {
            action.setText(R.string.contact);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), ViewProfileActivity.class);
                    intent.putExtra("id", event.host);
                    startActivity(intent);
                }
            });
        }
    }

    private void loadHost() {
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
    }

    private void loadAttendees() {
        final ArrayList<String> userIds = new ArrayList<>();
        DatabaseReference attendence = FirebaseDatabase.getInstance().getReference("events").child(eventId).child("attend");
        attendence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userId : dataSnapshot.getChildren()) {
                    userIds.add(userId.getKey());
                }
                getAttendees(userIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    final String user = result.getContents();

                    DatabaseReference eventAttendance = FirebaseDatabase.getInstance().getReference("events").child(eventId).child("attend").child(user);
                    eventAttendance.setValue("true");

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(event.host);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Profile profile = dataSnapshot.getValue(Profile.class);
                            Notification notifcation = new Notification(event.host, profile.name, event.category);
                            DatabaseReference notificationDatabase = FirebaseDatabase.getInstance().getReference("users").child(user).child("notifications").push();
                            notificationDatabase.setValue(notifcation);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                break;
        }
    }

    private void getAttendees(final ArrayList<String> userIds) {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (userIds.contains(user.getKey())) {
                        Profile profile = user.getValue(Profile.class);
                        attendees.add(new ProfileInfo(user.getKey(), profile));
                    }
                }

                AttendAdapter adapter = new AttendAdapter(getBaseContext(), attendees);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        ProfileInfo profileItem = (ProfileInfo) adapterView.getItemAtPosition(position);
                        Intent intent = new Intent(getBaseContext(), ViewProfileActivity.class);
                        intent.putExtra("id", profileItem.getKey());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setEventImage() {
        final StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(IMAGE_URL);
        imageRef.child(event.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ViewEventActivity.this).load(uri).into(image);
            }
        });
    }

    private void setGoogleMapImage() {
        String url = "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=" + event.latitude + "," + event.longitude +
                "&zoom=18" +
                "&size=250x141" +
                "&scale=2" +
                "&markers=color:red%7C" + event.latitude + "," + event.longitude +
                "&key=AIzaSyCg35ph81jkmUZRZ6r6HLc5ldQTUIWW3GY";
        Picasso.with(this).load(url).into(image);
    }

    private String getDateTime() {
        String dateString = "";
        SimpleDateFormat serverDateFormat = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.ENGLISH);
        try {
            Date startDate = serverDateFormat.parse(event.startDate);
            Date endDate = serverDateFormat.parse(event.endDate);

            SimpleDateFormat viewDateFormat = new SimpleDateFormat("h:mm a EEEE d MMMM yyyy", Locale.ENGLISH);
            dateString = String.format("%s - %s", viewDateFormat.format(startDate), viewDateFormat.format(endDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;

        }
    }

    public static void requestPermission(Activity activity , int code) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new IntentIntegrator(ViewEventActivity.this).initiateScan();
                } else {
                    showFailPermissionDialog();
                }
                break;
            default:
                break;
        }
    }

    private void showFailPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Activi needs this permission to function!");

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
