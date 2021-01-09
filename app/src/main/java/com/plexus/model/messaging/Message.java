package com.plexus.model.messaging;

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

public class Message {

    private String id;
    private String quotedMessageId;
    private String sender;
    private String receiver;
    private String message;
    private String downloadURL;
    private String type;
    private String timestamp;
    private boolean replied;
    private boolean isseen;

    //Map
    private double latitude;
    private double longitude;

    //File
    private String filename;
    private String size_file;

    //Settings
    private boolean forwarding_disabled;
    private boolean screenshots;
    private boolean copy_text_disabled;
    private String chat_type;
    private boolean listened;
    private boolean save_to_device_enabled;
    private boolean can_post_on_plexus_timeline;

    public Message(String id, String quotedMessageId, String sender, String receiver, String message, String downloadURL, String type, boolean replied, boolean isseen, String timestamp, double latitude, double longitude) {
        this.id = id;
        this.quotedMessageId = quotedMessageId;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.downloadURL = downloadURL;
        this.type = type;
        this.timestamp = timestamp;
        this.replied = replied;
        this.isseen = isseen;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Message() {
    }

    public Message(String message, String valueOf, String sender, String receiver) {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuotedMessageId() {
        return quotedMessageId;
    }

    public void setQuotedMessageId(String quotedMessageId) {
        this.quotedMessageId = quotedMessageId;
    }

    public boolean isReplied() {
        return replied;
    }

    public void setReplied(boolean replied) {
        this.replied = replied;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
