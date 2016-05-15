package edu.uw.mao1001.motiongame;

import android.graphics.Rect;

/**
 * Created by Nick on 5/14/2016.
 */
public class Panel {
    public Rect canvas;
    public String text;

    public Panel(Rect canvas) {
        this.canvas = canvas;
        this.text = "Temp";
    }
}
