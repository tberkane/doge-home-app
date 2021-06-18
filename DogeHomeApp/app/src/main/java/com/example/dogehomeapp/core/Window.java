package com.example.dogehomeapp.core;

import java.util.Collections;
import java.util.LinkedList;

/**
 * https://github.com/dimitrisraptis96/fall-detection-app/blob/master/FallDetector/app/src/main/java/com/example/dimitris/falldetector/core/Window.java
 */
public class Window {
    public static final String TAG = "Window.java";

    private final int SIZE;

    LinkedList<Float> samples;

    public Window(int size) {
        SIZE = size;
        samples = new LinkedList<>();
    }

    public void add(float value) {
        if (isFull()) {
            samples.removeFirst();
        }
        samples.add(value);
    }

    public void clear() {
        samples.clear();
    }

    public Boolean isFull() {
        return (samples.size() > SIZE);
    }

    public Boolean isFallDetected() {
        Float max = Collections.max(samples);
        Float min = Collections.min(samples);
        float diff = Math.abs(max - min);

        // check if min value detected earlier than max
        boolean isFall = (samples.indexOf(max) > samples.indexOf(min));

        float THRESHOLD = 50f;
        return (diff > THRESHOLD && isFall);
    }


}

