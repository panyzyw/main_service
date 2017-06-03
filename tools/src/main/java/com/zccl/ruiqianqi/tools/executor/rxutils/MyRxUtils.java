package com.zccl.ruiqianqi.tools.executor.rxutils;

import com.zccl.ruiqianqi.tools.beans.RxBaseResult;
import com.zccl.ruiqianqi.tools.LogUtils;

import java.util.concurrent.Executor;

import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 Creating Observables
 Operators that originate new Observables.
 Create — create an Observable from scratch by calling observer methods programmatically
 Defer — do not create the Observable until the observer subscribes, and create a fresh Observable for each observer
 Empty/Never/Throw — create Observables that have very precise and limited behavior
 From — convert some other object or data structure into an Observable
 Interval — create an Observable that emits a sequence of integers spaced by a particular time interval
 Just — convert an object or a set of objects into an Observable that emits that or those objects
 Range — create an Observable that emits a range of sequential integers
 Repeat — create an Observable that emits a particular item or sequence of items repeatedly
 Start — create an Observable that emits the return value of a function
 Timer — create an Observable that emits a single item after a given delay

 Transforming Observables
 Operators that transform items that are emitted by an Observable.
 Buffer — periodically gather items from an Observable into bundles and emit these bundles rather than emitting the items one at a time
 FlatMap — transform the items emitted by an Observable into Observables, then flatten the emissions from those into a single Observable
 GroupBy — divide an Observable into a set of Observables that each emit a different group of items from the original Observable, organized by key
 Map — transform the items emitted by an Observable by applying a function to each item
 Scan — apply a function to each item emitted by an Observable, sequentially, and emit each successive value
 Window — periodically subdivide items from an Observable into Observable windows and emit these windows rather than emitting the items one at a time

 Filtering Observables
 Operators that selectively emit items from a source Observable.
 Debounce — only emit an item from an Observable if a particular timespan has passed without it emitting another item
 Distinct — suppress duplicate items emitted by an Observable
 ElementAt — emit only item n emitted by an Observable
 Filter — emit only those items from an Observable that pass a predicate test
 First — emit only the first item, or the first item that meets a condition, from an Observable
 IgnoreElements — do not emit any items from an Observable but mirror its termination notification
 Last — emit only the last item emitted by an Observable
 Sample — emit the most recent item emitted by an Observable within periodic time intervals
 Skip — suppress the first n items emitted by an Observable
 SkipLast — suppress the last n items emitted by an Observable
 Take — emit only the first n items emitted by an Observable
 TakeLast — emit only the last n items emitted by an Observable

 Combining Observables
 Operators that work with multiple source Observables to create a single Observable
 And/Then/When — combine sets of items emitted by two or more Observables by means of Pattern and Plan intermediaries
 CombineLatest — when an item is emitted by either of two Observables, combine the latest item emitted by each Observable via a specified function and emit items based on the results of this function
 Join — combine items emitted by two Observables whenever an item from one Observable is emitted during a time window defined according to an item emitted by the other Observable
 Merge — combine multiple Observables into one by merging their emissions
 StartWith — emit a specified sequence of items before beginning to emit the items from the source Observable
 Switch — convert an Observable that emits Observables into a single Observable that emits the items emitted by the most-recently-emitted of those Observables
 Zip — combine the emissions of multiple Observables together via a specified function and emit single items for each combination based on the results of this function

 Error Handling Operators
 Operators that help to recover from error notifications from an Observable
 Catch — recover from an onError notification by continuing the sequence without error
 Retry — if a source Observable sends an onError notification, resubscribe to it in the hopes that it will complete without error

 Observable Utility Operators
 A toolbox of useful Operators for working with Observables
 Delay — shift the emissions from an Observable forward in time by a particular amount
 Do — register an action to take upon a variety of Observable lifecycle events
 Materialize/Dematerialize — represent both the items emitted and the notifications sent as emitted items, or reverse this process
 ObserveOn — specify the scheduler on which an observer will observe this Observable
 Serialize — force an Observable to make serialized calls and to be well-behaved
 Subscribe — operate upon the emissions and notifications from an Observable
 SubscribeOn — specify the scheduler an Observable should use when it is subscribed to
 TimeInterval — convert an Observable that emits items into one that emits indications of the amount of time elapsed between those emissions
 Timeout — mirror the source Observable, but issue an error notification if a particular period of time elapses without any emitted items
 Timestamp — attach a timestamp to each item emitted by an Observable
 Using — create a disposable resource that has the same lifespan as the Observable

 Conditional and Boolean Operators
 Operators that evaluate one or more Observables or items emitted by Observables
 All — determine whether all items emitted by an Observable meet some criteria
 Amb — given two or more source Observables, emit all of the items from only the first of these Observables to emit an item
 Contains — determine whether an Observable emits a particular item or not
 DefaultIfEmpty — emit items from the source Observable, or a default item if the source Observable emits nothing
 SequenceEqual — determine whether two Observables emit the same sequence of items
 SkipUntil — discard items emitted by an Observable until a second Observable emits an item
 SkipWhile — discard items emitted by an Observable until a specified condition becomes false
 TakeUntil — discard items emitted by an Observable after a second Observable emits an item or terminates
 TakeWhile — discard items emitted by an Observable after a specified condition becomes false

 Mathematical and Aggregate Operators
 Operators that operate on the entire sequence of items emitted by an Observable
 Average — calculates the average of numbers emitted by an Observable and emits this average
 Concat — emit the emissions from two or more Observables without interleaving them
 Count — count the number of items emitted by the source Observable and emit only this value
 Max — determine, and emit, the maximum-valued item emitted by an Observable
 Min — determine, and emit, the minimum-valued item emitted by an Observable
 Reduce — apply a function to each item emitted by an Observable, sequentially, and emit the final value
 Sum — calculate the sum of numbers emitted by an Observable and emit this sum
 */

