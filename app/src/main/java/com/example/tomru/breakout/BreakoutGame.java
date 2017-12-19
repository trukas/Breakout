package com.example.tomru.breakout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class BreakoutGame extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    BreakoutView breakoutView;
    private UserData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        breakoutView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        breakoutView.pause();
    }

    // Notice we implement runnable so we have
    // A thread and can override the run method.
    class BreakoutView extends SurfaceView implements Runnable {

        private final int rowCount = 8;
        private final int columnCount = 10;
        // This is our thread
        Thread gameThread = null;
        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;
        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;
        // Game is paused at the start
        boolean paused = true;
        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;
        // This variable tracks the game frame rate
        long fps;
        // The size of the screen in pixels
        int screenX;
        int screenY;
        Paddle paddle;
        Ball ball;
        List<Brick> bricks = new ArrayList<>();
        int numBricks = 0;
        int score = 0;
        int lives = 3;
        // This is used to help calculate the fps
        private long timeThisFrame;
        private Shader rainbowShader;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public BreakoutView(Context context) {
            super(context);
            data = (UserData) getIntent().getSerializableExtra("UserData");

            ourHolder = getHolder();
            paint = new Paint();

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            paddle = new Paddle(screenX, screenY);
            ball = new Ball(paddle.getRect());
            score = data.getPoints();
            data.updateFromRemote();
            createBricksAndRestart();
        }

        public void createBricksAndRestart() {
            final int OFFSET = 2;
            // Put the ball back to the start
            ball.setOnTop(paddle.getRect());

            int brickWidth = (screenX - (columnCount * OFFSET)) / columnCount;
            int brickHeight = ((screenY / 3) - (rowCount * OFFSET) ) / rowCount;

            // Build a wall of bricks
            numBricks = 0;
            for (int column = 0; column < columnCount; column++) {
                for (int row = 0; row < rowCount; row++) {
                    bricks.add(new Brick(row, column, brickWidth, brickHeight));
                    numBricks++;
                }
            }
            // if game over reset scores and lives
            if (lives == 0) {
                data.addPoints(score);
                score = 0;
                lives = 3;
            }

            rainbowShader = new LinearGradient(
                    bricks.get(0).getRect().left,
                    bricks.get(0).getRect().top,
                    bricks.get(rowCount - 1).getRect().left,
                    bricks.get(rowCount - 1).getRect().bottom,
                    new int[]{
                            Color.parseColor("#e74c3c"),
                            Color.parseColor("#f1c40f"),
                            Color.parseColor("#2ecc71"),
                            Color.parseColor("#3498db"),
                            Color.parseColor("#9b59b6")
                    },
                    null,
                    Shader.TileMode.MIRROR
            );
        }

        @Override
        public void run() {
            while (playing) {
                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();
                // Update the frame
                if (!paused) {
                    update();
                }
                // Draw the frame
                draw();
                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {
            paddle.update(fps);
            ball.update(fps);

            // Check for ball colliding with a brick
            boolean wasReversed = false;
            for (Brick brick : bricks) {
                if (brick.getVisibility() && RectF.intersects(brick.getRect(), ball.getRect())) {
                    brick.setInvisible();
                    if (!wasReversed) {
                        ball.setNewVelocityBrick(brick.getRect());
                        wasReversed = true;
                    }
                    score += 10;
                }
            }

            // Check for ball colliding with paddle
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.setNewVelocityPaddle(paddle.getRect());
                ball.clearObstacleY(paddle.getRect().top - 2);
            }

            // Bounce the ball back when it hits the bottom of screen
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);

                // Lose a life
                lives--;
                resetToStart();

                if (lives == 0) {
                    paused = true;
                    createBricksAndRestart();
                }
            }

            // Bounce the ball back when it hits the top of screen
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(12);
            }

            // If the ball hits left wall bounce
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
            }

            // If the ball hits right wall bounce
            if (ball.getRect().right > screenX - 10) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);

            }

            // Pause if cleared screen
            if (score == numBricks * 10) {
                paused = true;
                createBricksAndRestart();
            }
        }

        // Resets to starting position paddle and ball.
        public void resetToStart() {
            paddle.centerOnScreen(screenX, screenY);
            ball.setOnTop(paddle.getRect());
        }

        // Draw the newly updated scene
        public void draw() {
            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
//                canvas.drawColor(Color.argb(255, 26, 128, 182));
                canvas.drawColor(Color.parseColor("#151515"));
                // canvas.drawColor(Color.argb(255, 0, 0, 0));
                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 0, 0));

                // Draw the paddle
                canvas.drawRect(paddle.getRect(), paint);

                // Draw the ball
                canvas.drawRoundRect(ball.getRect(), 10, 10, paint);

                // Change the brush color for drawing
                paint.setColor(Color.argb(255, 0, 106, 255));

                Shader oldShader = paint.getShader();
                paint.setShader(rainbowShader);
                // Draw the bricks if visible
                for (Brick brick : bricks) {
                    if (brick.getVisibility()) {
                        canvas.drawRoundRect(brick.getRect(), 8, 8, paint);
                    }
                }
                paint.setShader(oldShader);

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // Draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + (score + data.getPoints()) + "   Lives: " + lives, 10, 50, paint);

                // Has the player cleared the screen?
                if (score == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenY / 2, paint);
                }

                // Has the player lost?
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, screenY / 2, paint);
                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    if (motionEvent.getX() > screenX / 2) {
                        paddle.setMovementState(paddle.RIGHT);
                    } else {
                        paddle.setMovementState(paddle.LEFT);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }

    }

}
