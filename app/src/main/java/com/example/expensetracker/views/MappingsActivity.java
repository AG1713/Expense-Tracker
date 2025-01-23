package com.example.expensetracker.views;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ActivityMappingsBinding;
import com.example.expensetracker.viewmodels.MappingsActivityViewModel;
import com.example.expensetracker.views.adapters.MappingsAdapter;

import java.util.function.Consumer;

public class MappingsActivity extends AppCompatActivity {
    ActivityMappingsBinding binding;
    MappingsActivityViewModel viewModel;
    ListView listView;
    MappingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mappings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(MappingsActivityViewModel.class);
        listView = binding.listView;
        viewModel.getAllMappings(new Consumer<Cursor>() {
            @Override
            public void accept(Cursor cursor) {
                adapter = new MappingsAdapter(MappingsActivity.this, cursor, 0);
                listView.setAdapter(adapter);
            }
        });




    }
}