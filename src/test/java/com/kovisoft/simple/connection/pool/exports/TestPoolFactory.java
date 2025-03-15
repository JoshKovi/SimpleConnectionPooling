package com.kovisoft.simple.connection.pool.exports;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestPoolFactory {

        private final static String url = System.getenv("url");
        private final static String user = System.getenv("user");
        private final static String pass = System.getenv("pass");
        private static final PoolConfig config = new PoolConfig(url, user, pass);
        private static SimplePgConnectionPool pool;

        /*
            I'll add additional tests later but it's not my priority at the moment.
         */

        @BeforeAll
        public static void setupPool(){
            config.setConnectionLifeSpan(1); //This should be one minute.
            Assertions.assertDoesNotThrow(() -> { pool = PoolFactory.createPgPool(config);});
        }

        @AfterAll
        public static void tearDownPool() {
            Assertions.assertDoesNotThrow(() -> pool.shutDownPool());
        }


        @Test
        public void testCreateDefaultPool(){
            Assertions.assertDoesNotThrow(() -> {
                SimplePgConnectionPool pool = PoolFactory.createDefaultPgPool(url, user, pass);
                Assertions.assertNotNull(pool);
                Assertions.assertNotNull(pool.borrowConnection());
                Map<String, String> testMap = new HashMap<>(){{put("key", "SELECT 1");}};
                Assertions.assertEquals(1, pool.addPreparedStatementsToPool(testMap));
                pool.shutDownPool();
            });
        }

}
