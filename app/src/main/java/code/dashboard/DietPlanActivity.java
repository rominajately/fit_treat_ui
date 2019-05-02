package code.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fittreat.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import code.common.SimpleHTTPConnection;
import code.database.AppSettings;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class DietPlanActivity extends BaseActivity implements View.OnClickListener {

    //GridLayoutManager
    GridLayoutManager mGridLayoutManager;

    //RelativeLayout
    RelativeLayout rlBack,rlFilter,rlFilterMain,rlBreakfast,rlLunch,rlDinner,rlSnack,rlJuice;

    //RecyclerView
    RecyclerView recyclerView;

    Adapter adapter;

    ArrayList<HashMap<String, String>> DietPlanList = new ArrayList<HashMap<String, String>>();

    //TextView
    TextView tvHeader;

    //View
    View view;

    //ImageView
    ImageView ivReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_plan);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);
        rlFilter = findViewById(R.id.rlFilter);
        rlFilterMain = findViewById(R.id.rlFilterMain);
        rlBreakfast= findViewById(R.id.rlBreakfast);
        rlLunch= findViewById(R.id.rlLunch);
        rlDinner= findViewById(R.id.rlDinner);
        rlSnack= findViewById(R.id.rlSnack);
        rlJuice= findViewById(R.id.rlJuice);

        //ImageView
        ivReset= findViewById(R.id.ivReset);

        //View when filter is open
        view= findViewById(R.id.view);

        //recyclerView
        recyclerView = findViewById(R.id.recyclerView);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);

        tvHeader.setText(getString(R.string.dietPlan));
        rlFilter.setVisibility(View.VISIBLE);
        rlFilterMain.setVisibility(View.GONE);
        ivReset.setVisibility(View.VISIBLE);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
            getDietPlanApi();
        } else {
            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
        }

        rlBack.setOnClickListener(this);
        rlFilter.setOnClickListener(this);
        rlBreakfast.setOnClickListener(this);
        rlLunch.setOnClickListener(this);
        rlDinner.setOnClickListener(this);
        rlSnack.setOnClickListener(this);
        rlJuice.setOnClickListener(this);
        view.setOnClickListener(this);
        ivReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivReset:

                if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    getDietPlanApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                return;

            case R.id.rlBack:

                onBackPressed();

                return;

            case R.id.rlFilter:

                if(rlFilterMain.getVisibility()==View.VISIBLE)
                {
                    view.setVisibility(View.GONE);
                    rlFilterMain.setVisibility(View.GONE);
                }
                else
                {
                    view.setVisibility(View.VISIBLE);
                    rlFilterMain.setVisibility(View.VISIBLE);
                }

                return;

            case R.id.rlBreakfast:

                view.setVisibility(View.GONE);
                rlFilterMain.setVisibility(View.GONE);

                if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    getFilterMealsApi(getString(R.string.breakfast));
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                return;

            case R.id.view:

                view.setVisibility(View.GONE);
                rlFilterMain.setVisibility(View.GONE);

                return;

            case R.id.rlLunch:

                view.setVisibility(View.GONE);
                rlFilterMain.setVisibility(View.GONE);

                if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    getFilterMealsApi(getString(R.string.lunch));
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                return;

            case R.id.rlDinner:

                view.setVisibility(View.GONE);
                rlFilterMain.setVisibility(View.GONE);

                if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    getFilterMealsApi(getString(R.string.dinner));
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                return;

            case R.id.rlSnack:

                view.setVisibility(View.GONE);
                rlFilterMain.setVisibility(View.GONE);

                if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    getFilterMealsApi(getString(R.string.snack));
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                return;

            case R.id.rlJuice:

                view.setVisibility(View.GONE);
                rlFilterMain.setVisibility(View.GONE);

                if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    getFilterMealsApi(getString(R.string.juice));
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                return;
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public Adapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_diet_plan, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppSettings.putString(AppSettings.json,data.get(position).get("json"));
                    startActivity(new Intent(mActivity, DietDetailsActivity.class));

                }
            });

            holder.tvName.setText(data.get(position).get("name"));
            holder.tvDetails.setText(data.get(position).get("type")
            +"\n"+data.get(position).get("serving")+" "+getString(R.string.serving)
            +"\n"+data.get(position).get("calories")+" cals");

            try {
                Picasso.get().load(data.get(position).get("photoURL")).into(holder.ivDietPhoto);
            } catch (Exception e) {
                e.printStackTrace();
                //holder.ivDietPhoto.setImageResource(R.drawable.ic_user);
            }

        }

        public int getItemCount() {
            return data.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        TextView tvName,tvDetails;
        ImageView ivDietPhoto;
        RelativeLayout rlMain;

        public Holder(View itemView) {
            super(itemView);
            tvName =  itemView.findViewById(R.id.tvName);
            tvDetails =  itemView.findViewById(R.id.tvDetails);
            ivDietPhoto =  itemView.findViewById(R.id.ivDietPhoto);
            rlMain =  itemView.findViewById(R.id.rlMain);
        }
    }


    private void getDietPlanApi() {

        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.getMeals;
        Log.v("getDietPlanApi-URL", url);

        AndroidNetworking.get(url)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("getDietPlanApi")
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

        DietPlanList.clear();

        Log.d("response ", response.toString());

        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> hashMap = new HashMap();

                hashMap.put("id",jsonObject.getString("_id"));
                hashMap.put("name",jsonObject.getString("name"));
                hashMap.put("calories",jsonObject.getString("calories"));
                try {
                    hashMap.put("type",jsonObject.getString("cuisine"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    hashMap.put("type","");
                }
                hashMap.put("serving",jsonObject.getString("servingSize"));
                hashMap.put("photoURL",jsonObject.getString("photoURL"));
                hashMap.put("json",jsonObject.toString());

                DietPlanList.add(hashMap);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, "Some error occurred");
        }

        adapter = new Adapter(DietPlanList);
        recyclerView.setAdapter(adapter);

    }





    private void getFilterMealsApi(String search) {

        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.filterMeals
                +search
                +"/"+AppSettings.getString(AppSettings.foodPreference)
                +"/"+AppSettings.getString(AppSettings.userId);
        Log.v("getFilterMealsApi-URL", url);

        AndroidNetworking.get(url)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("getFilterMealsApi")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        parseFilterMealsJSON(response);
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

    private void parseFilterMealsJSON(String response){

        AppUtils.hideDialog();

        DietPlanList.clear();

        Log.d("response ", response.toString());

        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> hashMap = new HashMap();

                hashMap.put("id",jsonObject.getString("_id"));
                hashMap.put("name",jsonObject.getString("name"));
                hashMap.put("calories",jsonObject.getString("calories"));
                try {
                    hashMap.put("type",jsonObject.getString("cuisine"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    hashMap.put("type","");
                }
                hashMap.put("serving",jsonObject.getString("servingSize"));
                hashMap.put("photoURL",jsonObject.getString("photoURL"));
                hashMap.put("json",jsonObject.toString());

                DietPlanList.add(hashMap);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }

        adapter = new Adapter(DietPlanList);
        recyclerView.setAdapter(adapter);

    }


}
