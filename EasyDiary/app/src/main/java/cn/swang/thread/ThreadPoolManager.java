
package cn.swang.thread;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    public static final int ASYNC_EXECUTOR_LEVEL_URGENT = 0;

    public static final int ASYNC_EXECUTOR_LEVEL_LOCAL_IO = 1;

    public static final int ASYNC_EXECUTOR_LEVEL_NETWORK = 2;

    public static final int ASYNC_EXECUTOR_LEVEL_IMAGE = 3;

    private static ThreadPoolExecutor sExecutors[] = new ThreadPoolExecutor[ASYNC_EXECUTOR_LEVEL_IMAGE + 1];

    public static void init() {
        final ThreadPoolExecutor backupExe = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        RejectedExecutionHandler rehHandler = new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                backupExe.execute(r);
                Log.v("cn.swang","Thread pool executor: reject work, put into backup pool");
            }
        };
        sExecutors[ASYNC_EXECUTOR_LEVEL_URGENT] = new ThreadPoolExecutor(3, 3, 60,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), rehHandler);
        sExecutors[ASYNC_EXECUTOR_LEVEL_LOCAL_IO] = new ThreadPoolExecutor(3, 10, 60,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), rehHandler);
        sExecutors[ASYNC_EXECUTOR_LEVEL_NETWORK] = new ThreadPoolExecutor(3, 10, 60,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), rehHandler);
        sExecutors[ASYNC_EXECUTOR_LEVEL_IMAGE] = new ThreadPoolExecutor(3, 10, 60,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), rehHandler);
    }

    public static Executor getExecutorByLevel(int level) {
        if (level < ASYNC_EXECUTOR_LEVEL_URGENT || level > ASYNC_EXECUTOR_LEVEL_IMAGE) {
            throw new IllegalArgumentException("wrong level");
        }
        return sExecutors[level];
    }

    /**
     *
     * @param runnable
     */
    public static void execute(final Runnable runnable, int level) {
        getExecutorByLevel(level).execute(runnable);
    }

    /**
     *
     * @param task
     */
    public static void execute(final AsyncTask task, final int level) {
        execute(new Runnable() {
            @Override
            public void run() {
                task.execute();
            }
        }, level);
    }

}
