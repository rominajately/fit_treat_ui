package code.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fittreat.android.BuildConfig;
import com.fittreat.android.R;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import code.common.SimpleHTTPConnection;
import code.database.AppSettings;
import code.general.RegisterActivity;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class MyProfileActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    RelativeLayout rlBack;

    //TextView
    TextView tvHeader;

    //ImageView
    ImageView ivMiddle;

    //Spinner
    Spinner spinnerWeight,spinnerHeight,spinnerFood,spinnerMedical;

    //LinearLayout
    LinearLayout llBottom,llTop;

    //ArrayList
    private  static ArrayList<String> WeightList = new ArrayList<String>();
    private  static ArrayList<String> HeightList = new ArrayList<String>();
    private  static ArrayList<String> FoodList = new ArrayList<String>();
    private  static ArrayList<String> MedicalList = new ArrayList<String>();

    //EditText
    EditText etFirstName,etLastName,etEmail,etDob,etHeight,etWeight,etGender;

    //TextView
    TextView tvSubmit;

    //RelativeLayout
    RelativeLayout rlEdit;

    //ImageView
    ImageView ivCamera;


    //String
    public String picturePath="",filename="",ext="",encodedString="";

    //Uri
    Uri picUri,fileUri;

    //StaticString
    private static final String IMAGE_DIRECTORY_NAME = String.valueOf(R.string.app_name);

    //Static Int
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100 , MEDIA_TYPE_IMAGE = 1;

    //Bitmap
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);
        tvSubmit = findViewById(R.id.tvSubmit);

        //RelativeLayout
        rlEdit = findViewById(R.id.rlEdit);

        //Spinner
        spinnerWeight= findViewById(R.id.spinnerWeight);
        spinnerHeight= findViewById(R.id.spinnerHeight);
        spinnerFood= findViewById(R.id.spinnerFood);
        spinnerMedical= findViewById(R.id.spinnerMedical);

        //LinearLayout
        llBottom = findViewById(R.id.llBottom);
        llTop = findViewById(R.id.llTop);

        //ImageView
        ivCamera = findViewById(R.id.ivCamera);

        //All Edittext
        etFirstName= findViewById(R.id.etFirstName);
        etLastName= findViewById(R.id.etLastName);
        etEmail= findViewById(R.id.etEmail);
        etGender= findViewById(R.id.etGender);
        etDob= findViewById(R.id.etDob);
        etHeight= findViewById(R.id.etHeight);
        etWeight= findViewById(R.id.etWeight);

        ivMiddle = findViewById(R.id.ivMiddle);

        tvHeader.setText(getString(R.string.my_profile));
        ivMiddle.setImageResource(R.mipmap.logo_light);

        settingValues();

        AppUtils.enableDisable(llTop,false);
        AppUtils.enableDisable(llBottom,false);
        rlEdit.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        tvSubmit.setVisibility(View.GONE);
        ivCamera.setVisibility(View.VISIBLE);

        rlBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        rlEdit.setOnClickListener(this);
        ivCamera.setOnClickListener(this);

        AppUtils.hideSoftKeyboard(mActivity);
    }

    private void settingValues() {

        etFirstName.setText(AppSettings.getString(AppSettings.firstName));
        etLastName.setText(AppSettings.getString(AppSettings.lastName));
        etEmail.setText(AppSettings.getString(AppSettings.email));
        etDob.setText(AppSettings.getString(AppSettings.dateOfBirth));
        etHeight.setText(AppSettings.getString(AppSettings.height));
        etWeight.setText(AppSettings.getString(AppSettings.weight));
        etGender.setText(AppSettings.getString(AppSettings.gender));

        if(!AppSettings.getString(AppSettings.profile).isEmpty())
        {
            ivMiddle.setImageBitmap(AppUtils.convertBase64ToBitmap(AppSettings.getString(AppSettings.profile)));
        }

        if(AppSettings.getString(AppSettings.userPhoto).isEmpty())
        {
            Resources res = getResources();
            Drawable background = res.getDrawable(R.drawable.ic_camera);
            int primaryColor = res.getColor(R.color.blueButton);
            background.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
            ivCamera.setImageDrawable(background);
        }
        else
        {
            Resources res = getResources();
            Drawable background = res.getDrawable(R.drawable.ic_camera);
            int primaryColor = res.getColor(R.color.white);
            background.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
            ivCamera.setImageDrawable(background);
        }

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

        int pos=0;
        for(int i=0;i<WeightList.size();i++)
        {
            if(AppSettings.getString(AppSettings.weightUnit).equalsIgnoreCase(WeightList.get(i)))
            {
                pos=i;
                break;
            }
        }

        spinnerWeight.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, WeightList));
        spinnerWeight.setSelection(pos);

        pos=0;
        for(int i=0;i<HeightList.size();i++)
        {
            if(AppSettings.getString(AppSettings.heightUnit).equalsIgnoreCase(HeightList.get(i)))
            {
                pos=i;
                break;
            }
        }

        spinnerHeight.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, HeightList));
        spinnerHeight.setSelection(pos);

        pos=0;
        for(int i=0;i<FoodList.size();i++)
        {
            if(AppSettings.getString(AppSettings.foodPreference).equalsIgnoreCase(FoodList.get(i)))
            {
                pos=i;
                break;
            }
        }

        spinnerFood.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, FoodList));
        spinnerFood.setSelection(pos);

        pos=0;
        for(int i=0;i<MedicalList.size();i++)
        {
            if(AppSettings.getString(AppSettings.medicalCondition).equalsIgnoreCase(MedicalList.get(i)))
            {
                pos=i;
                break;
            }
        }

        spinnerMedical.setAdapter(new adapterSpinner(mActivity, R.layout.spinner_adapter, MedicalList));
        spinnerMedical.setSelection(pos);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;

            case R.id.ivCamera:

                AlertChoose();

                return;

            case R.id.rlEdit:

                rlEdit.setVisibility(View.GONE);
                llTop.setVisibility(View.GONE);
                tvSubmit.setVisibility(View.VISIBLE);
                AppUtils.enableDisable(llBottom,true);

                return;

            case R.id.tvSubmit:

                if(etFirstName.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorFName));
                }
                else  if(!AppUtils.isValidEmail(etEmail.getText().toString()))
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEmail));
                }
                else if(etDob.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorDob));
                }
                else if(etHeight.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorHeight));
                }
                else if(etWeight.getText().toString().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorWeight));
                }
                else if(spinnerFood.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorFood));
                }
                else if(spinnerMedical.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorMedical));
                }
                else if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    updateProfileApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
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

    private void updateProfileApi() {

        AppUtils.hideSoftKeyboard(mActivity);
        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.updateProfile;
        Log.v("updateProfileApi-URL", url);

        JSONObject json_data = new JSONObject();

        try {

            json_data.put("id", AppSettings.getString(AppSettings.userId));
            json_data.put("firstName", etFirstName.getText().toString().trim());
            json_data.put("lastName", etLastName.getText().toString().trim());
            json_data.put("weight",  etWeight.getText().toString().trim());
            json_data.put("height",  etHeight.getText().toString().trim());
            json_data.put("foodPreference",  spinnerFood.getSelectedItem());
            json_data.put("medicalCondition",  spinnerMedical.getSelectedItem());
            json_data.put("weightUnit",  spinnerWeight.getSelectedItem());
            json_data.put("heightUnit",  spinnerHeight.getSelectedItem());

            Log.v("updateProfileApi", json_data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.put(url)
                .addJSONObjectBody(json_data)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("updateProfileApi")
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

            if(jsonObject.has("error"))
            {
                AppUtils.showToastSort(mActivity, jsonObject.getString("error"));
            }
            else
            {
                rlEdit.setVisibility(View.VISIBLE);
                llTop.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.GONE);
                AppUtils.enableDisable(llBottom,false);

                AppSettings.putString(AppSettings.firstName,jsonObject.getString("firstName"));
                AppSettings.putString(AppSettings.lastName,jsonObject.getString("lastName"));
                AppSettings.putString(AppSettings.email,jsonObject.getString("email"));
                AppSettings.putString(AppSettings.dateOfBirth,jsonObject.getString("dateOfBirth"));
                AppSettings.putString(AppSettings.weight,jsonObject.getString("weight"));
                AppSettings.putString(AppSettings.height,jsonObject.getString("height"));
                AppSettings.putString(AppSettings.medicalCondition,jsonObject.getString("medicalCondition"));
                AppSettings.putString(AppSettings.foodPreference,jsonObject.getString("foodPreference"));
                AppSettings.putString(AppSettings.targetWeight,jsonObject.getString("targetWeight"));
                AppSettings.putString(AppSettings.targetDate,jsonObject.getString("targetDate"));
                AppSettings.putString(AppSettings.targetCalories,jsonObject.getString("targetCalories"));
                AppSettings.putString(AppSettings.role,jsonObject.getString("role"));
                AppSettings.putString(AppSettings.userId,jsonObject.getString("_id"));
                AppSettings.putString(AppSettings.age,jsonObject.getString("age"));
                AppSettings.putString(AppSettings.gender,jsonObject.getString("gender"));
                AppSettings.putString(AppSettings.heightUnit,jsonObject.getString("heightUnit"));
                AppSettings.putString(AppSettings.weightUnit,jsonObject.getString("weightUnit"));

                settingValues();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }
    }





    public void AlertChoose() {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_choose);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextView tvChoose          = dialog.findViewById(R.id.textView79);
        TextView tvCamera          = dialog.findViewById(R.id.textView80);
        TextView tvLibrary         = dialog.findViewById(R.id.textView81);
        TextView tvCancel          = dialog.findViewById(R.id.textView83);

        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                //Uri imageFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // convert path to Uri

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(MEDIA_TYPE_IMAGE));

                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(it, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                }
                else
                {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                }

            }
        });

        tvLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,1);

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

    }

    public static String getFileType(String path){
        String fileType = null;
        fileType = path.substring(path.indexOf('.',path.lastIndexOf('/'))+1).toLowerCase();
        return fileType;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                android.util.Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "+ IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + IMAGE_DIRECTORY_NAME + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                beginCrop(fileUri);
            }
        }
        else if (requestCode == 1) {
            if (data != null) {
                try {
                    Uri contentURI = data.getData();

                    beginCrop(contentURI);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else
            {
                Toast.makeText(mActivity,"unable to select image",Toast.LENGTH_LONG).show();
            }

        }
        else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }

    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }



    private void updateProfilePicApi() {

        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.photoUpdate;
        Log.v("updateProfilePicApi-URL", url);

        JSONObject json_data = new JSONObject();

        try {

            json_data.put("id", AppSettings.getString(AppSettings.userId));
            json_data.put("userPhoto",  encodedString);

            Log.v("updateProfilePicApi", json_data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(url)
                .addJSONObjectBody(json_data)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("updateProfileApi")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        parsePicJSON(response);
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

    private void parsePicJSON(String response){

        AppUtils.hideDialog();

        Log.d("response ", response.toString());

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("error"))
            {
                AppUtils.showToastSort(mActivity, jsonObject.getString("error"));
            }
            else
            {
                AppSettings.putString(AppSettings.profile,encodedString);

                settingValues();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }
    }


    private void beginCrop(Uri source) {
        picturePath = source.getPath().toString();
        filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);
        Uri destination = Uri.fromFile(new File(this.getCacheDir(), filename));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            picturePath = Crop.getOutput(result).getPath().toString();
            //picturePath = fileUri.getPath().toString();
            filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);

            String selectedImagePath = picturePath;

            ext = "jpg";

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 500;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

            Matrix matrix = new Matrix();
            matrix.postRotate(getImageOrientation(picturePath));
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bao);

            bitmap = rotatedBitmap;

            ivMiddle.setImageBitmap(rotatedBitmap);
            encodedString = getEncoded64ImageStringFromBitmap(rotatedBitmap);

            if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                updateProfilePicApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
