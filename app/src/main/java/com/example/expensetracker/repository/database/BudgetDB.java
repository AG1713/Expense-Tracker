package com.example.expensetracker.repository.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BudgetDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BudgetDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TAG = "BudgetDB";

    // Specify formats for date and time
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String ACCOUNT_ACCOUNT = "account";

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
    public static final String RECORDS_ACCOUNT_NO = "account_no";
    public static final String RECORDS_DATE = "date"; // "YYYY-MM-DD"
    public static final String RECORDS_TIME = "time"; // "HH:MM:SS"
    public static final String RECORDS_OPERATION = "operation";
    public static final String RECORDS_AMOUNT = "amount";
    public static final String RECORDS_PARTY_ID = "party_id";
    public static final String RECORDS_DESCRIPTION = "description";
    public static final String RECORDS_CATEGORY_ID = "category_id";


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
        // TODO: Replace hardcoded column ad table names with variables
        String qry1 = "CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                ACCOUNT_ACCOUNT + " TEXT PRIMARY KEY CHECK (LENGTH(account) = 5))";

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
                PARTIES_NICKNAME + " TEXT NOT NULL CHECK (LENGTH(" + PARTIES_NICKNAME + ") <= 50) CHECK (" + PARTIES_NICKNAME + " NOT LIKE '%\\n%'))";

        String qry4 = "CREATE TABLE " + TABLE_RECORDS + " (" +
                RECORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RECORDS_ACCOUNT_NO + " TEXT," +
                RECORDS_DATE + " TEXT NOT NULL CHECK(" + RECORDS_DATE + " LIKE '____-__-__')," +
                RECORDS_TIME + " TEXT NOT NULL CHECK(" + RECORDS_TIME + " LIKE '__:__:__')," +
                RECORDS_OPERATION + " TEXT NOT NULL CHECK (operation IN ('credited', 'debited'))," +
                RECORDS_PARTY_ID + " INTEGER," +
                RECORDS_AMOUNT + " REAL NOT NULL CHECK (amount > 0 AND ROUND(amount,2) = amount)," +
                RECORDS_DESCRIPTION + " TEXT CHECK (LENGTH(description) <= 100) CHECK (description NOT LIKE '%\n%')," +
                RECORDS_CATEGORY_ID + " INTEGER," +
                "FOREIGN KEY (" + RECORDS_ACCOUNT_NO + ") REFERENCES " + TABLE_ACCOUNTS + "(" + ACCOUNT_ACCOUNT + ") " +
                "ON UPDATE CASCADE ON DELETE SET NULL, " +
                "FOREIGN KEY (" + RECORDS_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + ") " +
                "ON UPDATE CASCADE ON DELETE SET NULL, " +
                "FOREIGN KEY (" + RECORDS_PARTY_ID + ") REFERENCES " + TABLE_PARTY + "(" + PARTIES_ID + ") " +
                "ON UPDATE CASCADE ON DELETE SET NULL )";

        // TODO: Add triggers for date and time to validate them

        try {
            db.execSQL(qry1);
            db.execSQL(qry2);
            db.execSQL(qry3);
            db.execSQL(qry4);
        }
        catch (SQLException e){
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

    public void insertAccount(Account account){
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_ACCOUNT, account.getAccount());

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            long result = db.insert(TABLE_ACCOUNTS, null, values);
            if (result == -1) Log.d(TAG, "insertAccount: " + result + " a.k.a insertion failed");
            db.close();
        }
        catch (SQLException e){
            Log.d(TAG, "insertAccount: " + e.getMessage());
        }
    }

    public void insertCategory(Category category){
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_NAME, category.getName());
        values.put(CATEGORIES_PARENT_ID, category.getParent_id());

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            long result = db.insert(TABLE_CATEGORIES, null, values);
            if (result == -1) Log.d(TAG, "insertCategory: " + result + " a.k.a insertion failed");
            db.close();
        }
        catch (SQLException e){
            Log.d(TAG, "insertCategory: " + e.getMessage());
        }
    }

    public void insertParty(Party party){
        ContentValues values = new ContentValues();
        values.put(PARTIES_NAME, party.getName());
        values.put(PARTIES_NICKNAME, party.getNickname());

        try {
            SQLiteDatabase db = getWritableDatabase();
            long result = db.insert(TABLE_PARTY, null, values);
            if (result == -1) Log.d(TAG, "insertParty: " + result + " a.k.a insertion failed");
            db.close();
        }
        catch (SQLException e){
            Log.d(TAG, "insertParty: " + e.getMessage());
        }

    }

    public void insertRecord(Record record){
        ContentValues values = new ContentValues();
        values.put(RECORDS_ACCOUNT_NO, record.getAccount_no());
        values.put(RECORDS_DATE, record.getDate());
        values.put(RECORDS_TIME, record.getTime());
        values.put(RECORDS_OPERATION, record.getOperation());
        values.put(RECORDS_AMOUNT, record.getAmount());
        values.put(RECORDS_PARTY_ID, record.getParty());
        values.put(RECORDS_DESCRIPTION, record.getDescription());
        values.put(RECORDS_CATEGORY_ID, record.getCategory_id());

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            long result = db.insert(TABLE_RECORDS, null, values);
            if (result == -1) Log.d(TAG, "insertRecord: insertion = " + result + " a.k.a insertion failed");
            db.close();
        }
        catch (SQLException e){
            Log.d(TAG, "insertRecord: " + e.getMessage());
        }
    }

    public Cursor getAllRecords(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " +
                    TABLE_RECORDS + ".id AS _id, account_no, date, time, operation, " + TABLE_PARTY + "." + PARTIES_NICKNAME + ", amount, description, " + TABLE_CATEGORIES + "." + CATEGORIES_NAME + " " +
                    "FROM " + TABLE_RECORDS + " " +
                    "LEFT JOIN " + TABLE_CATEGORIES + " " +
                    "ON " + TABLE_RECORDS + "." + RECORDS_CATEGORY_ID + " = " + TABLE_CATEGORIES + "." + CATEGORIES_ID + " " +
                    "LEFT JOIN " + TABLE_PARTY + " " +
                    "ON " + TABLE_RECORDS + "." + RECORDS_PARTY_ID + " = " + TABLE_PARTY + "." + PARTIES_ID +
                    " ORDER BY date DESC",
                    null);
            return cursor;
        }
        catch (SQLException e){
            Log.d(TAG, "getAllRecords: " + e.getMessage());
        }
        return null;
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
        catch (SQLException e){
            Log.d(TAG, "deleteCategory: " + e.getMessage());
        }
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
        catch (SQLException e){
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
        catch (SQLException e){
            Log.d(TAG, "deleteRecord: " + e.getMessage());
        }
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
        catch (SQLException e){
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
        catch (SQLException e){
            Log.d(TAG, "updateParty: " + e.getMessage());
        }
    }

    public void updateRecord(Record record){
        ContentValues values = new ContentValues();
        values.put(RECORDS_ACCOUNT_NO, record.getAccount_no());
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
            db.close();
        }
        catch (SQLException e){
            Log.d(TAG, "updateRecord: " + e.getMessage());
        }
    }

    public long getPartyId(String name){
        try {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + PARTIES_ID + " FROM " + TABLE_PARTY + " WHERE " + PARTIES_NAME + " = ?", new String[]{name});
            if (cursor != null && cursor.moveToFirst()){
                int res = cursor.getColumnIndex(PARTIES_ID);
                if (res == -1) res = 0;
                long id = cursor.getLong(res);

                // The above check is not really necessary tho

                if (id >= 0) return id;

            }
            db.close();
        }
        catch (SQLException e){
            Log.d(TAG, "getPartyId: " + e.getMessage());
        }

        return 0;
    }


}
