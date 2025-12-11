import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class backend {
	private static final Path BASE = Paths.get("packege");

	public static void main(String[] args) throws IOException {
		int port = 8080;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

		server.createContext("/", exchange -> redirect(exchange, "/login"));
		server.createContext("/login", new StaticFileHandler(BASE.resolve("login.html")));
		server.createContext("/home", new StaticFileHandler(BASE.resolve("homePage.html")));
		server.createContext("/trip-setup", new StaticFileHandler(BASE.resolve("setuptrip.html")));

		server.setExecutor(null);
		server.start();
		System.out.println("Server running on http://localhost:" + port);
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
}
