package com.zccl.ruiqianqi.mind.provider;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.zccl.ruiqianqi.mind.provider.MyProviderMetaData.DATABASE_VERSION;

/**
 * Created by zc on 2016/4/8.
 *
 * 总结起来，有几点：
 * 1、经常变化的字段用varchar
 * 2、知道固定长度的用char
 * 3、尽量用varchar
 * 4、超过255字符的只能用varchar或者text
 * 5、能用varchar的地方不用text
 * 6、能够用数字类型的字段尽量选择数字类型而不用字符串类型的（电话号码），这会降低查询和连接的性能，并会增加存储开销。
 * 这是因为引擎在处理查询和连接回逐个比较字符串中每一个字符，而对于数字型而言只需要比较一次就够了。
 *
 * 1、CHAR。CHAR存储定长数据很方便，CHAR字段上的索引效率级高，比如定义char(10)，那么不论你存储的数据是否达到了10个字节，都要占去10个字节的空间,不足的自动用空格填充。
 * 2、VARCHAR。存储变长数据，但存储效率没有CHAR高。如果一个字段可能的值是不固定长度的，我们只知道它不可能超过10个字符，把它定义为 VARCHAR(10)是最合算的。VARCHAR类型的实际长度是它的值的实际长度+1。为什么“+1”呢？这一个字节用于保存实际使用了多大的长度。从空间上考虑，用varchar合适；从效率上考虑，用char合适，关键是根据实际情况找到权衡点。
 * 3、TEXT。text存储可变长度的非Unicode数据，最大长度为2^31-1(2,147,483,647)个字符。
 * 4、NCHAR、NVARCHAR、NTEXT。这三种从名字上看比前面三种多了个“N”。它表示存储的是Unicode数据类型的字符。我们知道字符中，英文字符只需要一个字节存储就足够了，但汉字众多，需要两个字节存储，英文与汉字同时存在时容易造成混乱，Unicode字符集就是为了解决字符集这种不兼容的问题而产生的，它所有的字符都用两个字节表示，即英文字符也是用两个字节表示。nchar、nvarchar的长度是在1到4000之间。和char、varchar比较起来，nchar、nvarchar则最多存储4000个字符，不论是英文还是汉字；而char、varchar最多能存储8000个英文，4000个汉字。可以看出使用nchar、nvarchar数据类型时不用担心输入的字符是英文还是汉字，较为方便，但在存储英文时数量上有些损失。
 * 所以一般来说，如果含有中文字符，用nchar/nvarchar，如果纯英文和数字，用char/varchar。
 *
 SQLite允许向一个integer型字段中插入字符, 这是一个特性，而不是一个bug。SQLite不强制数据类型约束。任何数据都可以插入任何列。
 你可以向一个整型列中插入任意长度的字符串，向布尔型列中插 入浮点数，或者向字符型列中插入日期型值。
 在CREATE TABLE中所指定的数据类型不会限制在该列中插入任何数据。任何列均可接受任意长度的字符串
 （只有一种情况除外：标志为INTEGER PRIMARY KEY的列只能存储64位整数，当向这种列中插数据除整数以外的数据时，将会产生错误）。
 *
 *
 SQLite3支持何种数据类型？
 NULL
 INTEGER
 REAL
 TEXT
 BLOB 表示二进制大对象，这种数据类型通过用来保存图片，图象，视频等。
 但实际上，sqlite3也接受如下的数据类型：
 smallint 16位元的整数。
 interger 32位元的整数。
 decimal(p,s) p精确值和s大小的十进位整数，精确值p是指全部有几个数(digits)大小值，s是指小数点後有几位数。如果没有特别指定，则系统会设为p=5; s=0。
 float 32位元的实数。
 double 64位元的实数。
 char(n) n长度的字串，n不能超过254。
 varchar(n)长度不固定且其最大长度为n的字串，n不能超过4000。
 graphic(n)和char(n)一样，不过其单位是两个字元double-bytes，n不能超过127。这个形态是为了支援两个字元长度的字体，例如中文字。
 vargraphic(n)可变长度且其最大长度为n的双字元字串，n不能超过2000。
 date包含了年份、月份、日期。
 time包含了小时、分钟、秒。
 timestamp包含了年、月、日、时、分、秒、千分之一秒。
 *
 *
 在SQLite中，如何在一个表上添加或删除一列？
 SQLite有有限地ALTER TABLE支持。你可以使用它来在表的末尾增加一列，可更改表的名称。如果需要对表结构做更复杂的改变，则必须重新建表。
 重建时可以先将已存在的数据放到一个临时表中，删除原表，创建新表，然后将数据从临时表中复制回来。
 如，假设有一个t1表，其中有"a", "b", "c"三列，如果要删除列c，以下过程描述如何做:
 BEGIN TRANSACTION;
 CREATE TEMPORARY TABLE t1_backup(a,b);
 INSERT INTO t1_backup SELECT a,b FROM t1;
 DROP TABLE t1;
 CREATE TABLE t1(a,b);
 INSERT INTO t1 SELECT a,b FROM t1_backup;
 DROP TABLE t1_backup;
 COMMIT;
 */
