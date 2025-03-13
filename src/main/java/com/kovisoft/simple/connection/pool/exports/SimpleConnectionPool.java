package com.kovisoft.simple.connection.pool.exports;

import java.sql.Connection;
import java.sql.SQLException;

public interface SimpleConnectionPool extends AutoCloseable{

    /**
     * Borrows a connection from the pool. The default pg implementation
     * marks this connection in use, then reclaims it when the connection
     * becomes idle as returned by pg_stat_activity. Default 20 ms poll time.
     * @return The borrowed connection.
     * @throws SQLException Thrown when no adequate connections were available for use in the queue.
     * @throws InterruptedException If interrupted while waiting for connection.
     */
    Connection borrowConnection() throws SQLException, InterruptedException;

    /**
     * Borrows a connection from the pool. The default pg implementation
     * marks this connection in use, then reclaims it when the connection
     * becomes idle as returned by pg_stat_activity
     * @param millis The amount of time (in milliseconds) before testing next connection.
     *               This can result in up to millis * maxConnections wait time.
     * @return The borrowed connection.
     * @throws SQLException Thrown when no adequate connections were available for use in the queue.
     * @throws InterruptedException If interrupted while waiting for connection.
     */
    Connection borrowConnection(long millis) throws SQLException, InterruptedException;


    /**
     * Tells the connection pool to shut down.
     */
    void shutDownPool();


}
