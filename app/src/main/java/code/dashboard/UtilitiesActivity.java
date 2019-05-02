package code.dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fittreat.android.R;

import code.utils.AppUtils;
import code.view.BaseActivity;

public class UtilitiesActivity extends BaseActivity implements View.OnClickListener {

    private static final int STORAGE_PERMISSION_CODE = 50;

    //RelativeLayout
    RelativeLayout rlBack,rlOTC,rlCalories,rlNearByDoctor,rlNearByPharmacy;

    //TextView
    TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilities);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);
        rlOTC = findViewById(R.id.rlOTC);
        rlCalories = findViewById(R.id.rlCalories);
        rlNearByDoctor = findViewById(R.id.rlNearByDoctor);
        rlNearByPharmacy = findViewById(R.id.rlNearByPharmacy);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);

        tvHeader.setText(getString(R.string.utilities));

        rlBack.setOnClickListener(this);
        rlOTC.setOnClickListener(this);
        rlCalories.setOnClickListener(this);
        rlNearByDoctor.setOnClickListener(this);
        rlNearByPharmacy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;

            case R.id.rlOTC:

                startActivity(new Intent(mActivity, OtcListingActivity.class));

                return;

            case R.id.rlCalories:

                startActivity(new Intent(mActivity, CalCalculatorActivity.class));

                return;

            case R.id.rlNearByDoctor:

               /* if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //TO do here if permission is granted by user

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=near+by+doctors"));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
                    }
                }*/

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=near+by+doctors"));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

                return;

            case R.id.rlNearByPharmacy:

                /*if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //TO do here if permission is granted by user

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=near+by+pharmacis"));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
                    }
                }*/

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=near+by+pharmacis"));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

                return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            //AppUtils.showToastSort(mActivity,getString(R.string.error));
        }
    }
}
