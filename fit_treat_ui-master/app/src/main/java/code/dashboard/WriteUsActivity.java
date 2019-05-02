package code.dashboard;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import code.common.SimpleHTTPConnection;
import code.database.AppSettings;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class WriteUsActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    RelativeLayout rlBack;

    //TextView
    TextView tvHeader;

    //ImageView
    ImageView ivMiddle;

    EditText etMessage;

    TextView tvSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_us);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);
        tvSubmit = findViewById(R.id.tvSubmit);

        etMessage = findViewById(R.id.etMessage);

        ivMiddle = findViewById(R.id.ivMiddle);

        tvHeader.setText(getString(R.string.write_to_us));
        ivMiddle.setImageResource(R.drawable.ic_write_to_us_main);

        rlBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;

            case R.id.tvSubmit:

                if(etMessage.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.enter_message));
                }
                else   if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    sendMsgAdminApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                return;

        }
    }

    private void sendMsgAdminApi() {

        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.sendMsgToAdmin;
        Log.v("sendMsgAdminApi-URL", url);

        JSONObject json_data = new JSONObject();

        try {

            json_data.put("id", AppSettings.getString(AppSettings.userId));
            json_data.put("msg",  etMessage.getText().toString().trim());

            Log.v("sendMsgAdminApi", json_data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(url)
                .addJSONObjectBody(json_data)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("sendMsgAdminApi")
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
            JSONObject jsonObject = new JSONObject(response);

            AppUtils.showToastSort(mActivity, jsonObject.getString("msg"));

            onBackPressed();

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }

    }
}
