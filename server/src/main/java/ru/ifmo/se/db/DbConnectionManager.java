package ru.ifmo.se.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import ru.ifmo.se.event.ShutdownListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DbConnectionManager implements ShutdownListener {

    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    @Getter
    private final DataSource dataSource;

    public DbConnectionManager() {
        HikariConfig config = new HikariConfig();
        // ssh -L 15433:localhost:5432 s501393@helios.cs.ifmo.ru -p 2222
        // команда для прокидки SSH-тоннеля с ноутбука на Гелиос
        config.setJdbcUrl("jdbc:postgresql://localhost:15433/studs");
        config.setUsername("s501393");
        config.setPassword("fGh4nObU1w9Qsxb0");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(600000);
        config.setLeakDetectionThreshold(30000);
        config.setDriverClassName("org.postgresql.Driver");
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn == null) {
            conn = dataSource.getConnection();
            connectionHolder.set(conn);
        }
        return conn;
    }

    public void beginTransaction(int isolationLevel) throws SQLException {
        Connection conn = getConnection();
        if (!conn.getAutoCommit()) {
            throw new SQLException("Транзакция уже активна. Завершите её перед началом новой");
        }
        conn.setTransactionIsolation(isolationLevel);
        conn.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn == null) {
            throw new SQLException("Нет активного соединения для коммита");
        }
        if (conn.getAutoCommit()) {
            throw new SQLException("AutoCommit включён. Транзакция не была начата");
        }
        try {
            conn.commit();
        } finally {
            conn.setAutoCommit(true);
            connectionHolder.remove();
        }
    }

    public void rollback() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn == null) {
            throw new SQLException("Нет активного соединения для отката");
        }
        if (conn.getAutoCommit()) {
            throw new SQLException("AutoCommit включён. Транзакция не была начата");
        }
        try {
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
            connectionHolder.remove();
        }
    }

    public void close() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
            } finally {
                conn.setAutoCommit(true);
                conn.close();
                connectionHolder.remove();
            }
        }
    }

    public boolean isTransactionActive() throws SQLException {
        Connection conn = connectionHolder.get();
        return conn != null && !conn.getAutoCommit();
    }

    @Override
    public void onShutdown() {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}