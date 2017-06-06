package edu.monash.ljket1.activi.activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.models.Profile;
import edu.monash.ljket1.activi.models.Rating;

public class RateActivity extends AppCompatActivity {

    @BindView(R.id.rateRatingBar)
    RatingBar ratingBar;

    @BindView(R.id.rateProfileImageView)
    ImageView image;

    @BindView(R.id.rateProfileNameTextView)
    TextView name;

    @BindView(R.id.rateCommentEditText)
    EditText comment;

    private String profileId;
    private String notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Rate");
        }

        profileId = getIntent().getStringExtra("profileId");
        notificationId = getIntent().getStringExtra("notificationId");

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(profileId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                if (profile.image.contains("gs://")) {
                    FirebaseStorage.getInstance().getReference(profile.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(RateActivity.this).load(uri).into(image);
                        }
                    });
                } else {
                    Picasso.with(RateActivity.this).load(profile.image).into(image);
                }
                name.setText(profile.name);
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

    @OnClick(R.id.rateRateButton)
    public void rate() {
        // Create Rating
        Rating rating = new Rating(String.valueOf(ratingBar.getRating()), comment.getText().toString());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(profileId).child("ratings").push();
        mDatabase.setValue(rating);

        // Delete Notification
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notifications").child(notificationId);
        notificationRef.setValue(null);
        finish();
    }
}
