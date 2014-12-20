package com.nDnDiceRoller;

/**
 * Proxy class for Histogram to make it more efficient for the access pattern of this application.
 */

public class HistogramLazyProxy {

    private Histogram mHistogram;
    private boolean mIsHistogramBuilt;

    private  DiceDatabase mDiceDb;



    /**
     * Constructor
     * @param diceDatabase - Reference to the dice that should be tracked in the histogram.
     */
    HistogramLazyProxy(DiceDatabase diceDatabase){
        mHistogram = new Histogram();
        mIsHistogramBuilt = true;
        mDiceDb = diceDatabase;
    }


    public void addDie(Die die)
    {
        if(mIsHistogramBuilt) {
            mHistogram.addDie(die);
        }
    }

    /**
     * Update the histogram to account for removing a die.
     * @param die - The die being removed.
     */
    public void removeDie(Die die)
    {

        mIsHistogramBuilt = false;


    }


    /**
     * Computes the probability of rolling an exact value.
     * @param totalRoll - The roll to find the probability for
     * @return a probability between 0 and 1, inclusive.
     */
    public double probabilityRoll(int totalRoll)
    {
        if(!mIsHistogramBuilt){
            buildHistogram();
        }
        return mHistogram.probabilityRoll(totalRoll);
    }

    /**
     * Computes the probability that low <= roll <= high
     * @param low - The lowest value in the range.
     * @param high - The highest value in the range.
     * @return a probability between 0 and 1, inclusive
     */
    public double probabilityRollBetween(int low, int high)
    {
        if(!mIsHistogramBuilt){
            buildHistogram();
        }
        return mHistogram.probabilityRollBetween(low, high);
    }

    /**
     * Rebuild the histogram using mDiceDb
     */
    private void buildHistogram()
    {
        mHistogram.clear();
        for(Die d : mDiceDb){
            mHistogram.addDie(d);
        }
        mIsHistogramBuilt = true;
    }

    /**
     *
     * @return the minimum possible value that can be rolled.
     */
    public int getMinRoll()
    {
        if(!mIsHistogramBuilt){
            buildHistogram();
        }
        return mHistogram.getMinRoll();
    }

    /**
     *
     * @return the maximum possible value that can be rolled.
     */
    public int getMaxRoll()
    {
        if(!mIsHistogramBuilt){
            buildHistogram();
        }
        return mHistogram.getMaxRoll();
    }

}
