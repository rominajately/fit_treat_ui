package code.general;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fittreat.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import code.common.SimpleHTTPConnection;
import code.dashboard.AboutUsActivity;
import code.dashboard.DashboardActivity;
import code.dashboard.DietPlanActivity;
import code.dashboard.OtcListingActivity;
import code.dashboard.ReferenceActivity;
import code.dashboard.UtilitiesActivity;
import code.database.AppSettings;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class SplashActivity extends BaseActivity {

    ImageView ivLogo;

    RelativeLayout rlLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        ivLogo = findViewById(R.id.imageView);
        rlLogo = findViewById(R.id.rlLogo);

        logoAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(AppSettings.getString(AppSettings.userId).isEmpty())
                {
                    startActivity(new Intent(mActivity, SelectionActivity.class));
                    finish();
                }else {

                    if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                        userInboxApi();
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }
                }

            }
        },1000);


    }

    private void logoAnimation() {
        AlphaAnimation mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(800);
        mAlphaAnimation.setFillAfter(true);
        rlLogo.setVisibility(View.VISIBLE);
        rlLogo.startAnimation(mAlphaAnimation);
    }

    private void userInboxApi() {

        AppUtils.hideSoftKeyboard(mActivity);
        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.inbox;
        Log.v("userInboxApi-URL", url);

        AndroidNetworking.get(url)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("userInboxApi")
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

                        } else {
                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorDetail()));
                        }
                    }
                });
    }

    private void parseJSON(String response){

        AppUtils.hideDialog();

        Log.d("response ", response.toString());

        AppConstants.InboxList.clear();

        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONObject jsonMsgCount = jsonObject.getJSONObject("msgSummary");

            AppSettings.putString(AppSettings.msgCount,jsonMsgCount.getString("totalCount"));
            AppSettings.putString(AppSettings.unreadCount,jsonMsgCount.getString("unreadCount"));

            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                HashMap<String, String> hashMap = new HashMap();

                hashMap.put("readFlag", jsonObject1.getString("readFlag"));

                String myDate = jsonObject1.getString("createDate");
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
                hashMap.put("id", jsonObject1.getString("_id"));
                hashMap.put("subject", jsonObject1.getString("subject"));
                hashMap.put("content", jsonObject1.getString("content"));

                AppConstants.InboxList.add(hashMap);
            }

            startActivity(new Intent(mActivity, DashboardActivity.class));
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }
    }
}