/**
 * Created by ruiqianqi on 2016/8/15 0015.
 * <p>
 * <T>要么指的是被传递进来的参数类型，要么指的是，像onError()方法这种情况里，异常可以抛出的类型
 * 功能接口全是 Action0 或者 Action1 . 类型。这意味着这些接口的单一方法不返回任何值并且要么没有参数，
 * 要么只有一个参数，视特定的事件而定。
 * 因为这些方法不返回任何值，他们不能被用来改变传递出来的数据，因此无论如何也不能改变字节流本身。
 * 相反这些方法将意图引起副作用方法，像在磁盘上写入一些东西、清空状态或者其他任何能操纵系统本身状态 ,
 * 而不是事件流本身的事情。
 * 注意：副作用方法本身（doOnNext(),doOnCompleted()等等）返回可观察事件，这保持了接口的流畅性。
 * 但是，这些副作用方法返回的可观察者和源观察者具有相同的类型，并且发射出同样的东西。
 * Method	            Functional Interface	Event
 * doOnSubscribe()	    Action0	                A subscriber subscribes to the Observable
 * doOnUnsubscribe()	Action0	                A subscriber unsubscribes from the subscription
 * doOnNext()	        Action1<T>	            The next item is emitted
 * doOnCompleted()	    Action0	                The Observable will emit no more items
 * doOnError()	        Action1<T>	            An error occurred
 * doOnTerminate()	    Action0	                Either an error occurred or the Observable will emit no more items
 * doOnEach()	        Action1<Notification<T>>	Either an item was emitted, the Observable completes or an error occurred. The Notification object contains information about the type of event
 * doOnRequest()	    Action1<Long>	        A downstream operator requests to emit more items
 * <p>
 * 使用doOnNext()来调试
 * 在flatMap()里使用doOnError()作为错误处理。
 * 使用doOnNext()去保存/缓存网络结果
 * <p>
 *
 * 数据流转换：
 * map 使用一个转换的参数把源Observable中的数据转换为【另外一种类型的数据】。返回的 Observable中包含了转换后的数据。
 * flatMap 把源 Observable 中的一个数据转换为一个【新的 Observable】 发射出去。
 * cast(Integer.class) 把Observable中的数据强制转换为另一种类型，如果遇到类型不一样的对象的话，就会抛出一个error。
 * ofType(Integer.class) 判断Observable中的数据是否为该类型，如果不是则跳过这个数据。
 * ofType = filter + cast
 * timestamp() 把数据转换为 Timestamped 类型，里面包含了原始的数据和一个原始数据是何时发射的时间戳。
 */
public class MyRxUtils {

