package com.tencent.shadow.core.pluginmanager.installplugin;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class InstalledPluginDBHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称
     */
    private final static String DB_NAME_PREFIX = "shadow_installed_plugin_db";
    /**
     * 表名称
     */
    public final static String TABLE_NAME_MANAGER = "shadowPluginManager";

    /**
     * 自增主键
     */
    public final static String COLUMN_ID = "id";
    /**
     * 插件的hash
     */
    public final static String COLUMN_HASH = "hash";
    /**
     * 插件的类型
     */
    public final static String COLUMN_TYPE = "type";
    /**
     * 插件的路径
     */
    public final static String COLUMN_PATH = "filePath";
    /**
     * 插件的businessName
     */
    public final static String COLUMN_BUSINESS_NAME = "businessName";
    /**
     * 插件的名称
     */
    public final static String COLUMN_PARTKEY = "partKey";
    /**
     * 插件的依赖
     */
    public final static String COLUMN_DEPENDSON = "dependsOn";
    /**
     * 插件的uuid
     */
    public final static String COLUMN_UUID = "uuid";
    /**
     * 插件的版本号
     */
    public final static String COLUMN_VERSION = "version";
    /**
     * 插件的安装时间
     */
    public final static String COLUMN_INSTALL_TIME = "installedTime";
    /**
     * 插件的dex目录
     */
    public final static String COLUMN_PLUGIN_ODEX = "odexPath";
    /**
     * 插件的lib目录
     */
    public final static String COLUMN_PLUGIN_LIB = "libPath";
    /**
     * 数据库的版本号
     */
    private final static int VERSION = 3;


    public InstalledPluginDBHelper(Context context, String name) {
        super(context, DB_NAME_PREFIX + name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MANAGER + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HASH + " VARCHAR , "
                + COLUMN_PATH + " VARCHAR, "
                + COLUMN_TYPE + " INTEGER, "
                + COLUMN_BUSINESS_NAME + " VARCHAR, "
                + COLUMN_PARTKEY + " VARCHAR, "
                + COLUMN_DEPENDSON + " VARCHAR, "
                + COLUMN_UUID + " VARCHAR, "
                + COLUMN_VERSION + " VARCHAR, "
                + COLUMN_INSTALL_TIME + " INTEGER ,"
                + COLUMN_PLUGIN_ODEX + " VARCHAR ,"
                + COLUMN_PLUGIN_LIB + " VARCHAR "
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            //todo #59 查询COLUMN_TYPE是Interface的uuid，然后删除这些uuid的记录。
            db.beginTransaction();
            try {
                Cursor cursor = db.query(
                        true,
                        TABLE_NAME_MANAGER,
                        new String[]{COLUMN_UUID, COLUMN_TYPE},
                        COLUMN_TYPE + " = ?",
                        new String[]{"2"},//Interface Type
                        null, null, null, null
                );
                List<String> uuids = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String uuid = cursor.getString(cursor.getColumnIndex(COLUMN_UUID));
                    uuids.add(uuid);
                }
                cursor.close();

                for (String uuid : uuids) {
                    db.delete(TABLE_NAME_MANAGER, COLUMN_UUID + " = ?", new String[]{uuid});
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}
