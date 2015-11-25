package com.nikhil.timeline.keyframe;

import com.nikhil.timeline.KeyValue;

/**
 * Any key value that exists by its value that's not in space
 * (eg: opacity,scale,rotation but NOT translation)
 * can be keyframed using this class.
 */
public class TemporalKeyframe extends Keyframe {

	private KeyValue keyValue;

	private TemporalKeyframe next;
	private TemporalKeyframe previous;

	/**
	 * Creates a temporal keyframe with no previous or next
	 * @param time time at which the value should be this
	 * @param keyValue the value at this keyframe
	 */
	public TemporalKeyframe(double time,KeyValue keyValue) {
		this(time,null,keyValue,null);
	}

	/**
	 * Creates a temporal keyframe with a previous but no next
	 * @param time time at which the value should be this
	 * @param previous previous temporal keyframe for which this one is the end
	 * @param keyValue the value at this keyframe
	 */
	public TemporalKeyframe(double time,TemporalKeyframe previous, KeyValue keyValue) {
		this(time,previous,keyValue,null);
	}

	/**
	 * Creates a temporal keyframe with a next but no previous
	 * @param time time at which the value should be this
	 * @param keyValue the value at this keyframe
	 * @param next next temporal keyframe containing the end of this keyframe
	 */
	public TemporalKeyframe(double time,KeyValue keyValue, TemporalKeyframe next) {
		this(time,null,keyValue,next);
	}

	/**
	 * Creates a temporal keyframe with a next but no previous
	 * @param time time at which the value should be this
	 * @param previous previous temporal keyframe for which this one is the end
	 * @param keyValue the value at this keyframe
	 * @param next next temporal keyframe containing the end of this keyframe
	 */
	public TemporalKeyframe(double time,TemporalKeyframe previous, KeyValue keyValue, TemporalKeyframe next) {
		super(time);
		this.previous = previous;
		this.keyValue = keyValue;
		this.next = next;
	}

	public KeyValue getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(KeyValue keyValue) {
		this.keyValue = keyValue;
	}

	@Override
	public TemporalKeyframe getNext() {
		return next;
	}

	public void setNext(TemporalKeyframe next) {
		this.next = next;
	}

	@Override
	public TemporalKeyframe getPrevious() {
		return previous;
	}

	public void setPrevious(TemporalKeyframe previous) {
		this.previous = previous;
	}
}