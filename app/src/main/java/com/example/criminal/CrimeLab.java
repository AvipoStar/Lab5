package com.example.criminal;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab
{
    // Префикс s у переменной sCrimeLab показывает нам, что она статическая
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

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
        mCrimes = new ArrayList<>();
        // Позже List будет содержать объекты, созданные пользователем,
        // а пока заполним массив на 100 однообразных объектов Crime
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0); // Для каждого второго объекта
            mCrimes.add(crime);
        }
    }
    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getmId().equals(id)) {
                return crime;
            }
        }
        return null;
    }
}
