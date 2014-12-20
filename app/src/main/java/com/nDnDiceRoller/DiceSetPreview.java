package com.nDnDiceRoller;

        import android.content.Context;
import android.widget.LinearLayout;

public class DiceSetPreview {

    Context mContext;
    LinearLayout mLinearLayout;
    String mName;

    public DiceSetPreview(Context context, LinearLayout linearLayout) {
        mContext = context;
        mLinearLayout = linearLayout;
    }

    void loadDiceSet(DiceSet diceSet)
    {
        //clear layout
        mLinearLayout.removeAllViews();

        mName = diceSet.getName();
        assert(mName != null);

        for(Die d : diceSet)
        {
            mLinearLayout.addView(new DieView(mContext, d));
        }

    }


    public void clear() {
        mLinearLayout.removeAllViews();
    }

}
