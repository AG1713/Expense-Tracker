package com.example.expensetracker.viewmodels;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.repository.Repository;
import com.example.expensetracker.repository.database.Mapping;

import java.util.function.Consumer;

public class MappingsActivityViewModel extends AndroidViewModel {
    private Repository repository;

    public MappingsActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
    }

    public void getAllMappings(Consumer<Cursor> callback){
        repository.getAllMappings(callback);
    }

    public void addMapping(Mapping mapping, ErrorCallback callback){
        repository.addMapping(mapping, callback);
    }

    public void removeMapping(long id){
        repository.removeMapping(id);
    }

    public void updateMapping(Mapping mapping, ErrorCallback callback){
        repository.updateMapping(mapping, callback);
    }

}
