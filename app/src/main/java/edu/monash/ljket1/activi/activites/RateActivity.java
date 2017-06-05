package edu.monash.ljket1.activi.activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.monash.ljket1.activi.R;

public class RateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        System.out.println(getIntent().getStringExtra("eventTitle"));
        System.out.println(getIntent().getStringExtra("profileId"));
    }
}
