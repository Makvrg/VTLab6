package ru.ifmo.se;

import lombok.NoArgsConstructor;
import ru.ifmo.se.application.AppStarter;

@NoArgsConstructor
public class App {

    public static void main(String[] args) {
        AppStarter appStarter = new AppStarter();
        appStarter.start();
    }
}
