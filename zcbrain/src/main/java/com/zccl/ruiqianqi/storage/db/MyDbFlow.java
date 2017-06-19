package com.zccl.ruiqianqi.storage.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.BaseTransactionManager;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.DefaultTransactionManager;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.PriorityTransactionQueue;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

import static com.zccl.ruiqianqi.storage.db.MyDbFlow.OP.DELETE;
import static com.zccl.ruiqianqi.storage.db.MyDbFlow.OP.INSERT;
import static com.zccl.ruiqianqi.storage.db.MyDbFlow.OP.QUERY;
import static com.zccl.ruiqianqi.storage.db.MyDbFlow.OP.UPDATE;


/**
 * Created by ruiqianqi on 2017/1/14 0014.
 */

public class MyDbFlow {

    // 类标识
    private static String TAG = MyDbFlow.class.getSimpleName();

    public enum OP{
        // 增
        INSERT,
        // 删
        DELETE,
        // 改
        UPDATE,
        // 查
        QUERY,
    }

    // 同步操作
    public final static int SYNC = 0;
    // 异步操作
    public final static int ASYNC = 1;

    /**
     * 初始化数据库
     *
     * @param context
     */
    public static void initDbFlow(Context context) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        builder.addDatabaseConfig(new DatabaseConfig.Builder(AppDatabase.class).
                transactionManagerCreator(new DatabaseConfig.TransactionManagerCreator() {
                    @Override
                    public BaseTransactionManager createManager(DatabaseDefinition databaseDefinition) {
                        // this will be called once database modules are loaded and created.
                        return new DefaultTransactionManager(
                                new PriorityTransactionQueue("DBFlow Priority Queue"),
                                databaseDefinition);
                    }
                }).build());
        // 初始化DBFlow数据库操作
        FlowManager.init(builder.build());
    }

    /**********************************************************************************************/
    /**
     * 事务、批量【增、删、改】服务器地址，
     *
     * 字段设置为空是什么个意思？
     * select * from ServerBean where flagVersion is null
     *
     * @param op 增、删、改
     * @param sync 同步、异步
     * @param serverBeanList 要操作的对象集合
     * @param dbCallback
     */
    public static void transactionServerBean(final OP op, int sync, final List<ServerBean> serverBeanList, final DbCallback dbCallback) {
        DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);
        try {
            // 批量插入
            ProcessModelTransaction<ServerBean> processModelTransaction =
                    new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<ServerBean>() {
                        @Override
                        public void processModel(ServerBean serverBean) {
                            if (INSERT == op) {
                                //serverBean.save();
                                serverBean.insert();
                            } else if (DELETE == op) {
                                serverBean.delete();
                            } else if (UPDATE == op) {
                                serverBean.update();
                            } else if (QUERY == op) {

                            }
                        }
                    }).processListener(new ProcessModelTransaction.OnModelProcessListener<ServerBean>() {
                        @Override
                        public void onModelProcessed(long current, long total, ServerBean modifiedModel) {

                        }
                    }).addAll(serverBeanList).build();
            if(SYNC == sync){
                // 批量同步
                database.executeTransaction(processModelTransaction);
            }else{
                // 批量异步
                database.beginTransactionAsync(processModelTransaction)
                        .success(new Transaction.Success() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                if(null != dbCallback) {
                                    dbCallback.OnSuccess();
                                }
                            }
                        })
                        .error(new Transaction.Error() {
                            @Override
                            public void onError(Transaction transaction, Throwable error) {
                                if(null != dbCallback) {
                                    dbCallback.OnFailure(error);
                                }
                            }
                        }).build().execute();
            }

            // 没什么用
            /*
            FastStoreModelTransaction
                    .insertBuilder(FlowManager.getModelAdapter(OpenQA.class))
                    .addAll(openQAList)
                    .build();
                    */
        } catch (SQLException e) {
            if (null != dbCallback) {
                dbCallback.OnFailure(e);
            }
        }
    }

    /**
     * 事务、【增、删、改】一个服务器地址，
     *
     * 字段设置为空是什么个意思？
     * select * from ServerBean where flagVersion is null
     *
     * @param op 增、删、改
     * @param sync 同步、异步
     * @param serverBean 要操作的对象
     * @param dbCallback 回调接口
     */
    public static void transactionServerBean(final OP op, int sync, ServerBean serverBean, final DbCallback dbCallback) {

        DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);
        try {

            // 批量插入
            ProcessModelTransaction<ServerBean> processModelTransaction =
                    new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<ServerBean>() {
                        @Override
                        public void processModel(ServerBean serverBean) {
                            if (INSERT == op) {
                                //serverBean.save();
                                serverBean.insert();
                            } else if (DELETE == op) {
                                serverBean.delete();
                            } else if (UPDATE == op) {
                                serverBean.update();
                            } else if (QUERY == op) {

                            }
                        }
                    }).processListener(new ProcessModelTransaction.OnModelProcessListener<ServerBean>() {
                        @Override
                        public void onModelProcessed(long current, long total, ServerBean modifiedModel) {

                        }
                    }).add(serverBean).build();

            if(SYNC == sync){
                // 批量同步
                database.executeTransaction(processModelTransaction);
            }else{
                // 批量异步
                database.beginTransactionAsync(processModelTransaction)
                        .success(new Transaction.Success() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                if(null != dbCallback) {
                                    dbCallback.OnSuccess();
                                }
                            }
                        })
                        .error(new Transaction.Error() {
                            @Override
                            public void onError(Transaction transaction, Throwable error) {
                                if(null != dbCallback) {
                                    dbCallback.OnFailure(error);
                                }
                            }
                        }).build().execute();
            }

        } catch (SQLException e) {
            if (null != dbCallback) {
                dbCallback.OnFailure(e);
            }
        }
    }

    /**********************************************************************************************/
    /**
     * 批量【增、删、改】服务器地址
     *
     * 字段设置为空是什么个意思？
     * select * from ServerBean where flagVersion is null
     *
     * @param op 增、删、改
     * @param sync 同步、异步
     * @param serverBeanList 要操作的对象集合
     * @param dbCallback
     */
    public static void operateServerBean(OP op, int sync, List<ServerBean> serverBeanList, final DbCallback dbCallback) {
        DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);
        try {
            if(SYNC == sync){
                // 同步保存，马上保存
                // run a transaction synchronous easily.
                database.executeTransaction(new MyTransaction(op, (ServerBean[]) serverBeanList.toArray()));
            }else {
                database.beginTransactionAsync(new MyTransaction(op, (ServerBean[]) serverBeanList.toArray())).success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        if (null != dbCallback) {
                            dbCallback.OnSuccess();
                        }
                    }
                }).error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        if (null != dbCallback) {
                            dbCallback.OnFailure(error);
                        }
                    }
                }).build().execute();
            }
        } catch (SQLException e) {
            if (null != dbCallback) {
                dbCallback.OnFailure(e);
            }
        }
    }

    /**
     * 【增、删、改】一个服务器地址
     *
     * 字段设置为空是什么个意思？
     * select * from ServerBean where flagVersion is null
     *
     * @param op 增、删、改
     * @param sync 同步、异步
     * @param serverBean
     * @param dbCallback
     */
    public static void operateServerBean(OP op, int sync, final ServerBean serverBean, final DbCallback dbCallback) {
        DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);
        try {
            if(SYNC == sync){
                // 同步保存，马上保存
                // run a transaction synchronous easily.
                database.executeTransaction(new MyTransaction(op, new ServerBean[]{serverBean}));
            }else {
                database.beginTransactionAsync(new MyTransaction(op, new ServerBean[]{serverBean})).success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        if (null != dbCallback) {
                            dbCallback.OnSuccess();
                        }
                    }
                }).error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        if (null != dbCallback) {
                            dbCallback.OnFailure(error);
                        }
                    }
                }).build().execute();
            }
        } catch (SQLException e) {
            if (null != dbCallback) {
                dbCallback.OnFailure(e);
            }
        }
    }

    /***********************************【同步数据库查询】*****************************************/
    /**
     * 根据信息，同步查询单个信息
     *
     * orderBy(ServerBean_Table.id, true) 代表升序
     * ASC   升序
     * DESC  降序
     *
     * @param flagVersion  服务器地址版本
     * @param ascDesc       true升序  false降序
     * @return
     */
    public static Cursor queryServerBeanCursor(String flagVersion, boolean ascDesc) {
        return SQLite.select()
                .from(ServerBean.class)
                .where(ServerBean_Table.flagVersion.eq(flagVersion))
                .orderBy(ServerBean_Table.id, ascDesc)
                .query();
    }

    /************************************【同步对象查询】******************************************/
    /**
     * 根据信息，同步查询单个信息
     *
     * orderBy(ServerBean_Table.id, true) 代表升序
     * ASC   升序
     * DESC  降序
     *
     * @param flagVersion                 服务器地址版本
     * @return
     */
    public static ServerBean queryServerBean(String flagVersion) {
        return SQLite.select()
                .from(ServerBean.class)
                .where(ServerBean_Table.flagVersion.eq(flagVersion))
                .querySingle();
    }

    /**
     * 同步查询所有的用户
     *
     * orderBy(ServerBean_Table.id, true) 代表升序
     * ASC   升序
     * DESC  降序
     *
     * @return
     */
    public static List<ServerBean> queryAllServerBean() {
        return SQLite.select()
                .from(ServerBean.class)
                .orderBy(ServerBean_Table.id, true)
                .queryList();
    }

    /**
     * 根据信息，同步查询多条信息
     *
     * orderBy(ServerBean_Table.id, true) 代表升序
     * ASC   升序
     * DESC  降序
     *
     * @param flagVersions                  多条信息查询条件
     * @return
     */
    public static List<ServerBean> querySomeServerBean(List<String> flagVersions) {
        List<String> flagVersionList = new ArrayList<>();
        flagVersionList.addAll(flagVersions);
        SQLCondition condition = Condition.column(ServerBean_Table.flagVersion.getNameAlias()).in(flagVersionList);
        return SQLite.select()
                .from(ServerBean.class)
                .where(condition)
                .orderBy(ServerBean_Table.id, true)
                .queryList();
    }

    /************************************【异步查询】**********************************************/
    /**
     * 根据信息，异步查询单个信息
     *
     * @param flagVersion                 服务器地址版本
     * @param queryServerBeanCallback    查询回调接口
     * @return
     */
    public static void queryServerBean(String flagVersion, final IQueryServerBeanCallback queryServerBeanCallback) {
        SQLite.select()
                .from(ServerBean.class)
                .where(ServerBean_Table.flagVersion.eq(flagVersion))
                .async()
                .querySingleResultCallback(new QueryTransaction.QueryResultSingleCallback<ServerBean>() {
                    @Override
                    public void onSingleQueryResult(QueryTransaction transaction, @Nullable ServerBean serverBean) {
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnQueryResult(serverBean);
                        }
                    }
                }).
                success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnSuccess();
                        }
                    }
                }).
                error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnFailure(error);
                        }
                    }
                })
                .execute();
    }

    /**
     * 异步查询所有的用户
     * orderBy(ServerBean_Table.id, true) 代表升序
     * ASC   升序
     * DESC  降序
     *
     * @param queryServerBeanCallback  查询回调接口
     * @return
     */
    public static void queryAllServerBean(final IQueryServerBeanCallback queryServerBeanCallback) {
        SQLite.select()
                .from(ServerBean.class)
                .orderBy(ServerBean_Table.id, true)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<ServerBean>() {
                    @Override
                    public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<ServerBean> tResult) {
                        // called when query returns on UI thread
                        List<ServerBean> serverBeanList = tResult.toListClose();
                        // do something with results
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnQueryListResult(serverBeanList);
                        }
                    }
                }).
                success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnSuccess();
                        }
                    }
                }).
                error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnFailure(error);
                        }
                    }
                })
                .execute();
    }

    /**
     * 根据信息，异步查询多条信息
     *
     * orderBy(ServerBean_Table.id, true) 代表升序
     * ASC   升序
     * DESC  降序
     *
     * @param flagVersions                  多条信息查询条件
     * @param queryServerBeanCallback      查询回调接口
     * @return
     */
    public static void querySomeServerBean(List<String> flagVersions, final IQueryServerBeanCallback queryServerBeanCallback) {
        List<String> flagVersionList = new ArrayList<>();
        flagVersionList.addAll(flagVersions);

        SQLCondition condition = Condition.column(ServerBean_Table.flagVersion.getNameAlias()).in(flagVersionList);
        SQLite.select()
                .from(ServerBean.class)
                .where(condition)
                .orderBy(ServerBean_Table.id, true)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<ServerBean>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @Nullable List<ServerBean> tResult) {
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnQueryListResult(tResult);
                        }
                    }
                }).
                success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnSuccess();
                        }
                    }
                }).
                error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        if (null != queryServerBeanCallback) {
                            queryServerBeanCallback.OnFailure(error);
                        }
                    }
                })
                .execute();
    }

    /**
     * 异步数据查询回调接口
     */
    public interface IQueryServerBeanCallback extends DbCallback {
        void OnQueryResult(ServerBean serverBean);
        void OnQueryListResult(List<ServerBean> serverBeanList);
    }

    /**
     * 数据库操作接口
     */
    public interface DbCallback {
        void OnSuccess();
        void OnFailure(Throwable error);
    }

    /**
     * 事件
     */
    protected static class MyTransaction implements ITransaction {
        private OP mOp = INSERT;
        private ServerBean[] serverBeans;

        private MyTransaction(OP op, ServerBean[] serverBeans) {
            this.mOp = op;
            this.serverBeans = serverBeans;
        }

        @Override
        public void execute(DatabaseWrapper databaseWrapper) {
            if (INSERT == mOp) {
                for (int i = 0; i < serverBeans.length; i++) {
                    serverBeans[i].save(databaseWrapper);
                }
            } else if (DELETE == mOp) {
                for (int i = 0; i < serverBeans.length; i++) {
                    serverBeans[i].delete(databaseWrapper);
                }
            } else if (UPDATE == mOp) {
                for (int i = 0; i < serverBeans.length; i++) {
                    serverBeans[i].update(databaseWrapper);
                }
            } else if (QUERY == mOp) {

            }
        }
    }
}
