package com.nDnDiceRoller;

/**
 * Created by jonckh10 on 11/16/2014.
 */
public interface IHandProbability {

    public abstract int getMinRoll();

    public abstract int getMaxRoll();

    public abstract double getProbabilityBeatOrTie(int n);
}
