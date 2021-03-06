package com.android.server.accessibility;

import android.content.Context;

abstract class BaseEventStreamTransformation implements EventStreamTransformation {
    private EventStreamTransformation mNext;

    BaseEventStreamTransformation() {
    }

    public void setNext(EventStreamTransformation next) {
        this.mNext = next;
    }

    public EventStreamTransformation getNext() {
        return this.mNext;
    }

    protected boolean showMagnDialog(Context context) {
        return false;
    }
}
