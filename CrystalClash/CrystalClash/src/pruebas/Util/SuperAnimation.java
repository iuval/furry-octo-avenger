package pruebas.Util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/** <p>
 * An Animation stores a list of {@link TextureRegion}s representing an animated sequence, e.g. for running or jumping. Each
 * region of an Animation is called a key frame, multiple key frames make up the animation.
 * </p>
 * 
 * @author -- */
public class SuperAnimation {
	public static final int NORMAL = 0;
	public static final int REVERSED = 1;
	public static final int LOOP = 2;
	public static final int LOOP_REVERSED = 3;
	public static final int LOOP_PINGPONG = 4;
	public static final int LOOP_RANDOM = 5;

	final TextureRegion[] keyFrames;
	final float[] keyDurations;
	public final float animationDuration;

	private int playMode = NORMAL;
	private float totalTime = 0;
	private int frameNumber = 0;

	/** Constructor, storing the frame duration and key frames.
	 * 
	 * @param frameDuration the time between frames in seconds.
	 * @param keyFrames the {@link TextureRegion}s representing the frames. */
	public SuperAnimation (float[] keyDurations, Array<? extends TextureRegion> keyFrames) {
		this.keyDurations = keyDurations;
		float totalDuration = 0;
		for(int i=0;i < keyDurations.length;i++){
			totalDuration += keyDurations[i];
		}
		this.animationDuration = keyFrames.size * totalDuration;
		this.keyFrames = new TextureRegion[keyFrames.size];
		for (int i = 0, n = keyFrames.size; i < n; i++) {
			this.keyFrames[i] = keyFrames.get(i);
		}

		this.playMode = NORMAL;
	}

	/** Constructor, storing the frame duration, key frames and play type.
	 * 
	 * @param frameDuration the time between frames in seconds.
	 * @param keyFrames the {@link TextureRegion}s representing the frames.
	 * @param playType the type of animation play (NORMAL, REVERSED, LOOP, LOOP_REVERSED, LOOP_PINGPONG, LOOP_RANDOM) */
	public SuperAnimation (float[] keyDurations, Array<? extends TextureRegion> keyFrames, int playType) {
		this.keyDurations = keyDurations;
		float totalDuration = 0;
		for(int i=0;i < keyDurations.length;i++){
			totalDuration += keyDurations[i];
		}
		this.animationDuration = keyFrames.size * totalDuration;
		this.keyFrames = new TextureRegion[keyFrames.size];
		for (int i = 0, n = keyFrames.size; i < n; i++) {
			this.keyFrames[i] = keyFrames.get(i);
		}

		this.playMode = playType;
	}

	/** Constructor, storing the frame duration and key frames.
	 * 
	 * @param frameDuration the time between frames in seconds.
	 * @param keyFrames the {@link TextureRegion}s representing the frames. */
	public SuperAnimation (float[] keyDurations, TextureRegion... keyFrames) {
		this.keyDurations = keyDurations;
		float totalDuration = 0;
		for(int i=0;i < keyDurations.length;i++){
			totalDuration += keyDurations[i];
		}
		this.animationDuration = keyFrames.length * totalDuration;
		this.keyFrames = keyFrames;
		this.playMode = NORMAL;
	}

	/** Returns a {@link TextureRegion} based on the so called state time. This is the amount of seconds an object has spent in the
	 * state this Animation instance represents, e.g. running, jumping and so on. The mode specifies whether the animation is
	 * looping or not.
	 * 
	 * @param stateTime the time spent in the state represented by this animation.
	 * @param looping whether the animation is looping or not.
	 * @return the TextureRegion representing the frame of animation for the given state time. */
	public TextureRegion getKeyFrame (float stateTime, boolean looping) {
		// we set the play mode by overriding the previous mode based on looping
		// parameter value
		if (looping && (playMode == NORMAL || playMode == REVERSED)) {
			if (playMode == NORMAL)
				playMode = LOOP;
			else
				playMode = LOOP_REVERSED;
		} else if (!looping && !(playMode == NORMAL || playMode == REVERSED)) {
			if (playMode == LOOP_REVERSED)
				playMode = REVERSED;
			else
				playMode = LOOP;
		}

		totalTime += stateTime;
		return getKeyFrame();
	}

	/** Returns a {@link TextureRegion} based on the so called state time. This is the amount of seconds an object has spent in the
	 * state this Animation instance represents, e.g. running, jumping and so on using the mode specified by
	 * {@link #setPlayMode(int)} method.
	 * 
	 * @param stateTime
	 * @return the TextureRegion representing the frame of animation for the given state time. */
	public TextureRegion getKeyFrame () {
		int frameNumber = getKeyFrameIndex();
		return keyFrames[frameNumber];
	}
	
	/** Returns the current frame number.
	 * @param stateTime
	 * @return current frame number */
	public int getKeyFrameIndex () {
		if(totalTime >= keyDurations[frameNumber]){
			frameNumber++;
			totalTime = 0;
		}
		
		if(keyFrames.length == 1)
         return 0;
		
		switch (playMode) {
		case NORMAL:
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
			break;
		case LOOP:
			frameNumber = frameNumber % keyFrames.length;
			break;
		default:
			// play normal otherwise
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
			break;
		}
		
		return frameNumber;
	}

	/** Sets the animation play mode.
	 * 
	 * @param playMode can be one of the following: Animation.NORMAL, Animation.LOOP */
	public void setPlayMode (int playMode) {
		this.playMode = playMode;
	}

	/** Whether the animation would be finished if played without looping (PlayMode Animation#NORMAL), given the state time.
	 * @param stateTime
	 * @return whether the animation is finished. */
	public boolean isAnimationFinished (float stateTime) {
		return keyFrames.length - 1 < frameNumber;
	}
}
