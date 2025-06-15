package net.agiledeveloper;

import net.agiledeveloper.mobtime.utils.AppLogger;

import java.time.Instant;

public class App {

    public static final AppLogger logger = new AppLogger(message -> System.out.println(message), Instant::now);

    private App() {}

}
