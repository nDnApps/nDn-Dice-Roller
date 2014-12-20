package com.nDnDiceRoller;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.graphics.Color;
        import android.view.View;
        import android.widget.LinearLayout;

/**
 * Encapsulates the top menu on the main screen that holds the types of dice that can be added to
 * the dice hand.
 */
public class AddDiceMenu {
    LinearLayout mMenuLayout;
    MainActivity mContext;
    String mName;
    DieFunction mOnDieClicked;


    static Integer[] mDefaultDice = {1,4,6,8,10,12,20};
    static Integer[] mDefaultDiceColors =
            {Color.WHITE, Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN};
    static Integer[] mDefaultTextColors =
            {Color.WHITE, Color.WHITE,Color.WHITE, Color.BLACK, Color.BLACK, Color.WHITE, Color.BLACK};

    /**
     * Constructor
     * @param context - Parent activity
     * @param menuLayout - LinearLayout that holds the menu.
     * @param onDieClicked - function to execute whenever a die in the menu is selected
     * @param startingDiceSet - dice to display in the menu
     */
    public AddDiceMenu(MainActivity context, LinearLayout menuLayout, DieFunction onDieClicked,
                       DiceSet startingDiceSet) {

        mContext = context;
        mMenuLayout = menuLayout;
        mOnDieClicked = onDieClicked;

        if(startingDiceSet != null){
            loadDiceSet(startingDiceSet);
        }
        else {
            loadDefaultDice();
        }
        loadDefaultDice();

    }

    /**
     * Show different dice in the menu.
     * @param diceSet - the dice to show in the menu.
     */
    void loadDiceSet(DiceSet diceSet)
    {
        //clear layout
        mMenuLayout.removeAllViews();

        mName = diceSet.getName();
        assert(mName != null);

        for(Die d : diceSet){
            addDie(d);
        }

    }

    /**
     * Return the dice in the menu.
     * @return a DiceSet object containing the dice in the menu.
     */
    DiceSet exportDiceSet(){
        DiceSet retDiceSet = new DiceSet(mName);
        for(int i = 0; i < mMenuLayout.getChildCount(); i++){
            DieView dV = (DieView)mMenuLayout.getChildAt(i);
            Die d = dV.getDie();
            retDiceSet.addDie(d);
        }
        return retDiceSet;
    }


    /**
     * Load the standard set of dice into the menu. D2,D4,D6,D8,D10,D12,D20,+1
     */
    public void loadDefaultDice() {
        mName = "Standard Dice";
        mMenuLayout.removeAllViews();
        for(int i = 0; i < mDefaultDice.length; i++){
            int sides = mDefaultDice[i];
            Die d = new Die(sides);
            d.dieColor = mDefaultDiceColors[i];
            d.textColor = mDefaultTextColors[i];
            addDie(d);
        }

    }

    /**
     * Add a die to the menu.
     * @param dieToAdd - the Die to add.
     */
    public void addDie(Die dieToAdd){
        DieView dVToAdd = new DieView(mContext, new Die(dieToAdd));
        initializeDieView(dVToAdd);

        //insert in sorted position. non exploding dice come first, then sorted based on color
        int i;
        boolean duplicateDieError = false;
        for( i = 0; i < mMenuLayout.getChildCount(); i++){
            DieView currDV = (DieView)mMenuLayout.getChildAt(i);
            Die currDie = currDV.getDie();

            int comparisonResult = dieToAdd.compareTo(currDie);
            //if dice are equal, quit loop with error
            if(comparisonResult == 0){
                duplicateDieError = true;
                break;
            }
            //if die is greater than than currDie,
            if(comparisonResult < 0){
                break;
            }
        }
        if(!duplicateDieError){
            mMenuLayout.addView(dVToAdd, i);
        }
    }

    /**
     * Remove one of the menu options
     * @param dieView - the menu item to remove
     */
    private void removeDieView(DieView dieView){
        mMenuLayout.removeView(dieView);
        mMenuLayout.invalidate();
    }

    /**
     * Remove a die from the menu
     * @param oldDie - die with the same values as the die to remove. Does not need to be a
     *               reference to the actual object.
     */
    public void removeDie(Die oldDie) {
        for(int i = 0; i < mMenuLayout.getChildCount(); i++){
            DieView dV = (DieView)mMenuLayout.getChildAt(i);
            Die d = dV.getDie();
            if(d.compareTo(oldDie) == 0){
                removeDieView(dV);
                break;
            }
        }

    }

    /**
     * Do any steps necessary to ready the DieView for use in the AddDiceMenu.
     * @param dieView - DieView to initialize.
     */
    private void initializeDieView(DieView dieView){
        dieView.setMenuMode(true);
        dieView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DieView dV = (DieView) view;
                mOnDieClicked.dieFunction(dV.getDie());
                mContext.updateOnChange();
            }
        });
        dieView.setHapticFeedbackEnabled(true);
        dieView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                final DieView currDV = (DieView) v;

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(currDV.getDie().getName() )
                        .setCancelable(true)
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mContext.launchEditDieActivity(currDV.getDie());
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeDieView(currDV);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setOwnerActivity(mContext);
                alert.show();
                return false;
            }
        });
    }


}
