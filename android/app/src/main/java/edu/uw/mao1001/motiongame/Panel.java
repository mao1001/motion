package edu.uw.mao1001.motiongame;

import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Nick on 5/14/2016.
 */
public class Panel {
    public Rect canvas;
    public String text;
    public double rotationDegree;
    public int target;

    public Panel(Rect canvas) {
        this.canvas = canvas;
        this.text = "Temp";
        this.target = 180;
        this.rotationDegree = 0.0;
    }

    public String getDegreesInString() {
        return String.format("%.3f",rotationDegree)+"\u00B0";
    }

    public boolean isMatched() {
        Double test = (Math.abs(rotationDegree) - Math.abs(target));
        //Log.d("Panel", "Results: [Test: " + test + ", Target: " + target + ", Actual: " + rotationDegree);
        return (-10 < test && test < 10);

    }
}
