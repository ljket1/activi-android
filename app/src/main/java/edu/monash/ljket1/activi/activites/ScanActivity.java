package edu.monash.ljket1.activi.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.monash.ljket1.activi.R;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.scanScanButton)
    public void scan() {
        Intent intent = new Intent();
        intent.putExtra("result", "some scanned host");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
