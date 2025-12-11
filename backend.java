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
	private static final Path BASE = Paths.get("packege");
	private static final Path USER_DATA = BASE.resolve("userData.json");
	private static final Path USERS_DB = BASE.resolve("users.json");

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
		server.createContext("/api/register", new RegisterHandler());
		server.createContext("/api/indices", new IndicesHandler());

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

	private static class SOSHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
				String sosData = new String(exchange.getRequestBody().readAllBytes());
				System.out.println("[SOS ALERT] " + sosData);
				
				String response = "{\"status\":\"SOS received\",\"message\":\"Emergency alert sent successfully\"}";
				exchange.getResponseHeaders().add("Content-Type", "application/json");
				exchange.sendResponseHeaders(200, response.length());
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(response.getBytes());
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
	private static class RegisterHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(405, -1);
				exchange.close();
				return;
			}

			byte[] body = exchange.getRequestBody().readAllBytes();
			String json = new String(body, StandardCharsets.UTF_8);

			// Basic validation and secure transformations
			// Expected fields: name, aadhaar, phone, guardian, pin
			String safeJson = secureUserJson(json);

			ensureFile(USERS_DB);
			String existing = Files.readString(USERS_DB, StandardCharsets.UTF_8);
			String updated;
			if (existing == null || existing.isBlank()) {
				updated = "[" + safeJson + "]";
			} else {
				// append to JSON array (naive but sufficient for demo)
				String trimmed = existing.trim();
				if (trimmed.endsWith("]")) {
					updated = trimmed.substring(0, trimmed.length() - 1) + "," + safeJson + "]";
				} else {
					updated = "[" + safeJson + "]";
				}
			}
			Files.writeString(USERS_DB, updated, StandardCharsets.UTF_8);

			String response = "{\"status\":\"ok\",\"message\":\"User registered securely\"}";
			exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
			exchange.sendResponseHeaders(200, response.getBytes().length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes(StandardCharsets.UTF_8));
			}
			exchange.close();
		}

		private void ensureFile(Path p) throws IOException {
			if (!Files.exists(p)) {
				Files.createFile(p);
				Files.writeString(p, "", StandardCharsets.UTF_8);
			}
		}

		private String secureUserJson(String rawJson) {
			// Very small JSON handling without external libs: extract fields by keys
			String name = extract(rawJson, "name");
			String aadhaar = extract(rawJson, "aadhaar");
			String phone = extract(rawJson, "phone");
			String guardian = extract(rawJson, "guardian");
			String pin = extract(rawJson, "pin");

			String maskedAadhaar = maskAadhaar(aadhaar);
			String hashedPin = sha256(pin);

			return String.format("{\"name\":\"%s\",\"aadhaarMasked\":\"%s\",\"phone\":\"%s\",\"guardian\":\"%s\",\"pinHash\":\"%s\"}",
				nullable(name), nullable(maskedAadhaar), nullable(phone), nullable(guardian), nullable(hashedPin)
			);
		}

		private String extract(String json, String key) {
			// naive extraction: "key":"value"
			String needle = "\"" + key + "\":";
			int i = json.indexOf(needle);
			if (i == -1) return "";
			int start = json.indexOf('"', i + needle.length());
			if (start == -1) return "";
			int end = json.indexOf('"', start + 1);
			if (end == -1) return "";
			return json.substring(start + 1, end);
		}

		private String maskAadhaar(String a) {
			if (a == null) return "";
			String digits = a.replaceAll("[^0-9]", "");
			if (digits.length() < 4) return "***";
			String last4 = digits.substring(digits.length() - 4);
			return "********" + last4; // show only last 4
		}

		private String sha256(String s) {
			if (s == null) return "";
			try {
				java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
				byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
				StringBuilder sb = new StringBuilder();
				for (byte b : hash) sb.append(String.format("%02x", b));
				return sb.toString();
			} catch (Exception e) {
				return "";
			}
		}

		private String nullable(String s) { return s == null ? "" : s; }
	}
}
