package ca.jvsh.fallingdroids;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.sensor.orientation.IOrientationListener;
import org.anddev.andengine.sensor.orientation.OrientationData;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class LiveWallpaperService extends BaseLiveWallpaperService implements IAccelerometerListener, IOnSceneTouchListener, IOnAreaTouchListener
{
	// ===========================================================
	// Constants
	// ===========================================================

	private static int			CAMERA_WIDTH	= 360;
	private static int			CAMERA_HEIGHT	= 240;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas	mBitmapTextureAtlas;

	private TiledTextureRegion	mBoxFaceTextureRegion;

	private PhysicsWorld		mPhysicsWorld;
	private ScreenOrientation	mScreenOrientation;

	private float				mGravityX;
	private float				mGravityY;
	private Scene				mScene;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public org.anddev.andengine.engine.Engine onLoadEngine()
	{
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		CAMERA_WIDTH = displayMetrics.widthPixels;
		CAMERA_HEIGHT = displayMetrics.heightPixels;

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, this.mScreenOrientation, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);

		return new org.anddev.andengine.engine.Engine(engineOptions);

	}

	@Override
	public void onLoadResources()
	{

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png", 0, 0, 1, 1); // 32x32
		this.getEngine().getTextureManager().loadTexture(this.mBitmapTextureAtlas);

	}

	@Override
	public Scene onLoadScene()
	{
		Log.i("test", "onLoadScene");
		this.getEngine().registerUpdateHandler(new FPSLogger());

		Log.i("test", "result" + this.getEngine().disableOrientationSensor(this));

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		this.mScene = new Scene();
		this.mScene.setBackground(new ColorBackground(255, 255, 255));
		this.mScene.setOnSceneTouchListener(this);

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		this.mScene.setOnAreaTouchListener(this);

		return this.mScene;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY)
	{
		if (pSceneTouchEvent.isActionDown())
		{
			final AnimatedSprite face = (AnimatedSprite) pTouchArea;
			this.jumpFace(face);
			return true;
		}

		return false;
	}

	@Override
	public void onLoadComplete()
	{

	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent)
	{
		if (this.mPhysicsWorld != null)
		{
			if (pSceneTouchEvent.isActionDown())
			{
				this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData)
	{
		this.mGravityX = pAccelerometerData.getX();
		this.mGravityY = pAccelerometerData.getY();

		final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, this.mGravityY);
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	@Override
	public void onUnloadResources()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPauseGame()
	{
		super.onPause();
		LiveWallpaperService.this.getEngine().onPause();
		LiveWallpaperService.this.onPause();

	}

	@Override
	public void onResumeGame()
	{
		super.onResume();
		LiveWallpaperService.this.getEngine().onResume();
		LiveWallpaperService.this.onResume();

	}

	/*@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Handle orientation changes, scale the scene in order not to be
		// streched
		if (mScreenOrientation == ScreenOrientation.PORTRAIT) {
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				mScene.setScaleX((float) CAMERA_WIDTH
						/ (float) CAMERA_HEIGHT);
				mScene.setScaleY((float) CAMERA_HEIGHT
						/ (float) CAMERA_WIDTH);
			}
			if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				mScene.setScale(1);
			}
		}
		if (mScreenOrientation == ScreenOrientation.LANDSCAPE) {
			if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				mScene.setScale(1);
			}
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				mScene.setScaleX((float) CAMERA_WIDTH
						/ (float) CAMERA_HEIGHT);
				mScene.setScaleY((float) CAMERA_HEIGHT
						/ (float) CAMERA_WIDTH);
			}
		}

		//updateBackground();
	}*/

	// ===========================================================
	// Methods
	// ===========================================================

	private void addFace(final float pX, final float pY)
	{

		final AnimatedSprite face;
		final Body body;

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

		face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion);
		body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));

		face.setUserData(body);
		this.mScene.registerTouchArea(face);
		this.mScene.attachChild(face);
	}

	private void jumpFace(final AnimatedSprite face)
	{
		final Body faceBody = (Body) face.getUserData();

		final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * -50, this.mGravityY * -50);
		faceBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}