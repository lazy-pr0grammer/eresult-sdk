package com.eresult.sdk.query;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Anindya Das on 1/17/24 6:55 AM
 **/
public class LazyQuery {
    public static <T> T query(Callable<T> callable, Class<T> clazz) {
        try {
            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<T> future = service.submit(callable);
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
