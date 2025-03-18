package com.kovisoft.simple.connection.pool.pg;

import com.kovisoft.logger.exports.Logger;
import com.kovisoft.logger.exports.LoggerFactory;
import com.kovisoft.simple.connection.pool.exports.ConnectionWrapper;
import com.kovisoft.simple.connection.pool.exports.PoolConfig;
import com.kovisoft.simple.connection.pool.exports.SimplePgConnectionPool;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;


import java.sql.SQLException;
import java.util.concurrent.*;


/**
 * This test takes 40 minutes total to run, it goes through 4 connection lifecycles each.
 * That being said, if you do decide to run it, I would do it while you are getting lunch.
 * It also is more of a functional test than a unit test, so don't be confused by the Junit.
 */
@EnabledIfEnvironmentVariable(named="enabled", matches="true", disabledReason = "This is a functional Pressure test that takes 40 minutes.")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConnectionPoolPressureTest {

    private final static String url = System.getenv("url");
    private final static String user = System.getenv("user");
    private final static String pass = System.getenv("pass");
    private static final PoolConfig config = new PoolConfig(url, user, pass);
    private static SimplePgConnectionPool pool;
    private static final Logger logger = LoggerFactory.createStaticLogger(System.getProperty("user.dir") + "/logs",
            "DB_pool_test_");
    private ConcurrentHashMap<Long, Exception> exceptionMap = new ConcurrentHashMap<>(20000);

    @BeforeAll
    public static void setupPressureTest() throws SQLException {
        config.setMaxConnections(50);
        config.setRequestsPerMinutePerConn(5);
        config.setConnectionLifeSpan(5);
        pool = new SimplePgConnectionPoolImpl(config);
    }

    @BeforeEach
    public void setupMap(){
        exceptionMap.clear();
    }


    @Test
    @Order(1)
    public void testPoolRecycling_with_release() {
        long startTime = System.currentTimeMillis();
        int runTime = 60000 * 20; // 5 minutes in milliseconds
        long endTime = startTime + runTime;

        logger.info("Start at: " + startTime + ", Finish at: " + endTime);

        ScheduledExecutorService testService = Executors.newScheduledThreadPool(20);
        testService.scheduleWithFixedDelay(() ->{
            try {
                ConnectionWrapper cw = pool.borrowConnection();
                boolean isValid = cw.getPreparedStatement("SELECT 1").executeQuery().next();
                cw.release();
                if(!isValid){
                    throw new IllegalStateException("Passed an invalid connection! At: " + System.currentTimeMillis());
                }
            } catch (Exception e){
                exceptionMap.put(System.currentTimeMillis(), e);
            }
        }, 20, 50, TimeUnit.MILLISECONDS);

        try{
            Thread.sleep(runTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Finished test at: " + System.currentTimeMillis());
        exceptionMap.forEach((time, exception) ->{
            logger.except(String.format("At: %d and exception occurred", time), exception);
        });
        Assertions.assertTrue(exceptionMap.isEmpty());

    }

    @Test
    @Order(2)
    public void testPoolRecycling_without_release() {
        long startTime = System.currentTimeMillis();
        int runTime = 60000 * 20; // 5 minutes in milliseconds
        long endTime = startTime + runTime;
        logger.info("Start at: " + startTime + ", Finish at: " + endTime);
        ScheduledExecutorService testService = Executors.newScheduledThreadPool(20);
        testService.scheduleWithFixedDelay(() -> {
            try {
                ConnectionWrapper cw = pool.borrowConnection();
                boolean isValid = cw.getPreparedStatement("SELECT 1").executeQuery().next();
                if (!isValid) {
                    throw new IllegalStateException("Passed an invalid connection! At: " + System.currentTimeMillis());
                }
            } catch (Exception e) {
                exceptionMap.put(System.currentTimeMillis(), e);
            }
        }, 20, 50, TimeUnit.MILLISECONDS);

        try {
            Thread.sleep(runTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Finished test at: " + System.currentTimeMillis());
        exceptionMap.forEach((time, exception) ->{
            logger.except(String.format("At: %d and exception occurred", time), exception);
        });

        Assertions.assertTrue(exceptionMap.isEmpty());
    }
}
