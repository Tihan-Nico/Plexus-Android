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

public class Story {
  private String imageurl;
  private long timestart;
  private long timeend;
  private String storyid;
  private String userid;
  private String timestamp;

  public Story(
      String imageurl,
      long timestart,
      long timeend,
      String storyid,
      String userid,
      String timestamp) {
    this.imageurl = imageurl;
    this.timestart = timestart;
    this.timeend = timeend;
    this.storyid = storyid;
    this.userid = userid;
    this.timestamp = timestamp;
  }

  public Story() {}

  public String getImageurl() {
    return imageurl;
  }

  public void setImageurl(String imageurl) {
    this.imageurl = imageurl;
  }

  public long getTimestart() {
    return timestart;
  }

  public void setTimestart(long timestart) {
    this.timestart = timestart;
  }

  public long getTimeend() {
    return timeend;
  }

  public void setTimeend(long timeend) {
    this.timeend = timeend;
  }

  public String getStoryid() {
    return storyid;
  }

  public void setStoryid(String storyid) {
    this.storyid = storyid;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
