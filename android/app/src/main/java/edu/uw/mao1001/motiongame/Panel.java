package edu.uw.mao1001.motiongame;

import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Nick on 5/14/2016.
 * Simple class to hold common information for each panel.
 */
public class Panel {
    public Rect canvas;
    public String text;
    public double rotationDegree;
    public int target;
    public boolean enabled;
    public int errorMargin;

    public Panel(Rect canvas) {
        this.canvas = canvas;
        this.text = "Temp";
        this.target = 180;
        this.rotationDegree = 0.0;
        this.enabled = true;
        this.errorMargin = 10;
    }

    //Converts the current rotation to a human readable format
    public String getDegreesInString() {
        return String.format("%.3f",rotationDegree)+"\u00B0";
    }

    //Function to test if the current degree and the target are within acceptable range
    public boolean isMatched() {
        Double test = (Math.abs(rotationDegree) - Math.abs(target));
        //Log.d("Panel", "Results: [Test: " + test + ", Target: " + target + ", Actual: " + rotationDegree);
        return (-errorMargin < test && test < errorMargin);
    }
}
