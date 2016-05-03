package com.rpmonteiro.flappydiver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
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
    //Textures
	SpriteBatch batch;
	Texture background;
    Texture[] birds;
    Texture topObstacle;
    Texture bottomObstacle;
    Texture splashScreen;
    Texture gameover;
    BitmapFont font;

    float windowHeight;
    float windowWidth;

    //Bird
    int flapState = 0;
    float birdY = 0;
    float birdX = 0;
    Sound jumpSound;
    Music music;

    //Velocity - Gravity
    float velocity = 0;
    int gameState = 0;
    double gravity = 2;

    Random randomGenerator;
    public static Preferences prefs;
    int jumpHeight = -27;

    //Score
    int score = 0;
    int scoringObstacle = 0;

    //Obstacles
    float maxObstacleOffset;
    float gap = 450;
    float obstacleVelocity = 8.5f;
    int numberOfObstacles = 4;
    float[] obstacleX = new float[numberOfObstacles];
    float[] obstacleOffset = new float[4];
    float distanceBetweenObstacles;

    //Shapes
    Circle birdCircle;
    Rectangle[] topObstacleRectangles;
    Rectangle[] bottomObstacleRectangles;


	@Override
	public void create () {
        windowHeight = Gdx.graphics.getHeight();
        windowWidth = Gdx.graphics.getWidth();

        prefs = Gdx.app.getPreferences("FlappyDiver");
        if (!prefs.contains("highScore")) {
            prefs.putInteger("highScore", 0);
        }

        initAssets();
        setupVariables();
        startGame();
    }

    public void initAssets() {
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump2.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        background = new Texture("bg.png");
        font = new BitmapFont(Gdx.files.internal("text.fnt"), Gdx.files.internal("text.png"), false);
        gameover = new Texture("scoreboardFinal.png");
        splashScreen = new Texture("splashFinal.png");
        topObstacle = new Texture("toptube.png");
        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        bottomObstacle = new Texture("bottomtube.png");
        batch = new SpriteBatch();
    }

    public void setupVariables() {
        birdY = windowHeight / 2 - birds[0].getHeight() / 2;
        birdX = windowWidth / 2 - birds[0].getWidth() / 2;
        birdCircle = new Circle();

        randomGenerator = new Random();

        maxObstacleOffset = windowHeight / 2 - gap / 2 - 100;
        distanceBetweenObstacles = windowWidth * 3 / 4;
        topObstacleRectangles = new Rectangle[numberOfObstacles];
        bottomObstacleRectangles = new Rectangle[numberOfObstacles];
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
        birdCircle.set(windowWidth / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);

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

    public void jump() {
        jumpSound.play(1.0f);
        velocity = jumpHeight;
    }

    public void drawBackground() {
        batch.draw(background, 0, 0, windowWidth, windowHeight);
    }

    public void drawScore() {
        font.getData().setScale(2);
        font.draw(batch, String.valueOf(score), 75, 175);
    }

    public void setHighScore(int score) {
        prefs.putInteger("highScore", score);
        prefs.flush();
    }

    public void checkIfBirdScored() {
        if (obstacleX[scoringObstacle] < windowWidth / 2) {
            score++;

            if (scoringObstacle < numberOfObstacles - 1) {
                scoringObstacle++;
            } else {
                scoringObstacle = 0;
            }
        }
    }

    public int getHighScore() {
        return prefs.getInteger("highScore");
    }

    public void checkHighScore() {
        if (getHighScore() < score) {
            setHighScore(score);
        }
    }

    public void moveBird() {
        if (birdY > 0) {
            velocity += gravity;
            birdY -= velocity;
        } else {
            gameState = 2;
        }
    }

    public void drawSplashScreen() {
        batch.draw(splashScreen, windowWidth / 6 - gameover.getWidth() / 2 - 25, windowHeight / 3 - gameover.getHeight() / 2, 1000, 1000);
    }

    public void resetGameState() {
        gameState = 1;
        birdY = windowHeight / 2 - birds[0].getHeight() / 2;
        score = 0;
        scoringObstacle = 0;
        velocity = 0;
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
        drawBackground();
        music.play();

        if (gameState == 1) {

            flap();
            moveBird();
            drawScore();
            drawObstacles();
            checkIfBirdScored();

            if (Gdx.input.justTouched()) {
                jump();
            }

        } else if (gameState == 0) {

            drawSplashScreen();

            if (Gdx.input.justTouched()) {
                gameState = 1;
            }

        } else if (gameState == 2) {

            checkHighScore();
            showGameoverScreen();

            if (Gdx.input.justTouched()) {
                resetGameState();
                startGame();
            }
        }

        batch.end();
        checkCollision();

    }
}
