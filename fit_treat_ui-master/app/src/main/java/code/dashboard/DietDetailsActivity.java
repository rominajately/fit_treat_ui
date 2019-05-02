package code.dashboard;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fittreat.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import code.database.AppSettings;
import code.view.BaseActivity;

public class DietDetailsActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    RelativeLayout rlBack;

    //TextView
    TextView tvHeader,tvIngredients,tvName,tvCalories,tvNutritionInfo,tvTags,tvDirections;

    //ImageView
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_detail);

        findViewById();
    }

    private void findViewById() {

        //RelativeLayout
        rlBack = findViewById(R.id.rlBack);

        //TextView for Header Text
        tvHeader = findViewById(R.id.tvHeader);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvName = findViewById(R.id.tvName);
        tvCalories = findViewById(R.id.tvCalories);
        tvNutritionInfo = findViewById(R.id.tvNutritionInfo);
        tvTags = findViewById(R.id.tvTags);
        tvDirections = findViewById(R.id.tvDirections);

        Typeface font = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        tvIngredients.setTypeface(font);
        tvNutritionInfo.setTypeface(font);
        tvDirections.setTypeface(font);

        ivImage = findViewById(R.id.ivImage);

        rlBack.setOnClickListener(this);
        
        setValues();
    }

    private void setValues() {

        try {
            JSONObject jsonObject = new JSONObject(AppSettings.getString(AppSettings.json));

            //tvHeader.setText(jsonObject.getString("name"));
            tvName.setText(jsonObject.getString("name"));
            tvCalories.setText(getString(R.string.calorie)+": "+jsonObject.getString("calories"));

            String str = jsonObject.getString("dietType").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"","").trim();

            /*if(str.contains(","))
            {
                str = str.replaceAll(",","\n").replaceAll(" ","").trim();
            }*/

            tvTags.setText(str.trim());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvIngredients.setText(Html.fromHtml(jsonObject.getString("ingredients"), Html.FROM_HTML_MODE_COMPACT));
                tvNutritionInfo.setText(Html.fromHtml(jsonObject.getString("nutritionInfo"), Html.FROM_HTML_MODE_COMPACT));
                tvDirections.setText(Html.fromHtml(jsonObject.getString("directions"), Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvIngredients.setText(Html.fromHtml(jsonObject.getString("ingredients")));
                tvNutritionInfo.setText(Html.fromHtml(jsonObject.getString("nutritionInfo")));
                tvDirections.setText(Html.fromHtml(jsonObject.getString("directions")));
            }

            try {
                Picasso.get().load(jsonObject.getString("photoURL")).into(ivImage);
            } catch (Exception e) {
                e.printStackTrace();
                //holder.ivDietPhoto.setImageResource(R.drawable.ic_user);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlBack:

                onBackPressed();

                return;

        }
    }
}
