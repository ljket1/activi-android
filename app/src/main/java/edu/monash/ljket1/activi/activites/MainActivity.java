package edu.monash.ljket1.activi.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.models.Event;
import edu.monash.ljket1.activi.models.Profile;
import edu.monash.ljket1.activi.models.domain.EventInfo;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {

    // Google
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    // Nav View
    private NavigationView navigationView;

    // Permissions Handling
    private static final int PERMISSION_REQUEST_CODE = 1;

    // Firebase Storage
    private static final String IMAGE_URL = "gs://activi-86191.appspot.com/profiles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // If not authenticated, goto Sign In page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Populate View
        loadProfile();

        // Auth
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create Event Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateEventActivity.class);
                // Put User Event on Intent
                intent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Load Notifications
        DatabaseReference notifications = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notifications");
        notifications.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Notification Counter
                TextView counter = (TextView) navigationView.getMenu().findItem(R.id.nav_notifications).getActionView();
                counter.setGravity(Gravity.CENTER_VERTICAL);
                counter.setTypeface(null, Typeface.BOLD);
                counter.setTextColor(getResources().getColor(R.color.colorAccent));
                counter.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * getLocation()
     * Request the users location
     */
    private void getLocation() {
        if (!checkPermission(getBaseContext())) {
            requestPermission(this, PERMISSION_REQUEST_CODE);
        } else {
            try {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        // Close the drawer if it's opened
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Signs out of Auth Account
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            // Shows the about page
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Goto User's Profile
            case R.id.nav_profile:
                Intent profileIntent = new Intent(getBaseContext(), ViewProfileActivity.class);
                profileIntent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(profileIntent);
                break;
            // Goto User's Notification
            case R.id.nav_notifications:
                Intent notificationsIntent = new Intent(getBaseContext(), NotificationsActivity.class);
                startActivity(notificationsIntent);
                break;
            // Goto User's Events
            case R.id.nav_events:
                Intent eventIntent = new Intent(getBaseContext(), MyEventsActivity.class);
                eventIntent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(eventIntent);
                break;
            // Goto User's Barcode
            case R.id.nav_barcode:
                Intent barcodeIntent = new Intent(getBaseContext(), BarcodeActivity.class);
                barcodeIntent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(barcodeIntent);
                break;
        }

        // Close the Drawer after selecting an option
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Do nothing
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Handle Clicking Marker Info Window
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Goto Associated Event
                EventInfo event = (EventInfo) marker.getTag();
                Intent intent = new Intent(getBaseContext(), ViewEventActivity.class);

                intent.putExtra("id", event != null ? event.getKey() : null);
                intent.putExtra("event", Parcels.wrap(event != null ? event.getEvent() : null));
                startActivity(intent);
            }
        });

        // Zoom into the Map
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));

        // Default to Melbourne
        LatLng pos = new LatLng(-37.8136, 144.9631);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

        // Get Location
        getLocation();

        // Populate Map with Markers
        DatabaseReference events = FirebaseDatabase.getInstance().getReference("events");
        events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Update Map data when Database changes
                mMap.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    Bitmap bitmap = drawableToBitmap(getDrawable(R.drawable.ic_map_location));
                    LatLng eventLatLng = new LatLng(Double.parseDouble(event.latitude), Double.parseDouble(event.longitude));
                    // Customise Marker
                    Marker marker = mMap.addMarker(
                            new MarkerOptions().position(eventLatLng)
                                    .title(event.title)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    );
                    marker.setTag(new EventInfo(eventSnapshot.getKey(), event));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * drawableToBitmap()
     * Converts drawable to bitmap
     * https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * loadProfile()
     * Loads profile from DB and updates view
     */
    private void loadProfile() {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("users");
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    startActivity(new Intent(getBaseContext(), CreateProfileActivity.class));
                    finish();
                } else {
                    Profile profile = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(Profile.class);
                    View header = navigationView.getHeaderView(0);
                    final ImageView avatar = (ImageView) header.findViewById(R.id.imageView);
                    if (profile.image.contains("gs://")) {
                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(IMAGE_URL);
                        imageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(MainActivity.this).load(uri).into(avatar);
                            }
                        });
                    } else {
                        Picasso.with(MainActivity.this).load(profile.image).into(avatar);
                    }
                    TextView name = (TextView) header.findViewById(R.id.name);
                    name.setText(profile.name);
                    TextView email = (TextView) header.findViewById(R.id.email);
                    email.setText(profile.email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        // Update the Map Camera Position
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * checkPermission()
     * Checks the app has Location Permissions
     */
    public static boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * requestPermission()
     * Requests Location Permission from the User
     */
    public static void requestPermission(Activity activity, int code) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Update Map Camera Location
                    getLocation();
                } else {
                    // Inform the User the Location is needed
                    showFailPermissionDialog();
                }
                break;
            default:
                break;
        }
    }

    /**
     * showFailPermissionDialog()
     * Show an Alert Dialog informing the user about permissions
     */
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
