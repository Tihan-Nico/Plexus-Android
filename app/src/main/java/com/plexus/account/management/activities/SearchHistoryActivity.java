package com.plexus.account.management.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.account.management.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryActivity extends AppCompatActivity {

    View toolbar;
    TextView clear_history;
    RecyclerView recycler_view;
    RelativeLayout relativeLayout;

    FirebaseUser firebaseUser;

    List<String> mSearches;

    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_search_history);

        toolbar = findViewById(R.id.toolbar);
        clear_history = findViewById(R.id.clear_history);
        recycler_view = findViewById(R.id.recycler_view);
        relativeLayout = findViewById(R.id.relativeLayout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        init();

    }

    private void init() {

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());

        toolbar_name.setText("Search History");

        clear_history.setOnClickListener(v -> clearHistory());

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mSearches = new ArrayList<>();
        searchAdapter = new SearchAdapter(getApplicationContext(), mSearches);
        recycler_view.setAdapter(searchAdapter);

        if (searchAdapter.getItemCount() == 0) {
            relativeLayout.setVisibility(View.GONE);
        }

        readSearches();

    }

    private void readSearches() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Searches").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mSearches.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mSearches.add(dataSnapshot.getKey());
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearHistory() {
        FirebaseDatabase.getInstance().getReference("Searches").child(firebaseUser.getUid()).removeValue();
    }

}
