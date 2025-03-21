package com.kovisoft.simple.connection.pool.exports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

public interface ConnectionWrapper {

    //These really don't matter outside the pool, but they are there if you want them for some reason;
    boolean hasExpired();
    LocalDateTime getExpiration();
    boolean isClosed();
    Integer getPid();
    Connection borrowConnection();
    boolean inUse();
    void close() throws Exception;


    /**
     * Allows you to explicitly release a connection, can help with pool management,
     * not necessary in default pool implementation, but completely necessary if
     * using a single instance wrapper in things like db initialization.
     */
    void release();

    /**
     * Gets a prepared statement either as a key for a cached statement or as a raw string statement.
     * If the statement is not explicitly cached using addPreparedStatement on pool this statement
     * will not ever be cached
     * @param keyOrStmtString The short key, in my case it is something like Table_Name-insert-many
     *                        or a full prepared statement string. The string is not checked for validity.
     * @return The statement prepared on the connection, no need to close unless you desire to, or
     * you used a non cached statement.
     * @throws NullPointerException Exception thrown for a null key
     */
    PreparedStatement getPreparedStatement(String keyOrStmtString) throws NullPointerException, SQLException;

    /**
     * Gets a prepared statement either as a key for a cached statement or as a raw string statement.
     * If the statement is not explicitly cached using addPreparedStatement on pool this statement
     * will not ever be cached
     * @param keyOrStmtString The short key, in my case it is something like Table_Name-insert-many
     *                        or a full prepared statement string. The string is not checked for validity.
     * @param statementConst This is the Statement.Constant for a non cached statement, if the statement
     *                       is cached already with the const use the single variable version.
     * @return The statement prepared on the connection, no need to close unless you desire to, or
     * you used a non cached statement.
     * @throws NullPointerException Exception thrown for a null key
     */
    PreparedStatement getPreparedStatement(String keyOrStmtString, int statementConst)
            throws NullPointerException, SQLException;

    /**
     * When dealing with an individual CW it is useful to be able to add statements.
     * That being said these statements do not propagate to other connections.
     * @param prepStatements The prepared statement strings with their short name keys to prepare.
     * @throws SQLException Typical Prepared statement SQL Exception
     */
    void addPreparedStatements(Map<String, String> prepStatements) throws SQLException;

    /**
     * When dealing with an individual CW it is useful to be able to add statements.
     * That being said these statements do not propagate to other connections.
     * @param prepStatements The prepared statement strings to prepare with their short keys.
     * @param stmtConstants The prepared statement constants with their short keys.
     * @throws SQLException Typical Prepared statement SQL Exception
     */
    void addPreparedStatements(Map<String, String> prepStatements, Map<String, Integer> stmtConstants) throws SQLException;


}
