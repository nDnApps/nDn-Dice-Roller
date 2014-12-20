package com.nDnDiceRoller;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by jonckh10 on 11/16/2014.
 */
public class TargetProbabilityDialog extends Dialog {

    private IHandProbability mDiceHandProb;
    private NumberRangeSpinner mNumberRangeSpinner;
    private TextView mProbabilityResult;

    public TargetProbabilityDialog(final Activity parent, IHandProbability diceHandProb)
    {
       super(parent);
       mDiceHandProb = diceHandProb;


        this.setContentView(R.layout.target_probability_dialog);
        this.setTitle("Target Probability");

        final Button goBack = (Button) this.findViewById(R.id.buttonGoBack);

        mNumberRangeSpinner = (NumberRangeSpinner) this.findViewById(R.id.probabilityTargetSpinner);
        // numberRangeSpinner.setEnabled(false);
        mProbabilityResult = (TextView) this.findViewById(R.id.probabilityResult);
        //adjust the number picker bounds
        int minPossibleRoll = mDiceHandProb.getMinRoll();
        int maxPossibleRoll = mDiceHandProb.getMaxRoll();
        mNumberRangeSpinner.setRange(minPossibleRoll, maxPossibleRoll);



        mNumberRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View v, int position,
                                       long id) {
                updateProbability();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                updateProbability();

            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }


    private void updateProbability(){
        int target = mNumberRangeSpinner.getValue();
        if(target == Spinner.INVALID_POSITION){
            return;
        }
        double probability = mDiceHandProb.getProbabilityBeatOrTie(target);
        //normalize to percentage of form xx.x
        probability *= 100;
        String percentage = String.format("Probability: %.1f%%", probability);
        mProbabilityResult.setText( percentage );
    }
}
