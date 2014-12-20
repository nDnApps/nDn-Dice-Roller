package com.nDnDiceRoller;

import java.util.ArrayList;

class Histogram
{
    private int mMinimum, mMaximum;
    private int mLowLikely, mHighLikely;
    private int mOffset;
    private ArrayList<Double> mProbabilities;

    private static double NEGLIGIBLE_CHANCE_CUTOFF = 0.0001;

    /**
     * Histogram constructor.
     */
    Histogram()
    {
        mProbabilities = new ArrayList<Double>();
        mProbabilities.add(1.0); //100% chance of getting 0
        mMinimum=0;
        mMaximum=0;
        mLowLikely=0;
        mHighLikely=0;
        mOffset = 0;
    }

    /**
     * Notify the histogram that a die has been added to the model.
     * @param die - die to add
     */
    public void addDie(Die die){

        //determine which values do not need to be computed
        double error = 0.0;
        mMinimum += die.getMinRoll();
        mMaximum += die.getMaxRoll();

        if(die.sides == 1) {
            mOffset = die.value;
            return;
        }

        for(mLowLikely = 0; mLowLikely < mProbabilities.size() - 1; mLowLikely++){

            if(mProbabilities.get(mLowLikely) > NEGLIGIBLE_CHANCE_CUTOFF){
                double errorCompensated = mProbabilities.get(mLowLikely) + error;
                mProbabilities.set(mLowLikely, errorCompensated);
                break;
            }else{
                error += mProbabilities.get(mLowLikely);
                mProbabilities.set(mLowLikely, 0.0);
            }
        }
        error = 0.0;
        for(mHighLikely = mProbabilities.size() - 1; mHighLikely > 1; mHighLikely--){
            if(mProbabilities.get(mHighLikely) > NEGLIGIBLE_CHANCE_CUTOFF){
                double errorCompensated = mProbabilities.get(mHighLikely) + error;
                mProbabilities.set(mHighLikely, errorCompensated);
                break;
            }else{
                error += mProbabilities.get(mHighLikely);
                mProbabilities.set(mHighLikely, 0.0);
            }
        }
        //created an appended histogram
        //grow the histogram to the size it will need to be after adding the dice
        mProbabilities.ensureCapacity(mProbabilities.size() + die.getMaxRoll());
        for(int i = 1; i <= die.getMaxRoll(); i++){
            mProbabilities.add(0.0);
        }

        //append the matrices using equation P_final(n+m) += P_oldDist(n) * P_newDie(m)
        //start at the highest n and go down so the list can be edited in place
        ArrayList<Double> dieHistogram = die.getHistogram();
        for( int n = mHighLikely; n >= mLowLikely; n--){
            for(int m = die.getMinRoll(); m <= die.getMaxRoll(); m++){
                double prob = mProbabilities.get(n+m) + mProbabilities.get(n) * dieHistogram.get(m);
                mProbabilities.set(n+m, prob);
            }
            //old probability of getting n is invalidated
            mProbabilities.set(n,0.0);
        }
    }

    /**
     * Clear the histogram to the starting state, which is a 100% chance of rolling 0.
     */
    public void clear()
    {
        mProbabilities.clear();
        mProbabilities.add(1.0); //100% chance of getting 0
        mMinimum=0;
        mMaximum=0;
        mLowLikely=0;
        mHighLikely=0;
        mOffset=0;
    }

    /**
     * Computes the probability of rolling an exact value.
     * @param rollTotal - the total value of rolling all the dice.
     * @return the probability as a number between 0 and 1, inclusive
     */
    public double probabilityRoll(int rollTotal){
        int index = rollTotal - 1;
        if(index >= mProbabilities.size()) {
            return 0;
        }else if(rollTotal == mLowLikely){
            return  1.0;
        }else{
            return mProbabilities.get(index);
        }
    }

    /**
     * computes the probability that low <= roll <= high.
     * @param low - the minimum roll
     * @param high - the maximum roll
     * @return the probability of rolling between low and high inclusive
     */
    public double probabilityRollBetween(int low, int high) {
        double probability = 0;
        if (mProbabilities.size() == 0 || high < low) {
            probability = 0;
        } else if (low == mMinimum && high == mMaximum) {
            probability = 1.0;
        } else if (low == mMinimum){
            probability = 0.0;
        }else{
            for(int i = low; i <= high; i++){
                probability += mProbabilities.get(i);
            }
        }
        return probability;
    }

    /**
     *
     * @return The minimum possible value that could be rolled.
     */
    public int getMinRoll()
    {
        return  mMinimum;
    }

    /**
     *
     * @return The maximum possible value that could be rolled.
     */
    public int getMaxRoll()
    {
        return mMaximum;
    }

    /**
     * Print a representation of the histogram for debugging purposes.
     */
    private void debugPrint(){
        for(int i = 0; i < mProbabilities.size(); i++)
            System.out.println( i + " " + mProbabilities.get(i));
    }

}
