package com.zccl.ruiqianqi.storage.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by ruiqianqi on 2016/7/28 0028.
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    // 数据库名称 we will add the .db extension
    public static final String NAME = "BrainDB";
    // 数据库版本号
    public static final int VERSION = 1;

    /*
    @Migration(version = 2, database = AppDatabase.class)
    public static class AddEmailToUserMigration extends AlterTableMigration<User> {

        public AddEmailToUserMigration(Class<User> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "email");
        }
    }
    */
}
