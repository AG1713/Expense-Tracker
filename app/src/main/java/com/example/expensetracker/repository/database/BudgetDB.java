package com.example.expensetracker.repository.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.expensetracker.repository.displayEntities.CategoryDisplay;
import com.example.expensetracker.repository.displayEntities.CategoryEntries;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BudgetDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BudgetDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TAG = "BudgetDB";

    // Specify formats for date and time
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String ACCOUNTS_ID = "id";
    public static final String ACCOUNTS_ACCOUNT_NO = "account_no";

    public static final String TABLE_CATEGORIES = "categories";
    public static final String CATEGORIES_ID = "id";
    public static final String CATEGORIES_NAME = "name";
    public static final String CATEGORIES_PARENT_ID = "parent_id";

    public static final String TABLE_PARTY = "parties";
    public static final String PARTIES_ID = "id";
    public static final String PARTIES_NAME = "name";
    public static final String PARTIES_NICKNAME = "nickname";

    public static final String TABLE_RECORDS = "records";
    public static final String RECORDS_ID = "id";
    public static final String RECORDS_ACCOUNT_ID = "account_id";
    public static final String RECORDS_DATE = "date"; // "YYYY-MM-DD"
    public static final String RECORDS_TIME = "time"; // "HH:MM:SS"
    public static final String RECORDS_OPERATION = "operation";
    public static final String RECORDS_AMOUNT = "amount";
    public static final String RECORDS_PARTY_ID = "party_id";
    public static final String RECORDS_DESCRIPTION = "description";
    public static final String RECORDS_CATEGORY_ID = "category_id";

    public static final String TABLE_MAPPINGS = "mappings";
    public static final String MAPPINGS_ID = "id";
    public static final String MAPPINGS_PARTY_ID = "party_id";
    public static final String MAPPINGS_AMOUNT = "amount";
    public static final String MAPPINGS_CATEGORY_ID = "category_id";

    public static final String TABLE_GOALS = "goals";
    public static final String GOALS_ID = "id";
    public static final String GOALS_NAME = "name";
    public static final String GOALS_CATEGORY_ID = "category_id";
    public static final String GOALS_AMOUNT = "amount";
    public static final String GOALS_EXPENSE = "expense";
    public static final String GOALS_START_DATE = "start_date";
    public static final String GOALS_END_DATE = "end_date";
    public static final String GOALS_STATUS = "status";


    public BudgetDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry1 = "CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                ACCOUNTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ACCOUNTS_ACCOUNT_NO + " TEXT UNIQUE NOT NULL CHECK (LENGTH(" + ACCOUNTS_ACCOUNT_NO + ") = 5))";

        // TODO: Figure out if category name required any other string constraint
        String qry2 = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CATEGORIES_NAME + " TEXT NOT NULL UNIQUE CHECK (LENGTH(name) <= 20) CHECK (name NOT LIKE '%\n%')," +
                CATEGORIES_PARENT_ID + " INTEGER," +
                "FOREIGN KEY (" + CATEGORIES_PARENT_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + ") " +
                "ON UPDATE CASCADE ON DELETE SET NULL)";

        String qry3 = "CREATE TABLE " + TABLE_PARTY + " (" +
                PARTIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PARTIES_NAME + " TEXT NOT NULL CHECK (LENGTH(" + PARTIES_NAME + ") <= 50) CHECK (" + PARTIES_NAME + " NOT LIKE '%\n%')," +
                PARTIES_NICKNAME + " TEXT UNIQUE NOT NULL CHECK (LENGTH(" + PARTIES_NICKNAME + ") <= 50) CHECK (" + PARTIES_NICKNAME + " NOT LIKE '%\\n%'))";

        String qry4 = "CREATE TABLE " + TABLE_RECORDS + " (" +
                RECORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RECORDS_ACCOUNT_ID + " INTEGER," +
                RECORDS_DATE + " TEXT NOT NULL CHECK(" + RECORDS_DATE + " LIKE '____-__-__')," +
                RECORDS_TIME + " TEXT NOT NULL CHECK(" + RECORDS_TIME + " LIKE '__:__:__')," +
                RECORDS_OPERATION + " TEXT NOT NULL CHECK (operation IN ('credited', 'debited'))," +
                RECORDS_PARTY_ID + " INTEGER," +
                RECORDS_AMOUNT + " REAL NOT NULL CHECK (amount > 0 AND ROUND(amount,2) = amount)," +
                RECORDS_DESCRIPTION + " TEXT CHECK (LENGTH(description) <= 100) CHECK (description NOT LIKE '%\n%')," +
                RECORDS_CATEGORY_ID + " INTEGER," +
                "FOREIGN KEY (" + RECORDS_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + ACCOUNTS_ID + ") " +
                "ON UPDATE CASCADE ON DELETE SET NULL, " +
                "FOREIGN KEY (" + RECORDS_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + ") " +
                "ON UPDATE CASCADE ON DELETE SET NULL, " +
                "FOREIGN KEY (" + RECORDS_PARTY_ID + ") REFERENCES " + TABLE_PARTY + "(" + PARTIES_ID + ") " +
                "ON UPDATE CASCADE ON DELETE SET NULL )";

        String qry5 = "CREATE TABLE " + TABLE_MAPPINGS + "(" +
                MAPPINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MAPPINGS_PARTY_ID + " INTEGER," +
                MAPPINGS_AMOUNT + " REAL," +
                MAPPINGS_CATEGORY_ID + " INTEGER," +
                "FOREIGN KEY (" + MAPPINGS_PARTY_ID + ") REFERENCES " + TABLE_PARTY + "(" + PARTIES_ID + ") " +
                "ON UPDATE CASCADE ON DELETE CASCADE," +
                "FOREIGN KEY (" + MAPPINGS_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + ") " +
                "ON UPDATE CASCADE ON DELETE CASCADE, " +
                "CONSTRAINT unique_mapping UNIQUE (" + MAPPINGS_PARTY_ID + ", " + MAPPINGS_AMOUNT + "))";

        String qry6 = "CREATE TABLE " + TABLE_GOALS + "(" +
                GOALS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GOALS_NAME + " TEXT NOT NULL UNIQUE," +
                GOALS_CATEGORY_ID + " INTEGER, " +
                GOALS_AMOUNT + " REAL NOT NULL CHECK (" + GOALS_AMOUNT + " > 0 AND ROUND( " + GOALS_AMOUNT + ",2) = " + GOALS_AMOUNT + ")," +
                GOALS_EXPENSE + " REAL NOT NULL CHECK (" + GOALS_EXPENSE + " >= 0 AND ROUND( " + GOALS_EXPENSE + ",2) = " + GOALS_EXPENSE + ")," +
                GOALS_START_DATE + " TEXT NOT NULL CHECK(" + GOALS_START_DATE + " LIKE '____-__-__')," +
                GOALS_END_DATE +  " TEXT NOT NULL CHECK(" + GOALS_END_DATE + " LIKE '____-__-__')," +
                GOALS_STATUS + " TEXT NOT NULL CHECK(" + GOALS_STATUS + " IN ('active', 'completed', 'failed', 'upcoming')), " +
                "FOREIGN KEY (" + GOALS_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + ") " +
                "ON UPDATE CASCADE ON DELETE CASCADE )";

        try {
            db.execSQL(qry1);
            db.execSQL(qry2);
            db.execSQL(qry3);
            db.execSQL(qry4);
            db.execSQL(qry5);
            db.execSQL(qry6);


        }
        catch (SQLiteException e){
            Log.d(TAG, "onCreate: " + e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);

        onCreate(db);
    }

    public long insertAccount(Account account) throws SQLiteException{
        ContentValues values = new ContentValues();
        values.put(ACCOUNTS_ACCOUNT_NO, account.getAccount_no());
        long result = -1;

        SQLiteDatabase db = this.getWritableDatabase();
        result = db.insertOrThrow(TABLE_ACCOUNTS, null, values);
        if (result == -1) Log.d(TAG, "insertAccount: " + result + " a.k.a insertion failed");
        db.close();

        return result;
    }

    public long insertCategory(Category category) throws SQLiteException{
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_NAME, category.getName());
        values.put(CATEGORIES_PARENT_ID, category.getParent_id());
        long result = -1;

        SQLiteDatabase db = getWritableDatabase();
        result = db.insertOrThrow(TABLE_CATEGORIES, null, values);
        if (result == -1) Log.d(TAG, "insertCategory: " + result + " a.k.a insertion failed");
        db.close();

        return result;
    }

    public long insertParty(Party party) throws SQLiteException{
        ContentValues values = new ContentValues();
        values.put(PARTIES_NAME, party.getName());
        values.put(PARTIES_NICKNAME, party.getNickname());
        long result = -1;

        try {
            SQLiteDatabase db = getWritableDatabase();
            result = db.insertOrThrow(TABLE_PARTY, null, values);
            if (result == -1) Log.d(TAG, "insertParty: " + result + " a.k.a insertion failed");
            db.close();
        }
        catch (SQLiteException e){
            throw new SQLiteException("Insertion failed. Duplicate row or constraint issue.");
        }

        return result;
    }

    public void insertRecord(Record record) throws SQLiteException{
        ContentValues values = new ContentValues();
        values.put(RECORDS_ACCOUNT_ID, record.getAccount_id());
        values.put(RECORDS_DATE, record.getDate());
        values.put(RECORDS_TIME, record.getTime());
        values.put(RECORDS_OPERATION, record.getOperation());
        values.put(RECORDS_AMOUNT, record.getAmount());
        values.put(RECORDS_PARTY_ID, record.getParty());
        values.put(RECORDS_DESCRIPTION, record.getDescription());
        values.put(RECORDS_CATEGORY_ID, record.getCategory_id());

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "insertRecord: " + record.getCategory_id());
        long result = db.insertOrThrow(TABLE_RECORDS, null, values);
        if (result == -1) Log.d(TAG, "insertRecord: insertion = " + result + " a.k.a insertion failed");
        else {
            if (record.getOperation().equals("debited")) {
                addExpenseOnGoal(record.getCategory_id(), record.getAmount());
                reviseGoalsStatus();
            }
        }
        db.close();
    }

    public void insertGoal(Goal goal) throws SQLiteException{
        ContentValues values = new ContentValues();
        values.put(GOALS_NAME, goal.getName());
        values.put(GOALS_CATEGORY_ID, goal.getCategory_id());
        values.put(GOALS_AMOUNT, goal.getAmount());
        values.put(GOALS_EXPENSE, goal.getExpense());
        values.put(GOALS_START_DATE, goal.getStart_date());
        values.put(GOALS_END_DATE, goal.getEnd_date());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date currentDate = dateFormat.parse(dateFormat.format(new Date()));
            Date end = sdf.parse(goal.getEnd_date());
            Date start = sdf.parse(goal.getStart_date());

            if (goal.getAmount() < goal.getExpense()) values.put(GOALS_STATUS, "failed");
            else if (currentDate.before(start)) values.put(GOALS_STATUS, "upcoming");
            else if (currentDate.after(end)) values.put(GOALS_STATUS, "completed");
            else values.put(GOALS_STATUS, "active");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insertOrThrow(TABLE_GOALS, null, values);
        if (result == -1) Log.d(TAG, "insertGoal: insertion = " + result + " a.k.a insertion failed");
        db.close();
    }


    public void deleteCategory(long id){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            db.execSQL("UPDATE " + TABLE_CATEGORIES + " SET " + CATEGORIES_PARENT_ID + " = " +
                    "(SELECT " + CATEGORIES_PARENT_ID + " FROM " + TABLE_CATEGORIES +
                    " WHERE " + CATEGORIES_ID + " = " + id + ") " +
                    "WHERE " + CATEGORIES_PARENT_ID + " = " + id);
            db.delete(TABLE_CATEGORIES, CATEGORIES_ID + " = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "deleteCategory: " + e.getMessage());
        }
    }

    public void deleteAccount(long id){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_ACCOUNTS, PARTIES_ID + " = ?", new String[]{String.valueOf(id)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    public void deleteParty(long id){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            db.delete(TABLE_PARTY, PARTIES_ID + " = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "deleteParty: " + e.getMessage());
        }
    }

    public void deleteRecord(long id){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            db.delete(TABLE_RECORDS, RECORDS_ID + " = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "deleteRecord: " + e.getMessage());
        }
    }

    public void deleteRecord(long id, Long category_id, double amount){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            db.delete(TABLE_RECORDS, RECORDS_ID + " = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            subtractExpenseOnGoal(category_id, amount);
        }
        catch (SQLiteException e){
            Log.d(TAG, "deleteRecord: " + e.getMessage());
        }
    }

    public void deleteGoal(long id) throws SQLiteException{
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_GOALS, GOALS_ID + " = ?", new String[]{String.valueOf(id)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void updateCategory(Category category){
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_NAME, category.getName());
        values.put(CATEGORIES_PARENT_ID, category.getParent_id());

        try{
            SQLiteDatabase db = getWritableDatabase();
            long result = db.update(TABLE_CATEGORIES, values,
                    CATEGORIES_ID + " = ?",
                    new String[]{String.valueOf(category.getId())});
            if (result == -1) Log.d(TAG, "updateCategory: " + result + " a.k.a update failed");
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "updateCategory: " + e.getMessage());
        }
    }

    public void updateParty(Party party){
        ContentValues values = new ContentValues();
        values.put(PARTIES_NAME, party.getName());
        values.put(PARTIES_NICKNAME, party.getNickname());

        try {
            SQLiteDatabase db = getWritableDatabase();
            long result = db.update(TABLE_PARTY, values,
                    PARTIES_ID + " = ?",
                    new String[]{String.valueOf(party.getId())});
            if (result == -1) Log.d(TAG, "updateParty: " + result + " a.k.a update failed");
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "updateParty: " + e.getMessage());
        }
    }

    public void updateRecord(Record record){
        ContentValues values = new ContentValues();
        values.put(RECORDS_ACCOUNT_ID, record.getAccount_id());
        values.put(RECORDS_DATE, record.getDate());
        values.put(RECORDS_TIME, record.getTime());
        values.put(RECORDS_OPERATION, record.getOperation());
        values.put(RECORDS_AMOUNT, record.getAmount());
        values.put(RECORDS_PARTY_ID, record.getParty());
        values.put(RECORDS_DESCRIPTION, record.getDescription());
        values.put(RECORDS_CATEGORY_ID, record.getCategory_id());

        try {
            SQLiteDatabase db = getWritableDatabase();
            long result = db.update(TABLE_RECORDS, values,
                    RECORDS_ID + " = ?",
                    new String[]{String.valueOf(record.getId())});
            if (result == -1) Log.d(TAG, "updateRecord: " + result + " a.k.a update failed");

            Cursor cursor1 = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECORDS + " WHERE " +
                    RECORDS_PARTY_ID + " = ? AND " + RECORDS_AMOUNT + " = ?", new String[]{String.valueOf(record.getParty()), String.valueOf(record.getAmount())});
            Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_MAPPINGS +
                    " WHERE " + MAPPINGS_PARTY_ID + " = ? AND " + MAPPINGS_AMOUNT + " = ?",
                    new String[]{String.valueOf(record.getParty()), String.valueOf(record.getAmount())});

            if (cursor1 != null && cursor1.moveToFirst()){
                int recordCount = cursor1.getInt(0);

                if (cursor2 != null && cursor2.moveToFirst()){
                    if (recordCount >= 3) {
                        updateMapping(new Mapping(cursor2.getLong(0), cursor2.getLong(1), cursor2.getDouble(2), record.getCategory_id()));
                    }
                }
                else if (recordCount >= 3){
                    Log.d(TAG, "recordCount >= 3");
                    insertMapping(new Mapping(record.getParty(), record.getAmount(), record.getCategory_id()));
                }
            }

            if (cursor2 != null) cursor2.close();
            if (cursor1 != null)cursor1.close();

            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "updateRecord: " + e.getMessage());
        }
    }

    public void updateRecord(Record record, Long old_category_id, double old_goal){
        ContentValues values = new ContentValues();
        values.put(RECORDS_ACCOUNT_ID, record.getAccount_id());
        values.put(RECORDS_DATE, record.getDate());
        values.put(RECORDS_TIME, record.getTime());
        values.put(RECORDS_OPERATION, record.getOperation());
        values.put(RECORDS_AMOUNT, record.getAmount());
        values.put(RECORDS_PARTY_ID, record.getParty());
        values.put(RECORDS_DESCRIPTION, record.getDescription());
        values.put(RECORDS_CATEGORY_ID, record.getCategory_id());

        try {
            SQLiteDatabase db = getWritableDatabase();
            long result = db.update(TABLE_RECORDS, values,
                    RECORDS_ID + " = ?",
                    new String[]{String.valueOf(record.getId())});
            if (result == -1) Log.d(TAG, "updateRecord: " + result + " a.k.a update failed");

            Cursor cursor1 = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECORDS + " WHERE " +
                    RECORDS_PARTY_ID + " = ? AND " + RECORDS_AMOUNT + " = ?", new String[]{String.valueOf(record.getParty()), String.valueOf(record.getAmount())});
            Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_MAPPINGS +
                            " WHERE " + MAPPINGS_PARTY_ID + " = ? AND " + MAPPINGS_AMOUNT + " = ?",
                    new String[]{String.valueOf(record.getParty()), String.valueOf(record.getAmount())});

            if (cursor1 != null && cursor1.moveToFirst()){
                int recordCount = cursor1.getInt(0);

                if (cursor2 != null && cursor2.moveToFirst()){
                    if (recordCount >= 3) {
                        updateMapping(new Mapping(cursor2.getLong(0), cursor2.getLong(1), cursor2.getDouble(2), record.getCategory_id()));
                    }
                }
                else if (recordCount >= 3){
                    Log.d(TAG, "recordCount >= 3");
                    insertMapping(new Mapping(record.getParty(), record.getAmount(), record.getCategory_id()));
                }
            }

            if (cursor2 != null) cursor2.close();
            if (cursor1 != null)cursor1.close();

            db.close();

            if (old_category_id != record.getCategory_id()) {
                subtractExpenseOnGoal(old_category_id, old_goal);
                addExpenseOnGoal(record.getCategory_id(), record.getAmount());
                reviseGoalsStatus();
            }
        }
        catch (SQLiteException e){
            Log.d(TAG, "updateRecord: " + e.getMessage());
        }
    }

    public long getPartyId(String name){
        long result = -1;
        try {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + PARTIES_ID + " FROM " + TABLE_PARTY + " WHERE " + PARTIES_NAME + " = ?", new String[]{name});
            if (cursor != null && cursor.moveToFirst()){
                int id = cursor.getColumnIndex(PARTIES_ID);
                if (id != -1) result = cursor.getLong(id);

            }
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "getPartyId: " + e.getMessage());
        }

        return result;
    }

    long searchAccount(String accountNo){
        long result = -1;
        try{
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + ACCOUNTS_ACCOUNT_NO + " = ?", new String[]{accountNo});
            if (cursor.moveToFirst()) result = cursor.getLong(0);
            cursor.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "searchParty: " + e.getMessage());
        }

        return result;
    }

    public void updateGoal(Goal goal){
        ContentValues values = new ContentValues();
        values.put(GOALS_NAME, goal.getName());
        values.put(GOALS_CATEGORY_ID, goal.getCategory_id());
        values.put(GOALS_AMOUNT, goal.getAmount());
        values.put(GOALS_EXPENSE, goal.getExpense());
        values.put(GOALS_START_DATE, goal.getStart_date());
        values.put(GOALS_END_DATE, goal.getEnd_date());
        values.put(GOALS_STATUS, goal.getStatus());

        SQLiteDatabase db = getWritableDatabase();
        long result = db.update(TABLE_GOALS, values, GOALS_ID + " = ?", new String[]{String.valueOf(goal.getId())});
        if (result == -1) Log.d(TAG, "updateGoal: " + result + " a.k.a update failed");

        db.close();
    }

    public void addExpenseOnGoal(Long category_id, double amount){
        String recursive_query = "WITH RECURSIVE category_hierarchy AS (" +
                "SELECT " + CATEGORIES_PARENT_ID + " " +
                "FROM " + TABLE_CATEGORIES + " " +
                "WHERE " + CATEGORIES_ID + " = " + category_id + " " +
                "UNION ALL " +
                "SELECT c." + CATEGORIES_PARENT_ID + " " +
                "FROM " + TABLE_CATEGORIES + " c " +
                "INNER JOIN category_hierarchy ch ON c." + CATEGORIES_ID + " = ch." + CATEGORIES_PARENT_ID + ") ";


        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(recursive_query +
                " " +
                "UPDATE " + TABLE_GOALS + " " +
                "SET " + GOALS_EXPENSE + " = " + GOALS_EXPENSE + " + " + amount + " " +
                "WHERE (" + GOALS_CATEGORY_ID + " IN category_hierarchy OR " +
                GOALS_CATEGORY_ID + " = " + category_id + " OR " +
                GOALS_CATEGORY_ID + " IS NULL) AND " +
                GOALS_STATUS + " = 'active'");

        db.close();
    }

    public void subtractExpenseOnGoal(long category_id, double amount){
        String recursive_query = "WITH RECURSIVE category_hierarchy AS (" +
                "SELECT " + CATEGORIES_PARENT_ID + " " +
                "FROM " + TABLE_CATEGORIES + " " +
                "WHERE " + CATEGORIES_ID + " = " + category_id + " " +
                "UNION ALL " +
                "SELECT c." + CATEGORIES_PARENT_ID + " " +
                "FROM " + TABLE_CATEGORIES + " c " +
                "INNER JOIN category_hierarchy ch ON c." + CATEGORIES_ID + " = ch." + CATEGORIES_PARENT_ID + ") ";


        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(recursive_query +
                " " +
                "UPDATE " + TABLE_GOALS + " " +
                "SET " + GOALS_EXPENSE + " = " + GOALS_EXPENSE + " - " + amount + " " +
                "WHERE (" + GOALS_CATEGORY_ID + " IN category_hierarchy OR " +
                GOALS_CATEGORY_ID + " = " + category_id + " OR " +
                GOALS_CATEGORY_ID + " IS NULL) AND " +
                GOALS_STATUS + " = 'active'");

        db.close();
    }

    public void reviseGoalsStatus(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_GOALS + " " +
                "SET " + GOALS_STATUS + " = 'failed' " +
                "WHERE " + GOALS_STATUS + " = 'active' AND " +
                GOALS_AMOUNT + " < " + GOALS_EXPENSE);
    }

    public void addTransaction(Record record, String partyName, String accountNo){
        long temp = getPartyId(partyName);
        record.setParty((temp == -1) ? insertParty(new Party(partyName, partyName)) : temp);
        Log.d(TAG, "addTransaction: " + temp);

        if (searchAccount(accountNo) == -1) record.setAccount_id(insertAccount(new Account(accountNo)));
        Long id = getCategoryUsingMapping(record.getParty(), record.getAmount());
        Log.d(TAG, "addTransaction: " + searchAccount(accountNo));
        Log.d(TAG, "addTransaction: " + id);

        if (record.getCategory_id() == null) record.setCategory_id(id);

        insertRecord(record);
    }

    public void insertMapping(Mapping mapping) throws SQLiteException {
        ContentValues values = new ContentValues();
        values.put(MAPPINGS_PARTY_ID, mapping.getParty_id());
        values.put(MAPPINGS_AMOUNT, mapping.getAmount());
        values.put(MAPPINGS_CATEGORY_ID, mapping.getCategory_id());

        SQLiteDatabase db = getWritableDatabase();

        long result = db.insertOrThrow(TABLE_MAPPINGS, null, values);
        Log.d(TAG, "addMapping: " + result);

        if (result == -1) throw new SQLiteException("Insertion failed. Duplicate row or constraint issue.");
        db.close();
    }

    public void deleteMapping(long id){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_MAPPINGS, MAPPINGS_ID + " = ?", new String[]{String.valueOf(id)});
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "deleteMapping: " + e.getMessage());
        }
    }

    public void updateMapping(Mapping mapping){
        ContentValues values = new ContentValues();
        values.put(MAPPINGS_PARTY_ID, mapping.getParty_id());
        values.put(MAPPINGS_AMOUNT, mapping.getAmount());
        values.put(MAPPINGS_CATEGORY_ID, mapping.getCategory_id());

        try {
            SQLiteDatabase db = getWritableDatabase();
            db.update(TABLE_MAPPINGS, values, MAPPINGS_ID + " = ?", new String[]{String.valueOf(mapping.getId())});
        }
        catch (SQLiteException e){
            Log.d(TAG, "updateMapping: " + e.getMessage());
        }
        Log.d(TAG, "updateMapping: Mappings updated");
    }

    public Cursor getAllRecords(){
        Cursor cursor = null;
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Log.d(TAG, "getAllRecords is open: " + db.isOpen());
            cursor = db.rawQuery("SELECT " +
                            TABLE_RECORDS + ".id AS _id, " + ACCOUNTS_ACCOUNT_NO + ", date, time, operation, " + TABLE_PARTY + "." + PARTIES_NICKNAME + ", amount, description, " + TABLE_CATEGORIES + "." + CATEGORIES_NAME + ", " + RECORDS_CATEGORY_ID + " " +
                            "FROM " + TABLE_RECORDS + " " +
                            "LEFT JOIN " + TABLE_CATEGORIES + " " +
                            "ON " + TABLE_RECORDS + "." + RECORDS_CATEGORY_ID + " = " + TABLE_CATEGORIES + "." + CATEGORIES_ID + " " +
                            "LEFT JOIN " + TABLE_PARTY + " " +
                            "ON " + TABLE_RECORDS + "." + RECORDS_PARTY_ID + " = " + TABLE_PARTY + "." + PARTIES_ID + " " +
                            "LEFT JOIN " + TABLE_ACCOUNTS + " " +
                            "ON " + TABLE_RECORDS + "." + RECORDS_ACCOUNT_ID + " = " + TABLE_ACCOUNTS + "." + ACCOUNTS_ID + " " +
                            " ORDER BY date DESC, time DESC",
                    null);
        }
        catch (SQLiteException e){
            Log.d(TAG, "getAllRecords: " + e.getMessage());
        }
        return cursor;
    }


    public Cursor getAllMappings(){
        Cursor cursor = null;

        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + TABLE_MAPPINGS + "." + MAPPINGS_ID + " AS _id, " + TABLE_PARTY + "." + PARTIES_NICKNAME + "," +
                MAPPINGS_AMOUNT + "," + TABLE_CATEGORIES + "." + CATEGORIES_NAME + " " +
                "FROM " + TABLE_MAPPINGS + " " +
                "LEFT JOIN " + TABLE_CATEGORIES + " " +
                "ON " + TABLE_MAPPINGS + "." + MAPPINGS_CATEGORY_ID + " = " + TABLE_CATEGORIES + "." + CATEGORIES_ID + " " +
                "LEFT JOIN " + TABLE_PARTY + " " +
                "ON " + TABLE_MAPPINGS + "." + MAPPINGS_PARTY_ID + " = " + TABLE_PARTY + "." + PARTIES_ID,
                null);

        return cursor;
    }

    public Long getCategoryUsingMapping(long partyId, double amount){
        try {
            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT " + MAPPINGS_CATEGORY_ID + " FROM " + TABLE_MAPPINGS +
                    " WHERE (" + MAPPINGS_PARTY_ID + " = ? OR " + MAPPINGS_AMOUNT + " IS NULL) " +
                    "AND (" + MAPPINGS_PARTY_ID + " IS NULL OR " + MAPPINGS_AMOUNT + " = ?) " +
                    "ORDER BY " +
                    "CASE " +
                    "WHEN " + MAPPINGS_PARTY_ID + " IS NOT NULL AND " + MAPPINGS_AMOUNT + " IS NOT NULL THEN 1 " +
                    "WHEN " + MAPPINGS_PARTY_ID + " IS NOT NULL THEN 2 " +
                    "WHEN " + MAPPINGS_AMOUNT + " IS NOT NULL THEN 3 " +
                    "ELSE 4 " +
                    "END " +
                    "LIMIT 1", new String[]{String.valueOf(partyId), String.valueOf(amount)});

            if (cursor.moveToFirst()) {
                Log.d(TAG, "getCategoryUsingMapping: Cursor has no values");
                return cursor.getLong(0);
            }

            cursor.close();
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "getCategoryUsingMapping: " + e.getMessage());
        }

        return null;
    }

    public Cursor getAllPartiesWithAmounts(){
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getReadableDatabase();

            cursor = db.rawQuery(
                    "SELECT " + TABLE_PARTY + "." + PARTIES_ID + " AS _id," + PARTIES_NAME + "," + PARTIES_NICKNAME + "," +
                            "SUM(" + RECORDS_AMOUNT + ") AS Total " +
                            "FROM " + TABLE_PARTY + " " +
                            "LEFT JOIN " + TABLE_RECORDS + " " +
                            "ON " + TABLE_PARTY + "." + PARTIES_ID + " = " + TABLE_RECORDS + "." + RECORDS_PARTY_ID + " " +
                            "GROUP BY " + TABLE_PARTY + "." + PARTIES_ID,
                    null
            );
            Log.d(TAG, "getAllPartiesWithAmounts: " + cursor.getCount());
        }
        catch (SQLiteException e){
            Log.d(TAG, "getAllPartiesWithAmounts: " + e.getMessage());
        }

        return cursor;
    }

    public Cursor getAllAccountsWithAmounts(){
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getReadableDatabase();

            cursor = db.rawQuery(
                    "SELECT " + TABLE_ACCOUNTS + "." + ACCOUNTS_ID + " AS _id, " + ACCOUNTS_ACCOUNT_NO + "," +
                            "SUM(" + RECORDS_AMOUNT + ") As Total " +
                            "FROM " + TABLE_ACCOUNTS + " " +
                            "LEFT JOIN " + TABLE_RECORDS + " " +
                            "ON " + TABLE_ACCOUNTS + "." + ACCOUNTS_ID + " = " +
                            TABLE_RECORDS + "." + RECORDS_ACCOUNT_ID + " " +
                            "GROUP BY " + TABLE_ACCOUNTS + "." + ACCOUNTS_ID,
                    null
            );
            Log.d(TAG, "getAllAccountsWithAmounts: " + cursor.getCount());
        }
        catch (SQLiteException e){
            Log.d(TAG, "getAllAccountsWithAmounts: " + e.getMessage());
        }

        return cursor;
    }

    public ArrayList<CategoryDisplay> getCategoriesInDFS(){
        Cursor cursor = null;
        ArrayList<Category> categories = new ArrayList<>();
        Map<Long, ArrayList<Category>> categoryMap = new HashMap<>();
        ArrayList<CategoryDisplay> categoryDisplays = new ArrayList<>();

        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_CATEGORIES + " " +
                            "ORDER BY " + CATEGORIES_PARENT_ID + " ASC",
                    null
            );

            Log.d(TAG, "Categories count: " + cursor.getCount());

            if (cursor.moveToFirst()){
                do {
                    Long parent_id = (cursor.getLong(2) == 0) ? null : cursor.getLong(2);
                    Category category = new Category(cursor.getString(1), parent_id);
                    category.setId(cursor.getLong(0));
                    categoryMap.putIfAbsent(parent_id, new ArrayList<>());
                    categoryMap.get(category.getParent_id()).add(category);
                }while(cursor.moveToNext());
            }

            Log.d(TAG, "categoryMap size: " + categoryMap.size());

            cursor.close();
            db.close();
        }
        catch (SQLiteException e){
            Log.d(TAG, "getCategoriesInDFS: " + e.getMessage());
        }

        getDFS(categoryMap, categoryDisplays, null, 0, 1);

        for (CategoryDisplay i : categoryDisplays){
            Log.d(TAG, i.getCategory().getId() + " " + i.getCategory().getName() + " " + i.getCategory().getParent_id() + " " + i.getLevel());
        }

        return categoryDisplays;
    }

    private void getDFS (Map<Long, ArrayList<Category>> categoryMap, ArrayList<CategoryDisplay> categoryDisplays, Long parent_id, double amount, int level){
        if (!categoryMap.containsKey(parent_id)) {
            return;
        }

        for (Category category : categoryMap.get(parent_id)){
            categoryDisplays.add(new CategoryDisplay(category, level));
            getDFS(categoryMap, categoryDisplays, category.getId(), 0, level+1);
        }

    }

    public Cursor getAllCategories() throws SQLiteException{
        Cursor cursor = null;

        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + CATEGORIES_ID + " AS _id, " +
                CATEGORIES_NAME + " " +
                "FROM " + TABLE_CATEGORIES, null);

        return cursor;
    }

    public Cursor getAllAccounts() throws SQLiteException {
        Cursor cursor = null;

        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + ACCOUNTS_ID + " AS _id," +
                ACCOUNTS_ACCOUNT_NO + " " +
                "FROM " + TABLE_ACCOUNTS, null);

        return cursor;
    }

    public Cursor getAllParties() throws SQLiteException {
        Cursor cursor = null;

        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + PARTIES_ID + " AS _id," +
                PARTIES_NICKNAME +
                " FROM " + TABLE_PARTY, null);

        return cursor;
    }

    public Cursor getAllGoals(){
        Cursor cursor = null;

        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " +
                TABLE_GOALS + "." + GOALS_ID + " AS _id," +
                TABLE_GOALS + "." + GOALS_NAME + "," +
                TABLE_CATEGORIES + "." + CATEGORIES_NAME + " AS category_name," +
                GOALS_AMOUNT + "," +
                GOALS_EXPENSE + "," +
                GOALS_START_DATE + "," +
                GOALS_END_DATE  + "," +
                GOALS_STATUS + " " +
                "FROM " + TABLE_GOALS + " " +
                "LEFT JOIN " + TABLE_CATEGORIES + " " +
                "ON " + TABLE_CATEGORIES + "." + CATEGORIES_ID + " = " + TABLE_GOALS + "." + GOALS_CATEGORY_ID + " " +
                "ORDER BY " +
                "CASE WHEN " + GOALS_STATUS + " = 'active' THEN 0 ELSE 1 END, " +
                GOALS_END_DATE + " DESC", null);

        Log.d(TAG, "getAllGoals: " + cursor.getCount());

        return cursor;
    }

    public void getSevenDayExpenses(ArrayList<BarEntry> data, ArrayList<String> dates){
        Cursor cursor = null;
        int i=0;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());

        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + RECORDS_DATE + ", " +
                "SUM(CASE " +
                "WHEN " + RECORDS_OPERATION + " = 'credited' THEN " + RECORDS_AMOUNT + " " +
                "WHEN " + RECORDS_OPERATION + " = 'debited' THEN -" + RECORDS_AMOUNT + " " +
                "ELSE 0 " +
                "END) AS Total " +
                "FROM " + TABLE_RECORDS + " " +
                "WHERE " + RECORDS_DATE + " > DATE('now', '-7 days') AND " + RECORDS_DATE + " <= " + "DATE('now')" +
                "GROUP BY " + RECORDS_DATE + " " +
                "ORDER BY " + RECORDS_DATE + " ASC " +
                "LIMIT 7", null);

        if (!cursor.moveToFirst()) return;

        do {
            try {
                dates.add(outputFormat.format(inputFormat.parse(cursor.getString(0))));
            } catch (ParseException e) {
                Log.d(TAG, "getSevenDayExpenses: EXCEPTION");
                throw new RuntimeException(e);
            }
            data.add(new BarEntry(i, (float) cursor.getDouble(1)));
            i++;
        } while (cursor.moveToNext());

        cursor.close();
        db.close();
    }

    public void getSevenDaysCategoriesAmounts(CategoryEntries categoryEntries){
        Cursor cursor = null;
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + CATEGORIES_NAME + ", " +
                "SUM(CASE " +
                "WHEN " + RECORDS_OPERATION + " = 'credited' THEN " + RECORDS_AMOUNT + " " +
                "WHEN " + RECORDS_OPERATION + " = 'debited' THEN -" + RECORDS_AMOUNT + " " +
                "ELSE 0 " +
                "END) AS Total " +
                "FROM " + TABLE_RECORDS + " " +
                "LEFT JOIN " + TABLE_CATEGORIES + " " +
                "ON " + TABLE_RECORDS + "." + RECORDS_CATEGORY_ID + " = " + TABLE_CATEGORIES + "." + CATEGORIES_ID + " " +
                "WHERE " + RECORDS_DATE + " > DATE('now', '-7 days') AND " + RECORDS_DATE + " <= " + "DATE('now')" +
                "GROUP BY " + CATEGORIES_NAME
                ,null);

        if (!cursor.moveToFirst()) return;
        int i=0;

        do {
            entries.add(new BarEntry(i, (float) cursor.getDouble(1)));
            i++;
            if (cursor.getString(0) == null) categories.add("Others");
            else categories.add(cursor.getString(0));
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        for (int j=0 ; j<entries.size() ; j++){
            Log.d(TAG, "getSevenDaysCategoriesAmounts: " + entries.get(j) + " " + categories.get(j));
        }

        categoryEntries.setEntries(entries);
        categoryEntries.setCategoryNames(categories);
    }

    public void getAllDatesCategoriesAmounts(Map<String, CategoryEntries> dateCategoryEntriesMap){
        dateCategoryEntriesMap.clear();
        Cursor cursor = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());

        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + RECORDS_DATE + "," + CATEGORIES_NAME + ", " +
                        "SUM(CASE " +
                        "WHEN " + RECORDS_OPERATION + " = 'credited' THEN " + RECORDS_AMOUNT + " " +
                        "WHEN " + RECORDS_OPERATION + " = 'debited' THEN -" + RECORDS_AMOUNT + " " +
                        "ELSE 0 " +
                "END) AS Total " +
                "FROM " + TABLE_RECORDS + " " +
                "LEFT JOIN " + TABLE_CATEGORIES + " " +
                "ON " + TABLE_RECORDS + "." + RECORDS_CATEGORY_ID + " = " + TABLE_CATEGORIES + "." + CATEGORIES_ID + " " +
                "WHERE " +
                RECORDS_DATE + " > DATE('now', '-7 days') AND " + RECORDS_DATE + " <= " + "DATE('now') " +
                "GROUP BY " + RECORDS_DATE + ", " + RECORDS_CATEGORY_ID + " " +
                "HAVING Total != 0 " +
                "ORDER BY " + RECORDS_DATE + " ASC"  , null);

        if (!cursor.moveToFirst()) return;

        String currentDate = cursor.getString(0);
        int i=0;
        ArrayList<BarEntry> currentEntries = new ArrayList<>();
        ArrayList<String> currentCategories = new ArrayList<>();
        do {
            if (!currentDate.equals(cursor.getString(0))){
                try {
                    currentDate = outputFormat.format(inputFormat.parse(currentDate));
                } catch (ParseException e) {
                    Log.d(TAG, "getSevenDayExpenses: EXCEPTION");
                    throw new RuntimeException(e);
                }
                dateCategoryEntriesMap.put(currentDate, new CategoryEntries(currentEntries, currentCategories));
                currentDate = cursor.getString(0);
                currentEntries = new ArrayList<>();
                currentCategories = new ArrayList<>();
                i=0;
            }
            currentEntries.add(new BarEntry(i, (float) cursor.getDouble(2)));
            i++;
            if (cursor.getString(1) == null) currentCategories.add("Others");
            else currentCategories.add(cursor.getString(1));

        } while (cursor.moveToNext());
        try {
            currentDate = outputFormat.format(inputFormat.parse(currentDate));
        } catch (ParseException e) {
            Log.d(TAG, "getSevenDayExpenses: EXCEPTION");
            throw new RuntimeException(e);
        }
        dateCategoryEntriesMap.put(currentDate, new CategoryEntries(currentEntries, currentCategories));

        cursor.close();
        db.close();
    }


    public void test(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECORDS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Log.d("DB_TEST", "Row: " + cursor.getString(0)); // Replace index with column indices
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

}
