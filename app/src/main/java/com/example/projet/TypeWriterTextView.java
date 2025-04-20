package com.example.projet;

import android.content.Context;
import android.util.AttributeSet;

public class TypeWriterTextView extends androidx.appcompat.widget.AppCompatTextView {
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 100;

    public TypeWriterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;
        setText("");
        removeCallbacks(characterAdder);
        postDelayed(characterAdder, mDelay);
    }

    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                postDelayed(this, mDelay);
            }
        }
    };
}
