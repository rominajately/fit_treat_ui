package code.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.SpannableString;

import code.callbacks.CallBackDialog;


/**
 * Created by Mohammad Faiz
 */

public class AppDefaultDialog {
    Activity mActivity;
    CallBackDialog mCallBackDialog;
    AlertDialog myAlertDialog;

    public AppDefaultDialog(final Activity mActivity, CallBackDialog mCallBackDialog) {
        this.mActivity = mActivity;
        this.mCallBackDialog = mCallBackDialog;
    }


    public void displayDialog(String message, String positiveButton, String negativeButton) {
        Typeface typefaceRegular = Typeface.createFromAsset(mActivity.getAssets(), "centurygothic.otf");
        Typeface typefaceMedium = Typeface.createFromAsset(mActivity.getAssets(), "centurygothic.otf");
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);
        //@ Setting Dialog Message
        mBuilder.setMessage(customTypeface(typefaceMedium, message));
        //@ Setting Positive "Yes" Button
        mBuilder.setPositiveButton(customTypeface(typefaceRegular, positiveButton), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //@ Clear all screens and go to mobile home screen.
                mCallBackDialog.callBackYes(myAlertDialog);
            }
        });
        if (!negativeButton.isEmpty()) {
            //@ Setting Negative "NO" Button
            mBuilder.setNegativeButton(customTypeface(typefaceRegular, negativeButton), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mCallBackDialog.callBackNo(myAlertDialog);
                }
            });
        }
        //@ Showing Alert Message
        myAlertDialog = mBuilder.create();
        myAlertDialog.show();
    }


    /**
     * <p>Return spannable string with applied typeface in certain style</p>
     * <p>
     * http://stackoverflow.com/questions/8607707/how-to-set-a-custom-font-in-the-actionbar-title
     *
     * @param typeface The typeface to set to the {@link SpannableString}
     * @param string   the string to place in the span
     * @return SpannableString that can be used in TextView.setText() method
     */
    public static SpannableString customTypeface(Typeface typeface, CharSequence string) {
        SpannableString s = new SpannableString(string);
        //s.setSpan(new CustomTypefaceSpan("", typeface), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return s;
    }
}
