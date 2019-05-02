package code.general;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fittreat.android.R;

import code.view.BaseActivity;

public class SelectionActivity extends BaseActivity  implements View.OnClickListener {

    //ImageView
    ImageView ivLogo;

    //RelativeLayout
    RelativeLayout rlLogo;

    //TextView
    TextView tvLogin,tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_selection);

        //ImageView
        ivLogo = findViewById(R.id.imageView);

        //RelativeLayout
        rlLogo = findViewById(R.id.rlLogo);

        //TextView
        tvLogin = findViewById(R.id.tvLogin);
        tvRegister = findViewById(R.id.tvRegister);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomViewAnimation();
            }
        },600);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation LeftSwipe = AnimationUtils.loadAnimation(mActivity, R.anim.slide_view_from_right);
                tvRegister.startAnimation(LeftSwipe);
                tvRegister.setVisibility(View.VISIBLE);

                Animation RightSwipe = AnimationUtils.loadAnimation(mActivity, R.anim.slide_view_from_left);
                tvLogin.startAnimation(RightSwipe);
                tvLogin.setVisibility(View.VISIBLE);
            }
        },1600);

        tvLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

    }

    private void logoAnimation() {
        AlphaAnimation mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(600);
        mAlphaAnimation.setFillAfter(true);
        ivLogo.setVisibility(View.VISIBLE);
        ivLogo.startAnimation(mAlphaAnimation);
    }

    private void bottomViewAnimation() {
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(0, 0, getResources().getDimension(R.dimen._150sdp), 0);
        mTranslateAnimation.setDuration(1000);
        mTranslateAnimation.setFillAfter(true);
        rlLogo.setVisibility(View.VISIBLE);
        rlLogo.startAnimation(mTranslateAnimation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvLogin:

                startActivity(new Intent(mActivity, LoginActivity.class));
                finish();

                return;

            case R.id.tvRegister:

                startActivity(new Intent(mActivity, RegisterActivity.class));
                finish();

                return;
        }
    }
}
