package code.dashboard;

import android.content.Intent;
import android.os.Build;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import code.common.SimpleHTTPConnection;
import code.database.AppSettings;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class InboxDetailActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    RelativeLayout rlBack;

    //TextView
    TextView tvHeader,tvName,tvTime,tvDetails;

    //View
    View view;

    int position=0;

    String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        tvDetails = findViewById(R.id.tvDetails);

        //tvHeader.setText(getString(R.string.inbox));

        Intent intent = getIntent();

        tvName.setText(intent.getStringExtra("subject"));
        tvTime.setText(intent.getStringExtra("createDate"));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            tvDetails.setText(Html.fromHtml(intent.getStringExtra("content"), Html.FROM_HTML_MODE_COMPACT));
        }
        else
        {
            tvDetails.setText(Html.fromHtml(intent.getStringExtra("content")));
        }

        position = Integer.parseInt(intent.getStringExtra("position"));
        id = intent.getStringExtra("id");

        if(intent.getStringExtra("readFlag").equals("false"))
        {
            if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                readMessageApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        }

        /*if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
            readMessageApi();
        } else {
            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
        }*/

        rlBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;
        }
    }

    private void readMessageApi() {

        //AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.readMessage+id;
        Log.v("readMessageApi-URL", url);

        AndroidNetworking.get(url)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("readMessageApi")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON(response);
                    }

                    @Override
                    public void onError(ANError error) {
                        //AppUtils.hideDialog();
                        // handle error
                        if (error.getErrorCode() != 0) {
                            AppUtils.showToastSort(mActivity,String.valueOf(error.getErrorCode()));
                            Log.d("onError errorCode ", "onError errorCode : " + error.getErrorCode());
                            Log.d("onError errorBody", "onError errorBody : " + error.getErrorBody());
                            Log.d("onError errorDetail", "onError errorDetail : " + error.getErrorDetail());

                        } else {
                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorDetail()));
                        }
                    }
                });
    }

    private void parseJSON(String response){

        //AppUtils.hideDialog();

        Log.d("response ", response.toString());

        try {
            JSONObject jsonObject = new JSONObject(response);

            HashMap<String, String> hashMap = new HashMap();

            hashMap.put("readFlag", jsonObject.getString("readFlag"));

            if(jsonObject.getString("readFlag").equals("true"))
            {
                int count = Integer.parseInt(AppSettings.getString(AppSettings.unreadCount));
                count = count-1;
                AppSettings.putString(AppSettings.unreadCount,String.valueOf(count));
            }

            String myDate = jsonObject.getString("createDate");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = null;
            long millis = 0;
            try {
                date = sdf.parse(myDate);
                millis = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            hashMap.put("createDate", AppUtils.getTimeAgo(millis));
            hashMap.put("id", jsonObject.getString("_id"));
            hashMap.put("subject", jsonObject.getString("subject"));
            hashMap.put("content", jsonObject.getString("content"));

            AppConstants.InboxList.set(position,hashMap);

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }

    }
}
