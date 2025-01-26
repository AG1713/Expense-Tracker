package com.example.expensetracker.viewmodels;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.repository.Repository;
import com.example.expensetracker.repository.database.Mapping;

import java.util.function.Consumer;

public class MappingsActivityViewModel extends AndroidViewModel {
    private Repository repository;
    private MutableLiveData<Cursor> mappings = new MutableLiveData<>();

    public MappingsActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
        repository.getAllMappings(new Consumer<Cursor>() {
            @Override
            public void accept(Cursor cursor) {
                mappings.postValue(cursor);
            }
        });
    }

    public void getAllMappings(Runnable callback){
        repository.getAllMappings(new Consumer<Cursor>() {
            @Override
            public void accept(Cursor cursor) {
                mappings.postValue(cursor);
                callback.run();
            }
        });
    }

    public void addMapping(Mapping mapping, ErrorCallback callback){
        repository.addMapping(mapping, callback);
    }

    public void removeMapping(long id, Runnable callback){
        repository.removeMapping(id,callback);
    }

    public void updateMapping(Mapping mapping, ErrorCallback callback){
        repository.updateMapping(mapping, callback);
    }

    public void getAllParties(Consumer<Cursor> callback) {repository.getAllParties(callback);}

    public void getAllCategories(Consumer<Cursor> callback){ repository.getAllCategories(callback);}

    public MutableLiveData<Cursor> getMappings() {
        return mappings;
    }

    public void setMappings(MutableLiveData<Cursor> mappings) {
        this.mappings = mappings;
    }
}
