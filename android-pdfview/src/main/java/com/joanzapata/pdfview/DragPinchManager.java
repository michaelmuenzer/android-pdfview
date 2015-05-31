/**
 * Copyright 2014 Joan Zapata
 *
 * This file is part of Android-pdfview.
 *
 * Android-pdfview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android-pdfview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android-pdfview.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joanzapata.pdfview;

import android.graphics.PointF;

import com.joanzapata.pdfview.listener.OnClickListener;
import com.joanzapata.pdfview.util.Constants;
import com.joanzapata.pdfview.util.DragPinchListener;
import com.joanzapata.pdfview.util.DragPinchListener.OnDoubleTapListener;
import com.joanzapata.pdfview.util.DragPinchListener.OnDragListener;
import com.joanzapata.pdfview.util.DragPinchListener.OnPinchListener;

import static com.joanzapata.pdfview.util.Constants.Pinch.QUICK_MOVE_THRESHOLD_DISTANCE;
import static com.joanzapata.pdfview.util.Constants.Pinch.QUICK_MOVE_THRESHOLD_TIME;

/**
 * @author Joan Zapata
 *         This Manager takes care of moving the PDFView,
 *         set its zoom track user actions.
 */
class DragPinchManager implements OnDragListener, OnPinchListener, OnDoubleTapListener {

    private PDFView pdfView;

    private DragPinchListener dragPinchListener;
    private OnPinchListener onPinchListener;
    private OnDragListener onDragListener;

    private long startDragTime;

    private float startDragX;

    private boolean isSwipeEnabled;

    public DragPinchManager(PDFView pdfView) {
        this.pdfView = pdfView;
        this.isSwipeEnabled = false;
        dragPinchListener = new DragPinchListener();
        dragPinchListener.setOnDragListener(this);
        dragPinchListener.setOnPinchListener(this);
        dragPinchListener.setOnDoubleTapListener(this);
        pdfView.setOnTouchListener(dragPinchListener);
    }

    @Override
    public void onPinch(float dr, PointF pivot) {
        float wantedZoom = pdfView.getZoom() * dr;
        if (wantedZoom < Constants.MINIMUM_ZOOM) {
            dr = Constants.MINIMUM_ZOOM / pdfView.getZoom();
        } else if (wantedZoom > Constants.MAXIMUM_ZOOM) {
            dr = Constants.MAXIMUM_ZOOM / pdfView.getZoom();
        }
        pdfView.zoomCenteredRelativeTo(dr, pivot);

        if(onPinchListener != null) {
            onPinchListener.onPinch(dr, pivot);
        }
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (isZooming() || isSwipeEnabled) {
            pdfView.moveRelativeTo(dx, dy);
        }

        if(onDragListener != null) {
            onDragListener.onDrag(dx, dy);
        }
    }

    @Override
    public void startDrag(float x, float y) {
        startDragTime = System.currentTimeMillis();
        startDragX = x;

        if(onDragListener != null) {
            onDragListener.startDrag(x, y);
        }
    }

    @Override
    public void endDrag(float x, float y) {
        if (!isZooming()) {
            if (isSwipeEnabled) {
                float distance = x - startDragX;
                long time = System.currentTimeMillis() - startDragTime;
                int diff = distance > 0 ? -1 : +1;

                if (isQuickMove(distance, time) || isPageChange(distance)) {
                    pdfView.showPage(pdfView.getCurrentPage() + diff);
                } else {
                    pdfView.showPage(pdfView.getCurrentPage());
                }
            }
        } else {
            pdfView.loadPages();
        }

        if(onDragListener != null) {
            onDragListener.endDrag(x, y);
        }
    }

    public boolean isZooming() {
        return pdfView.isZooming();
    }

    private boolean isPageChange(float distance) {
        return Math.abs(distance) > Math.abs(pdfView.toCurrentScale(pdfView.getOptimalPageWidth()) / 2);
    }

    private boolean isQuickMove(float dx, long dt) {
        return Math.abs(dx) >= QUICK_MOVE_THRESHOLD_DISTANCE && //
                dt <= QUICK_MOVE_THRESHOLD_TIME;
    }

    public void setSwipeEnabled(boolean isSwipeEnabled) {
        this.isSwipeEnabled = isSwipeEnabled;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        dragPinchListener.setOnClickListener(onClickListener);
    }

    @Override
    public void onDoubleTap(float x, float y) {
        if (isZooming()) {
            pdfView.resetZoomWithAnimation();
        }
    }

    public void setOnPinchListener(OnPinchListener onPinchListener) {
        this.onPinchListener = onPinchListener;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }
}
