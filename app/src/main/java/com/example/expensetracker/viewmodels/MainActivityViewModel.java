package com.example.expensetracker.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.repository.Repository;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.database.Category;
import com.example.expensetracker.repository.database.Goal;
import com.example.expensetracker.repository.displayEntities.CategoryDisplay;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.repository.database.Record;
import com.example.expensetracker.repository.displayEntities.CategoryEntries;
import com.example.expensetracker.repository.displayEntities.ChartData;
import com.example.expensetracker.repository.displayEntities.Filter;
import com.example.expensetracker.repository.displayEntities.LineChartData;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class MainActivityViewModel extends AndroidViewModel {
    private final Repository repository;
    private MutableLiveData<Filter> filterMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Cursor> records = new MutableLiveData<>();
    private MutableLiveData<LineChartData> recordsChartData = new MutableLiveData<>();
    private MutableLiveData<Cursor> parties = new MutableLiveData<>();
    private MutableLiveData<ChartData> partiesChartData = new MutableLiveData<>();
    private MutableLiveData<Cursor> accounts = new MutableLiveData<>();
    private MutableLiveData<ChartData> accountsChartData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<CategoryDisplay>> categories = new MutableLiveData<>();
    private MutableLiveData<ChartData> categoriesChartData = new MutableLiveData<>();
    private MutableLiveData<Cursor> goals = new MutableLiveData<>();

    private final String TAG = "MainActivityViewModel";

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();

        filterMutableLiveData.setValue(new Filter());

        ChartData categoriesData = new ChartData();
        repository.getAllCategoriesInDFS(new Filter(), categoriesData, categoryDisplays -> {
            categories.postValue(categoryDisplays);
            categoriesChartData.postValue(categoriesData);
        });

        LineChartData lineChartData = new LineChartData();
        repository.getAllRecords(lineChartData, new Filter(), cursor -> {
            records.postValue(cursor);
            recordsChartData.postValue(lineChartData);
        });

        ChartData partiesData = new ChartData();
        repository.getAllPartiesWithAmount(new Filter(), partiesData, cursor -> {
            parties.postValue(cursor);
            partiesChartData.postValue(partiesData);
        });

        ChartData accountsData = new ChartData();
        repository.getAllAccountsWithAmounts(new Filter(), accountsData, cursor -> {
            accounts.postValue(cursor);
            accountsChartData.postValue(accountsData);
        });
        repository.getAllGoals(cursor -> goals.postValue(cursor));

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

    public void removeCategory(long id, Runnable callback){
        repository.removeCategory(id, callback);
    }
    public void removeParty(long id, Runnable callback){
        repository.removeParty(id, callback);
    }

    public void removeAccount(long id, Runnable callback){
        repository.removeAccount(id, callback);
    }
    public void removeRecord(long id, Long category_id, double amount, Runnable callback){
        repository.removeRecord(id, category_id, amount, callback);
    }
    public void removeGoal(long id){
        repository.removeGoal(id);
    }
    public void updateParty(Party party, ErrorCallback callback){
        repository.updateParty(party, callback);
    }
    public void updateCategory(Category category, ErrorCallback callback){
        repository.updateCategory(category, callback);
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

    public void getAllRecords(Runnable callback){
        LineChartData lineChartData = new LineChartData();
        repository.getAllRecords(lineChartData, filterMutableLiveData.getValue(), cursor -> {
            records.postValue(cursor);
            recordsChartData.postValue(lineChartData);
            callback.run();
        });
    }
    public void getAllPartiesWithAmount(Runnable callback){
        ChartData partiesData = new ChartData();
        repository.getAllPartiesWithAmount(filterMutableLiveData.getValue(), partiesData, cursor -> {
            parties.postValue(cursor);
            partiesChartData.postValue(partiesData);
            callback.run();
        });
    }
    public void getAllCategoriesInDFS(Runnable callback){
        ChartData categoriesData = new ChartData();
        repository.getAllCategoriesInDFS(filterMutableLiveData.getValue(), categoriesData, categoryDisplays -> {
            Log.d(TAG, "getAllCategoriesInDFS: " + categoryDisplays.size());
            categories.postValue(categoryDisplays);
            categoriesChartData.postValue(categoriesData);
            callback.run();
        });
    }
    public void getAllAccountsWithAmount(Runnable callback){
        ChartData accountsData = new ChartData();
        repository.getAllAccountsWithAmounts(filterMutableLiveData.getValue(), accountsData, cursor -> {
            accounts.postValue(cursor);
            accountsChartData.postValue(accountsData);
            callback.run();
        });
    }

    public void getAllCategories(Consumer<Cursor> callback){ repository.getAllCategories(callback);}
    public void getAllAccounts(Consumer<Cursor> callback){repository.getAllAccounts(callback);}
    public void getAllParties(Consumer<Cursor> callback) {repository.getAllParties(callback);}
    public void getAllGoals(Runnable callback){
        repository.getAllGoals(cursor -> {
            goals.postValue(cursor);
            callback.run();
        });
    }
    public void getSevenDayExpense(ArrayList<BarEntry> entries, ArrayList<String> dates, double[] min, double[] max, Runnable callback){
        repository.getSevenDayExpenses(entries, dates, min, max, callback);
    }
    public void getAllDatesCategoriesAmounts(Map<String, CategoryEntries> dateCategoryEntriesMap){
        repository.getAllDatesCategoriesAmounts(dateCategoryEntriesMap);
    }
    public void getSevenDaysCategoriesAmounts(CategoryEntries categoryEntries, Runnable callback){
        repository.getSevenDaysCategoriesAmounts(categoryEntries, callback);
    }

    public MutableLiveData<Cursor> getRecords() {
        return records;
    }

    public MutableLiveData<LineChartData> getRecordsChartData() {
        return recordsChartData;
    }

    public void setRecordsChartData(MutableLiveData<LineChartData> recordsChartData) {
        this.recordsChartData = recordsChartData;
    }

    public MutableLiveData<ChartData> getPartiesChartData() {
        return partiesChartData;
    }

    public void setPartiesChartData(MutableLiveData<ChartData> partiesChartData) {
        this.partiesChartData = partiesChartData;
    }

    public MutableLiveData<ChartData> getAccountsChartData() {
        return accountsChartData;
    }

    public void setAccountsChartData(MutableLiveData<ChartData> accountsChartData) {
        this.accountsChartData = accountsChartData;
    }

    public MutableLiveData<Cursor> getParties() {
        return parties;
    }

    public MutableLiveData<Cursor> getAccounts() {
        return accounts;
    }

    public MutableLiveData<ArrayList<CategoryDisplay>> getCategories() {
        return categories;
    }

    public MutableLiveData<Cursor> getGoals() {
        return goals;
    }

    public MutableLiveData<ChartData> getCategoriesChartData() {
        return categoriesChartData;
    }

    public void setCategoriesChartData(MutableLiveData<ChartData> categoriesChartData) {
        this.categoriesChartData = categoriesChartData;
    }

    public MutableLiveData<Filter> getFilterMutableLiveData() {
        return filterMutableLiveData;
    }

    public void postToFilterMutableLiveData(Filter filter) {
        filterMutableLiveData.setValue(filter);
    }
}
