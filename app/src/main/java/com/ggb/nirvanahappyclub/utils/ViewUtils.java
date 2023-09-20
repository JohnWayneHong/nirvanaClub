package com.ggb.nirvanahappyclub.utils;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewParent;

public class ViewUtils {
    public static void expandTouchArea(final View view, final int top, final int left, final int bottom, final int right) {
        ViewParent viewParent = view.getParent();
        if (!(viewParent instanceof View)) {
            return;
        }
        final View parentView= (View) viewParent;
        parentView.post(() -> {
            Rect rect = new Rect();
            view.getHitRect(rect);
            rect.top-=top;
            rect.left-=left;
            rect.bottom+=bottom;
            rect.right+=right;
            parentView.setTouchDelegate(new TouchDelegate(rect,view));
        });
    }
}
