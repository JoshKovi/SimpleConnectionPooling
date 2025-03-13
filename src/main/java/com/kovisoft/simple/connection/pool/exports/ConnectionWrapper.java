package com.kovisoft.simple.connection.pool.exports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public interface ConnectionWrapper extends AutoCloseable {

    //These really don't matter outside the pool, but they are there if you want them for some reason;
    boolean hasExpired();
    LocalDateTime getExpiration();
    boolean isClosed();
    Integer getPid();
    Connection borrowConnection();
    boolean inUse();

    /**
     * Gets a prepared statement based on the string, is cached temporarily but does
     * not live long after released.
     * @param key The short key, in my case it is something like Table_Name-insert-many
     * @return The statement prepared on the connection, no need to close unless you desire to.
     * @throws NullPointerException Exception thrown for a null key
     */
    PreparedStatement getPreparedStatement(String key) throws NullPointerException;

}
