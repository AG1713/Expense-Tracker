package com.example.expensetracker.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.repository.Repository;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.database.Category;
import com.example.expensetracker.repository.database.CategoryDisplay;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.repository.database.Record;

import java.util.ArrayList;

public class MainActivityViewModel extends AndroidViewModel {
    private Repository repository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
    }

    public void addRecord(Record record){
        repository.addRecord(record);
    }
    public void addAccount(Account account, ErrorCallback callback){
        repository.addAccount(account, callback);
    }
    public void addCategory(Category category){
        repository.addCategory(category);
    }
    public void addParty (Party party, ErrorCallback callback) throws SQLiteException {
        repository.addParty(party, callback);
    }
    public Cursor getAllRecords(){
        return repository.getAllRecords();
    }
    public void removeCategory(long id){
        repository.removeCategory(id);
    }
    public void removeParty(long id){
        repository.removeParty(id);
    }
    public void removeRecord(long id){
        repository.removeRecord(id);
    }
    public void updateParty(Party party){
        repository.updateParty(party);
    }
    public void updateCategory(Category category){
        repository.updateCategory(category);
    }
    public void updateRecord(Record record){
        repository.updateRecord(record);
    }
    public void addTransaction(Record record, String partyName, String accountNo){
        repository.addTransaction(record, partyName, accountNo);
    };
    public Cursor getAllPartiesWithAmount(){
        return repository.getAllPartiesWithAmount();
    }
    public ArrayList<CategoryDisplay> getAllCategoriesInDFS(){
        return repository.getAllCategoriesInDFS();
    }
    public Cursor getAllAccountsWithAmount(){return repository.getAllAccountsWithAmounts();}
}
