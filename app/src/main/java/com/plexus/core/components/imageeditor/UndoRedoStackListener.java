package com.plexus.core.components.imageeditor;

public interface UndoRedoStackListener {

    void onAvailabilityChanged(boolean undoAvailable, boolean redoAvailable);
}
