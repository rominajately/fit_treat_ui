package code.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import code.common.SimpleHTTPConnection;
import code.database.AppSettings;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class InboxActivity extends BaseActivity implements View.OnClickListener {

    //GridLayoutManager
    GridLayoutManager mGridLayoutManager;

    //RelativeLayout
    RelativeLayout rlBack;

    //RecyclerView
    RecyclerView recyclerView;

    Adapter adapter;

    //TextView
    TextView tvHeader;

    //View
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);

        //recyclerView
        recyclerView = findViewById(R.id.recyclerView);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);

        tvHeader.setText(getString(R.string.inbox));

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        rlBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new Adapter(AppConstants.InboxList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public Adapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_inbox,parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i=new Intent(mActivity,InboxDetailActivity.class);
                    i.putExtra("subject", data.get(position).get("subject"));
                    i.putExtra("content", data.get(position).get("content"));
                    i.putExtra("createDate", data.get(position).get("createDate"));
                    i.putExtra("readFlag", data.get(position).get("readFlag"));
                    i.putExtra("id", data.get(position).get("id"));
                    i.putExtra("position", String.valueOf(position));
                    startActivity(i);

                }
            });

            holder.tvName.setText(data.get(position).get("subject"));
            holder.tvDetails.setText(data.get(position).get("content"));
            holder.tvTime.setText(data.get(position).get("createDate"));

            if(data.get(position).get("readFlag").equals("false"))
            {
                holder.cardView.setBackgroundResource(R.color.blueGrey);
            }
            else
            {
                holder.cardView.setBackgroundResource(R.color.white);
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                holder.tvDetails.setText(Html.fromHtml(data.get(position).get("content"), Html.FROM_HTML_MODE_COMPACT));
            }
            else
            {
                holder.tvDetails.setText(Html.fromHtml(data.get(position).get("content")));
            }

            /*try {
                Picasso.get().load(data.get(position).get("profile")).into(holder.ivDietPhoto);
            } catch (Exception e) {
                e.printStackTrace();
                holder.ivDietPhoto.setImageResource(R.mipmap.user);
            }*/

        }

        public int getItemCount() {
            return data.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        TextView tvName,tvDetails,tvTime;
        ImageView ivDietPhoto;
        RelativeLayout rlMain;
        CardView cardView;


        public Holder(View itemView) {
            super(itemView);
            tvName =  itemView.findViewById(R.id.tvName);
            tvDetails =  itemView.findViewById(R.id.tvDetails);
            tvTime =  itemView.findViewById(R.id.tvTime);
            ivDietPhoto =  itemView.findViewById(R.id.ivDietPhoto);
            rlMain =  itemView.findViewById(R.id.rlMain);
            cardView =  itemView.findViewById(R.id.cardView);
        }
    }

    /*private void userInboxApi() {

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

                InboxList.add(hashMap);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppUtils.showToastSort(mActivity, response);
        }

        adapter = new Adapter(AppConstants.InboxList);
        recyclerView.setAdapter(adapter);
    }*/
}
