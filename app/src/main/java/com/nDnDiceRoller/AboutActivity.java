package com.nDnDiceRoller;

import android.app.Activity;
import android.os.Bundle;

/**
 * Static activity that shows standard about information, including author, version, and contact
 * information.
 */
public class AboutActivity extends Activity {
    /**
     * Create the activity.
     * @param savedInstanceState - If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }
}
