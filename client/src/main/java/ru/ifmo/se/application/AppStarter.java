package ru.ifmo.se.application;

import java.util.List;

public class AppStarter {

    private final AppCompositionRoot appCompositionRoot =
            new AppCompositionRoot();

    public void start() {
        appCompositionRoot.getCommandInvoker()
                          .addListenersToExitCommand(
                                  List.of(appCompositionRoot.getPipeline())
                );
        appCompositionRoot.getPipeline().run();
    }
}
