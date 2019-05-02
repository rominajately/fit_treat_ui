package code.general;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fittreat.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import code.common.SimpleHTTPConnection;
import code.dashboard.DashboardActivity;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;
import okhttp3.OkHttpClient;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    RelativeLayout rlLogo,rlLoginBottom;

    //TextView
    TextView tvRegister,tvForgot,tvLogin;

    //EditText
    EditText etEmail,etPassword;

    private static final int REQUEST_PERMISSIONS = 1;

    private static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        int result = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA);

        if(result!= PackageManager.PERMISSION_GRANTED)
        {
            getPermission();
        }

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlLogo = findViewById(R.id.rlLogo);
        rlLoginBottom = findViewById(R.id.rlLoginBottom);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        tvRegister= findViewById(R.id.tvRegister);
        tvForgot= findViewById(R.id.tvForgot);
        tvLogin= findViewById(R.id.tvLogin);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                upViewAnimation();
            }
        },600);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rlLoginBottom.setVisibility(View.VISIBLE);
            }
        },1600);

        tvRegister.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

        /*etEmail.setText("test@gmail.com");
        etPassword.setText("Asdf1234");*/

        AppUtils.hideSoftKeyboard(mActivity);
    }

    private void upViewAnimation() {
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(0, 0, -(getResources().getDimension(R.dimen._220sdp)), 0);
        mTranslateAnimation.setDuration(1000);
        mTranslateAnimation.setFillAfter(true);
        rlLogo.setVisibility(View.VISIBLE);
        rlLogo.startAnimation(mTranslateAnimation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvRegister:

                startActivity(new Intent(mActivity, RegisterActivity.class));

                return;

            case R.id.tvForgot:

                startActivity(new Intent(mActivity, ForgotActivity.class));

                return;

            case R.id.tvLogin:

                if(!AppUtils.isValidEmail(etEmail.getText().toString()))
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEmail));
                }
                else  if(etPassword.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorPassword));
                }
                else  if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    userLoginApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                return;
        }
    }


    private void userLoginApi() {

        AppUtils.hideSoftKeyboard(mActivity);
        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.login;
        Log.v("userLoginApi-URL", url);

        JSONObject json_data = new JSONObject();

        try {

            json_data.put("email", etEmail.getText().toString().trim());
            json_data.put("password",  etPassword.getText().toString().trim());

            Log.v("userLoginApi", json_data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(url)
                .addJSONObjectBody(json_data)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("userLoginApi")
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
                            //AppUtils.showToastSort(mActivity,String.valueOf(error.getErrorCode()));
                            Log.d("onError errorCode ", "onError errorCode : " + error.getErrorCode());
                            Log.d("onError errorBody", "onError errorBody : " + error.getErrorBody());
                            Log.d("onError errorDetail", "onError errorDetail : " + error.getErrorDetail());

                            if( error.getErrorCode()==401)
                            {
                                String errorString;
                                try {
                                    JSONObject obj = new JSONObject(error.getErrorBody());
                                    errorString = obj.getString("error");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    errorString = "Invalid Credentials";
                                }
                                AppUtils.showToastSort(mActivity, errorString);
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

           AppSettings.putString(AppSettings.firstName,jsonObject.getString("firstName"));
           AppSettings.putString(AppSettings.lastName,jsonObject.getString("lastName"));
           AppSettings.putString(AppSettings.email,jsonObject.getString("email"));
           AppSettings.putString(AppSettings.dateOfBirth,jsonObject.getString("dateOfBirth"));
           AppSettings.putString(AppSettings.weight,jsonObject.getString("weight"));
           AppSettings.putString(AppSettings.height,jsonObject.getString("height"));

           try {
                AppSettings.putString(AppSettings.bmi,jsonObject.getString("bmi"));
            } catch (JSONException e) {
                e.printStackTrace();
                AppSettings.putString(AppSettings.bmi,"");
            }

            AppSettings.putString(AppSettings.userPhoto,jsonObject.getString("userPhoto"));
           AppSettings.putString(AppSettings.medicalCondition,jsonObject.getString("medicalCondition"));
           AppSettings.putString(AppSettings.foodPreference,jsonObject.getString("foodPreference"));

           try {
                AppSettings.putString(AppSettings.targetWeight,jsonObject.getString("targetWeight"));

            } catch (JSONException e) {
                e.printStackTrace();
                AppSettings.putString(AppSettings.targetWeight,"");
            }

            try {
                AppSettings.putString(AppSettings.targetDate,jsonObject.getString("targetDate"));
            } catch (JSONException e) {
                e.printStackTrace();
                AppSettings.putString(AppSettings.targetDate,"");
            }

            try {
                AppSettings.putString(AppSettings.targetCalories,jsonObject.getString("targetCalories"));
            } catch (JSONException e) {
                e.printStackTrace();
                AppSettings.putString(AppSettings.targetCalories,"");
            }

            AppSettings.putString(AppSettings.userPhoto,jsonObject.getString("userPhoto"));
           AppSettings.putString(AppSettings.role,jsonObject.getString("role"));
           AppSettings.putString(AppSettings.userId,jsonObject.getString("_id"));
           AppSettings.putString(AppSettings.age,jsonObject.getString("age"));
           AppSettings.putString(AppSettings.gender,jsonObject.getString("gender"));
           AppSettings.putString(AppSettings.heightUnit,jsonObject.getString("heightUnit"));
           AppSettings.putString(AppSettings.weightUnit,jsonObject.getString("weightUnit"));

            if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                userInboxApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }
    }


    public void getPermission() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                ||  ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                ||  ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ){
            Log.d("Permission for contacts", "Displaying contacts permission rationale to provide additional context.");
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS);
        } else
        {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
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
                        parseNewJSON(response);
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

    private void parseNewJSON(String response){

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
