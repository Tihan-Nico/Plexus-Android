package com.plexus.model.posts;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class SavedPostsCollection {

    private String id;
    private String collection_name;
    private String collection_image_url;
    private String collection_owner;
    private String timestamp;

    public SavedPostsCollection() {

    }

    public SavedPostsCollection(String id, String collection_name, String collection_image_url, String collection_owner, String timestamp) {
        this.id = id;
        this.collection_name = collection_name;
        this.collection_image_url = collection_image_url;
        this.collection_owner = collection_owner;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollection_name() {
        return collection_name;
    }

    public void setCollection_name(String collection_name) {
        this.collection_name = collection_name;
    }

    public String getCollection_image_url() {
        return collection_image_url;
    }

    public void setCollection_image_url(String collection_image_url) {
        this.collection_image_url = collection_image_url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCollection_owner() {
        return collection_owner;
    }

    public void setCollection_owner(String collection_owner) {
        this.collection_owner = collection_owner;
    }
}