public class MySQLite extends SQLiteOpenHelper {

    public MySQLite(Context context, int version){
        super(context, MyProviderMetaData.DATABASE_NAME, null, version);
    }

    public MySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    /**
     * 1、在第一次打开数据库的时候才会走
     * 2、在清除数据之后再次运行-->打开数据库，这个方法会走
     * 3、没有清除数据，不会走这个方法
     * 4、数据库升级的时候这个方法不会走
     * @return
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /** 创建表的语句 */
        String sql = "create table " + MyProviderMetaData.USER_DATA_TABLE_NAME + "(" +
                MyProviderMetaData.UserTableMetaData._ID + " integer primary key autoincrement, " +
                MyProviderMetaData.UserTableMetaData.USER_NAME + " varchar(30) not null, " +
                MyProviderMetaData.UserTableMetaData.USER_VALUE + " varchar(30)" +
                ")";

        db.execSQL(sql);

        // 若不是第一个版本安装，直接执行数据库升级
        // 请不要修改FIRST_DATABASE_VERSION的值，其为第一个数据库版本大小
        final int FIRST_DATABASE_VERSION = 1;
        onUpgrade(db, FIRST_DATABASE_VERSION, DATABASE_VERSION);
    }

    /**
     * 数据库版本或表结构改变会被调用
     * 1、第一次创建数据库的时候，这个方法不会走
     * 2、清除数据后再次运行(相当于第一次创建)这个方法不会走
     * 3、数据库已经存在，而且版本升高的时候，这个方法才会调用
     *
     * @param db
     * @param oldVersion    老版本号
     * @param newVersion    新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 使用for实现跨版本升级数据库
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 1: // 1---->2
                    upgradeToVersion2(db);
                    break;
                case 2:// 2---->3
                    upgradeToVersion3(db);
                    break;
                case 3:// 3---->4
                    upgradeToVersion4(db);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 升级到版本2【新增地址】
     * @param db
     */
    private void upgradeToVersion2(SQLiteDatabase db){
        // favorite表新增1个字段，并且只能在表的末尾添加字段
        String sql1 = "ALTER TABLE " + MyProviderMetaData.USER_DATA_TABLE_NAME + " ADD COLUMN address VARCHAR";
        db.execSQL(sql1);
    }

    /**
     * 升级到版本3【新增性别、年龄】
     * @param db
     */
    private void upgradeToVersion3(SQLiteDatabase db){
        // favorite表新增2个字段,添加新字段只能一个字段一个字段加，sqlite有限制不允许一条语句加多个字段
        String sql1 = "ALTER TABLE " + MyProviderMetaData.USER_DATA_TABLE_NAME + " ADD COLUMN sex CHAR";
        String sql2 = "ALTER TABLE " + MyProviderMetaData.USER_DATA_TABLE_NAME + " ADD COLUMN age INTEGER";
        db.execSQL(sql1);
        db.execSQL(sql2);
    }

    /**
     * 升级到版本4【新增习惯】，并保留原数据
     * @param db
     */
    private void upgradeToVersion4(SQLiteDatabase db){

        // 重命名原表
        String rename = "ALTER TABLE " + MyProviderMetaData.USER_DATA_TABLE_NAME + " RENAME TO " + MyProviderMetaData.USER_DATA_TABLE_NAME + "_bak";

        // 创建新表
        String create = "create table " + MyProviderMetaData.USER_DATA_TABLE_NAME + "(" +
                MyProviderMetaData.UserTableMetaData._ID + " integer primary key autoincrement, " +
                MyProviderMetaData.UserTableMetaData.USER_NAME + " varchar(30) not null, " +
                MyProviderMetaData.UserTableMetaData.USER_VALUE + " varchar(30) " +
                "address VARCHAR " +
                "sex CHAR " +
                "age INTEGER " +
                "habit VARCHAR " +
                ")";

        // 拷贝数据 （注意' '是为新加的字段插入默认值的必须加上，否则就会出错）
        String copyData = "insert into " + MyProviderMetaData.USER_DATA_TABLE_NAME + " select *,' ' from " + MyProviderMetaData.USER_DATA_TABLE_NAME + "_bak";

        // 丢弃备份表
        String dropBak = "drop table IF EXISTS " + MyProviderMetaData.USER_DATA_TABLE_NAME + "_bak";

        db.execSQL(rename);
        db.execSQL(create);
        db.execSQL(copyData);
        db.execSQL(dropBak);
    }
}
