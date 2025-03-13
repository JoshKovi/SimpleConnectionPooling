package com.kovisoft.simple.connection.pool.exports;

import java.io.Serializable;

public class PoolConfig  implements Serializable {

    private int minConnections = 2;
    private int maxConnections = 10;
    private int requestsPerMinutePerConn = 20;
    private int connectionLifeSpan = 30;
    private int connectionCheckIntervals = 50;
    private int maxCharacters = 2048;
    private int maxCachedStatements = 200;
    private String url;
    private String user;
    private String pass;

    public PoolConfig(){
        url = "";
        user = "";
        pass = "";
    }

    public PoolConfig(String url, String user, String pass){
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    public int getMinConnections() {
        return minConnections;
    }

    public void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getRequestsPerMinutePerConn() {
        return requestsPerMinutePerConn;
    }

    public void setRequestsPerMinutePerConn(int requestsPerMinutePerConn) {
        this.requestsPerMinutePerConn = requestsPerMinutePerConn;
    }

    public int getConnectionLifeSpan() {
        return connectionLifeSpan;
    }

    public void setConnectionLifeSpan(int connectionLifeSpan) {
        this.connectionLifeSpan = connectionLifeSpan;
    }

    public int getConnectionCheckIntervals() {
        return connectionCheckIntervals;
    }

    public void setConnectionCheckIntervals(int connectionCheckIntervals) {
        this.connectionCheckIntervals = connectionCheckIntervals;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }

    public int getMaxCachedStatements() {
        return maxCachedStatements;
    }

    public void setMaxCachedStatements(int maxCachedStatements) {
        this.maxCachedStatements = maxCachedStatements;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


}
