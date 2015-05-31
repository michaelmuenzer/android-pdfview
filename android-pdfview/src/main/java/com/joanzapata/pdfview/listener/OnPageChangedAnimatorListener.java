package com.joanzapata.pdfview.listener;

import android.animation.Animator;
import android.view.animation.Animation;

public interface OnPageChangedAnimatorListener {
    void onAnimationStart(Animator animation, int fromPage);

    void onAnimationEnd(Animator animation, int fromPage);

    void onAnimationCancel(Animator animation, int fromPage);

    void onAnimationRepeat(Animator animation, int fromPage);
}
