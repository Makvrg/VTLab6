package ru.ifmo.se.io.input.init;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import ru.ifmo.se.logger.AppLogger;

import javax.sql.DataSource;
import java.util.logging.Level;

public class DbMigrator {

    public void migrate(DataSource dataSource) {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas("public")
                    .locations("classpath:db/migration")
                    .validateOnMigrate(true)
                    .load();

            MigrationInfoService infoService = flyway.info();
            AppLogger.LOGGER.info("Проверка состояния миграций");
            MigrationInfo[] allMigrations = infoService.all();
            for (MigrationInfo info : allMigrations) {
                AppLogger.LOGGER.info(
                        String.format("Версия: %s | Описание: %s | Состояние: %s%n",
                                info.getVersion(),
                                info.getDescription(),
                                info.getState()
                        )
                );
            }

            flyway.migrate();
            AppLogger.LOGGER.info("Миграции успешно применены");
        } catch (Exception e) {
            AppLogger.LOGGER.log(
                    Level.SEVERE,
                    "Ошибка при выполнении миграций: " + e.getMessage(), e);
            throw new IllegalStateException("Не удалось применить миграции", e);
        }
    }
}