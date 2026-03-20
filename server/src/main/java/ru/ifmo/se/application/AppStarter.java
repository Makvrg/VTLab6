package ru.ifmo.se.application;

import ru.ifmo.se.io.output.CollectionActionsMessages;
import ru.ifmo.se.logger.AppLogger;

import java.io.IOException;
import java.util.logging.Level;

public class AppStarter {

    private final AppCompositionRoot appCompositionRoot =
            new AppCompositionRoot();

    public void start() {
        appCompositionRoot.getCollectionService()
                          .addShutdownListener(
                                  appCompositionRoot.getNetworkService()
                );
        try {
            appCompositionRoot.getCollectionInitializer().initialize();
        } catch (IOException e) {
            AppLogger.LOGGER.log(
                    Level.SEVERE,
                    CollectionActionsMessages.VEHICLE_INIT_OPEN_FILE_EXC
                            + e.getMessage()
            );
        }
        appCompositionRoot.getNetworkService().run();
    }
}
