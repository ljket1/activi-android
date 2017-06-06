package edu.monash.ljket1.activi.activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.adapters.RatingAdapter;
import edu.monash.ljket1.activi.models.Profile;
import edu.monash.ljket1.activi.models.Rating;

public class ViewProfileActivity extends AppCompatActivity {


    private ArrayList<Rating> mRatings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        final String profileId = getIntent().getStringExtra("id");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(profileId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final Profile profile = dataSnapshot.getValue(Profile.class);

                final ImageView profileImageView = (ImageView) findViewById(R.id.profileImageView);
                if (profile.image.contains("gs://")) {
                    FirebaseStorage.getInstance().getReference(profile.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(ViewProfileActivity.this).load(uri).into(profileImageView);
                        }
                    });
                } else {
                    Picasso.with(ViewProfileActivity.this).load(profile.image).into(profileImageView);
                }

                TextView profileNameText = (TextView) findViewById(R.id.profileNameText);
                profileNameText.setText(profile.name);

                TextView profileNumberText = (TextView) findViewById(R.id.profileNumberText);
                profileNumberText.setText(profile.phone);

                ImageButton messageButton = (ImageButton) findViewById(R.id.profileMessageImageButton);
                messageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setType("vnd.android-dir/mms-sms");
                        intent.putExtra("address", profile.phone);
                        startActivity(intent);
                    }
                });

                ImageButton phoneButton = (ImageButton) findViewById(R.id.profilePhoneImageButton);
                phoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + profile.phone));
                        startActivity(intent);
                    }
                });

                TextView profileEmailText = (TextView) findViewById(R.id.profileEmailText);
                profileEmailText.setText(profile.email);

                ImageButton emailButton = (ImageButton) findViewById(R.id.profileEmailImageButton);
                emailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + profile.email));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(profileId).child("ratings");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    Rating rating = ratingSnapshot.getValue(Rating.class);
                    mRatings.add(rating);
                }

                RatingAdapter adapter = new RatingAdapter(getBaseContext(), mRatings);
                ListView listView = (ListView) findViewById(R.id.profileRatingsListView);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
}
