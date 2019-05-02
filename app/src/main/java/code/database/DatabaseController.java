package code.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import code.utils.AppUtils;

public class DatabaseController {
    public static SQLiteDatabase myDataBase;
    private static DataBaseHelper myDataBaseHelper;

    public static void openDataBase(Context mContext) {
        if (myDataBaseHelper == null) {
            myDataBaseHelper = new DataBaseHelper(mContext);
        }
        if (myDataBase == null) {
            myDataBase = myDataBaseHelper.getWritableDatabase();
        }
    }

    public static void removeTable(String tableName) {
        myDataBase.delete(tableName, null, null);
    }

    public static long insertData(ContentValues values, String tableName) {
        long id = -1;
        try {
            id = myDataBase.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppUtils.print("====insertData " + id);
        return id;
    }

    public static long insertUpdateData(ContentValues values, String tableName, String columnName, String uniqueId) {
        try {

            if (checkRecordExist(tableName, columnName, uniqueId)) {
                Log.d("update-data",values.toString());
                Log.d("unique-id",uniqueId);
                return (long) myDataBase.update(tableName, values, columnName + "='" + uniqueId + "'", null);
            }
            Log.d("insert-data",values.toString());
            Log.d("unique-id",uniqueId);
            return myDataBase.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static void updateEqual(ContentValues mContentValues, String tableName, String columnName, String columnValue) {
        myDataBase.update(tableName, mContentValues, columnName + " = '" + columnValue + "'", null);
    }

    public static void updateNotEqual(ContentValues mContentValues, String tableName, String columnName, String columnValue) {
        myDataBase.update(tableName, mContentValues, columnName + " != '" + columnValue + "'", null);
    }

    public static long insertUpdateNotData(ContentValues values, String tableName, String columnName, String uniqueId) {
        try {
            if (checkRecordExist(tableName, columnName, uniqueId)) {
                return (long) myDataBase.update(tableName, values, columnName + "!='" + uniqueId + "'", null);
            }
            return myDataBase.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static long insertUpdateDataMultiple(ContentValues values, String tableName, String where) {
        try {
            if (checkRecordExistMultiple(tableName, where)) {
                return (long) myDataBase.update(tableName, values, where, null);
            }
            return myDataBase.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean checkRecordExist(String tableName, String columnName, String uniqueId) {
        boolean status = false;
        Cursor cursor = myDataBase.query(tableName, null, columnName + "='" + uniqueId + "'", null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                status = true;
            }
            cursor.close();
        }
        return status;
    }

    public static boolean checkRecordExistWhere(String tableName, String where) {
        boolean status = false;
        Cursor cursor = myDataBase.query(tableName, null, where, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                status = true;
            }
            cursor.close();
        }
        return status;
    }

    public static boolean checkRecordExistMultiple(String tableName, String where) {
        boolean status = false;
        Cursor cursor = myDataBase.query(tableName, null, where, null, null, null, null);
        AppUtils.print("isDataExit-SubCategory" + cursor.getCount());
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                status = true;
            }
            cursor.close();
        }
        return status;
    }

    public static long isDataExit(String tableName) {
        long cnt = DatabaseUtils.queryNumEntries(myDataBase, tableName);
        AppUtils.print("isDataExit " + cnt);
        Log.d("isDataExit", String.valueOf(cnt));
        return cnt;
    }

    public static boolean deleteRow(String tableName, String keyName, String keyValue) {
        try {
            return myDataBase.delete(tableName, new StringBuilder().append(keyName).append("=").append(keyValue).toString(), null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public static int delete(String tableName, String where, String[] args) {
        return myDataBase.delete(tableName, where, args);
    }

    public static ArrayList<JSONObject> fetchAllDataFromValues(String tableName) {
        String query = "SELECT * FROM " + tableName;
        AppUtils.print("===fetchAllDataFromValues" + query);
        Cursor cursor = myDataBase.rawQuery(query, null);
        ArrayList<JSONObject> arrayList = new ArrayList();
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                JSONObject jsonObject = new JSONObject();
                for (int j = 0; j < cursor.getColumnCount(); j++) {
                    try {
                        jsonObject.put(cursor.getColumnName(j), cursor.getString(j));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                arrayList.add(jsonObject);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }


    public static ArrayList<HashMap<String, String>> getQuestionData(String chapter) {
        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();
        Cursor cur = myDataBase.rawQuery("SELECT * from "+TableQuestion.question
                + " where " + TableQuestion.questionColumn.observation_id + " = '" + chapter+ "' and "
                + TableQuestion.questionColumn.status + " = '0'", null);

        String query = "SELECT * from "+TableQuestion.question
                + " where " + TableQuestion.questionColumn.observation_id + " = '" + chapter+ "' and "
                + TableQuestion.questionColumn.status + " = '0'";

        Log.d("getStepData-query", query);
        Log.d("getStepData-Count()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("id", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.id.toString())));
                hashlist.put("question_title", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.question_title.toString())));
                hashlist.put("video_url", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.video_url.toString())));
                hashlist.put("thumbnail", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.thumbnail.toString())));
                hashlist.put("question_type", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.question_type.toString())));
                hashlist.put("option_type", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.option_type.toString())));
                hashlist.put("answer_selection_type", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.answer_selection_type.toString())));
                hashlist.put("observation_id", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.observation_id.toString())));
                hashlist.put("question_id", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.question_id.toString())));
                hashlist.put("selected", cur.getString(cur.getColumnIndex(TableQuestion.questionColumn.status.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();
        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getOptionData(String question) {
        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();
        Cursor cur = myDataBase.rawQuery("SELECT * from "+TableOptions.options
                + " where " + TableOptions.optionsColumn.question_id + " = '" + question+"'" , null);

        String query = "SELECT * from "+TableOptions.options
                + " where " + TableOptions.optionsColumn.question_id + " = '" + question ;

        Log.d("getStepData-query", query);
        Log.d("getStepData-Count()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("id", cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.id.toString())));
                hashlist.put("option", cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.option.toString())));
                hashlist.put("imageUrl", cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.imageUrl.toString())));
                hashlist.put("correctAnswer", cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.correctAnswer.toString())));
                hashlist.put("question_id", cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.question_id.toString())));
                hashlist.put("selected", cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.selected.toString())));
                hashlist.put("answer_selection_type", cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.answer_selection_type.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();
        return CatList;
    }

    public static boolean checkAnswer(String question) {

        boolean bool=false;

        Cursor cur = myDataBase.rawQuery("SELECT * from "+TableOptions.options
                + " where " + TableOptions.optionsColumn.question_id + " = '" + question+"' and "
                +  TableOptions.optionsColumn.correctAnswer + " = '1'", null);

        Cursor cur2 = myDataBase.rawQuery("SELECT * from "+TableOptions.options
                + " where " + TableOptions.optionsColumn.question_id + " = '" + question+"' and "
                +  TableOptions.optionsColumn.selected + " = '1'", null);

        String query = "SELECT * from "+TableOptions.options
                + " where " + TableOptions.optionsColumn.question_id + " = '" + question+"' and "
                +  TableOptions.optionsColumn.correctAnswer + " = '1'";

        String query2 = "SELECT * from "+TableOptions.options
                + " where " + TableOptions.optionsColumn.question_id + " = '" + question+"' and "
                +  TableOptions.optionsColumn.selected + " = '1'";

        Log.d("getStepData-query", query);
        Log.d("getStepData-query2", query2);
        Log.d("getStepData-Count()", String.valueOf(cur.getCount()));
        Log.d("getStepData-cur2Count()", String.valueOf(cur2.getCount()));

        if(cur.getCount()==cur2.getCount())
        {
            if (cur != null && cur.moveToNext()) {
                for (int i = 0; i < cur.getCount(); i++) {

                    if(cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.selected.toString())).equalsIgnoreCase("1"))
                    {
                        bool = true;
                    }
                    else
                    {
                        bool = false;
                        break;
                    }

                    cur.moveToNext();
                }
            }
        }
        else
        {
            bool = false;
        }

        cur.close();
        cur2.close();
        return bool;
    }

    public static List<String> getAnswerData(String question) {
        List<String> list = new ArrayList<String>();

        Cursor cur = myDataBase.rawQuery("SELECT * from "+TableOptions.options
                + " where " + TableOptions.optionsColumn.question_id + " = '" + question+"' and "
                +TableOptions.optionsColumn.selected +" = '1'", null);

        String query = "SELECT * from "+TableOptions.options
                + " where " + TableOptions.optionsColumn.question_id + " = '" + question+"' and "
                +TableOptions.optionsColumn.selected +" = '1'";

        Log.d("getStepData-query", query);
        Log.d("getStepData-Count()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                list.add(cur.getString(cur.getColumnIndex(TableOptions.optionsColumn.option.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return list;
    }

}
