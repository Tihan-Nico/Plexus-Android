package com.plexus.model;

import java.util.ArrayList;

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

public class Event {

  private String event_name;
  private String fullname;
  private String details;
  private String imageURL;
  private String event_id;
  private String event_link;
  private String event_type;
  private String event_location;
  private String event_start_date;
  private String event_end_date;
  private ArrayList<String> peopleInterested;

  public Event(
      String event_name,
      String fullname,
      String details,
      String imageURL,
      String event_id,
      String event_link,
      String event_type,
      String event_location,
      String event_start_date,
      String event_end_date) {
    peopleInterested = new ArrayList<>();
    this.event_name = event_name;
    this.fullname = fullname;
    this.details = details;
    this.imageURL = imageURL;
    this.event_id = event_id;
    this.event_link = event_link;
    this.event_type = event_type;
    this.event_location = event_location;
    this.event_end_date = event_end_date;
    this.event_start_date = event_start_date;
  }

  public Event() {}

  public String getEvent_name() {
    return event_name;
  }

  public void setEvent_name(String event_name) {
    this.event_name = event_name;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public String getEvent_id() {
    return event_id;
  }

  public void setEvent_id(String event_id) {
    this.event_id = event_id;
  }

  public String getEvent_link() {
    return event_link;
  }

  public void setEvent_link(String event_link) {
    this.event_link = event_link;
  }

  public String getEvent_type() {
    return event_type;
  }

  public void setEvent_type(String event_type) {
    this.event_type = event_type;
  }

  public String getEvent_location() {
    return event_location;
  }

  public void setEvent_location(String event_location) {
    this.event_location = event_location;
  }

  public String getEvent_start_date() {
    return event_start_date;
  }

  public void setEvent_start_date(String event_start_date) {
    this.event_start_date = event_start_date;
  }

  public String getEvent_end_date() {
    return event_end_date;
  }

  public void setEvent_end_date(String event_end_date) {
    this.event_end_date = event_end_date;
  }
}
