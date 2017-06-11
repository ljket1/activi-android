package edu.monash.ljket1.activi.activites;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.fragments.DatePickerFragment;
import edu.monash.ljket1.activi.fragments.TimePickerFragment;
import edu.monash.ljket1.activi.models.Event;

public class CreateEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    /**
     * https://firebase.google.com/docs/database/android/read-and-write
     * https://www.simplifiedcoding.net/firebase-realtime-database-crud/
     */

    // Request Codes
    public final static int LOCATION_REQUEST_CODE = 1;
    public final static int GALLERY_REQUEST_CODE = 2;

    // Date and Time Picker
    private String currentPicker;
    private final static String START = "Start";
    private final static String END = "End";

    // User
    private String userId;

    // User Input
    private String eventLat;
    private String eventLong;
    private Calendar eventStart;
    private Calendar eventEnd;

    // Upload Image
    private ProgressDialog pd;
    private Uri imageUri;
    private static final String IMAGE_URL = "gs://activi-86191.appspot.com/events";

    // Views
    @BindView(R.id.createEventImageView)
    ImageView image;

    @BindView(R.id.createEventTitleEditText)
    EditText title;

    @BindView(R.id.createEventDescriptionEditText)
    EditText description;

    @BindView(R.id.createEventCategorySpinner)
    Spinner category;

    @BindView(R.id.createEventStartDateTextView)
    TextView startDate;

    @BindView(R.id.createEventEndDateTextView)
    TextView endDate;

    @BindView(R.id.createEventLocationTextView)
    TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);

        userId = getIntent().getStringExtra("id");

        // Progress Dialog for Uploading images
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");

        String[] categories = new String[]{
                "Sport",
                "Arts",
                "Outdoor Recreation",
                "Indoor Leisure",
                "Food",
                "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categories);
        category.setAdapter(adapter);
    }

    @OnClick(R.id.createEventStartDateButton)
    public void setStartDate() {
        currentPicker = START;
        startDate.setText(START);
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "startDatePicker");
    }

    @OnClick(R.id.createEventEndDateButton)
    public void setEndDate() {
        currentPicker = END;
        endDate.setText(END);
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "endDatePicker");
    }

    @OnClick(R.id.createEventLocationButton)
    public void setLocation() {
        Intent intent = new Intent(getBaseContext(), SetLocationActivity.class);
        startActivityForResult(intent, LOCATION_REQUEST_CODE);
    }

    @OnClick(R.id.createEventCreateButton)
    public void createEvent() {
        if (validate()) {
            if (imageUri != null) {
                // If the user selected and image, upload the image to Firebase Storage
                // and create the event.
                pd.show();
                final String uuid = UUID.randomUUID().toString();
                StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(IMAGE_URL).child(uuid + ".jpg");
                UploadTask uploadTask = imageRef.putFile(imageUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        writeEvent(uuid + ".jpg");
                        Toast.makeText(CreateEventActivity.this, "Upload successful.", Toast.LENGTH_SHORT).show();
                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CreateEventActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // If the user didn't select an image, create the event with no image
                writeEvent("");
            }
        }
    }

    @OnClick(R.id.createEventImageView)
    public void choose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_REQUEST_CODE);
    }

    /**
     * writeEvent()
     * Writes Event to DB
     */
    private void writeEvent(String url) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.ENGLISH);
        Event event = new Event(
                title.getText().toString(), description.getText().toString(),
                eventLong, eventLat, dateFormat.format(eventStart.getTime()),
                dateFormat.format(eventEnd.getTime()), userId, category.getSelectedItem().toString(), url
        );
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("events").push();
        mDatabase.setValue(event);
        finish();
    }

    /**
     * validate()
     * returns True or False if the profile data is valid
     */
    private boolean validate() {
        boolean valid = true;
        String titleString = title.getText().toString();
        if (titleString.length() <= 0) {
            title.setError("Must be a valid title!");
            valid = false;
        }
        String descriptionString = description.getText().toString();
        if (descriptionString.length() <= 0) {
            description.setError("Must be a valid description!");
            valid = false;
        }
        if ((startDate.getText() == START) || (endDate.getText() == END)) {
            Toast.makeText(this, "Please select valid date!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (eventLat == null || eventLong == null) {
            Toast.makeText(this, "Please select a location!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        switch (currentPicker) {
            case START:
                eventStart = Calendar.getInstance();
                eventStart.set(year, month, day);
                DialogFragment startTimeFragment = new TimePickerFragment();
                startTimeFragment.show(getFragmentManager(), "startTimePicker");
                break;
            case END:
                eventEnd = Calendar.getInstance();
                eventEnd.set(year, month, day);
                DialogFragment endTimeFragment = new TimePickerFragment();
                endTimeFragment.show(getFragmentManager(), "endTimePicker");
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a EEEE d MMMM yyyy", Locale.ENGLISH);
        switch (currentPicker) {
            case START:
                eventStart.set(Calendar.HOUR_OF_DAY, hour);
                eventStart.set(Calendar.MINUTE, minute);
                startDate.setText(dateFormat.format(eventStart.getTime()));
                break;
            case END:
                eventEnd.set(Calendar.HOUR_OF_DAY, hour);
                eventEnd.set(Calendar.MINUTE, minute);
                endDate.setText(dateFormat.format(eventEnd.getTime()));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    eventLat = data.getStringExtra("latitude");
                    eventLong = data.getStringExtra("longitude");
                    try {
                        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(Double.parseDouble(eventLat), Double.parseDouble(eventLong), 1);
                        location.setText(addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case GALLERY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        image.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
