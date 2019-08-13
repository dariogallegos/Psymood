package com.example.psymood.Helpers;

import android.os.CountDownTimer;

public abstract class CountUpTimer extends CountDownTimer {

    private final long INTERVAL_MS;
    private final long duration;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountUpTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);

        this.duration =  millisInFuture;
        this.INTERVAL_MS = countDownInterval;
    }

    public  abstract void onTick(int second);

    @Override
    public void onTick(long millisUntilFinished) {
        int second = (int) ((duration - millisUntilFinished) / INTERVAL_MS);
        onTick(second);
    }

    @Override
    public void onFinish() {
        onTick(duration / INTERVAL_MS);
    }
}
