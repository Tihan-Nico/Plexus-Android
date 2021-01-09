package com.plexus.story.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;
import com.plexus.story.adapter.GridImageAdapter;
import com.plexus.utils.FilePaths;
import com.plexus.utils.FileSearch;

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

public class ChooseStoryTypeActivity extends AppCompatActivity {

    private static final int NUM_GRID_COLUMNS = 3;

    //widgets
    private GridView gridView;
    private Spinner directorySpinner;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_add_type);

        gridView = findViewById(R.id.gridView);
        directorySpinner = findViewById(R.id.spinnerDirectory);
        directories = new ArrayList<>();

        init();
    }

    private void init() {
        FilePaths filePaths = new FilePaths();

        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        directories.add(filePaths.CAMERA);
        directories.add(filePaths.Facebook);
        directories.add(filePaths.Whatsapp);
        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupGridView(String selectedDirectory) {
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(getApplicationContext(), R.layout.story_gallery_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        mSelectedImage = imgURLs.get(0);

        gridView.setOnItemClickListener((parent, view, position, id) -> mSelectedImage = imgURLs.get(position));

    }
}
