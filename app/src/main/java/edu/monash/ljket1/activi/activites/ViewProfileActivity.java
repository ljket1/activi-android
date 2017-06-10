package edu.monash.ljket1.activi.activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.adapters.RatingAdapter;
import edu.monash.ljket1.activi.models.Profile;
import edu.monash.ljket1.activi.models.Rating;

public class ViewProfileActivity extends AppCompatActivity {


    @BindView(R.id.viewProfileNameTextView)
    TextView name;

    @BindView(R.id.viewProfileNumberTextView)
    TextView number;

    @BindView(R.id.viewProfileEmailTextView)
    TextView email;

    @BindView(R.id.viewProfileImageView)
    ImageView image;

    @BindView(R.id.viewProfileRatingsListView)
    ListView ratings;

    private Profile profile;
    private ArrayList<Rating> mRatings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        final String profileId = getIntent().getStringExtra("id");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(profileId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                profile = dataSnapshot.getValue(Profile.class);

                if (profile.image.contains("gs://")) {
                    FirebaseStorage.getInstance().getReference(profile.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(ViewProfileActivity.this).load(uri).into(image);
                        }
                    });
                } else {
                    Picasso.with(ViewProfileActivity.this).load(profile.image).into(image);
                }

                name.setText(profile.name);
                number.setText(profile.phone);
                email.setText(profile.email);

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
                ratings.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @OnClick(R.id.viewProfilePhoneImageButton)
    public void phone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + profile.phone));
        startActivity(intent);
    }

    @OnClick(R.id.viewProfileMessageImageButton)
    public void message() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra("address", profile.phone);
        startActivity(intent);
    }

    @OnClick(R.id.viewProfileEmailImageButton)
    public void email() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + profile.email));
        startActivity(intent);
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
