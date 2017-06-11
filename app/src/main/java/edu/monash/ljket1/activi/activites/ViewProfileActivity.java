package edu.monash.ljket1.activi.activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Objects;

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
    private String profileId;
    private ArrayList<Rating> mRatings = new ArrayList<>();
    private static final String IMAGE_URL = "gs://activi-86191.appspot.com/profiles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        // Get Profile Id to load
        profileId = getIntent().getStringExtra("id");

        // Load Profile Data from DB and populate View
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(profileId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                profile = dataSnapshot.getValue(Profile.class);
                // Load Image
                if (profile.image.contains("gs://")) {
                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(IMAGE_URL);
                    imageRef.child(profile.image.replace(IMAGE_URL, "")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(ViewProfileActivity.this).load(uri).into(image);
                        }
                    });
                } else {
                    Picasso.with(ViewProfileActivity.this).load(profile.image).into(image);
                }

                // Update View with data
                name.setText(profile.name);
                number.setText("+61" + profile.phone);
                email.setText(profile.email);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Load Ratings Data and add to List
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

    /**
     * Call User
     */
    @OnClick(R.id.viewProfilePhoneImageButton)
    public void phone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+61" + profile.phone));
        startActivity(intent);
    }

    /**
     * Message User
     */
    @OnClick(R.id.viewProfileMessageImageButton)
    public void message() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + Uri.encode("+61" + profile.phone)));
        startActivity(intent);
    }

    /**
     * Email User
     */
    @OnClick(R.id.viewProfileEmailImageButton)
    public void email() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + profile.email));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // If User's Account, allow the ability to Edit
        if (Objects.equals(profileId, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            getMenuInflater().inflate(R.menu.edit, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            // Goto Edit Profile Page
            case R.id.action_edit:
                Intent intent = new Intent(this, CreateProfileActivity.class);
                intent.putExtra("profile", Parcels.wrap(profile));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
