package net.agiledeveloper.mobtime.utils;

import java.time.Instant;

public class App {

    public static final AppLogger logger = new AppLogger(message -> System.out.println(message), Instant::now);

    private App() {}

}
