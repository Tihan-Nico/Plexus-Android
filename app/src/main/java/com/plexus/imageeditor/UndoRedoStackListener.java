package com.plexus.imageeditor;

public interface UndoRedoStackListener {

  void onAvailabilityChanged(boolean undoAvailable, boolean redoAvailable);
}
