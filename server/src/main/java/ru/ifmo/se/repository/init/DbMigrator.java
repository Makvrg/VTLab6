package ru.ifmo.se.repository.init;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSetStatus;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.ifmo.se.logger.AppLogger;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

public class DbMigrator {

    public void migrate(Connection connection) {
        try {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            String changelogFile = "changelog/db.changelog-master.xml";
            Liquibase liquibase = new Liquibase(
                    changelogFile,
                    new ClassLoaderResourceAccessor(),
                    database
            );

            AppLogger.LOGGER.info("Проверка состояния миграций");
            List<ChangeSetStatus> statuses = liquibase.getChangeSetStatuses(null, null);
            for (ChangeSetStatus status : statuses) {
                AppLogger.LOGGER.info(String.format(
                        "ChangeSet: %s | Автор: %s | Описание: %s | Статус: %s | ID: %s",
                        status.getChangeSet().getId(),
                        status.getChangeSet().getAuthor(),
                        status.getChangeSet().getDescription(),
                        status.getWillRun() ? "NOT APPLIED" : "APPLIED",
                        status.getChangeSet().getChangeLog()
                ));
            }

            liquibase.update("");
            AppLogger.LOGGER.info("Миграции успешно применены");

        } catch (LiquibaseException e) {
            AppLogger.LOGGER.log(
                    Level.SEVERE,
                    "Ошибка при выполнении миграций: " + e.getMessage(), e);
            throw new IllegalStateException("Не удалось применить миграции", e);
        }
    }
}