package pruebas.Util;

import pruebas.Renders.UnitRender.FACING;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * <p>
 * An Animation stores a list of {@link TextureRegion}s representing an animated
 * sequence, e.g. for running or jumping. Each region of an Animation is called
 * a key frame, multiple key frames make up the animation.
 * </p>
 * 
 * @author --
 */
public class SuperAnimation {
	private TextureRegion current;

	public static final int NORMAL = 0;
	public static final int LOOP = 1;

	public int handle_x = 0;
	public int handle_y = 0;

	final TextureRegion[] keyFrames;
	final float[] keyTimes;
	final float[] keyDurations;

	private boolean looping = true;
	private float totalTime = 0;
	private float animationTime = 0;
	private int frameNumber = 0;

	/**
	 * Constructor, storing the frame duration and key frames.
	 * 
	 * @param animation_time
	 *            the total time the animation takes to play.
	 * @param keyTimes
	 *            the time at which each key frame must show up.
	 * @param keyFrames
	 *            the {@link TextureRegion}s representing the frames.
	 */
	public SuperAnimation(float animation_time, float[] keyTimes, TextureRegion... keyFrames) {
		this(animation_time, keyTimes, keyFrames, true);
	}

	/**
	 * Constructor, storing the frame duration, key frames and play type.
	 * 
	 * @param animation_time
	 *            the total time the animation takes to play.
	 * @param keyTimes
	 *            the time at which each key frame must show up.
	 * @param keyFrames
	 *            the {@link TextureRegion}s representing the frames.
	 * @param playType
	 *            the type of animation play (NORMAL, REVERSED, LOOP,
	 *            LOOP_REVERSED, LOOP_PINGPONG, LOOP_RANDOM)
	 */
	public SuperAnimation(float animation_time, float[] keyTimes,
			TextureRegion[] keyFrames, boolean loop) {
		this.animationTime = animation_time;
		this.keyTimes = keyTimes;
		this.keyDurations = calculateDurations(keyTimes);
		this.keyFrames = getShallowCopy(keyFrames);
		this.looping = loop;
	}

	private TextureRegion[] getShallowCopy(TextureRegion[] keyFrames)
	{
		TextureRegion[] frames = new TextureRegion[keyFrames.length];
		for (int i = 0, n = keyFrames.length; i < n; i++) {
			frames[i] = keyFrames[i];
		}
		return frames;
	}

	private float[] calculateDurations(float[] keyTimes)
	{
		float[] durations = new float[keyTimes.length];

		if (keyTimes.length >= 1)
		{
			for (int i = 0; i + 1 < keyTimes.length; i++) {
				durations[i] = keyTimes[i + 1] - keyTimes[i];
			}
			durations[keyTimes.length - 1] = animationTime - keyTimes[keyTimes.length - 1];
		}

		return durations;
	}

	public void update(float stateTime) {
		update(stateTime, FACING.right);
	}

	public void update(float stateTime, FACING at) {
		totalTime += stateTime;
		current = getKeyFrame();

		if (at == FACING.left && !current.isFlipX()) {
			current.flip(true, false);
		} else if (at == FACING.right && current.isFlipX()) {
			current.flip(true, false);
		}
	}

	/**
	 * Returns a {@link TextureRegion} based on the so called state time. This
	 * is the amount of seconds an object has spent in the state this Animation
	 * instance represents, e.g. running, jumping and so on. The mode specifies
	 * whether the animation is looping or not.
	 * 
	 * @param stateTime
	 *            the time spent in the state represented by this animation.
	 * @param looping
	 *            whether the animation is looping or not.
	 * @return the TextureRegion representing the frame of animation for the
	 *         given state time.
	 */
	private TextureRegion getKeyFrame(float stateTime, boolean loop) {
		looping = loop;

		totalTime += stateTime;
		return getKeyFrame();
	}

	/**
	 * Returns a {@link TextureRegion} based on the so called state time. This
	 * is the amount of seconds an object has spent in the state this Animation
	 * instance represents, e.g. running, jumping and so on using the mode
	 * specified by {@link #setLooping(int)} method.
	 * 
	 * @param stateTime
	 * @return the TextureRegion representing the frame of animation for the
	 *         given state time.
	 */
	private TextureRegion getKeyFrame() {
		frameNumber = getKeyFrameIndex();
		return keyFrames[frameNumber];
	}

	/**
	 * Returns the current frame number.
	 * 
	 * @param stateTime
	 * @return current frame number
	 */
	private int getKeyFrameIndex() {
		if (keyFrames.length == 1)
			return 0;

		if (totalTime >= keyDurations[frameNumber]) {
			frameNumber++;
			totalTime = 0;
		}

		if (looping)
			frameNumber = frameNumber % keyFrames.length;
		else
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);

		return frameNumber;
	}

	/**
	 * Sets the animation play mode.
	 * 
	 * @param looping
	 *            can be one of the following: Animation.NORMAL, Animation.LOOP
	 */
	public void setLooping(boolean loop) {
		this.looping = loop;
	}

	/**
	 * Whether the animation would be finished if played without looping
	 * (PlayMode Animation#NORMAL), given the state time.
	 * 
	 * @return whether the animation is finished.
	 */
	public boolean isAnimationFinished() {
		return keyFrames.length - 1 == frameNumber;
	}

	public void draw(SpriteBatch batch, float x, float y) {
		batch.draw(current, x - handle_x, y - handle_y);
	}

	/**
	 * All SuperAnimations clones share the frames and duration instances
	 */
	public SuperAnimation clone() {
		SuperAnimation anim = new SuperAnimation(totalTime, keyDurations, keyFrames);
		anim.handle_x = handle_x;
		anim.handle_y = handle_y;
		return anim;
	}

	/**
	 * Sets the current frame to a random value between 0 and total_frames.
	 */
	public void randomCurrentFrame()
	{
		this.frameNumber = MathUtils.random(0, keyFrames.length - 1);
		this.totalTime = keyTimes[frameNumber];
	}

	public float getFirstHeight() {
		if (keyFrames == null || keyFrames.length == 0)
			return 0;
		return keyFrames[0].getRegionHeight();
	}

	public float getFirstWidth() {
		if (keyFrames == null || keyFrames.length == 0)
			return 0;
		return keyFrames[0].getRegionWidth();
	}
}
