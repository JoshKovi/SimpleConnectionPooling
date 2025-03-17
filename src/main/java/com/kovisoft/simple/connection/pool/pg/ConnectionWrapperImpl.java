package com.kovisoft.simple.connection.pool.pg;

import com.kovisoft.logger.exports.Logger;
import com.kovisoft.logger.exports.LoggerFactory;
import com.kovisoft.simple.connection.pool.exports.ConnectionWrapper;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ConnectionWrapperImpl implements ConnectionWrapper, AutoCloseable {

    private final Logger logger;
    private static final String GET_PID = "SELECT pid FROM pg_stat_activity WHERE pid = pg_backend_pid();";
    private static final int REPLACEMENT_WARNING = 2;
    private Integer pid;
    private Connection connection;
    private final LocalDateTime expiration;

    volatile boolean inUse = false;
    protected HashMap<String, PreparedStatement> preparedStatements = new HashMap<>();
    private boolean closed = false;

    @Override
    public boolean hasExpired(){
        return LocalDateTime.now().isAfter(expiration);
    }

    @Override
    public LocalDateTime getExpiration(){
        return expiration;
    }

    @Override
    public boolean isClosed(){
        try{
            closed = connection.isClosed();
        } catch (SQLException e) {
            logger.except("Connection threw exception on isClosed() check.", e);
            closed = true;
        }
        return closed;
    }

    @Override
    public Integer getPid(){
        return pid;
    }

    @Override
    public Connection borrowConnection() {
        if(inUse) return null;
        inUse = true;
        return connection;
    }

    @Override
    public boolean inUse(){
        return inUse;
    }

    @Override
    public void release() {
        inUse = false;
    }

    @Override
    public PreparedStatement getPreparedStatement(String keyOrStmtString) throws NullPointerException, SQLException {
        if(keyOrStmtString == null) throw new NullPointerException("Prepared statement keys cannot be null!");
        if(preparedStatements.containsKey(keyOrStmtString)){
            return preparedStatements.get(keyOrStmtString);
        } else {
            return connection.prepareStatement(keyOrStmtString);
        }
    }

    @Override
    public PreparedStatement getPreparedStatement(String keyOrStmtString, int statementConst) throws NullPointerException, SQLException {
        if(keyOrStmtString == null) throw new NullPointerException("Prepared statement keys cannot be null!");
        if(preparedStatements.containsKey(keyOrStmtString)){
            return preparedStatements.get(keyOrStmtString);
        } else {
            return connection.prepareStatement(keyOrStmtString, statementConst);
        }
    }

    /**
     * Not meant to be used for pooling, intended to allow a db init process to temporarily
     * use this connection wrapper amd then dispose of after single threaded init is completed.
     * @param url The url to the db.
     * @param user The user
     * @param pass the pass
     * @throws SQLException Thrown from creating connection.
     */
    public ConnectionWrapperImpl(String url, String user, String pass) throws SQLException {
        try{
            logger = LoggerFactory.createLogger(System.getProperty("user.dir") + "/logs",
                    "DB_pool_");
        } catch (IOException e) {
            throw new RuntimeException("Could not startup the Connection Wrapper logger!", e);
        }
        connection = DriverManager.getConnection(url, user, pass);
        this.expiration = LocalDateTime.now();
    }


    protected ConnectionWrapperImpl(String url, String user, String pass, int lifespanMinutes) throws SQLException {
        try{
            logger = LoggerFactory.createLogger(System.getProperty("user.dir") + "/logs",
                    "DB_pool_");
        } catch (IOException e) {
            throw new RuntimeException("Could not startup the Connection Wrapper logger!", e);
        }
        this.expiration = LocalDateTime.now().plusMinutes(lifespanMinutes);
        connection = DriverManager.getConnection(url, user, pass);
        setConnectionPid();
    }

    protected ConnectionWrapperImpl(String url, String user, String pass, int lifespanMinutes,
                                    Map<String, String> statements) throws SQLException {
        this(url, user, pass, lifespanMinutes);
        addPreparedStatements(statements);
    }

    protected ConnectionWrapperImpl(String url, String user, String pass, int lifespanMinutes,
                                    Map<String, String> statements, Map<String, Integer> constants) throws SQLException {
        this(url, user, pass, lifespanMinutes);
        addPreparedStatements(statements, constants);
    }

    public void addPreparedStatements(Map<String, String> prepStatements) throws SQLException {
        for(Map.Entry<String, String> entry : prepStatements.entrySet()){
            if(preparedStatements.containsKey(entry.getKey())) continue;
            if(entry.getValue() == null) continue;
            preparedStatements.put(entry.getKey(), connection.prepareStatement(entry.getValue()));
        }
    }

    public void addPreparedStatements(Map<String, String> prepStatements,
                                         Map<String, Integer> stmtConstants) throws SQLException {
        for(Map.Entry<String, String> entry : prepStatements.entrySet()){
            if(preparedStatements.containsKey(entry.getKey())) continue;
            if(entry.getValue() == null) continue;
            if(stmtConstants.containsKey(entry.getKey())){
                preparedStatements.put(entry.getKey(),
                        connection.prepareStatement(entry.getValue(), stmtConstants.get(entry.getKey())));
            } else {
                preparedStatements.put(entry.getKey(), connection.prepareStatement(entry.getValue()));
            }
        }
    }

    protected int countStatements(){
        return preparedStatements.size();
    }

    protected Connection getConnection(){
        return connection;
    }

    protected boolean notReadyForReplacement(){
        return !LocalDateTime.now().plusMinutes(REPLACEMENT_WARNING).isAfter(expiration);
    }

    protected boolean validate() throws SQLException {
        boolean valid = connection.isValid(1);
        logger.info("Connection validity: " + valid);
        return valid;
    }



    private void setConnectionPid() throws SQLException {
        PreparedStatement pStmt = connection.prepareStatement(GET_PID);
        ResultSet rs = pStmt.executeQuery();
        if(rs.next()){
            pid = rs.getInt(1);
        }
        if(pid == null) throw new SQLException("Could not retrieve pid for established connection");
        preparedStatements.put(GET_PID, pStmt);
    }

    @Override
    public void close() throws Exception {
        logger.info("Closing connection!");
        Exception lastException = null;
        if(preparedStatements != null){
            for(Map.Entry<String, PreparedStatement> entry : preparedStatements.entrySet()){
                try{
                    entry.getValue().close();
                } catch (Exception e){
                    String stmt = entry.getKey();
                    stmt = (stmt.length() > 64) ? stmt.substring(0,64) : stmt;
                    logger.except("Exception trying to close prepared statement: " + stmt, e);
                    lastException = e;
                }
            }
        }
        preparedStatements = null;
        try{
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        } catch (Exception e){
            logger.except("Exception trying to close connection!", e);
            lastException = e;
            connection = null;
        }
        if(lastException != null){
            throw lastException;
        }
        closed = true;
    }
}