    private static String TAG = MyRxUtils.class.getSimpleName();

    /*****************************【Transformer和Observable.compose()】****************************/
    /**
     * 统一对Observable进行设置
     * 统一设置子线程及主线程
     * 由compose操作符调用
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> handleSchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable
                        // 指定事件发生的线程
                        .subscribeOn(Schedulers.io())
                        // 指定回调的线程
                        .observeOn(AndroidSchedulers.mainThread());
                //.subscribeOn(Schedulers.from(scheduleTask.getSchedule()))
                //.subscribeOn(Schedulers.from(MyScheduleTask.getInstance().getSchedule()))
            }
        };
    }

    /**
     * 让任务执行在单独的线程，指定的线程池中
     *
     * @param executor
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> handleUniqueScheduler(final Executor executor) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                Scheduler scheduler;
                if (null != executor) {
                    scheduler = Schedulers.from(executor);
                } else {
                    scheduler = Schedulers.newThread();
                }
                return observable
                        // 指定事件发生的线程
                        .subscribeOn(scheduler)
                        // 指定回调的线程
                        .observeOn(AndroidSchedulers.mainThread());
                //.subscribeOn(Schedulers.from(scheduleTask.getSchedule()))
                //.subscribeOn(Schedulers.from(MyScheduleTask.getInstance().getSchedule()))
            }
        };
    }

    /****************************************【flatMap】*******************************************/

