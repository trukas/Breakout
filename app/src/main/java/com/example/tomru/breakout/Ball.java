package com.example.tomru.breakout;

/**
 * Created by tomru on 2017-12-13.
 */

import android.graphics.RectF;

import java.util.Random;

public class Ball {
    private RectF ballRect;
    private float xVelocity;
    private float yVelocity;
    private float ballWidth = 10;
    private float ballHeight = 10;
    private final int SPACING = 5;
    private final int MAX_ANGLE = 15;

    public Ball(RectF paddleRect){

        // Start the ball travelling straight up at 100 pixels per second
        xVelocity = 0;
        yVelocity = -400;

        // Place the ball in the centre of the screen at the bottom
        // Make it a 10 pixel x 10 pixel square
        ballRect = new RectF();
        setOnTop(paddleRect);
    }

    public RectF getRect(){
        return ballRect;
    }

    public void update(long fps){
        ballRect.top = ballRect.top + (yVelocity / fps);
        ballRect.left = ballRect.left + (xVelocity / fps);

        ballRect.bottom = ballRect.top + ballHeight;
        ballRect.right = ballRect.left + ballWidth;
    }

    public void reverseYVelocity(){
        yVelocity *= -1;
    }

    public void reverseXVelocity(){
        xVelocity *= -1;
    }

    public void setNewVelocityBrick(RectF collidedRect) {
        if (Math.abs(collidedRect.centerX() - ballRect.centerX()) < collidedRect.width() / 2) {
            yVelocity *= -1;
        } else {
            xVelocity *= -1;
        }
    }

    public void clearObstacleY(float y){
        ballRect.bottom = y;
        ballRect.top = y - ballHeight;
    }

    public void clearObstacleX(float x){
        ballRect.left = x;
        ballRect.right = x + ballWidth;
    }

    public void setOnTop(RectF rect){
        ballRect.left = rect.centerX() - (ballWidth/2);
        ballRect.top = rect.top - ballHeight - SPACING;
        ballRect.right = rect.centerX() + (ballWidth/2);
        ballRect.bottom = rect.top - SPACING;

        Random rand = new Random();
        xVelocity = rand.nextInt(200);
        yVelocity = -400;
        if (rand.nextInt(2) > 0) {
             xVelocity *= -1;
        }
    }

    public void setNewVelocityPaddle(RectF rect) {
        if (Math.abs(rect.centerX() - ballRect.centerX()) < rect.width() / 2) {
            yVelocity *= -1;
            xVelocity = (ballRect.centerX() - rect.centerX()) / (rect.width() / 2) * 400;
        } else {
            xVelocity *= -1;
        }
    }
}
