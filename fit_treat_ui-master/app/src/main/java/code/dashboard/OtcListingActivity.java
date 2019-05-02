package code.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;

import code.common.SimpleHTTPConnection;
import code.database.AppSettings;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;


public class OtcListingActivity extends BaseActivity implements View.OnClickListener {

    //GridLayoutManager
    GridLayoutManager mGridLayoutManager;

    //RelativeLayout
    RelativeLayout rlBack,rlSearch;

    //EditText
    EditText etSearch;

    //RecyclerView
    RecyclerView recyclerView;

    Adapter adapter;

    ArrayList<HashMap<String, String>> OtcListingList = new ArrayList<HashMap<String, String>>();

    //TextView
    TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);
        rlSearch = findViewById(R.id.rlSearch);

        //recyclerView
        recyclerView = findViewById(R.id.recyclerView);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);

        //Edittext for search
        etSearch = findViewById(R.id.etSearch);

        tvHeader.setText(getString(R.string.otcMedicines));

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
            initialSymptomsApi();
        } else {
            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
        }

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(etSearch.getText().toString().trim().isEmpty())
                    {
                        //AppUtils.showToastSort(mActivity, "Enter Symptoms to Search");

                        if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                            initialSymptomsApi();
                        } else {
                            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                        }
                    }
                    else if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                        searchSymptomsApi();
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }

                    return true;
                }
                return false;
            }
        });

        rlBack.setOnClickListener(this);
        rlSearch.setOnClickListener(this);

        AppUtils.hideSoftKeyboard(mActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;

            case R.id.rlSearch:

                if(etSearch.getText().toString().trim().isEmpty())
                {
                    //AppUtils.showToastSort(mActivity, "Enter Symptoms to Search");

                    if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                        initialSymptomsApi();
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }
                }
                else if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
                    searchSymptomsApi();
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
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_otc_listing, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppSettings.putString(AppSettings.json,data.get(position).get("json"));
                    AppSettings.putString(AppSettings.otcName,data.get(position).get("name"));
                    startActivity(new Intent(mActivity, OtcDetailActivity.class));

                }
            });

            holder.tvName.setText(data.get(position).get("name"));

        }

        public int getItemCount() {
            return data.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        TextView tvName;

        public Holder(View itemView) {
            super(itemView);
            tvName =  itemView.findViewById(R.id.tvName);
        }
    }


    private void initialSymptomsApi() {

        AppUtils.hideSoftKeyboard(mActivity);
        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.initialSymptoms;
        Log.v("initialSymptomsApi-URL", url);

        AndroidNetworking.get(url)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("initialSymptomsApi")
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

        OtcListingList.clear();

        Log.d("response ", response.toString());

        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> hashMap = new HashMap();

                hashMap.put("id",jsonObject.getString("_id"));
                hashMap.put("name",jsonObject.getString("name"));
                hashMap.put("json",jsonObject.toString());

                OtcListingList.add(hashMap);
            }



        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }

        adapter = new Adapter(OtcListingList);
        recyclerView.setAdapter(adapter);
    }

    private void searchSymptomsApi() {

        AppUtils.hideSoftKeyboard(mActivity);
        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.searchSymptoms+etSearch.getText().toString().trim();
        Log.v("searchSymptomsApi-URL", url);

        AndroidNetworking.get(url)
                .addHeaders("Content-Type","application/json")
                //.setContentType("application/json; charset=utf-8")
                .setPriority(Priority.HIGH)
                .setTag("searchSymptomsApi")
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
}
