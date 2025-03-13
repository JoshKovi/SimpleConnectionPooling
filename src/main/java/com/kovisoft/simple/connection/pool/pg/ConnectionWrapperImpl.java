package com.kovisoft.simple.connection.pool.pg;

import com.kovisoft.logger.exports.Logger;
import com.kovisoft.logger.exports.LoggerFactory;
import com.kovisoft.simple.connection.pool.exports.ConnectionWrapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ConnectionWrapperImpl implements ConnectionWrapper {

    private static final Logger logger = LoggerFactory.getLogger("DB_pool_");
    private static final String GET_PID = "SELECT pid FROM pg_stat_activity WHERE pid = pg_backend_pid();";
    private Integer pid;
    private Connection connection;
    private final LocalDateTime expiration;

    volatile boolean inUse = false;
    protected HashMap<String, PreparedStatement> preparedStatements = new HashMap<>();
    protected HashMap<String, PreparedStatement> tempStatements = new HashMap<>();
    private boolean closed = false;

    public boolean hasExpired(){
        return LocalDateTime.now().isAfter(expiration);
    }

    public LocalDateTime getExpiration(){
        return expiration;
    }

    public boolean isClosed(){
        return closed;
    }

    public Integer getPid(){
        return pid;
    }

    @Override
    public PreparedStatement getPreparedStatement(String stringStmt) throws SQLException {
        if(stringStmt == null) throw new SQLException("Prepared statements cannot be null!");
        if(preparedStatements.containsKey(stringStmt)){
            return preparedStatements.get(stringStmt);
        } else if(tempStatements.containsKey(stringStmt)){
            return tempStatements.get(stringStmt);
        } else {
            tempStatements.put(stringStmt, connection.prepareStatement(stringStmt));
            return tempStatements.get(stringStmt);
        }
    }

    @Override
    public PreparedStatement getPreparedStatement(String stringStmt, int statementConstant) throws SQLException {
        if(stringStmt == null) throw new SQLException("Prepared statements cannot be null!");
        if(preparedStatements.containsKey(stringStmt)){
            return preparedStatements.get(stringStmt);
        } else if(tempStatements.containsKey(stringStmt)){
            return tempStatements.get(stringStmt);
        } else {
            preparedStatements.put(stringStmt, connection.prepareStatement(stringStmt, statementConstant));
            return preparedStatements.get(stringStmt);
        }
    }


    protected ConnectionWrapperImpl(String url, String user, String pass, int lifespanMinutes) throws SQLException {
        this.expiration = LocalDateTime.now().plusMinutes(lifespanMinutes);
        connection = DriverManager.getConnection(url, user, pass);
        setConnectionPid();
    }

    protected ConnectionWrapperImpl(String url, String user, String pass, int lifespanMinutes, Set<String> statements) throws SQLException {
        this(url, user, pass, lifespanMinutes);
        addPreparedStatements(statements);
    }

    protected void addPreparedStatements(Set<String> prepStatements) throws SQLException {
        for(String stmt : prepStatements){
            if(preparedStatements.containsKey(stmt)) continue;
            preparedStatements.put(stmt, connection.prepareStatement(stmt));
        }
    }

    protected void addPreparedStatements(List<String> prepedStatements) throws SQLException {
        for(String stmt : prepedStatements){
            if(preparedStatements.containsKey(stmt)) continue;
            preparedStatements.put(stmt, connection.prepareStatement(stmt));
        }
    }

    protected Set<String> getPreparedStatementsList(){
        return preparedStatements.keySet();
    }

    protected int countStatements(){
        return preparedStatements.size();
    }

    protected Connection borrowConnection() {
        if(inUse) return null;
        inUse = true;
        return connection;
    }

    protected Connection getConnection(){
        return connection;
    }

    protected boolean notReadyForReplacement(){
        return !LocalDateTime.now().plusMinutes(2).isAfter(expiration);
    }

    protected void release() {
        inUse = false;
        for(Map.Entry<String, PreparedStatement> entry : tempStatements.entrySet()){
            try{
                entry.getValue().close();
            } catch (Exception e){
                String stmt = entry.getKey();
                stmt = (stmt.length() > 64) ? stmt.substring(0,64) : stmt;
                logger.except("Exception trying to close prepared statement: " + stmt, e);

            }
        }
        tempStatements.clear();
    }

    protected boolean isInUse(){
        return inUse;
    }

    protected boolean validate() throws SQLException {
        return connection.isValid(2);
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
