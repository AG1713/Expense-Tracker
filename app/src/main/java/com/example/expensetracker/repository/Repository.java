package com.example.expensetracker.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Category;
import com.example.expensetracker.repository.database.Goal;
import com.example.expensetracker.repository.database.Mapping;
import com.example.expensetracker.repository.displayEntities.CategoryDisplay;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.repository.database.Record;
import com.example.expensetracker.repository.displayEntities.CategoryEntries;
import com.example.expensetracker.repository.displayEntities.ChartData;
import com.example.expensetracker.repository.displayEntities.Filter;
import com.example.expensetracker.repository.displayEntities.LineChartData;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Repository {
    private BudgetDB db;

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

    public void addGoal(Goal goal, ErrorCallback callback){
        executor.execute(() -> {
            try {
                db.insertGoal(goal);
                callback.onSuccess();
            }
            catch (SQLiteException e){
                callback.onError(e);
            }
        });
    }

    public void addMapping(Mapping mapping, ErrorCallback callback){
        executor.execute(() -> {
            try {
                db.insertMapping(mapping);
                callback.onSuccess();
            }
            catch (SQLiteException e){
                callback.onError(e);
            }
        });
    }

    public void removeCategory(long id, Runnable callback){
        executor.execute(() -> {
            db.deleteCategory(id);
            callback.run();
        });
    }

    public void removeAccount(long id, Runnable callback) {
        executor.execute(() -> {
            db.deleteAccount(id);
            callback.run();
        });
    }
    public void removeParty(long id, Runnable callback){
        executor.execute(() -> {
            db.deleteParty(id);
            callback.run();
        });
    }

    public void removeRecord(long id, Long category_id, double amount, Runnable callback){
        executor.execute(() -> {
            db.deleteRecord(id, category_id, amount);
            callback.run();
        });
    }

    public void removeGoal(long id){
        executor.execute(() -> db.deleteGoal(id));
    }

    public void removeMapping(long id, Runnable callback) {
        executor.execute(() -> {
            db.deleteMapping(id);
            callback.run();
        });
    }

    public void updateCategory(Category category, ErrorCallback callback){
        executor.execute(() -> {
            try {
                db.updateCategory(category);
                callback.onSuccess();
                Log.d("BudgetDB", "updateCategory: reached");
            }
            catch (SQLiteException e){
                callback.onError(e);
            }
        });
    }

    public void updateParty(Party party, ErrorCallback callback){
        executor.execute(() -> {
            try {
                db.updateParty(party);
                callback.onSuccess();
            }
            catch (SQLiteException e){
                callback.onError(e);
            }
        });
    }

    public void updateRecord(Record record, Long old_category_id, double old_amount){
        executor.execute(() -> db.updateRecord(record, old_category_id, old_amount));
    }

    public void updateGoal(Goal goal, ErrorCallback callback){
        executor.execute(() -> {
            try {
                db.updateGoal(goal);
                callback.onSuccess();
            }
            catch (SQLiteException e){
                callback.onError(e);
            }

        });
    }

    public void updateMapping(Mapping mapping, ErrorCallback callback){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    db.updateMapping(mapping);
                    callback.onSuccess();
                }
                catch (SQLiteException e){
                    callback.onError(e);
                }
            }
        });
    }


    public void addTransaction(Record record, String partyName, String accountNo){
        executor.execute(() -> db.addTransaction(record, partyName, accountNo));
    }

    public void getAllRecords(LineChartData lineChartData, Filter filter, Consumer<Cursor> callback){
        executor.execute(() -> {
            try {
                callback.accept(db.getAllRecords(lineChartData, filter));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void getAllMappings(Consumer<Cursor> callback){
        executor.execute(() -> callback.accept(db.getAllMappings()));
    }

    public void getAllPartiesWithAmount(Filter filter, ChartData partiesData, Consumer<Cursor> callback){
        executor.execute(() -> callback.accept(db.getAllPartiesWithAmounts(filter, partiesData)));
    }

//    public Cursor getAllPartiesWithAmount(){
//        return db.getAllPartiesWithAmounts();
//    }

    public void getAllCategoriesInDFS(Filter filter, ChartData categoriesData, Consumer<ArrayList<CategoryDisplay>> callback){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                callback.accept(db.getCategoriesInDFS(filter, categoriesData));
            }
        });
    }

    public void getAllAccountsWithAmounts(Filter filter, ChartData chartData, Consumer<Cursor> callback){
        executor.execute(() -> callback.accept(db.getAllAccountsWithAmounts(filter, chartData)));
    }

    public void getAllCategories(Consumer<Cursor> callback){
        executor.execute(() -> {
            Cursor cursor = db.getAllCategories();
            callback.accept(cursor);
        });
    }

    public void getAllAccounts(Consumer<Cursor> callback){
        executor.execute(() -> {
            Cursor cursor = db.getAllAccounts();
            callback.accept(cursor);
        });
    }

    public void getAllParties(Consumer<Cursor> callback){
        executor.execute(() -> {
            Cursor cursor = db.getAllParties();
            callback.accept(cursor);
        });
    }

    public void getAllGoals(Consumer<Cursor> callback){
        executor.execute(() -> {
            Cursor cursor = db.getAllGoals();
            callback.accept(cursor);
        });
    }

    public void getSevenDayExpenses(ArrayList<BarEntry> entries, ArrayList<String> dates, double[] min, double[] max, Runnable callback){
        executor.execute(() -> {
            db.getSevenDayExpenses(entries, dates, min, max);
            callback.run();
        });
    }

    public void getSevenDaysCategoriesAmounts(CategoryEntries categoryEntries, Runnable callback){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.getSevenDaysCategoriesAmounts(categoryEntries);
                callback.run();
            }
        });

    }

    public void getAllDatesCategoriesAmounts(Map<String, CategoryEntries> dateCategoryEntriesMap){
        executor.execute(() -> {
            db.getAllDatesCategoriesAmounts(dateCategoryEntriesMap);
        });
    }

}
