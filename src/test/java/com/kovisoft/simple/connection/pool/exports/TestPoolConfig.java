package com.kovisoft.simple.connection.pool.exports;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPoolConfig {

    private static final String url = "url";
    private static final String user = "user";
    private static final String pass = "pass";

    @Test
    public void testEmptyConstructor(){
        Assertions.assertDoesNotThrow(() -> new PoolConfig());
        PoolConfig config = new PoolConfig();
        Assertions.assertTrue(config.getUrl() != null && config.getUrl().isBlank());
        Assertions.assertTrue(config.getUser()!= null && config.getUser().isBlank());
        Assertions.assertTrue(config.getPass()!= null && config.getPass().isBlank());
    }

    @Test
    public void testMainConstructor(){
        Assertions.assertDoesNotThrow(() -> new PoolConfig(url, user, pass));
        PoolConfig config = new PoolConfig(url, user, pass);
        Assertions.assertEquals(url, config.getUrl());
        Assertions.assertEquals(user, config.getUser());
        Assertions.assertEquals(pass, config.getPass());
    }

    @Test
    public void confirmDefaultsRemainAndCanBeSet(){
        PoolConfig configEmpty = new PoolConfig();
        PoolConfig configFull = new PoolConfig(url, user, pass);
        Assertions.assertEquals(configEmpty.getMinConnections(), configFull.getMinConnections());
        Assertions.assertEquals(configEmpty.getMaxConnections(), configFull.getMaxConnections());
        Assertions.assertEquals(configEmpty.getRequestsPerMinutePerConn(), configFull.getRequestsPerMinutePerConn());
        Assertions.assertEquals(configEmpty.getConnectionLifeSpan(), configFull.getConnectionLifeSpan());
        Assertions.assertEquals(configEmpty.getConnectionCheckIntervals(), configFull.getConnectionCheckIntervals());
        Assertions.assertEquals(configEmpty.getMaxCharacters(), configFull.getMaxCharacters());
        Assertions.assertEquals(configEmpty.getMaxCachedStatements(), configFull.getMaxCachedStatements());

        int defaultInt = 1;
        Assertions.assertDoesNotThrow(() -> configEmpty.setMinConnections(defaultInt));
        Assertions.assertEquals(defaultInt, configEmpty.getMinConnections());

        Assertions.assertDoesNotThrow(() -> configEmpty.setMaxConnections(defaultInt));
        Assertions.assertEquals(defaultInt, configEmpty.getMaxConnections());

        Assertions.assertDoesNotThrow(() -> configEmpty.setRequestsPerMinutePerConn(defaultInt));
        Assertions.assertEquals(defaultInt, configEmpty.getRequestsPerMinutePerConn());

        Assertions.assertDoesNotThrow(() -> configEmpty.setConnectionLifeSpan(defaultInt));
        Assertions.assertEquals(defaultInt, configEmpty.getConnectionLifeSpan());

        Assertions.assertDoesNotThrow(() -> configEmpty.setConnectionCheckIntervals(defaultInt));
        Assertions.assertEquals(defaultInt, configEmpty.getConnectionCheckIntervals());

        Assertions.assertDoesNotThrow(() -> configEmpty.setMaxCharacters(defaultInt));
        Assertions.assertEquals(defaultInt, configEmpty.getMaxCharacters());

        Assertions.assertDoesNotThrow(() -> configEmpty.setMaxCachedStatements(defaultInt));
        Assertions.assertEquals(defaultInt, configEmpty.getMaxCachedStatements());

        Assertions.assertDoesNotThrow(() -> configEmpty.setUrl(url));
        Assertions.assertEquals(url, configEmpty.getUrl());

        Assertions.assertDoesNotThrow(() -> configEmpty.setUser(user));
        Assertions.assertEquals(user, configEmpty.getUser());

        Assertions.assertDoesNotThrow(() -> configEmpty.setPass(pass));
        Assertions.assertEquals(pass, configEmpty.getPass());


    }
}
