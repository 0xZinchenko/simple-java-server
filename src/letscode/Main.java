package letscode;

public class Main {
    public static void main(String[] args) {
        new Server((req, resp) -> {
            return "<html><body><h1>Hello, silver</h1></body></html>";
        }).bootstrap();

    }
}

