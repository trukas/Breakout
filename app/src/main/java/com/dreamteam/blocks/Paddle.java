package com.dreamteam.blocks;

/**
 * Created by tomru on 2017-12-13.
 */
import android.graphics.RectF;

public class Paddle {
    // Which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    private final int OFFSET_BOTTOM = 100;
    private RectF rect;
    // How long and high our paddle will be
    private float length = 130;
    private float height = 20;
    // This will hold the pixels per second speed that the paddle will move
    private float paddleSpeed = 350;
    private int paddleMoving = STOPPED;

    private int screenWidth;

    public Paddle(int screenX, int screenY){
        rect = new RectF();
        screenWidth = screenX;
        centerOnScreen(screenX, screenY);
    }

    public RectF getRect(){
        return rect;
    }

    public void setMovementState(int state){
        paddleMoving = state;
    }

    public void update(long fps){
        if(paddleMoving == LEFT){
            rect.left = rect.left - paddleSpeed / fps;
            if (rect.left < 0) {
                rect.left = 0;
            }
        }

        if(paddleMoving == RIGHT){
            rect.left = rect.left + paddleSpeed / fps;
            if (rect.left + length > screenWidth) {
                rect.left = screenWidth - length;
            }
        }

        rect.right = rect.left + length;
    }

    public void centerOnScreen(int screenX, int screenY) {
        rect.left = (screenX / 2) - (length / 2);
        rect.right = rect.left + length;
        rect.top = screenY - height - OFFSET_BOTTOM;
        rect.bottom = screenY - OFFSET_BOTTOM;
    }
}