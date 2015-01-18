/*
 *   Copyright 2012 Hai Bison
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.haibison.android.lockpattern.util;

import java.util.List;

import android.os.Handler;

import com.haibison.android.lockpattern.collect.Lists;

/**
 * Float animator.
 * 
 * @author Hai Bison
 *
 */
public class FloatAnimator {

    /**
     * Event listener.
     * 
     * @author Hai Bison
     *
     */
    public static interface EventListener {

        /**
         * Will be called when animation starts.
         * 
         * @param animator
         *            the animator.
         */
        void onAnimationStart(FloatAnimator animator);

        /**
         * Will be called when new animated value is calculated.
         * 
         * @param animator
         *            the animator.
         */
        void onAnimationUpdate(FloatAnimator animator);

        /**
         * Will be called when animation cancels.
         * 
         * @param animator
         *            the animator.
         */
        void onAnimationCancel(FloatAnimator animator);

        /**
         * Will be called when animation ends.
         * 
         * @param animator
         *            the animator.
         */
        void onAnimationEnd(FloatAnimator animator);

    }// EventListener

    /**
     * Simple event listener.
     * 
     * @author Hai Bison
     *
     */
    public static class SimpleEventListener implements EventListener {

        @Override
        public void onAnimationStart(FloatAnimator animator) {
        }

        @Override
        public void onAnimationUpdate(FloatAnimator animator) {
        }

        @Override
        public void onAnimationCancel(FloatAnimator animator) {
        }

        @Override
        public void onAnimationEnd(FloatAnimator animator) {
        }

    }// SimpleEventListener

    /**
     * Animation delay, in milliseconds.
     */
    private static final long ANIMATION_DELAY = 1;

    private final float mStartValue, mEndValue;
    private final long mDuration;
    private float mAnimatedValue;

    private List<EventListener> mEventListeners;
    private Handler mHandler;
    private long mStartTime;

    /**
     * Creates new instance.
     * 
     * @param start
     *            start value.
     * @param end
     *            end value.
     * @param duration
     *            duration, in milliseconds. This should not be long, as delay
     *            value between animation frame is just 1 millisecond.
     */
    public FloatAnimator(float start, float end, long duration) {
        mStartValue = start;
        mEndValue = end;
        mDuration = duration;

        mAnimatedValue = mStartValue;
    }// FloatAnimator()

    /**
     * Adds event listener.
     * 
     * @param listener
     *            the listener.
     */
    public void addEventListener(EventListener listener) {
        if (mEventListeners == null)
            mEventListeners = Lists.newArrayList();
        mEventListeners.add(listener);
    }// addEventListener()

    /**
     * Gets animated value.
     * 
     * @return animated value.
     */
    public float getAnimatedValue() {
        return mAnimatedValue;
    }// getAnimatedValue()

    /**
     * Starts animating.
     */
    public void start() {
        if (mHandler != null)
            return;

        notifyAnimationStart();

        mStartTime = System.currentTimeMillis();

        mHandler = new Handler();
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                final Handler handler = mHandler;
                if (handler == null)
                    return;

                final long elapsedTime = System.currentTimeMillis()
                        - mStartTime;
                if (elapsedTime > mDuration) {
                    mHandler = null;
                    notifyAnimationEnd();
                } else {
                    float fraction = mDuration > 0 ? (float) (elapsedTime)
                            / mDuration : 1f;
                    float delta = mEndValue - mStartValue;
                    mAnimatedValue = mStartValue + delta * fraction;

                    notifyAnimationUpdate();
                    handler.postDelayed(this, ANIMATION_DELAY);
                }
            }// run()

        });
    }// start()

    /**
     * Cancels animating.
     */
    public void cancel() {
        if (mHandler == null)
            return;

        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;

        notifyAnimationCancel();
        notifyAnimationEnd();
    }// cancel()

    /**
     * Notifies all listeners that animation starts.
     */
    protected void notifyAnimationStart() {
        final List<EventListener> listeners = mEventListeners;
        if (listeners != null) {
            for (EventListener listener : listeners)
                listener.onAnimationStart(this);
        }// if
    }// notifyAnimationStart()

    /**
     * Notifies all listeners that animation updates.
     */
    protected void notifyAnimationUpdate() {
        final List<EventListener> listeners = mEventListeners;
        if (listeners != null) {
            for (EventListener listener : listeners)
                listener.onAnimationUpdate(this);
        }// if
    }// notifyAnimationUpdate()

    /**
     * Notifies all listeners that animation cancels.
     */
    protected void notifyAnimationCancel() {
        final List<EventListener> listeners = mEventListeners;
        if (listeners != null) {
            for (EventListener listener : listeners)
                listener.onAnimationCancel(this);
        }// if
    }// notifyAnimationCancel()

    /**
     * Notifies all listeners that animation ends.
     */
    protected void notifyAnimationEnd() {
        final List<EventListener> listeners = mEventListeners;
        if (listeners != null) {
            for (EventListener listener : listeners)
                listener.onAnimationEnd(this);
        }// if
    }// notifyAnimationEnd()

}
