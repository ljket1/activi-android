package edu.monash.ljket1.activi.activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.monash.ljket1.activi.R;

public class CreateProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.createProfileButton)
    public void createPofile() {
        finish();
    }
}
