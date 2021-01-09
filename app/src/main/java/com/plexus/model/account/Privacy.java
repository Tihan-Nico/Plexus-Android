package com.plexus.model.account;

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

public class Privacy {

    String screenshot_enabled;
    String last_seen_enabled;
    boolean private_account;
    private boolean blocked;
    private String blocked_timestamp;
    private String blocked_platform;

    public Privacy(){

    }

    public String getScreenshot_enabled() {
        return screenshot_enabled;
    }

    public void setScreenshot_enabled(String screenshot_enabled) {
        this.screenshot_enabled = screenshot_enabled;
    }

    public String getLast_seen_enabled() {
        return last_seen_enabled;
    }

    public void setLast_seen_enabled(String last_seen_enabled) {
        this.last_seen_enabled = last_seen_enabled;
    }

    public boolean isPrivate_account() {
        return private_account;
    }

    public void setPrivate_account(boolean private_account) {
        this.private_account = private_account;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getBlocked_timestamp() {
        return blocked_timestamp;
    }

    public void setBlocked_timestamp(String blocked_timestamp) {
        this.blocked_timestamp = blocked_timestamp;
    }

    public String getBlocked_platform() {
        return blocked_platform;
    }

    public void setBlocked_platform(String blocked_platform) {
        this.blocked_platform = blocked_platform;
    }
}
