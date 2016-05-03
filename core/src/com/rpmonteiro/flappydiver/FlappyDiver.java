package com.rpmonteiro.flappydiver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyDiver extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
    Texture[] birds;
    Texture topObstacle;
    Texture bottomObstacle;
    Texture splashScreen;
    Texture gameover;
    BitmapFont font;
    public static Preferences prefs;

    float windowHeight;
    float windowWidth;

    int flapState = 0;
    float birdY = 0;
    float birdX = 0;
    Sound jumpSound;

    float velocity = 0;
    int gameState = 0;
    double gravity = 2.3;
    float gap = 500;
    Random randomGenerator;

    int score = 0;
    int scoringObstacle = 0;

    float maxObstacleOffset;
    float obstacleVelocity = 9;
    int numberOfObstacles = 4;
    float[] obstacleX = new float[numberOfObstacles];
    float[] obstacleOffset = new float[4];
    float distanceBetweenObstacles;

    int jumpHeight = -35;

    Circle birdCircle;
    Rectangle[] topObstacleRectangles;
    Rectangle[] bottomObstacleRectangles;


	@Override
	public void create () {
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump2.wav"));
        batch = new SpriteBatch();
        windowHeight = Gdx.graphics.getHeight();
        windowWidth = Gdx.graphics.getWidth();
		background = new Texture("bg.png");
        gameover = new Texture("scoreboardFinal.png");
        splashScreen = new Texture("splashFinal.png");

        prefs = Gdx.app.getPreferences("FlappyDiver");

        if (!prefs.contains("highScore")) {
            prefs.putInteger("highScore", 0);
        }

        font = new BitmapFont(Gdx.files.internal("text.fnt"), Gdx.files.internal("text.png"), false);

        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        birdY = windowHeight / 2 - birds[0].getHeight() / 2;
        birdX = windowWidth / 2 - birds[0].getWidth() / 2;

        birdCircle = new Circle();

        topObstacle = new Texture("toptube.png");
        bottomObstacle = new Texture("bottomtube.png");
        maxObstacleOffset = windowHeight / 2 - gap / 2 - 100;

        randomGenerator = new Random();

        distanceBetweenObstacles = windowWidth * 3 / 4;
        topObstacleRectangles = new Rectangle[numberOfObstacles];
        bottomObstacleRectangles = new Rectangle[numberOfObstacles];

        startGame();
    }

    public void startGame() {
        for (int i = 0; i < numberOfObstacles; i++) {
            obstacleOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (windowHeight - gap - 200);
            obstacleX[i] = windowWidth / 2 - topObstacle.getWidth() / 2 + windowWidth + i * distanceBetweenObstacles;

            topObstacleRectangles[i] = new Rectangle();
            bottomObstacleRectangles[i] = new Rectangle();
        }
    }

    public void flap() {
        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }
        int angle = 45;
//        batch.draw(birds[flapState], birdX, birdY); Not rotating :(
        batch.draw(birds[flapState], birdX, birdY, 64, 64, 128, 128, 1, 1,
                -velocity, 0, 0, birds[0].getWidth(), birds[0].getHeight(), false, false);

    }

    public void drawObstacles() {
        for (int i = 0; i < numberOfObstacles; i++) {

            if (obstacleX[i] < -topObstacle.getWidth()) {

                obstacleOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (windowHeight - gap - 200);
                obstacleX[i] += numberOfObstacles * distanceBetweenObstacles;

            } else {
                obstacleX[i] -= obstacleVelocity;
            }
            batch.draw(topObstacle, obstacleX[i], windowHeight / 2 + gap / 2 + obstacleOffset[i]);
            batch.draw(bottomObstacle, obstacleX[i], windowHeight / 2 - gap / 2 - bottomObstacle.getHeight() + obstacleOffset[i]);

            topObstacleRectangles[i] = new Rectangle(obstacleX[i], windowHeight / 2 + gap / 2 + obstacleOffset[i], topObstacle.getWidth(), topObstacle.getHeight());
            bottomObstacleRectangles[i] = new Rectangle(obstacleX[i], windowHeight / 2 - gap / 2 - bottomObstacle.getHeight() + obstacleOffset[i], bottomObstacle.getWidth(), bottomObstacle.getHeight());
        }
    }

    public void checkCollision() {
        for (int i = 0; i < numberOfObstacles; i++) {
            if (Intersector.overlaps(birdCircle, topObstacleRectangles[i]) || Intersector.overlaps(birdCircle, bottomObstacleRectangles[i])) {
                gameState = 2;
            }
        }
    }

    public void setHighScore(int score) {
        prefs.putInteger("highScore", score);
        prefs.flush();
    }

    public int getHighScore() {
        return prefs.getInteger("highScore");
    }

    public void showGameoverScreen() {
        batch.draw(gameover, windowWidth / 6 - gameover.getWidth() / 2 - 25, windowHeight / 3 - gameover.getHeight() / 2, 1000, 1200);
        font.getData().setScale(2);
        font.draw(batch, String.valueOf(score), windowWidth / 2 + 225, windowHeight / 2 + 325);
        font.draw(batch, String.valueOf(getHighScore()), windowWidth / 2 + 195, windowHeight / 2 + 110);
    }

	@Override
	public void render () {
        batch.begin();
        batch.draw(background, 0, 0, windowWidth, windowHeight);

        if (gameState == 1) {

            if (obstacleX[scoringObstacle] < windowWidth / 2) {
                score++;

                if (scoringObstacle < numberOfObstacles - 1) {
                    scoringObstacle++;
                } else {
                    scoringObstacle = 0;
                }
            }

            if (Gdx.input.justTouched()) {
                jumpSound.play(1.0f);
                velocity = jumpHeight;
            }

            flap();
            drawObstacles();
            font.getData().setScale(2);
            font.draw(batch, String.valueOf(score), 75, 175);


            if (birdY > 0) {
                velocity += gravity;
                birdY -= velocity;
            } else {
                gameState = 2;
            }

        } else if (gameState == 0) {
            batch.draw(splashScreen, windowWidth / 6 - gameover.getWidth() / 2 - 25, windowHeight / 3 - gameover.getHeight() / 2, 1000, 1000);

            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            if (getHighScore() < score) {
                setHighScore(score);
            }

            showGameoverScreen();

            if (Gdx.input.justTouched()) {
                gameState = 1;
                birdY = windowHeight / 2 - birds[0].getHeight() / 2;
                startGame();
                score = 0;
                scoringObstacle = 0;
                velocity = 0;
            }
        }

        batch.end();

        birdCircle.set(windowWidth / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);

        checkCollision();

    }

}
