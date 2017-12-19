package com.dreamteam.blocks;

import android.graphics.RectF;

public class Brick {

    private RectF rect;

    private boolean isVisible;

    public Brick(int row, int column, int width, int height){

        isVisible = true;

        final int padding = 2;
        final int OFFSET_Y = 60;

        rect = new RectF(column * width + padding,
                row * height + padding + OFFSET_Y,
                column * width + width - padding,
                row * height + height - padding + OFFSET_Y);
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}