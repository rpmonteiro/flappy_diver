package com.rpmonteiro.flappydiver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    float windowHeight;
    float windowWidth;

    int flapState = 0;
    float birdY = 0;
    float birdX = 0;

    float velocity = 0;
    int gameState = 0;
    double gravity = 2.45;
    float gap = 400;
    Random randomGenerator;

    int score = 0;
    int scoringObstacle = 0;

    float maxObstacleOffset;
    float obstacleVelocity = 4;
    int numberOfObstacles = 4;
    float[] obstacleX = new float[numberOfObstacles];
    float[] obstacleOffset = new float[4];
    float distanceBetweenObstacles;

    Circle birdCircle;
    Rectangle[] topObstacleRectangles;
    Rectangle[] bottomObstacleRectangles;


	@Override
	public void create () {
		batch = new SpriteBatch();
        windowHeight = Gdx.graphics.getHeight();
        windowWidth = Gdx.graphics.getWidth();
		background = new Texture("bg.png");

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

        for (int i = 0; i < numberOfObstacles; i++) {
            obstacleOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (windowHeight - gap - 200);
            obstacleX[i] = windowWidth / 2 - topObstacle.getWidth() / 2 + windowWidth + i * distanceBetweenObstacles;

            topObstacleRectangles[i] = new Rectangle();
            bottomObstacleRectangles[i] = new Rectangle();
        }
    }

	@Override
	public void render () {
        batch.begin();
        batch.draw(background, 0, 0, windowWidth, windowHeight);

        if (gameState != 0) {

            if (obstacleX[scoringObstacle] < windowWidth / 2) {
                score++;

                Gdx.app.log("FlappyDiver", "Score is " + score);

                if (scoringObstacle < numberOfObstacles - 1) {
                    scoringObstacle++;
                } else {
                    scoringObstacle = 0;
                }
            }

            if (Gdx.input.justTouched()) {
                velocity = -35;
            }

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

            if (birdY > 0 || velocity < 0) {
                velocity += gravity;
                birdY -= velocity;
            }

        } else {
            if (Gdx.input.justTouched()) {
                Gdx.app.log("FlappyDiver", "User just touched");
                gameState = 1;
            }
        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        batch.draw(birds[flapState], birdX, birdY);
        batch.end();

        birdCircle.set(windowWidth / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);

        for (int i = 0; i < numberOfObstacles; i++) {
            if (Intersector.overlaps(birdCircle, topObstacleRectangles[i]) || Intersector.overlaps(birdCircle, bottomObstacleRectangles[i])) {
                Gdx.app.log("FlappyDiver", "Collision Detected!!");
            }
        }

    }

}
