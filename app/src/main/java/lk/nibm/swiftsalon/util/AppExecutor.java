package lk.nibm.swiftsalon.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutor {

    private static AppExecutor instance;
    private final Executor diskIO = Executors.newSingleThreadExecutor();
    private final Executor mainThreadExecutor = new MainThreadExecutor();
    private final ScheduledExecutorService networkIO = Executors.newScheduledThreadPool(3);

    public static AppExecutor getInstance() {
        if (instance == null) {
            instance = new AppExecutor();
        }
        return instance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThreadExecutor;
    }

    public ScheduledExecutorService getNetworkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}

