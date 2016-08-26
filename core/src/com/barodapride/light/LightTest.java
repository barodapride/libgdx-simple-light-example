package com.barodapride.light;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class LightTest extends ApplicationAdapter {

	SpriteBatch spriteBatch;
	OrthographicCamera camera;
	OrthographicCamera screenCamera;

	Texture texture;
	Texture light;

	FrameBuffer frameBuffer;

	Vector3 currentInput = new Vector3();

	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera(40, 22.5f);
		camera.position.set(20, 11.25f, 0);
		camera.update();

		screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenCamera.position.set(screenCamera.viewportWidth/2f, screenCamera.viewportHeight/2f, 0);
		screenCamera.update();

		texture = new Texture(Gdx.files.internal("badlogic.jpg"));
		light = new Texture(Gdx.files.internal("light1.png"));

		frameBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Gdx.input.isKeyPressed(Input.Keys.MINUS)){
			camera.zoom += .1f;
			camera.rotate(.1f);
			camera.update();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.PLUS)){
			camera.zoom -= .1f;
			camera.rotate(-.1f);
			camera.update();
		}

		currentInput.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(currentInput);

		//Draw our game in world units
		spriteBatch.setProjectionMatrix(camera.combined);

		//render the scene with normal blending
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.begin();
		spriteBatch.draw(texture, 0, 0, 40, 22.5f);
		spriteBatch.end();

		//Render lights to the fbo
		frameBuffer.begin();
		Gdx.gl.glClearColor(.1f, .1f, .1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		spriteBatch.begin();
		spriteBatch.draw(light, currentInput.x - 5, currentInput.y - 5, 10, 10);
		spriteBatch.end();

		frameBuffer.end();


		//Blend lights with the scene already rendered to the renderbuffer
		//Set the projection matrix of the batch to the screen camera that doesnt move, so we can simply draw using 0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight()
		spriteBatch.setProjectionMatrix(screenCamera.combined);
		spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		spriteBatch.begin();
		spriteBatch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
		spriteBatch.end();
	}


}