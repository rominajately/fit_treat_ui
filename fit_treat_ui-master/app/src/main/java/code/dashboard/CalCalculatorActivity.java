package code.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fittreat.android.BuildConfig;
import com.fittreat.android.R;

import java.util.ArrayList;

import code.database.AppSettings;
import code.general.RegisterActivity;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class CalCalculatorActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    RelativeLayout rlBack,rlInfo;

    //TextView
    TextView tvHeader,tvSubmit;

    //ImageView
    ImageView ivMiddle;

    EditText etTarWeight,etTime,etHeight,etWeight,etAge;

    Spinner spinnerTarWeight,spinnerTime,spinnerActivity,spinnerWeight,spinnerHeight;

    //RadioButton
    RadioButton rbMale,rbFemale;

    //ArrayList
    private  static ArrayList<String> TargetWeightList = new ArrayList<String>();
    private  static ArrayList<String> TargetTimeList = new ArrayList<String>();
    private  static ArrayList<String> ActivityList = new ArrayList<String>();
    private  static ArrayList<String> WeightList = new ArrayList<String>();
    private  static ArrayList<String> HeightList = new ArrayList<String>();

    String gender="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal_calculator);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);
        rlInfo = findViewById(R.id.rlInfo);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);
        tvSubmit = findViewById(R.id.tvSubmit);

        ivMiddle = findViewById(R.id.ivMiddle);

        //Radio Buttons for Male and Female
        rbMale= findViewById(R.id.rbMale);
        rbFemale= findViewById(R.id.rbFemale);

        etTarWeight = findViewById(R.id.etTarWeight);
        etTime = findViewById(R.id.etTime);
        etHeight= findViewById(R.id.etHeight);
        etWeight= findViewById(R.id.etWeight);
        etAge= findViewById(R.id.etAge);

        spinnerTarWeight = findViewById(R.id.spinnerTarWeight);
        spinnerTime= findViewById(R.id.spinnerTime);
        spinnerActivity= findViewById(R.id.spinnerActivity);
        spinnerWeight= findViewById(R.id.spinnerWeight);
        spinnerHeight= findViewById(R.id.spinnerHeight);

        rlInfo.setVisibility(View.VISIBLE);

        tvHeader.setText(getString(R.string.calCalculator));
        ivMiddle.setImageResource(R.drawable.ic_calories_calculator);

        TargetWeightList.clear();
        TargetWeightList.add(getString(R.string.kg));
        TargetWeightList.add(getString(R.string.lb));

        TargetTimeList.clear();
        TargetTimeList.add(getString(R.string.days));
        TargetTimeList.add(getString(R.string.weeks));

        ActivityList.clear();
        ActivityList.add(getString(R.string.dailyActivities));
        ActivityList.add(getString(R.string.sedentart));
        ActivityList.add(getString(R.string.slightlyActive));
        ActivityList.add(getString(R.string.moderatelyActive));
        ActivityList.add(getString(R.string.veryActive));
        ActivityList.add(getString(R.string.extraActive));

        WeightList.clear();
        WeightList.add(getString(R.string.kg));
        WeightList.add(getString(R.string.lb));

        HeightList.clear();
        HeightList.add(getString(R.string.cm));
        HeightList.add(getString(R.string.m));
        HeightList.add(getString(R.string.ft));


        spinnerTarWeight.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, TargetWeightList));
        spinnerTarWeight.setSelection(0);

        spinnerTime.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, TargetTimeList));
        spinnerTime.setSelection(0);

        spinnerActivity.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, ActivityList));
        spinnerActivity.setSelection(0);

        spinnerWeight.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, WeightList));
        spinnerWeight.setSelection(0);

        spinnerHeight.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, HeightList));
        spinnerHeight.setSelection(0);

        rbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    gender=getString(R.string.male);
                    rbMale.setChecked(true);
                    rbFemale.setChecked(false);
                }

            }
        });

        rbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    gender=getString(R.string.female);
                    rbMale.setChecked(false);
                    rbFemale.setChecked(true);
                }

            }
        });

        rlBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        rlInfo.setOnClickListener(this);

        AppUtils.hideSoftKeyboard(mActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;

            case R.id.rlInfo:

                AlertInfo();

                return;

            case R.id.tvSubmit:

                if(gender.isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorGender));
                }
                else if(etHeight.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorHeight));
                }
                else if(etWeight.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorWeight));
                }
                else if(etAge.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorAge));
                }
                else if(etTarWeight.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorTarWeight));
                }
                else if(etTime.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorTarTime));
                }
                else if(spinnerActivity.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorDailyActivity));
                }
                else
                {
                    /*
                    * double height,
                       String heightType,
                       String gender,
                       int age,
                       double weight,
                       String weigthType,
                       double targetWeight,
                       String tarWeiType,
                       double weeks,
                       String weeksType,
                       String active*/

                    double calCalorie = AppUtils.calculateCalories(mActivity,
                            Double.valueOf(etHeight.getText().toString().trim()),
                            spinnerHeight.getSelectedItem().toString(),
                            gender,
                            Integer.parseInt(etAge.getText().toString().trim()),
                            Double.valueOf(etWeight.getText().toString().trim()),
                            spinnerWeight.getSelectedItem().toString(),
                            Double.valueOf(etTarWeight.getText().toString().trim()),
                            spinnerTarWeight.getSelectedItem().toString(),
                            Double.valueOf(etTime.getText().toString().trim()),
                            spinnerTime.getSelectedItem().toString(),
                            spinnerActivity.getSelectedItem().toString());

                    AppUtils.hideSoftKeyboard(mActivity);

                    calCalorie =  Math.round(calCalorie);

                    String cal = String.valueOf(calCalorie);

                    if(cal.contains("."))
                    {
                        String[] separated = cal.split("\\.");
                        cal = separated[0];
                    }



                    double bmr = AppUtils.calculateBMR(mActivity,
                            Double.valueOf(etHeight.getText().toString().trim()),
                            spinnerHeight.getSelectedItem().toString(),
                            gender,
                            Integer.parseInt(etAge.getText().toString().trim()),
                            Double.valueOf(etWeight.getText().toString().trim()),
                            spinnerWeight.getSelectedItem().toString());

                    Log.d("CaculationBMR", String.valueOf(bmr));

                    float activityLevel = AppUtils.getActivity(mActivity,
                            spinnerActivity.getSelectedItem().toString());

                    Log.d("CaculationActivity", String.valueOf(activityLevel));

                    double calToSustainWeight = bmr * activityLevel;
                    Log.d("CaculationSusTain", String.valueOf(calToSustainWeight));

                    calToSustainWeight =  Math.round(calToSustainWeight);

                    String calB = String.valueOf(calToSustainWeight);

                    if(calB.contains("."))
                    {
                        String[] separated = calB.split("\\.");
                        calB = separated[0];
                    }

                    AlertResult(calB,cal);
                }

                return;

        }
    }

    //Spinner DemoAdapter
    public class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        public adapterSpinner(Context context, int textViewResourceId, ArrayList <String> arraySpinner_time) {

            super(context, textViewResourceId, arraySpinner_time);

            this.data = arraySpinner_time;

        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row=inflater.inflate(R.layout.spinner_adapter, parent, false);
            TextView label=(TextView)row.findViewById(R.id.tv_spinner_name);

            label.setText(data.get(position).toString());

            return row;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void AlertResult(String sus,String cal) {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_result);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextView tvSus          = dialog.findViewById(R.id.textView80);
        TextView tvCal         = dialog.findViewById(R.id.textView81);
        TextView tvCancel          = dialog.findViewById(R.id.textView83);


        String strStart="\u25CF Calories needed to sustain your current weight are ";
        String strEnd=" calories per day.";

        Spannable wordtoSpan = null;
        String str = strStart +sus + strEnd;
        String str1 = strStart +sus;
        wordtoSpan = new SpannableString(str);
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blueDark)), strStart.length(), str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSus.setText(wordtoSpan);

        Typeface font = Typeface.createFromAsset(getAssets(), "Arciform.otf");
        tvSus.setTypeface(font);

        strStart="\u25CF Calories needed to achieve your target weight are ";
        strEnd=" calories per day.";
        wordtoSpan = null;
        str = strStart +cal + strEnd;
        str1 = strStart +cal;
        wordtoSpan = new SpannableString(str);
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blueDark)), strStart.length(), str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvCal.setText(wordtoSpan);
        tvCal.setTypeface(font);

        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                onBackPressed();
            }
        });

    }

    public void AlertInfo() {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_info);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextView tvInfo          = dialog.findViewById(R.id.textView80);
        TextView tvCancel          = dialog.findViewById(R.id.textView83);

        String info1="\u25CF Sedentary Activity Level – When a person has little to no exercise";
        String info2="\n\n\u25CF Slightly Active – When a person has light exercise or light sports at least one to three times per week";
        String info3="\n\n\u25CF Moderately Active – When a person has a moderate amount of exercises or sports three to five times per week";
        String info4="\n\n\u25CF Very Active – When a person does hard exercises or sports six to seven times per week";
        String info5="\n\n\u25CF Extra Active – When a person does rigorous exercises or sports or physical training";

        tvInfo.setText(info1+info2+info3+info4+info5);

        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

    }

}
