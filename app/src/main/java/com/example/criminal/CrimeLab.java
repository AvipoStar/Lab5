package com.example.criminal;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.criminal.database.CrimeBaseHelper;
import com.example.criminal.database.CrimeDbSchema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeLab
{
    // Префикс s у переменной sCrimeLab показывает нам, что она статическая
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private Cursor cursor;

    //Запрос для получения данных Crime
    private Cursor queryCrimes(String whereClause, String[] whereArgs) {
        cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null, // columns - с null выбираются все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return cursor;
    }
    //Обновление записи в таблице
    public void updateCrime(Crime crime) {
        String uuidString = crime.getmId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }
    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    // Параметр Context не используется, но пригодится в будущем
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    //Закрытый конструктор CrimeLab. Другие классы не
    //смогут создать экземпляр CrimeLab в обход метода get()
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime() {
        String uuidString = cursor.getString(cursor.getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID));
        String title = cursor.getString(cursor.getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        long date = cursor.getLong(cursor.getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE));
        int isSolved = cursor.getInt(cursor.getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED));
        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setmTitle(title);
        crime.setmDate(new Date(date));
        crime.setmSolved(isSolved != 0);
        return crime;
    }
    public Crime getCrime(UUID id) {
        cursor = queryCrimes(
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return getCrime();
        } finally {
            cursor.close();
        }
    }
    //Запись и обновление баз данных осуществляются
    //с помощью класса ContentValues
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getmId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getmTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getmDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.ismSolved() ? 1 : 0);
        return values;
    }

}
