package com.kovisoft.simple.connection.pool.exports;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public interface ConnectionWrapper extends AutoCloseable {

    //These really don't matter outside the pool, but they are there if you want them for some reason;
    boolean hasExpired();
    LocalDateTime getExpiration();
    boolean isClosed();
    Integer getPid();

    /**
     * Gets a prepared statement based on the string, is cached temporarily but does
     * not live long after released.
     * @param stringStmt The statement to prepare.
     * @return The statement prepared on the connection, no need to close unless you desire to.
     * @throws SQLException Typical SQL Exception when these are created apply
     */
    PreparedStatement getPreparedStatement(String stringStmt) throws SQLException;

    /**
     * This technically can bypass the caching limit in the default implementation so use carefully,
     * as this was the most convenient way to handle the limited use case I had for
     * these keyed statements.
     * @param stringStmt The traditional String of the prepared statement
     * @param statementConstant The integer related to the constant I.E Statement.RETURN_GENERATED_KEYS = 1
     * @return The statement prepared on the connection, no need to close unless you desire to.
     * @throws SQLException Typical SQL Exception when these are created apply
     */
    PreparedStatement getPreparedStatement(String stringStmt, int statementConstant) throws SQLException;

}
