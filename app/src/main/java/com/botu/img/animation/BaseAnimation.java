package com.botu.img.animation;

import android.animation.Animator;
import android.view.View;

/**
 * @author: swolf
 * @date : 2016-11-15 17:44
 */
public interface BaseAnimation {
    Animator[] getAnimators(View view);
}
