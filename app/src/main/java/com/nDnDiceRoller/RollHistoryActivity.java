package com.nDnDiceRoller;

        import android.app.Activity;
        import android.os.Bundle;
        import android.widget.TextView;

public class RollHistoryActivity extends Activity {


    /**
     * Create the activity.
     * @param savedInstanceState - If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        if(getIntent().getExtras() != null){
            TextView historyTextView = (TextView) findViewById(R.id.textViewHistory);
            String history = getIntent().getExtras().getString("HistoryString");
            historyTextView.setText(history);
        }
    }

}
