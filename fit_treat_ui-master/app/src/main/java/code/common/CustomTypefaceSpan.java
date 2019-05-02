package code.common;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

@SuppressLint({"ParcelCreator"})
public class CustomTypefaceSpan extends ClickableSpan {

    private int newColor;
    private CallBackMyClickableSpan myClickableSpan;

   /* public CustomTypefaceSpan(Typeface type) {
        newType = type;
    }*/

    public CustomTypefaceSpan(int color, int i, int endPosition, int spanExclusiveInclusive) {
        //newType = type;
        newColor = color;
    }

    public CustomTypefaceSpan(int color) {
        //newType = type;
        newColor = color;
    }

    public CustomTypefaceSpan( int color, CallBackMyClickableSpan myClickableSpan) {
        newColor = color;
        this.myClickableSpan = myClickableSpan;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newColor);
    }

    @Override
    public void onClick(View widget) {
        if (myClickableSpan != null)
            myClickableSpan.onClickable();
    }

    private void applyCustomTypeFace(Paint paint, int color) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle;
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }
        if (newColor != 0)
            paint.setColor(color);
        //paint.setTypeface(tf);
    }
}
