package com.dreamteam.blocks;

/**
 * Created by tomru on 2017-12-13.
 */

import android.graphics.Point;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class Ball {
    private final int SPACING = 5;
    private final int MAX_ANGLE = 15;
    private RectF ballRect;
    private float xVelocity;
    private float yVelocity;
    private float ballWidth = 10;
    private float ballHeight = 10;

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
        Point topLeft = new Point((int) collidedRect.left, (int) collidedRect.top);
        Point topRight = new Point((int) collidedRect.right, (int) collidedRect.top);
        Point bottomLeft = new Point((int) collidedRect.left, (int) collidedRect.bottom);
        Point bottomRight = new Point((int) collidedRect.right, (int) collidedRect.bottom);
        Map<String, Double> map = new HashMap<>();

        map.put("T", distanceToLine(topLeft.x, topLeft.y, topRight.x, topRight.y));
        map.put("B", distanceToLine(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y));
        map.put("L", distanceToLine(bottomLeft.x, bottomLeft.y, topLeft.x, topLeft.y));
        map.put("R", distanceToLine(bottomRight.x, bottomRight.y, topRight.x, topRight.y));

        double smallestValue = map.get("T");
        String smallest = "T";

        for (int i = 0; i < map.entrySet().size(); i++) {
            Set<Map.Entry<String, Double>> set = map.entrySet();
            for (Map.Entry<String, Double> entry : set) {
                if (entry.getValue() < smallestValue) {
                    smallest = entry.getKey();
                    smallestValue = entry.getValue();
                }
            }
        }

        String key = smallest;

        if (Objects.equals(key, "T") || Objects.equals(key, "B")) { // Math.abs(collidedRect.centerX() - ballRect.centerX()) < collidedRect.width() / 2 -- old
            yVelocity *= -1;
        } else {
            xVelocity *= -1;
        }
    }

    private double distanceToLine(int x1, int y1, int x2, int y2) {
        float x0 = ballRect.centerX();
        float y0 = ballRect.centerY();

        return Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1) / Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
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
