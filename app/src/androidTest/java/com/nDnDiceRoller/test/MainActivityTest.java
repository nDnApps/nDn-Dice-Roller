package com.nDnDiceRoller.test;
import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.nDnDiceRoller.DiceSet;
import com.nDnDiceRoller.MainActivity;
import com.nDnDiceRoller.AddDiceMenu;


/**
 * Created by jonckh10 on 12/12/2014.
 */
public class MainActivityTest extends android.test.ActivityInstrumentationTestCase2<MainActivity>{

    private Activity mActivity;
    private LinearLayout mAddDiceMenu;
    private DiceSet mDiceSet;
    private Die

    public MainActivityTest(){
        super(MainActivity.Class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        mAddDiceMenu = (LinearLayout)mActivity.findViewById(com.nDnDiceRoller.R.id.addDiceMenu);

        mDiceSet

    }

    public void testPreConditions() {
        assertTrue
    }

  }
}
