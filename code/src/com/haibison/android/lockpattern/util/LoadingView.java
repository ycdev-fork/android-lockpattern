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

import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

/**
 * An implementation of {@link AsyncTask}, used to show a view while doing some
 * background tasks, then hide it when done.
 * 
 * @author Hai Bison
 */
public abstract class LoadingView<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {

    @SuppressWarnings("unused")
    private static final String CLASSNAME = LoadingView.class.getName();

    private final View mView;

    /**
     * Delay time in milliseconds. Default delay is half a second.
     */
    private long mDelayTime = SECOND_IN_MILLIS / 2;

    /**
     * Flag to use along with {@link #mDelayTime}
     */
    private boolean mFinished = false;

    private Throwable mLastException;

    /**
     * Creates new instance.
     * 
     * @param context
     *            the context.
     * @param view
     *            the view to be controlled by this async task.
     */
    public LoadingView(Context context, View view) {
        mView = view;
    }// LoadingView()

    /**
     * If you override this method, you must call {@code super.onPreExecute()}
     * at beginning of the method.
     */
    @Override
    protected void onPreExecute() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!mFinished)
                    mView.setVisibility(View.VISIBLE);
            }// run()

        }, getDelayTime());
    }// onPreExecute()

    /**
     * If you override this method, you must call
     * {@code super.onPostExecute(result)} at beginning of the method.
     */
    @Override
    protected void onPostExecute(Result result) {
        doFinish();
    }// onPostExecute()

    /**
     * If you override this method, you must call {@code super.onCancelled()} at
     * beginning of the method.
     */
    @Override
    protected void onCancelled() {
        doFinish();
        super.onCancelled();
    }// onCancelled()

    private void doFinish() {
        mFinished = true;
        mView.setVisibility(View.GONE);
    }// doFinish()

    /**
     * Gets the delay time before showing the view.
     * 
     * @return the delay time, in milliseconds.
     */
    public long getDelayTime() {
        return mDelayTime;
    }// getDelayTime()

    /**
     * Sets the delay time before showing the view.
     * 
     * @param delayTime
     *            the delay time to set, in milliseconds.
     * @return the instance of this object, for chaining multiple calls into a
     *         single statement.
     */
    public LoadingView<Params, Progress, Result> setDelayTime(int delayTime) {
        mDelayTime = delayTime >= 0 ? delayTime : 0;
        return this;
    }// setDelayTime()

    /**
     * Sets last exception. This method is useful in case an exception raises
     * inside {@link #doInBackground(Void...)}
     * 
     * @param t
     *            {@link Throwable}
     */
    protected void setLastException(Throwable t) {
        mLastException = t;
    }// setLastException()

    /**
     * Gets last exception.
     * 
     * @return {@link Throwable}
     */
    public Throwable getLastException() {
        return mLastException;
    }// getLastException()

}
