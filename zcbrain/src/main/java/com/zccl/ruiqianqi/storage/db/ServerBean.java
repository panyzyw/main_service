package com.zccl.ruiqianqi.storage.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ruiqianqi on 2017/1/14 0014.
 */
//@ModelContainer
@Table(database = AppDatabase.class)
public class ServerBean extends BaseModel {
    // 自增ID
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String httpRequest;
    @Column
    public String httpResource;
    @Column
    public String tcpRequest;
    @Column
    public String tcpPort;
    @Column @NotNull
    @Unique
    public String flagVersion;
    @Column
    public String rid;
    @Column
    public String isInControl = "false";
}
