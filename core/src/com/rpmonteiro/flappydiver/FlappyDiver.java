package com.rpmonteiro.flappydiver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class FlappyDiver extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;

    Texture[] birds;
    int flapState = 0;
    float windowHeight;
    float windowWidth;
    float birdY = 0;
    float birdX = 0;
    float velocity = 0;
    int gameState = 0;
    double gravity = 2.45;
    float gap = 400;
    Texture topObstacle;
    Texture bottomObstacle;
    float maxObstacleOffset;
    Random randomGenerator;
    float obstacleOffset;

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

        topObstacle = new Texture("toptube.png");
        bottomObstacle = new Texture("bottomtube.png");

        maxObstacleOffset = windowHeight / 2 - gap / 2 - 100;

        randomGenerator = new Random();
    }

	@Override
	public void render () {
        batch.begin();
        batch.draw(background, 0, 0, windowWidth, windowHeight);

        if (gameState != 0) {

            if (Gdx.input.justTouched()) {
                velocity = -35;
                obstacleOffset = (randomGenerator.nextFloat() - 0.5f) * (windowHeight - gap - 200);
            }

            batch.draw(topObstacle, windowWidth / 2 - topObstacle.getWidth() / 2,
                    windowHeight / 2 + gap / 2 + obstacleOffset);
            batch.draw(bottomObstacle, windowWidth / 2 - bottomObstacle.getWidth() / 2,
                    windowHeight / 2 - gap / 2 - bottomObstacle.getHeight() + obstacleOffset);

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

	}

}
