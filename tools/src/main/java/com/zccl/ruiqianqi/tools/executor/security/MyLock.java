package com.zccl.ruiqianqi.tools.executor.security;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ruiqianqi on 2016/8/25 0025.
 * Lock和synchronized的选择
 * 总结来说，Lock和synchronized有以下几点不同：
 　　1）Lock是一个接口，而synchronized是Java中的关键字，synchronized是内置的语言实现；
 　　2）synchronized在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象发生；而Lock在发生异常时，如果没有主动通过unLock()去释放锁，则很可能造成死锁现象，因此使用Lock时需要在finally块中释放锁；
 　　3）Lock可以让等待锁的线程响应中断，而synchronized却不行，使用synchronized时，等待的线程会一直等待下去，不能够响应中断；
 　　4）通过Lock可以知道有没有成功获取锁，而synchronized却无法办到。
 　　5）Lock可以提高多个线程进行读操作的效率。
 　　在性能上来说，如果竞争资源不激烈，两者的性能是差不多的，而当竞争资源非常激烈时（即有大量线程同时竞争），此时Lock的性能要远远优于synchronized。所以说，在具体使用时要根据适当情况选择。
 */
public class MyLock {

    /**
     * 类的标志
     */
    private static String TAG = MyLock.class.getSimpleName();
    /**
     * 可重入锁: 方法A访问了方法B，而两个方法都加了锁，访问B时就不需要再重新申请锁，可以直接使用，如果使用的是同一个锁的话。
     * 如果参数为true表示为公平锁，为fasle为非公平锁。默认情况下，如果使用无参构造器，则是非公平锁。
     * isFair()        // 判断锁是否是公平锁
     * isLocked()      // 判断锁是否被任何线程获取了
     * isHeldByCurrentThread() // 判断锁是否被当前线程获取了
     * hasQueuedThreads()      // 判断是否有线程在等待该锁
     */
    private final Lock lock;
    // 类Condition成员
    private final Condition condition;
    /**
     * 文件锁
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * 允许一组线程互相等待，直到到达某个公共屏障点
     */
    private CyclicBarrier cyclicBarrier;

    /**
     * 在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待
     *
     * CountDownLatch countDownLatch = new CountDownLatch(3);
     * 在需要等待的线程调用
     * countDownLatch.await();当然还有超时用法，也就是不一直等待
     *
     * 在其他需要处理任务的线程调用countDownLatch.countDown();
     * 在这里，同步线程数为3个，所以需要3个线程countDownLatch.countDown();
     * 等到为count为零时，调用await的线程就可以继续执行了
     */
    private CountDownLatch countDownLatch;

    private MyLock() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
        //condition.await();
        //condition.signal();
    }

    /**
     * lock()方法是平常使用得最多的一个方法，就是用来获取锁。如果锁已被其他线程获取，则进行等待。
     * 由于在前面讲到如果采用Lock，必须主动去释放锁，并且在发生异常时，不会自动释放锁。
     * 因此一般来说，使用Lock必须在try{}catch{}块中进行，并且将释放锁的操作放在finally块中进行，
     * 以保证锁一定被被释放，防止死锁的发生。
     *
     * @param runnable
     */
    public void doLockRun(Runnable runnable) {
        lock.lock();
        try {
            //处理任务
            runnable.run();

        } catch (Exception e) {

        } finally {
            lock.unlock();   //释放锁
        }
    }

    /**
     * tryLock()方法是有返回值的，它表示用来尝试获取锁，如果获取成功，则返回true，如果获取失败（即锁已被其他线程获取），则返回false，
     * 也就说这个方法无论如何都会立即返回。在拿不到锁时不会一直在那等待。
     *
     * @param runnable
     */
    public void doTryLockRun(Runnable runnable) {
        if (lock.tryLock()) {
            try {
                //处理任务
                runnable.run();
            } catch (Exception e) {

            } finally {
                lock.unlock();   //释放锁
            }
        } else {
            //如果不能获取锁，则直接做其他事情
            LogUtils.e(TAG, "获取锁失败");
        }
    }

    /**
     * lockInterruptibly()方法比较特殊，当通过这个方法去获取锁时，如果线程正在等待获取锁，则这个线程能够响应中断，
     * 即中断线程的等待状态。也就使说，当两个线程同时通过lock.lockInterruptibly()想获取某个锁时，假若此时线程A获取到了锁，
     * 而线程B只有在等待，那么对线程B调用threadB.interrupt()方法能够中断线程B的等待过程。
     * <p/>
     * 由于lockInterruptibly()的声明中抛出了异常，所以lock.lockInterruptibly()必须放在try块中或者在调用lockInterruptibly()的方法外声明抛出InterruptedException。
     * <p/>
     * 注意，当一个线程获取了锁之后，是不会被interrupt()方法中断的。因为本身在前面的文章中讲过单独调用interrupt()方法不能中断正在运行过程中的线程，只能中断阻塞过程中的线程。
     * 因此当通过lockInterruptibly()方法获取某个锁时，如果不能获取到，只有进行等待的情况下，是可以响应中断的。
     *
     * @param runnable
     * @throws InterruptedException
     */
    public void doLockInterruptRun(Runnable runnable) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            //处理任务
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 同步读文件
     * @param runnable
     */
    public void doLockRead(Runnable runnable){
        readWriteLock.readLock().lock();
        try {
            runnable.run();
        } catch (Exception e) {

        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * 同步写文件
     * @param runnable
     */
    public void doWriteRead(Runnable runnable){
        readWriteLock.writeLock().lock();
        try {
            runnable.run();
        } catch (Exception e) {

        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * 多个线程调用这个 cyclicBarrier.await(); 当然这个也有超时设置
     * 当数量达到threadNum，就可以执行runnable了，然后再各自往下执行
     * @param threadNum
     * @param runnable
     * @return
     */
    public CyclicBarrier createCyclicBarrier(int threadNum, Runnable runnable){
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum, runnable);
        return cyclicBarrier;
    }
}