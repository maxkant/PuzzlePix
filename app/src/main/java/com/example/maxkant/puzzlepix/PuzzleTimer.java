package com.example.maxkant.puzzlepix;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;

import java.util.Locale;


public class PuzzleTimer extends android.support.v7.widget.AppCompatTextView {

    long startTime;
    long pauseTime;
    long timeIncrement = 0;

    long saveTime;

    Handler timerHandler = new Handler();
    Runnable timerRunnable;

    public PuzzleTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PuzzleTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PuzzleTimer(Context context) {
        super(context);
    }

    GradientDrawable gd = new GradientDrawable();

    public void startTimer() {

        gd.setColor(Color.WHITE);
        gd.setCornerRadius(5);
        gd.setStroke(5, Color.GREEN);
        setBackground(gd);

        startTime = System.currentTimeMillis();

        pauseTime = 0;
        saveTime = 0;

        timeIncrement = 0;
        timerRunnable = new Runnable() {

            @Override
            public void run() {

                long millis = (System.currentTimeMillis() - startTime - timeIncrement);
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;

                seconds = seconds % 60;


                if ((minutes < 60) & (minutes != 0)){

                    setText(String.format(Locale.US, "%d:%02d", minutes, seconds));

                }

                else if (minutes == 0) {


                    setText(String.format(Locale.US, "%1d:%02d", minutes, seconds));

                }

                else {

                    int hours = minutes / 60;
                    minutes = minutes % 60;

                    setText(String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds));

                }


                timerHandler.removeCallbacksAndMessages(null);
                timerHandler.postDelayed(this, 500);

            }

        };

        timerHandler.removeCallbacksAndMessages(null);
        timerHandler.postDelayed(timerRunnable, 0);


    }

    public void pauseTimer() {

        timerHandler.removeCallbacksAndMessages(null);
        pauseTime = System.currentTimeMillis();

        gd.setColor(Color.WHITE);
        gd.setCornerRadius(5);
        gd.setStroke(5, Color.RED);
        setBackground(gd);

    }

    public void resumeTimer() {


            timeIncrement += (System.currentTimeMillis() - pauseTime);
            timerRunnable = new Runnable() {

                @Override
                public void run() {

                    long millis = (System.currentTimeMillis() - startTime - timeIncrement);
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;

                    seconds = seconds % 60;


                    if ((minutes < 60) & (minutes != 0)){

                        setText(String.format(Locale.US, "%d:%02d", minutes, seconds));

                    }

                    else if (minutes == 0) {


                        setText(String.format(Locale.US, "%1d:%02d", minutes, seconds));

                    }

                    else {

                        int hours = minutes / 60;
                        minutes = minutes % 60;

                        setText(String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds));

                    }


                    timerHandler.removeCallbacksAndMessages(null);
                    timerHandler.postDelayed(this, 500);

                }

            };
            timerHandler.removeCallbacksAndMessages(null);
            timerHandler.postDelayed(timerRunnable, 0);

        }

    }




