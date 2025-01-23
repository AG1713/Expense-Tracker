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
import com.example.expensetracker.repository.database.Goal;
import com.example.expensetracker.repository.displayEntities.CategoryDisplay;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.repository.database.Record;
import com.example.expensetracker.repository.displayEntities.CategoryEntries;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

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
    public void addGoal(Goal goal, ErrorCallback callback) {
        repository.addGoal(goal, callback);
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
    public void removeRecord(long id, Long category_id, double amount){
        repository.removeRecord(id, category_id, amount);
    }
    public void removeGoal(long id){
        repository.removeGoal(id);
    }
    public void updateParty(Party party){
        repository.updateParty(party);
    }
    public void updateCategory(Category category){
        repository.updateCategory(category);
    }
    public void updateRecord(Record record, Long old_category_id, double amount){
        repository.updateRecord(record, old_category_id, amount);
    }
    public void updateGoal(Goal goal, ErrorCallback callback){
        repository.updateGoal(goal, callback);
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
    public void getAllCategories(Consumer<Cursor> callback){ repository.getAllCategories(callback);}
    public void getAllAccounts(Consumer<Cursor> callback){repository.getAllAccounts(callback);}
    public void getAllParties(Consumer<Cursor> callback) {repository.getAllParties(callback);}
    public void getAllGoals(Consumer<Cursor> callback){
        repository.getAllGoals(callback);
    }
    public void getSevenDayExpense(ArrayList<BarEntry> entries, ArrayList<String> dates, Runnable callback){
        repository.getSevenDayExpenses(entries, dates, callback);
    }
    public void getAllDatesCategoriesAmounts(Map<String, CategoryEntries> dateCategoryEntriesMap){
        repository.getAllDatesCategoriesAmounts(dateCategoryEntriesMap);
    }
    public void getSevenDaysCategoriesAmounts(CategoryEntries categoryEntries, Runnable callback){
        repository.getSevenDaysCategoriesAmounts(categoryEntries, callback);
    }
}
