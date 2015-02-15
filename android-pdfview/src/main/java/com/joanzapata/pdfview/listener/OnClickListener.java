package com.joanzapata.pdfview.listener;

import android.view.MotionEvent;
import android.view.View;

/**
 * Implements this interface to receive events from IPDFView
 * when a click has been performed
 */
public interface OnClickListener {

    /**
     * Called when the user performs a click
     */
    void onClick(View v, MotionEvent event);

}