package code.general;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fittreat.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import code.common.SimpleHTTPConnection;
import code.dashboard.DashboardActivity;
import code.database.AppSettings;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    RelativeLayout rlLogo;

    //LinearLayout
    LinearLayout llRegisterBottom;

    //Spinner
    Spinner spinnerWeight, spinnerHeight, spinnerFood, spinnerMedical;

    //ArrayList
    private static ArrayList<String> WeightList = new ArrayList<String>();
    private static ArrayList<String> HeightList = new ArrayList<String>();
    private static ArrayList<String> FoodList = new ArrayList<String>();
    private static ArrayList<String> MedicalList = new ArrayList<String>();

    //EditText
    EditText etFirstName, etLastName, etEmail, etPassword, etDob, etHeight, etWeight;

    //RadioButton
    RadioButton rbMale, rbFemale;

    //ImageView
    ImageView ivDob;

    //TextView
    TextView tvSubmit;

    String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById();
    }

    private void findViewById() {

        //Spinner
        spinnerWeight = findViewById(R.id.spinnerWeight);
        spinnerHeight = findViewById(R.id.spinnerHeight);
        spinnerFood = findViewById(R.id.spinnerFood);
        spinnerMedical = findViewById(R.id.spinnerMedical);

        //RelativeLayout
        rlLogo = findViewById(R.id.rlLogo);

        //LinearLayout
        llRegisterBottom = findViewById(R.id.llRegisterBottom);

        //All Edittext
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDob = findViewById(R.id.etDob);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);

        //Radio Buttons for Male and Female
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        //TextView
        tvSubmit = findViewById(R.id.tvSubmit);

        //ImageView for Calendar
        ivDob = findViewById(R.id.ivDob);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                upViewAnimation();
            }
        }, 600);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                llRegisterBottom.setVisibility(View.VISIBLE);
            }
        }, 1600);

        WeightList.clear();
        WeightList.add(getString(R.string.kg));
        WeightList.add(getString(R.string.lb));

        HeightList.clear();
        HeightList.add(getString(R.string.cm));
        HeightList.add(getString(R.string.m));
        HeightList.add(getString(R.string.ft));

        FoodList.clear();
        FoodList.add(getString(R.string.food_preferences));
        FoodList.add(getString(R.string.vegan));
        FoodList.add(getString(R.string.vegetarian));
        FoodList.add(getString(R.string.nonVegetarian));
        FoodList.add(getString(R.string.none));

        MedicalList.clear();
        MedicalList.add(getString(R.string.medical_condition));
        MedicalList.add(getString(R.string.none));
        MedicalList.add(getString(R.string.diabetes));
        MedicalList.add(getString(R.string.thyroid));

        spinnerWeight.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, WeightList));
        spinnerWeight.setSelection(0);

        spinnerHeight.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, HeightList));
        spinnerHeight.setSelection(0);

        spinnerFood.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, FoodList));
        spinnerFood.setSelection(0);

        spinnerMedical.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, MedicalList));
        spinnerMedical.setSelection(0);

        rbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    gender = getString(R.string.male);
                    rbMale.setChecked(true);
                    rbFemale.setChecked(false);
                }

            }
        });

        rbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    gender = getString(R.string.female);
                    rbMale.setChecked(false);
                    rbFemale.setChecked(true);
                }

            }
        });

        ivDob.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);

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

            case R.id.tvSubmit:

                boolean retFlag = false;
                if (etFirstName.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorFName));
                    retFlag = true;
                } else if (!AppUtils.isValidEmail(etEmail.getText().toString())) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEmail));
                    retFlag = true;
                } else if (etPassword.getText().toString().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorPassword));
                    retFlag = true;
                } else if (gender.isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorGender));
                    retFlag = true;
                } else if (etDob.getText().toString().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorDob));
                    retFlag = true;
                } else if (etHeight.getText().toString().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorHeight));
                    retFlag = true;
                } else if (etWeight.getText().toString().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorWeight));
                    retFlag = true;
                } else if (spinnerFood.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorFood));
                    retFlag = true;
                } else if (spinnerMedical.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorMedical));
                    retFlag = true;
                }
                if (!etHeight.getText().toString().isEmpty() && !etWeight.getText().toString().isEmpty()) {
                    // Logic to handle invalid weight and height values - zeroes and
                    String weight = etWeight.getText().toString();
                    String[] wSplit = weight.split("\\.");
                    if (wSplit.length > 1) {
                        AppUtils.showToastSort(mActivity, "Weight Error: Decimal values not allowed");
                        retFlag = true;
                    } else if (Integer.parseInt(weight) == 0) {
                        AppUtils.showToastSort(mActivity, "Weight can not be zero");
                        retFlag = true;
                    }
                    String height = etHeight.getText().toString();
                    String[] hSplit = height.split("\\.");
                    if (hSplit.length > 1) {
                        if (spinnerHeight.getSelectedItem().toString().equals("cm")) {
                            AppUtils.showToastSort(mActivity, "Invalid Height");
                            retFlag = true;
                        }
                        if (hSplit[0].isEmpty() || Integer.parseInt(hSplit[0]) == 0) {
                            AppUtils.showToastSort(mActivity, getString(R.string.invalidHeight));
                            retFlag = true;
                        } else if (spinnerHeight.getSelectedItem().toString().equals("ft") && Integer.parseInt(hSplit[1]) > 9) {
                            AppUtils.showToastSort(mActivity, getString(R.string.invalidHeight));
                            retFlag = true;
                        }
                    } else if (Integer.parseInt(hSplit[0]) == 0) {
                        AppUtils.showToastSort(mActivity, "Height can not be zero");
                        retFlag = true;
                    }
                }
                if(retFlag){
                    return;
                } else if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    userRegistrationApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    return;
                }
            case R.id.ivDob:

                AppUtils.dateDialog(mActivity, etDob, 1);

                return;


        }
    }

    //Spinner DemoAdapter
    public class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        public adapterSpinner(Context context, int textViewResourceId, ArrayList<String> arraySpinner_time) {

            super(context, textViewResourceId, arraySpinner_time);

            this.data = arraySpinner_time;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.spinner_adapter, parent, false);
            TextView label = (TextView) row.findViewById(R.id.tv_spinner_name);

            label.setText(data.get(position).toString());

            return row;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void userRegistrationApi() {

        AppUtils.hideSoftKeyboard(mActivity);
        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.register;
        Log.v("userRegistrationApi-URL", url);

        JSONObject json_data = new JSONObject();

        try {

            json_data.put("firstName", etFirstName.getText().toString().trim());
            json_data.put("lastName", etLastName.getText().toString().trim());
            json_data.put("email", etEmail.getText().toString().trim());
            json_data.put("password", etPassword.getText().toString().trim());
            json_data.put("dateOfBirth", etDob.getText().toString().trim());
            json_data.put("weight", etWeight.getText().toString().trim());
            json_data.put("height", etHeight.getText().toString().trim());
            json_data.put("foodPreference", spinnerFood.getSelectedItem());
            json_data.put("medicalCondition", spinnerMedical.getSelectedItem());
            json_data.put("gender", gender);
            json_data.put("weightUnit", spinnerWeight.getSelectedItem());
            json_data.put("heightUnit", spinnerHeight.getSelectedItem());

            int tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();
            //String time = Calendar.getInstance().getTimeZone().getDisplayName(false, TimeZone.SHORT);
            String time = "" + tzOffset;
            Log.d("time", time);

            json_data.put("timeZone", time);

            Log.v("userRegistrationApi", json_data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(url)
                .addJSONObjectBody(json_data)
                .addHeaders("Content-Type", "application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("userRegistrationApi")
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
                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorCode()));
                            Log.d("onError errorCode ", "onError errorCode : " + error.getErrorCode());
                            Log.d("onError errorBody", "onError errorBody : " + error.getErrorBody());
                            Log.d("onError errorDetail", "onError errorDetail : " + error.getErrorDetail());

                           /* if( error.getErrorCode()==422)
                            {*/
                            try {
                                JSONObject jsonObject = new JSONObject(error.getErrorBody());

                                AppUtils.showToastSort(mActivity, jsonObject.getString("error"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // }

                        } else {
                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorDetail()));
                        }
                    }
                });
    }

    private void parseJSON(String response) {

        AppUtils.hideDialog();

        Log.d("response ", response.toString());

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("error")) {
                AppUtils.showToastSort(mActivity, jsonObject.getString("error"));
            } else {
                AppSettings.putString(AppSettings.firstName, jsonObject.getString("firstName"));
                AppSettings.putString(AppSettings.lastName, jsonObject.getString("lastName"));
                AppSettings.putString(AppSettings.email, jsonObject.getString("email"));
                AppSettings.putString(AppSettings.dateOfBirth, jsonObject.getString("dateOfBirth"));
                AppSettings.putString(AppSettings.weight, jsonObject.getString("weight"));
                AppSettings.putString(AppSettings.height, jsonObject.getString("height"));
                AppSettings.putString(AppSettings.medicalCondition, jsonObject.getString("medicalCondition"));
                AppSettings.putString(AppSettings.foodPreference, jsonObject.getString("foodPreference"));
                AppSettings.putString(AppSettings.userPhoto, "");
                AppSettings.putString(AppSettings.targetWeight, "");
                AppSettings.putString(AppSettings.targetDate, "");
                AppSettings.putString(AppSettings.targetCalories, "");
                AppSettings.putString(AppSettings.bmi, "0.0");
                AppSettings.putString(AppSettings.role, jsonObject.getString("role"));
                AppSettings.putString(AppSettings.userId, jsonObject.getString("_id"));
                AppSettings.putString(AppSettings.age, jsonObject.getString("age"));
                AppSettings.putString(AppSettings.gender, jsonObject.getString("gender"));
                AppSettings.putString(AppSettings.heightUnit, jsonObject.getString("heightUnit"));
                AppSettings.putString(AppSettings.weightUnit, jsonObject.getString("weightUnit"));

                startActivity(new Intent(mActivity, DashboardActivity.class));
                finishAffinity();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
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
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
