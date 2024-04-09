package com.cristian.animations1;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Animations1 implements ApplicationListener {

	// Constant rows and columns of the sprite sheet
	private static final int FRAME_COLS = 6, FRAME_ROWS = 4;

	private OrthographicCamera camera;



	// Objects used
	Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
	private Animation<TextureRegion> walkUpAnimation, walkDownAnimation, walkLeftAnimation, walkRightAnimation;


	Texture walkSheet;
	SpriteBatch spriteBatch;

	Texture background;
	TextureRegion bgRegion;

	// A variable for tracking elapsed time for the animation
	float stateTime;

	Rectangle up, down, left, right;
	final int IDLE = 0, UP = 1, DOWN = 2, LEFT = 3, RIGHT = 4;

	float SCR_HEIGHT = 480;
	float SCR_WIDTH = 800;



	@Override
	public void create() {

		// Load the sprite sheet as a Texture
		walkSheet = new Texture(Gdx.files.internal("movimiento2.png"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false,SCR_WIDTH,SCR_HEIGHT);
		// Use the split utility method to create a 2D array of TextureRegions
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);
		System.out.println(tmp);

		walkUpAnimation = createAnimation(tmp, 1);
		walkDownAnimation = createAnimation(tmp, 0);
		walkLeftAnimation = createAnimation(tmp, 3);
		walkRightAnimation = createAnimation(tmp, 2);



		// Instantiate a SpriteBatch for drawing
		spriteBatch = new SpriteBatch();
		stateTime = 0f;

		// Background
		background = new Texture(Gdx.files.internal("fondo.jpg"));
		background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
		bgRegion = new TextureRegion(background);

		// facilities per calcular el "touch"
		up = new Rectangle(0, SCR_HEIGHT * 2 / 3, SCR_WIDTH, SCR_HEIGHT / 3);
		down = new Rectangle(0, 0, SCR_WIDTH, SCR_HEIGHT / 3);
		left = new Rectangle(0, 0, SCR_WIDTH / 3, SCR_HEIGHT);
		right = new Rectangle(SCR_WIDTH * 2 / 3, 0, SCR_WIDTH / 3, SCR_HEIGHT);

	}

	private Animation<TextureRegion> createAnimation(TextureRegion[][] regions, int row) {
		TextureRegion[] frames = new TextureRegion[FRAME_COLS];
		for (int i = 0; i < FRAME_COLS; i++) {
			frames[i] = regions[row][i];
		}
		return new Animation<TextureRegion>(0.1f, frames);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		// Get current frame of animation for the current stateTime
		TextureRegion currentFrame;
		// Center coordinates
		float x = 400;
		float y = 240;

		// Handle virtual joystick control
		int direction = virtual_joystick_control();

		// Adjust background position based on joystick direction
		currentFrame = walkUpAnimation.getKeyFrame(stateTime, false);

		float bgSpeed = 5f; // Adjust this value to control the speed of background movement
		switch (direction) {
			case UP:
				currentFrame = walkUpAnimation.getKeyFrame(stateTime, true);
				bgRegion.setRegion(bgRegion.getRegionX(), (int) (bgRegion.getRegionY() - bgSpeed), bgRegion.getRegionWidth(), bgRegion.getRegionHeight());
				break;
			case DOWN:
				currentFrame = walkDownAnimation.getKeyFrame(stateTime, true);
				bgRegion.setRegion(bgRegion.getRegionX(), (int) (bgRegion.getRegionY() + bgSpeed), bgRegion.getRegionWidth(), bgRegion.getRegionHeight());
				break;
			case LEFT:
				currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
				bgRegion.setRegion((int) (bgRegion.getRegionX() - bgSpeed), bgRegion.getRegionY(), bgRegion.getRegionWidth(), bgRegion.getRegionHeight());
				break;
			case RIGHT:
				currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
				bgRegion.setRegion((int) (bgRegion.getRegionX() + bgSpeed), bgRegion.getRegionY(), bgRegion.getRegionWidth(), bgRegion.getRegionHeight());
				break;
			default:
				currentFrame = walkUpAnimation.getKeyFrame(0); // Default to idle animation
				// Do nothing
				break;
		}

		// Draw the current frame at the center of the screen and scale it
		spriteBatch.begin();
		spriteBatch.draw(bgRegion, 0, 0);
		spriteBatch.draw(currentFrame, 800 ,480, currentFrame.getRegionWidth() * 3, currentFrame.getRegionHeight() * 3);
		spriteBatch.end();
	}


	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		// Dispose SpriteBatch and Textures
		spriteBatch.dispose();
		walkSheet.dispose();
		background.dispose();
	}

	protected int virtual_joystick_control() {
		// iterar per multitouch
		// cada "i" és un possible "touch" d'un dit a la pantalla
		for(int i=0;i<10;i++)
			if (Gdx.input.isTouched(i)) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				// traducció de coordenades reals (depen del dispositiu) a 800x480
				camera.unproject(touchPos);
				if (up.contains(touchPos.x, touchPos.y)) {
					return UP;
				} else if (down.contains(touchPos.x, touchPos.y)) {
					return DOWN;
				} else if (left.contains(touchPos.x, touchPos.y)) {
					return LEFT;
				} else if (right.contains(touchPos.x, touchPos.y)) {
					return RIGHT;
				}
			}
		return IDLE;
	}
}
