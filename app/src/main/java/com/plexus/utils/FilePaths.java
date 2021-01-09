package com.plexus.utils;

import android.os.Environment;

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

public class FilePaths {
    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/Camera";
    public String DCIM = ROOT_DIR + "/DCIM";
    public String Facebook = ROOT_DIR + "/DCIM/Facebook";
    public String Whatsapp = ROOT_DIR + "/WhatsApp/Media/WhatsApp Images";
    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
}
