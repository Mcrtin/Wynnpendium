package wynn.pendium.utils;



import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sk1er
 */
public class Multithreading {
    private static final AtomicInteger counter;
    private static final ScheduledExecutorService RUNNABLE_POOL;
    public static ThreadPoolExecutor POOL;

    public static ScheduledFuture<?> schedule(final Runnable r, final long initialDelay, final long delay, final TimeUnit unit) {
        return Multithreading.RUNNABLE_POOL.scheduleAtFixedRate(r, initialDelay, delay, unit);
    }

    public static ScheduledFuture<?> schedule(final Runnable r, final long delay, final TimeUnit unit) {
        return Multithreading.RUNNABLE_POOL.schedule(r, delay, unit);
    }

    public static void runAsync(final Runnable runnable) {
        Multithreading.POOL.execute(runnable);
    }

    public static Future<?> submit(final Runnable runnable) {
        return Multithreading.POOL.submit(runnable);
    }

    static {
        counter = new AtomicInteger(0);
        RUNNABLE_POOL = Executors.newScheduledThreadPool(10, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				// TODO Auto-generated method stub
				return new Thread(r, "Client Thread " + Multithreading.counter.incrementAndGet());
			}
		}) ;
        Multithreading.POOL = new ThreadPoolExecutor(50, 50, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), 
        		new ThreadFactory() {
					
					@Override
					public Thread newThread(Runnable r) {
						// TODO Auto-generated method stub
						return new Thread(r, String.format("Thread %s", Multithreading.counter.incrementAndGet()));
					}
				});
    }
}