package com.kovisoft.simple.connection.pool.exports;

import com.kovisoft.simple.connection.pool.pg.SimplePgConnectionPoolImpl;

import java.sql.SQLException;

public class PoolFactory {

    public static SimplePgConnectionPool createDefaultPgPool(String url, String user, String pass) throws SQLException {
        return createPgPool(new PoolConfig(url, user, pass));
    }
    public static SimplePgConnectionPool createPgPool(PoolConfig poolConfig) throws SQLException {
        return new SimplePgConnectionPoolImpl(poolConfig);
    }
}
