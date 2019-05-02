package code.dashboard;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fittreat.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import code.common.SimpleHTTPConnection;
import code.general.LoginActivity;
import code.general.RegisterActivity;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    RelativeLayout rlBack;

    //TextView
    TextView tvHeader,tvAboutUs,tvReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);
        tvAboutUs = findViewById(R.id.tvAboutUs);
        tvReference = findViewById(R.id.tvReference);

        tvHeader.setText(getString(R.string.aboutUs));

        Typeface font = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        rlBack.setOnClickListener(this);

        if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
            getAppDataApi();
        } else {
            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
        }

        tvAboutUs.setTypeface(font);
        tvReference.setTypeface(font);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;

        }
    }

    private void getAppDataApi() {

        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.getAppData;
        Log.v("getAppDataApi-URL", url);

        AndroidNetworking.get(url)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("getAppDataApi")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON(response);
                    }

                    @Override
                    public void onError(ANError error) {
                        AppUtils.hideDialog();
                        // handle error
                        if (error.getErrorCode() != 0) {
                            AppUtils.showToastSort(mActivity,String.valueOf(error.getErrorCode()));
                            Log.d("onError errorCode ", "onError errorCode : " + error.getErrorCode());
                            Log.d("onError errorBody", "onError errorBody : " + error.getErrorBody());
                            Log.d("onError errorDetail", "onError errorDetail : " + error.getErrorDetail());

                            if( error.getErrorCode()==422)
                            {
                                try {
                                    JSONObject jsonObject = new JSONObject(error.getErrorBody());

                                    AppUtils.showToastSort(mActivity, jsonObject.getString("error"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        } else {
                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorDetail()));
                        }
                    }
                });
    }

    private void parseJSON(String response){

        AppUtils.hideDialog();

        Log.d("response ", response.toString());

        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvAboutUs.setText(Html.fromHtml(jsonObject.getString("aboutSection"), Html.FROM_HTML_MODE_COMPACT));
                    tvReference.setText(Html.fromHtml(jsonObject.getString("references"), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tvAboutUs.setText(Html.fromHtml(jsonObject.getString("aboutSection")));
                    tvReference.setText(Html.fromHtml(jsonObject.getString("references")));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }

    }
}
