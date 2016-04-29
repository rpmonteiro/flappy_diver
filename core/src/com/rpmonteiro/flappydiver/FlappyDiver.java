package com.rpmonteiro.flappydiver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

import java.util.Random;

public class FlappyDiver extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
    ShapeRenderer shapeRenderer;

    Texture[] birds;
    int flapState = 0;
    float windowHeight;
    float windowWidth;
    float birdY = 0;
    float birdX = 0;
    Circle birdCircle;

    float velocity = 0;
    int gameState = 0;
    double gravity = 2.45;
    float gap = 400;
    Texture topObstacle;
    Texture bottomObstacle;
    float maxObstacleOffset;
    Random randomGenerator;
    float obstacleVelocity = 4;
    int numberOfObstacles = 4;
    float[] obstacleX = new float[numberOfObstacles];
    float[] obstacleOffset = new float[4];
    float distanceBetweenObstacles;


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
        shapeRenderer = new ShapeRenderer();

        topObstacle = new Texture("toptube.png");
        bottomObstacle = new Texture("bottomtube.png");
        maxObstacleOffset = windowHeight / 2 - gap / 2 - 100;

        randomGenerator = new Random();

        distanceBetweenObstacles = windowWidth * 3 / 4;

        for (int i = 0; i < numberOfObstacles; i++) {
            obstacleX[i] = windowWidth / 2 - topObstacle.getWidth() / 2 + i * distanceBetweenObstacles;
        }
    }

	@Override
	public void render () {
        batch.begin();
        batch.draw(background, 0, 0, windowWidth, windowHeight);

        if (gameState != 0) {

            if (Gdx.input.justTouched()) {
                velocity = -35;
            }

            for (int i = 0; i < numberOfObstacles; i++) {

                if (obstacleX[i] < -topObstacle.getWidth()) {
                    obstacleX[i] += numberOfObstacles * distanceBetweenObstacles;
                    obstacleOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (windowHeight - gap - 200);

                }

                obstacleX[i] -= obstacleVelocity;

                batch.draw(topObstacle, obstacleX[i],
                        windowHeight / 2 + gap / 2 + obstacleOffset[i]);
                batch.draw(bottomObstacle, obstacleX[i],
                        windowHeight / 2 - gap / 2 - bottomObstacle.getHeight() + obstacleOffset[i]);
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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

	    shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shapeRenderer.end();

    }

}
