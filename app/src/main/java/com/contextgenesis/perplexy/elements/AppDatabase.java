package com.contextgenesis.perplexy.elements;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GenericAnswerDetails.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static AppDatabase db;

    private static volatile AppDatabase INSTANCE;

    public abstract GenericAnswerDetailsDao answerDetailsDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "perplexy.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                    db = INSTANCE;
                }
            }
        }
        return INSTANCE;
    }
}
