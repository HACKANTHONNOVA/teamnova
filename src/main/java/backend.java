import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class backend {
    private static final Path BASE = resolveBase();
    private static final Path USER_DATA = BASE.resolve("userData.json");

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> redirect(exchange, "/login"));
        server.createContext("/login", new StaticFileHandler(BASE.resolve("login.html")));
        server.createContext("/home", new StaticFileHandler(BASE.resolve("homePage.html")));
        server.createContext("/trip-setup", new StaticFileHandler(BASE.resolve("setuptrip.html")));
        server.createContext("/trip-progress", new StaticFileHandler(BASE.resolve("tripprogress.html")));
        server.createContext("/quick-sos", new StaticFileHandler(BASE.resolve("quickSOS.html")));
        server.createContext("/profile", new StaticFileHandler(BASE.resolve("profile.html")));
        server.createContext("/api/sos", new SOSHandler());
        server.createContext("/api/user", new UserDataHandler());
        server.createContext("/api/indices", new IndicesHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server running on http://localhost:" + port);
    }

    private static Path resolveBase() {
        Path sourceBase = Paths.get("packege");
        if (Files.exists(sourceBase)) {
            return sourceBase;
        }
        Path targetBase = Paths.get("target", "classes", "packege");
        if (Files.exists(targetBase)) {
            return targetBase;
        }
        return sourceBase;
    }

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private static class StaticFileHandler implements HttpHandler {
        private final Path filePath;

        StaticFileHandler(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            if (!Files.exists(filePath)) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            byte[] body = Files.readAllBytes(filePath);
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        }
    }

    private static class SOSHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String sosData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("[SOS ALERT] " + sosData);
                
                String response = "{\"status\":\"SOS received\",\"message\":\"Emergency alert sent successfully\"}";
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }
    }

    private static class UserDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json; charset=UTF-8");

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                byte[] body = Files.readAllBytes(USER_DATA);
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
                exchange.close();
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                byte[] body = exchange.getRequestBody().readAllBytes();
                Files.writeString(USER_DATA, new String(body, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
                exchange.close();
                return;
            }

            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    }

    private static class IndicesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String city = "Bengaluru";
            if (query != null && query.contains("city=")) {
                city = query.split("city=")[1].split("&")[0];
                try {
                    city = java.net.URLDecoder.decode(city, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    // Keep original city
                }
            }

            // Generate demo indices based on city
            int securityIndex = generateSecurityIndex(city);
            int weatherIndex = generateWeatherIndex(city);

            String response = String.format(
                    "{\"city\":\"%s\",\"securityIndex\":%d,\"weatherIndex\":%d}",
                    city, securityIndex, weatherIndex
            );

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            exchange.close();
        }

        private int generateSecurityIndex(String city) {
            // Demo values for different cities
            return switch (city.toLowerCase()) {
                case "bengaluru" -> 72;
                case "delhi" -> 58;
                case "mumbai" -> 68;
                case "goa" -> 85;
                case "jaipur" -> 64;
                case "agra" -> 70;
                default -> 65 + (city.hashCode() % 20);
            };
        }

        private int generateWeatherIndex(String city) {
            // Demo values for different cities
            return switch (city.toLowerCase()) {
                case "bengaluru" -> 75;
                case "delhi" -> 62;
                case "mumbai" -> 70;
                case "goa" -> 80;
                case "jaipur" -> 68;
                case "agra" -> 72;
                default -> 70 + (city.hashCode() % 25);
            };
        }
    }
}
