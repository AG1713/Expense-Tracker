package com.example.expensetracker.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Category;
import com.example.expensetracker.repository.database.CategoryDisplay;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.repository.database.Record;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private final BudgetDB db;

    ExecutorService executor;
    Handler handler;

    public Repository(Context context){
        db = new BudgetDB(context);

        executor = Executors.newSingleThreadExecutor();
        handler = new android.os.Handler(Looper.getMainLooper());
    }

    public void addRecord(Record record){
        executor.execute(() -> db.insertRecord(record));
    }

    public void addCategory(Category category){
        executor.execute(() -> db.insertCategory(category));
    }

    public void addParty(Party party, ErrorCallback callback) throws SQLiteException {
        executor.execute(() -> {
            try {
                db.insertParty(party);
                callback.onSuccess();
            }
            catch (SQLiteException e){
                callback.onError(e);
            }
        });
    }

    public void addAccount(Account account, ErrorCallback callback){
        executor.execute(() -> {

            try {
                db.insertAccount(account);
                callback.onSuccess();
            }
            catch (SQLiteException e){
                callback.onError(e);
            }
        });
    }

    public Cursor getAllRecords(){
        return db.getAllRecords();
    }

    public void removeCategory(long id){
        executor.execute(() -> db.deleteCategory(id));
    }

    public void removeParty(long id){
        executor.execute(() -> db.deleteParty(id));
    }

    public void removeRecord(long id){
        executor.execute(() -> db.deleteRecord(id));
    }

    public void updateCategory(Category category){
        executor.execute(() -> db.updateCategory(category));
    }

    public void updateParty(Party party){
        executor.execute(() -> db.updateParty(party));
    }

    public void updateRecord(Record record){
        executor.execute(() -> db.updateRecord(record));
    }

    public void addTransaction(Record record, String partyName, String accountNo){
        executor.execute(() -> db.addTransaction(record, partyName, accountNo));
    }

    public Cursor getAllPartiesWithAmount(){
        return db.getAllPartiesWithAmounts();
    }

    public ArrayList<CategoryDisplay> getAllCategoriesInDFS(){
        return db.getCategoriesInDFS();
    }

    public Cursor getAllAccountsWithAmounts(){
        return db.getAllAccountsWithAmounts();
    }

}
