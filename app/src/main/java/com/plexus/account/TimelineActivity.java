package com.plexus.account;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plexus.lookout.R;
import com.plexus.lookout.widgets.PlexusRecyclerView;

public class TimelineActivity extends AppCompatActivity {

    PlexusRecyclerView recyclerView;
    ImageView back;
    View empty_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        recyclerView = findViewById(R.id.recycler_view);
        back = findViewById(R.id.back);
        empty_view = findViewById(R.id.empty_view);

        back.setOnClickListener(v -> finish());

        recyclerView.setEmptyView(empty_view);

    }
}
