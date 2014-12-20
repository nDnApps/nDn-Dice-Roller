package com.nDnDiceRoller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends Activity {


    private static final int ADD_CUSTOM_DIE_ID = Menu.FIRST;
    private static final int HISTORY_ID = Menu.FIRST + 1;
    private static final int ABOUT_ID = Menu.FIRST + 2;
    public static final int ADDED_CUSTOM_DIE = Menu.FIRST + 6;
    public static final int EDITED_DIE = Menu.FIRST + 7;
    private static final int DEFAULT_DICE_ID = Menu.FIRST + 8;
    private static final int OPEN_DICE_SET_ID = Menu.FIRST + 9;
    private static final int SAVE_DICE_SET_ID = Menu.FIRST + 10;
    private static final int TARGET_PROBABILITY_ID = Menu.FIRST + 11;
    private static final int OPENED_DICE_SET = 0;


    private AddDiceMenu mAddDiceMenu;
    private DiceHandAdapter mDiceHandAdapter;
    private DiceDatabase mDiceDatabase;

    private TextView mDiceToRoll, mTotal;

    private String mHistoryString;

    private Button mRollButton;
    private Button mResetButton;

    DiceSetFilesystem mDiceSetFilesystem;

    @Override
    public void onStart()
    {
        super.onStart();
        updateOnChange();
    }

    /**
     * Create the activity.
     * @param savedInstanceState - If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        DiceSet lastDiceSet;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDiceToRoll = (TextView)findViewById(R.id.textViewDiceToRoll);
        mTotal = (TextView) findViewById(R.id.textViewTotal);

        mDiceDatabase = new DiceDatabase();
        mDiceHandAdapter = new DiceHandAdapter(this, mDiceDatabase, savedInstanceState);
        GridView gridview = (GridView) findViewById(R.id.gridView1);
        gridview.setAdapter(mDiceHandAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {
                mDiceHandAdapter.removeDie(position);
                updateOnChange();
            }
        });

        mDiceSetFilesystem = new DiceSetFilesystem(this);
        try {
             lastDiceSet = mDiceSetFilesystem.getLastDiceSet();
        }
        catch (Exception e)
        {
            lastDiceSet = null;
        }
        LinearLayout addDiceMenuLayout = (LinearLayout)findViewById(R.id.addDiceMenu);
        DieFunction onDieClicked = new DieFunction() {
            @Override
            public void dieFunction(Die d) {
                mDiceHandAdapter.addDie(d);
            }
        };
        mAddDiceMenu  = new AddDiceMenu(this, addDiceMenuLayout, onDieClicked, lastDiceSet);

        mRollButton = (Button) findViewById(R.id.buttonRoll);
        mRollButton.setEnabled(false);
        mRollButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Integer result = mDiceHandAdapter.rollDice();
                mTotal.setText("Total: " + result.toString());
                //history string has most recent first
                mHistoryString = mDiceHandAdapter.diceStringRep()
                        + " = " + Integer.toString(result) + "\n"
                        + mHistoryString;
            }

        });

        mResetButton = (Button) findViewById(R.id.buttonReset);
        mResetButton.setEnabled(false);
        mResetButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mDiceHandAdapter.clearDice();
                updateOnChange();
            }

        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_CUSTOM_DIE_ID, 0, R.string.menu_add_custom_die);
        menu.add(0, HISTORY_ID, 0, R.string.menu_history);
        menu.add(0, ABOUT_ID, 0, R.string.menu_about);
        menu.add(0, OPEN_DICE_SET_ID, 0, "Open Dice Set");
        menu.add(0, SAVE_DICE_SET_ID, 0, "Save Dice Set");
        menu.add(0, TARGET_PROBABILITY_ID, 0,"Target Probability");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case ABOUT_ID:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                return true;
            case HISTORY_ID:
                Intent intentHistory = new Intent(this, RollHistoryActivity.class);
                intentHistory.putExtra("HistoryString", mHistoryString);
                startActivity(intentHistory);
                return true;
            case ADD_CUSTOM_DIE_ID:
                Intent intentCreateNewDie = new Intent(getApplicationContext(), AddCustomDieActivity.class);
                startActivityForResult(intentCreateNewDie, ADDED_CUSTOM_DIE);
                return true;
            case DEFAULT_DICE_ID:
                mAddDiceMenu.loadDefaultDice();
                return true;
            case OPEN_DICE_SET_ID:
                Intent intentOpenDiceSet = new Intent(this, OpenDiceSetActivity.class);
                startActivityForResult(intentOpenDiceSet, OPENED_DICE_SET);
                return true;
            case SAVE_DICE_SET_ID:
                displaySaveAsDialog();
                return true;
            case TARGET_PROBABILITY_ID:
                displayTargetProbabilityDialog();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);

    }



    private void displaySaveAsDialog()
    {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.saveas_dialog);
        dialog.setTitle("Save Die Set As");

        final EditText filenameField = (EditText) dialog.findViewById(R.id.editTextFilename);
        final Button okButton = (Button) dialog.findViewById(R.id.buttonOk);
        final TextView errorMessage = (TextView) dialog.findViewById(R.id.textViewError);
        okButton.setEnabled(false);

        filenameField.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String currFilename = filenameField.getText().toString();
                if(mDiceSetFilesystem.isReadOnly(currFilename)){
                    okButton.setEnabled(false);
                    errorMessage.setText("Reserved Name");
                    errorMessage.setTextColor(Color.RED);
                }else if(currFilename.length() > 0 ){
                    okButton.setEnabled(true);
                    errorMessage.setText("Legal Name");
                    errorMessage.setTextColor(Color.GREEN);
                }else{
                    okButton.setEnabled(false);
                    errorMessage.setText("Enter a name");
                    errorMessage.setTextColor(Color.YELLOW);
                }
                return false;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    DiceSet dS = mAddDiceMenu.exportDiceSet();
                    dS.setName(filenameField.getText().toString());
                    mDiceSetFilesystem.saveDiceSet(dS);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.cancel();

            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });
        dialog.show();
        mDiceToRoll.setText(filenameField.getText());
    }

    private void displayTargetProbabilityDialog()
    {
        final Dialog dialog = new TargetProbabilityDialog(this, mDiceHandAdapter.getHandProbability());

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (ADDED_CUSTOM_DIE) :{
                if (resultCode == Activity.RESULT_OK) {
                    Die newDie = (Die) data.getSerializableExtra("CustomDie");
                    mAddDiceMenu.addDie(newDie);
                }
                break;
            }
            case (EDITED_DIE) : {
                if (resultCode == Activity.RESULT_OK) {
                    Die oldDie = (Die) data.getSerializableExtra("OldDie");
                    Die newDie = (Die) data.getSerializableExtra("CustomDie");
                    mAddDiceMenu.removeDie(oldDie);
                    mAddDiceMenu.addDie(newDie);
                }
                break;
            }
            case (OPENED_DICE_SET) : {
                if (resultCode == Activity.RESULT_OK) {
                    DiceSet dS = (DiceSet)data.getSerializableExtra("diceSet");
                    mAddDiceMenu.loadDiceSet(dS);
                }
                break;
            }
        }
    }

    public void launchEditDieActivity(Die d){
        Intent intentCreateNewDie = new Intent(getApplicationContext(), AddCustomDieActivity.class);
        intentCreateNewDie.putExtra("OldDie", d);
        startActivityForResult(intentCreateNewDie, EDITED_DIE);
    }


    public void updateOnChange()
    {
        //update the total of the dice currently displayed

        mDiceToRoll.setText("Dice to Roll: " + mDiceHandAdapter.diceStringRep());
        int newTotal = mDiceHandAdapter.getTotal();
        mTotal.setText("Total: " + Integer.toString(newTotal));


        boolean enableRollReset = !mDiceHandAdapter.isEmpty();
        mRollButton.setEnabled(enableRollReset);
        mResetButton.setEnabled(enableRollReset);
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        mDiceHandAdapter.onSaveInstanceState(savedInstanceState);
        try {

            DiceSet dS = mAddDiceMenu.exportDiceSet();
            dS.setName(DiceSetFilesystem.FILENAME_LAST);
            mDiceSetFilesystem.saveDiceSet(dS);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(savedInstanceState);
    }


}