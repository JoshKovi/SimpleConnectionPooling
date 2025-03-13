module SimpleConnectionPool {
    requires java.sql;
    requires org.postgresql.jdbc;
    requires Logger;
    exports com.kovisoft.simple.connection.pool.exports;
}