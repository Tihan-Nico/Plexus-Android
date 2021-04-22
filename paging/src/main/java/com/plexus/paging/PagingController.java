package com.plexus.paging;

public interface PagingController {
    void onDataNeededAroundIndex(int aroundIndex);
    void onDataInvalidated();
}
