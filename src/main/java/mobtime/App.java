package mobtime;

public class App {

    public static void main(String[] args) {
        if (args != null) {
            processArguments(args);
        }

        System.out.println("Done");
    }

    private static void processArguments(String[] arguments) {
        for (var argument : arguments) {
            System.out.println(argument);

            if (argument.startsWith("--invalid")) {
                var msg = "Error: --invalid is not a valid argument";
                System.out.println(msg);
                throw new IllegalArgumentException(msg);
            }
        }
    }

}
