package code.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fittreat.android.R;


public class DataBaseHelper extends SQLiteOpenHelper {
    DataBaseHelper(Context context) {
        super(context, context.getString(R.string.database_name), null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableQuestion.SQL_CREATE_QUESTION);
        db.execSQL(TableOptions.SQL_CREATE_OPTIONS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TableQuestion.question);
            db.execSQL("DROP TABLE IF EXISTS " + TableOptions.options);
            onCreate(db);
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