    /**
     * 对结果进行预处理，flatMap 还不知道怎么用
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<RxBaseResult<T>, T> handleResult() {
        return new Observable.Transformer<RxBaseResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<RxBaseResult<T>> observable) {
                return observable.flatMap(new Func1<RxBaseResult<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(RxBaseResult<T> result) {
                        if (result.success()) {
                            //Observable.just(result.getData());
                            return MyRxUtils.createData(result.getData());
                        } else {
                            return Observable.error(new Throwable(result.getMsg()));
                        }
                    }
                }).compose(MyRxUtils.<T>handleSchedulers());
            }
        };
    }

    /**
     * 发射数据
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Observable<T> createData(final T data) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }


    /**************************************【RX操作符】********************************************/
    /**
     * http://reactivex.io/documentation/operators.html
     *
     * 什么Single操作符
     *
     * <p>
     * Create — 通过调用观察者的方法从头创建一个Observable
     * Defer — 在观察者订阅之前不创建这个Observable，为每一个观察者创建一个新的Observable
     * Empty/Never/Throw — 创建行为受限的特殊Observable
     * From — 将其它的对象或数据结构转换为Observable
     * Interval — 创建一个定时发射整数序列的Observable
     * Just — 将对象或者对象集合转换为一个会发射这些对象的Observable
     * Range — 创建发射指定范围的整数序列的Observable
     * Repeat — 创建重复发射特定的数据或数据序列的Observable
     * Start — 创建发射一个函数的返回值的Observable
     * Timer — 创建在一个指定的延迟之后发射单个数据的Observable
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Single<T> single(final T data) {
        return Single.create(new Single.OnSubscribe<T>() {
            @Override
            public void call(SingleSubscriber<? super T> singleSubscriber) {
                try {
                    singleSubscriber.onSuccess(data);
                } catch (Exception e) {
                    singleSubscriber.onError(e);
                }
            }
        });
    }



    /**************************************【RX线程类型】******************************************/
    /**
     * 执行异步任务【无界线程池】
     * immediate(): Creates and returns a Scheduler that executes work immediately on the current thread.
     * trampoline(): Creates and returns a Scheduler that queues work on the current thread to be executed after the current work completes.
     * newThread(): Creates and returns a Scheduler that creates a new Thread for each unit of work.
     * computation(): Creates and returns a Scheduler intended for computational work. This can be used for event-loops, processing callbacks and other computational work. Do not perform IO-bound work on this scheduler. Use Schedulers.io() instead.
     * io(): Creates and returns a Scheduler intended for IO-bound work. The implementation is backed by an Executor thread-pool that will grow as needed. This can be used for asynchronously performing blocking IO. Do not perform computational work on this scheduler. Use Schedulers.computation() instead.
     * <p>
     * Questions:
     * The first 3 schedulers are pretty self explanatory; however, I’m a little confused about computation and io.
     * What exactly is “IO-bound work”? Is it used for dealing with streams (Java.io) and files (java.nio.files)? Is it used for database queries? Is it used for downloading files or accessing REST APIs?
     * How is computation() different from newThread()? Is it that all computation() calls are on a single (background) thread instead of a new (background) thread each time?
     * Why is it bad to call computation() when doing IO work?
     * Why is it bad to call io() when doing computational work?
     * <p>
     * Great questions, I think the documentation could do with some more detail.
     * io() is backed by an unbounded thread-pool and is the sort of thing you’d use for non-computationally intensive tasks, that is stuff that doesn’t put much load on the CPU. So yep interaction with the file system, interaction with databases or services on a different host are good examples.
     * computation() is backed by a bounded thread-pool with size equal to the number of available processors. If you tried to schedule cpu intensive work in parallel across more than the available processors (say using newThread()) then you are up for thread creation overhead and context switching overhead as threads vie for a processor and it’s potentially a big performance hit.
     * It’s best to leave computation() for CPU intensive work only otherwise you won’t get good CPU utilization.
     * It’s bad to call io() for computational work for the reason discussed in 2. io() is unbounded and if you schedule a thousand computational tasks on io() in parallel then each of those thousand tasks will each have their own thread and be competing for CPU incurring context switching costs.
     *
     * computation()是有界线池，等于CPU核心数
     * io()是无界线程池，来一个执行一个
     * @param runnable
     * @return 任务描述体
     */
    public static void doAsyncRun(final Runnable runnable) {
        // 对数据做改变，不改变流本身
        Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (null != runnable) {
                    runnable.run();
                }
            }
        });
    }

    /**
     * 执行异步延时任务【无界线程池】
     * @param runnable    任务体
     * @param delayTime   延时的毫秒数
     * @return 任务描述体  a subscription to be able to unsubscribe the action (unschedule it if not executed)
     */
    public static Subscription doAsyncRun(final Runnable runnable, long delayTime) {
        // 对数据做改变，不改变流本身
        return Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (null != runnable) {
                    runnable.run();
                }
            }
        }, delayTime, MILLISECONDS);
    }

    /**
     * 开新线程执行任务
     * @param runnable 任务体
     */
    public static void doNewThreadRun(final Runnable runnable) {
        // 对数据做改变，不改变流本身
        /*
        Schedulers.newThread().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (null != runnable) {
                    runnable.run();
                }
            }
        });
        */
        if (null != runnable) {
            new Thread(runnable).start();
        }
    }

    /**
     * 开新线程执行延时任务
     * @param runnable    任务体
     * @param delayTime   延时的毫秒数
     * @return 任务描述体  a subscription to be able to unsubscribe the action (unschedule it if not executed)
     */
    public static Subscription doNewThreadRun(final Runnable runnable, long delayTime) {
        // 对数据做改变，不改变流本身
        return Schedulers.newThread().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (null != runnable) {
                    runnable.run();
                }
            }
        }, delayTime, MILLISECONDS);
    }

    /**
     * 用传入的executor执行任务
     * @param executor 运行器
     * @param runnable 任务体
     */
    public static void doExecutorRun(Executor executor, final Runnable runnable){
        // 对数据做改变，不改变流本身
        Schedulers.from(executor).createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (null != runnable) {
                    runnable.run();
                }
            }
        });
    }

    /**
     * 用传入的executor执行延时任务
     * @param executor  运行器
     * @param runnable  任务体
     * @param delayTime 延时的毫秒数
     * @return 任务描述体  a subscription to be able to unsubscribe the action (unschedule it if not executed)
     */
    public static Subscription doExecutorRun(Executor executor, final Runnable runnable, long delayTime){
        // 对数据做改变，不改变流本身
        return Schedulers.from(executor).createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (null != runnable) {
                    runnable.run();
                }
            }
        }, delayTime, MILLISECONDS);
    }

    /**
     * 从源码中不能发现：
     * 原来它在finally里自动取消了订阅!!
     */
    /*
    public void onCompleted() {
        if (!done) {
            done = true;
            try {
                actual.onCompleted();
            } catch (Throwable e) {
                // we handle here instead of another method so we don't add stacks to the frame
                // which can prevent it from being able to handle StackOverflow
                Exceptions.throwIfFatal(e);
                // handle errors if the onCompleted implementation fails, not just if the Observable fails
                _onError(e);
            } finally {
                // auto-unsubscribe
                unsubscribe();
            }
        }
    }
    */

    /**
     * 同样，在出现error时，也会自动取消订阅。
     */
    /*
    protected void _onError(Throwable e) {
        RxJavaPluginUtils.handleException(e);
        try {
            actual.onError(e);
        } catch (Throwable e2) {
            if (e2 instanceof OnErrorNotImplementedException) {
                try {
                    unsubscribe();
                } catch (Throwable unsubscribeException) {
                    RxJavaPluginUtils.handleException(unsubscribeException);
                    throw new RuntimeException("Observer.onError not implemented and error while unsubscribing.", new CompositeException(Arrays.asList(e, unsubscribeException)));
                }
                throw (OnErrorNotImplementedException) e2;
            } else {
                RxJavaPluginUtils.handleException(e2);
                try {
                    unsubscribe();
                } catch (Throwable unsubscribeException) {
                    RxJavaPluginUtils.handleException(unsubscribeException);
                    throw new OnErrorFailedException("Error occurred when trying to propagate error to Observer.onError and during unsubscription.", new CompositeException(Arrays.asList(e, e2, unsubscribeException)));
                }

                throw new OnErrorFailedException("Error occurred when trying to propagate error to Observer.onError", new CompositeException(Arrays.asList(e, e2)));
            }
        }
        // if we did not throw above we will unsubscribe here, if onError failed then unsubscribe happens in the catch
        try {
            unsubscribe();
        } catch (RuntimeException unsubscribeException) {
            RxJavaPluginUtils.handleException(unsubscribeException);
            throw new OnErrorFailedException(unsubscribeException);
        }
    }
    */

    /**********************************************************************************************/
    /**
     * 执行异步任务【无界线程池】
     * @param rxTask      任务体
     * @param subscriber 订阅者，订阅返回结果
     */
    public static <T> void createTask(final MyRxTask<T> rxTask, Subscriber<T> subscriber){
        Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    T result = null;
                    if (null != rxTask) {
                        result = rxTask.run();
                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .compose(MyRxUtils.<T>handleSchedulers())
                .subscribe(subscriber);
    }

    /**
     * RX各种方法解析
     * @param rxTask
     */
    private static <T>void createTask(final MyRxTask<T> rxTask) {
        Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    T result = null;
                    if (null != rxTask) {
                        result = rxTask.run();
                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .compose(MyRxUtils.<T>handleSchedulers())
                /**
                 * 被观察者Observable.doOnSubscribe()
                 * 观察者Subscriber.onStart()
                 * 相同点：都是在 subscribe() 调用后而且在事件发送前执行
                 * 不同点：
                 * Subscriber.onStart() 在subscribe所发生的线程被调用，而不能指定线程；
                 * Observable.doOnSubscribe() 默认情况下，执行在 subscribe()发生的线程；
                 *
                 * 如果在 doOnSubscribe()【之后】有 subscribeOn() 的话，它将执行在离它最近的 subscribeOn()所指定的线程。
                 * （1）doOnSubscribe()之前的subscribeOn()，不会影响它。
                 * （2）doOnSubscribe()之后的subscribeOn()，且是最近的才会影响它。
                 */
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.e(TAG, "doOnSubscribe");
                    }
                })
                //【订阅者接收到事件后】
                // The next item is emitted
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T s) {
                        LogUtils.e(TAG, "doOnNext");
                    }
                })
                //【订阅者接收到事件后】
                // The Observable will emit no more items
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.e(TAG, "doOnCompleted");
                    }
                })
                //【订阅者接收到事件后】
                // An error occurred
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e(TAG, "doOnError");
                    }
                })
                //【订阅者接收到事件后】
                // Either an error occurred or the Observable will emit no more items
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.e(TAG, "doOnTerminate");
                    }
                })
                // 订阅事件
                .subscribe(new Subscriber<T>() {

                    @Override
                    public void onStart() {
                        LogUtils.e(TAG, "onStart");
                    }

                    @Override
                    public void onNext(T s) {
                        LogUtils.e(TAG, "onNext");
                    }

                    @Override
                    public void onCompleted() {
                        LogUtils.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "onError");
                    }
                });

    }
}
