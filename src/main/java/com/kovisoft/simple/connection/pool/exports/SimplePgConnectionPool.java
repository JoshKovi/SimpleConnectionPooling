package com.kovisoft.simple.connection.pool.exports;

import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

public interface SimplePgConnectionPool extends SimpleConnectionPool {

    /**
     * Takes in a collection of prepared statement strings then caches
     * them as Prepared Statements on each connection if they match the
     * Pool Config criteria. The default implementation checks the
     * maxCachedStatements and maxCharacter per statement.
     *
     * @param prepStmts The prepared statements to add to the spooled
     *                  prepared statements on the connections.
     * @return Returns -1 if the statements would overfill the cache,
     * otherwise returns the amount added (after length check).
     */
    int addPreparedStatementsToPool(Collection<String> prepStmts);

}
