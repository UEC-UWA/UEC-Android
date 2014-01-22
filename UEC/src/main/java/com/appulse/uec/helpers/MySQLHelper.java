package com.appulse.uec.helpers;

/**
 * Created by Matt on 30/12/2013.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class MySQLHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NewsDB3";

    private String package_name = "com.appulse.app.model.";

    public MySQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_NEWS_TABLE = "CREATE TABLE News ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "summary TEXT, " +
                "content TEXT, " +
                "category TEXT, " +
                "date TIMESTAMP, " +
                "link TEXT" +
                ")";

        String CREATE_COMMITTEE_TABLE = "CREATE TABLE Committee ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT, "+
                "last_name TEXT, " +
                "photo_path TEXT, " +
                "position TEXT, " +
                "email TEXT, " +
                "subcommittee TEXT, " +
                "summary TEXT" +
                ")";

        // create books table

        db.execSQL(CREATE_COMMITTEE_TABLE);
        db.execSQL(CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS News");
        db.execSQL("DROP TABLE IF EXISTS Committee");
        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */


    public void addEntity(String entity, ManagedEntity e,String[] columns) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        int num_columns = columns.length;

        values.put(columns[0],e.getId());

        for (int i = 1; i < num_columns; i++) {
            Object value = e.getValue(columns[i]);
            String insertValue = null;
            if (value instanceof Integer) {
                insertValue = value.toString();
            } else {
                insertValue = (String) value;
            }
            values.put(columns[i], insertValue); // get title
        }


        db.replace(entity, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public ManagedEntity getEntity(String entity, int id, String[] columns) {
        ManagedEntity results = null;
        try {
            results  = (ManagedEntity) Class.forName(package_name + entity).newInstance();
        } catch (InstantiationException e) {
            return results;
        } catch (IllegalAccessException e) {
            return results;
        } catch (ClassNotFoundException e) {
            return results;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(entity, // a. table
                        columns, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        results.setId(Integer.parseInt(cursor.getString(0)));

        int num_columns = columns.length;

        for (int i = 1; i < num_columns; i++) {
            results.setValue(columns[i],cursor.getString(i));
        }

        return results;
    }

    private String arrayToCommaList(String[] list) {
        StringBuilder result = new StringBuilder();
        for(String string : list) {
            result.append(string);
            result.append(",");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1): "";
    }
    public List<Object> getAllForEntityWithSections(String entity, String[] columns, String section) {
        List<Object> list = new LinkedList<Object>();

        // 1. build the query
        String columnList = arrayToCommaList(columns);
        String query = "SELECT " +columnList + " FROM " + entity +  " ORDER BY " + section;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String previousSectionName = "";
        if (cursor.moveToFirst()) {
            do {
                ManagedEntity results = null;
                try {
                    results  = (ManagedEntity) Class.forName(package_name + entity).newInstance();
                    results.setId(Integer.parseInt(cursor.getString(0)));
                    int num_columns = columns.length;
                    // Log.e("MysqlHelper","Cursor " + cursor.get);
                    for (int i = 1; i < num_columns; i++) {
                        if (columns[i].equals("date")) {
                            String value = cursor.getString(i);

                            results.setValue(columns[i],Integer.valueOf(value));
                        } else if (columns[i].equals(section)) {
                            results.setValue(columns[i],cursor.getString(i));
                            if (!cursor.getString(i).equals(previousSectionName)) {
                                previousSectionName = cursor.getString(i);
                                SectionSeparator s = new SectionSeparator();
                                s.sectionName = previousSectionName;

                                list.add(s);
                            }
                        } else {
                            results.setValue(columns[i],cursor.getString(i));
                        }


                    }


                    // Add book to books
                    list.add(results);
                } catch (InstantiationException e) {
                    return null;
                } catch (IllegalAccessException e) {
                    return null;
                } catch (ClassNotFoundException e) {
                    return null;
                }
            } while (cursor.moveToNext());
        }

        return list;
    }

    public List<ManagedEntity> getAllForEntity(String entity, String[] columns) {
        List<ManagedEntity> list = new LinkedList<ManagedEntity>();

        // 1. build the query
        String query = "SELECT id,title,summary,category, content, date,link FROM " + entity;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ManagedEntity results = null;
                try {
                    results  = (ManagedEntity) Class.forName(package_name + entity).newInstance();
                    results.setId(Integer.parseInt(cursor.getString(0)));
                    int num_columns = columns.length;
                    // Log.e("MysqlHelper","Cursor " + cursor.get);
                    for (int i = 1; i < num_columns; i++) {
                        if (columns[i].equals("date")) {
                            String value = cursor.getString(i);

                            results.setValue(columns[i],Integer.valueOf(value));
                        } else {
                            results.setValue(columns[i],cursor.getString(i));
                        }

                    }

                    // Add book to books
                    list.add(results);
                } catch (InstantiationException e) {
                    return null;
                } catch (IllegalAccessException e) {
                    return null;
                } catch (ClassNotFoundException e) {
                    return null;
                }
            } while (cursor.moveToNext());
        }

        return list;
    }

    public int updateEntity(String entity, ManagedEntity e) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        Resources res = Resources.getSystem();

        int res_id = res.getIdentifier(entity + "_entity",null,null);

        String[] column = res.getStringArray(res_id);

        int num_colums = column.length;

        for (int i = 0; i < num_colums; i++) {
            values.put(column[i], (String) e.getValue(column[i])); // get title
        }

        // 3. updating row
        int i = db.update(entity, //table
                values, // column/value
                column[0]+" = ?", // selections
                new String[] { String.valueOf(e.getId()) }); //selection args

        // 4. close
        db.close();

        return i;


    }

    public void deleteEntity(String entity, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(entity,
                "id = ?",
                new String[] { String.valueOf(id) });

        // 3. close
        db.close();
    }

    public void deleteAllForEntity(String entity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(entity,"",null);

        // 3. close
        db.close();
    }



}
