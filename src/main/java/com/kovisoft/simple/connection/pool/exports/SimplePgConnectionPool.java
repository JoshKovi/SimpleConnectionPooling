package com.kovisoft.simple.connection.pool.exports;


import java.util.Map;

public interface SimplePgConnectionPool extends SimpleConnectionPool {

    /**
     * Takes in a collection of prepared statement strings then caches
     * them as Prepared Statements on each connection if they match the
     * Pool Config criteria. The default implementation checks the
     * maxCachedStatements and maxCharacter per statement.
     *
     * @param prepStmts The prepared statements to add to the spooled
     *                  prepared statements on the connections. Key is
     *                  a short reference like table_name-insert-many
     * @return Returns -1 if the statements would overfill the cache,
     * otherwise returns the amount added (after length check).
     */
    int addPreparedStatementsToPool(Map<String, String> prepStmts);

    /**
     * Takes in a collection of prepared statement strings then caches
     * them as Prepared Statements on each connection if they match the
     * Pool Config criteria. The default implementation checks the
     * maxCachedStatements and maxCharacter per statement.
     *
     * @param prepStmts The prepared statements to add to the spooled
     *                  prepared statements on the connections. Key is
     *                  a short reference like table_name-insert-many
     * @param statmentConstMap A map with a matching key to prepStmts that
     *                         contains a statement constant like Statement.RETURN_GENERATE_KEYS
     * @return Returns -1 if the statements would overfill the cache,
     * otherwise returns the amount added (after length check).
     */
    int addPreparedStatementsToPool(Map<String, String> prepStmts, Map<String, Integer> statmentConstMap);

}
