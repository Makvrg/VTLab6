package ru.ifmo.se.application;

import ru.ifmo.se.io.output.CollectionActionsMessages;
import ru.ifmo.se.logger.AppLogger;

import java.util.logging.Level;

public class AppStarter {

    private final AppCompositionRoot appCompositionRoot =
            new AppCompositionRoot();

    public void start() {
        appCompositionRoot.getCollectionService()
                          .addShutdownListener(
                                  appCompositionRoot.getNetworkService()
                );
        appCompositionRoot.getCollectionService()
                .addShutdownListener(
                        appCompositionRoot.getTerminalPipeline()
                );
        appCompositionRoot.getCollectionService()
                .addShutdownListener(
                        appCompositionRoot.getConnectionManager()
                );
        try {
            appCompositionRoot.getCollectionInitializer().initialize();
        } catch (Throwable e) {
            AppLogger.LOGGER.log(
                    Level.SEVERE,
                    CollectionActionsMessages.VEHICLE_INIT_EXC
                            + e.getMessage(), e
            );
        }
        Thread networkThread = new Thread(() ->
                appCompositionRoot.getNetworkService().run()
        );

        Thread inputThread = new Thread(() ->
                appCompositionRoot.getTerminalPipeline().run()
        );

        networkThread.start();
        inputThread.start();
    }
}
