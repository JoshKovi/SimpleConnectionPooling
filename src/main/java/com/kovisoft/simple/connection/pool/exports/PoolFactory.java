package com.kovisoft.simple.connection.pool.exports;

import com.kovisoft.simple.connection.pool.pg.ConnectionWrapperImpl;
import com.kovisoft.simple.connection.pool.pg.SimplePgConnectionPoolImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class PoolFactory {

    public static SimplePgConnectionPool createDefaultPgPool(String url, String user, String pass) throws SQLException {
        return createPgPool(new PoolConfig(url, user, pass));
    }
    public static SimplePgConnectionPool createPgPool(PoolConfig poolConfig) throws SQLException {
        return new SimplePgConnectionPoolImpl(poolConfig);
    }

    public static SimplePgConnectionPool createPgPool(PoolConfig poolConfig, Map<String, String> prepStatements) throws SQLException {
        return new SimplePgConnectionPoolImpl(poolConfig, prepStatements);
    }

    public static SimplePgConnectionPool createPgPool(PoolConfig poolConfig, Map<String, String> prepStatements,
                                                      Map<String, Integer> statementConstants) throws SQLException {
        return new SimplePgConnectionPoolImpl(poolConfig, prepStatements, statementConstants);
    }

    public static ConnectionWrapper createSingleConnectionWrapper(String url, String user, String pass) throws SQLException {
        return new ConnectionWrapperImpl(url, user, pass);
    }

}
