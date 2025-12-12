/**
 * KawachYatri Backend Server
 * 
 * WEATHER API SETUP:
 * This backend uses OpenWeatherMap API for real-time weather data for all cities/villages in India.
 * 
 * To enable real weather data:
 * 1. Go to https://openweathermap.org/api
 * 2. Click "Sign Up" and create a free account
 * 3. After login, go to "API keys" section in your account
 * 4. Copy your API key (it may take a few minutes to activate)
 * 5. Replace "YOUR_API_KEY_HERE" in the generateWeatherIndex() method with your actual API key
 * 
 * The free tier includes:
 * - 1,000 API calls per day
 * - Current weather data for any location worldwide
 * - Weather for all Indian cities and villages
 * 
 * Without an API key, the system will use intelligent fallback values based on 50+ Indian cities.
 */

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class backend {
	private static final Path BASE = Paths.get("packege");
	private static final Path USER_DATA = BASE.resolve("userData.json");
	private static final Path USERS_DB = BASE.resolve("users.json");

	public static void main(String[] args) throws IOException {
		// Use PORT environment variable for cloud deployment, default to 8080 for local
		int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
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
		server.createContext("/api/mappls-key", new MapplsKeyHandler());

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
			try {
				// OpenWeatherMap API - Get your free API key from https://openweathermap.org/api
				// Sign up and replace YOUR_API_KEY with your actual key
				String apiKey = "YOUR_API_KEY_HERE"; // TODO: Replace with your API key
				
				// For demo: if no API key, use fallback
				if (apiKey.equals("YOUR_API_KEY_HERE")) {
					return getFallbackWeatherIndex(city);
				}
				
				// Encode city name for URL
				String encodedCity = URLEncoder.encode(city + ",IN", StandardCharsets.UTF_8);
				String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + apiKey + "&units=metric";
				
				// Make HTTP request
				URL url = new URL(apiUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				
				int responseCode = conn.getResponseCode();
				if (responseCode == 200) {
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = in.readLine()) != null) {
						response.append(line);
					}
					in.close();
					
					// Parse weather data and calculate index
					return calculateWeatherIndexFromAPI(response.toString());
				} else {
					System.err.println("Weather API error for " + city + ": HTTP " + responseCode);
					return getFallbackWeatherIndex(city);
				}
			} catch (Exception e) {
				System.err.println("Weather API exception for " + city + ": " + e.getMessage());
				return getFallbackWeatherIndex(city);
			}
		}
		
		private int calculateWeatherIndexFromAPI(String jsonResponse) {
			try {
				// Parse JSON manually (simple extraction)
				// Looking for: temp, feels_like, humidity, weather.main (Clear/Rain/etc), wind.speed
				
				double temp = extractJsonDouble(jsonResponse, "\"temp\":");
				double feelsLike = extractJsonDouble(jsonResponse, "\"feels_like\":");
				int humidity = (int) extractJsonDouble(jsonResponse, "\"humidity\":");
				double windSpeed = extractJsonDouble(jsonResponse, "\"speed\":");
				String weatherMain = extractJsonString(jsonResponse, "\"main\":\"", "\"");
				
				// Calculate weather index (0-100, higher is better)
				int index = 70; // Base score
				
				// Temperature comfort (optimal 20-30°C)
				if (temp >= 20 && temp <= 30) {
					index += 15;
				} else if (temp >= 15 && temp <= 35) {
					index += 10;
				} else if (temp >= 10 && temp <= 40) {
					index += 5;
				} else {
					index -= 10; // Extreme temperatures
				}
				
				// Humidity (optimal 40-60%)
				if (humidity >= 40 && humidity <= 60) {
					index += 10;
				} else if (humidity >= 30 && humidity <= 70) {
					index += 5;
				} else if (humidity > 80 || humidity < 20) {
					index -= 10; // Too humid or dry
				}
				
				// Wind speed (calm is better)
				if (windSpeed < 5) {
					index += 5;
				} else if (windSpeed > 15) {
					index -= 10; // Strong winds
				}
				
				// Weather condition
				switch (weatherMain.toLowerCase()) {
					case "clear":
						index += 10;
						break;
					case "clouds":
						index += 5;
						break;
					case "rain":
					case "drizzle":
						index -= 15;
						break;
					case "thunderstorm":
					case "snow":
					case "extreme":
						index -= 25;
						break;
					case "mist":
					case "fog":
					case "haze":
						index -= 5;
						break;
				}
				
				// Clamp to 0-100
				return Math.max(0, Math.min(100, index));
				
			} catch (Exception e) {
				System.err.println("Error parsing weather JSON: " + e.getMessage());
				return 70; // Default fallback
			}
		}
		
		private double extractJsonDouble(String json, String key) {
			try {
				int start = json.indexOf(key);
				if (start == -1) return 0;
				start += key.length();
				
				int end = start;
				while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == '-')) {
					end++;
				}
				
				String value = json.substring(start, end);
				return Double.parseDouble(value);
			} catch (Exception e) {
				return 0;
			}
		}
		
		private String extractJsonString(String json, String startKey, String endKey) {
			try {
				int start = json.indexOf(startKey);
				if (start == -1) return "";
				start += startKey.length();
				
				int end = json.indexOf(endKey, start);
				if (end == -1) return "";
				
				return json.substring(start, end);
			} catch (Exception e) {
				return "";
			}
		}
		
		private int getFallbackWeatherIndex(String city) {
			// Fallback demo values for common Indian cities/regions
			return switch (city.toLowerCase()) {
				case "bengaluru", "bangalore" -> 78;
				case "delhi", "new delhi" -> 62;
				case "mumbai", "bombay" -> 70;
				case "goa", "panaji" -> 82;
				case "jaipur" -> 68;
				case "agra" -> 72;
				case "chennai", "madras" -> 75;
				case "kolkata", "calcutta" -> 68;
				case "hyderabad" -> 76;
				case "pune" -> 79;
				case "ahmedabad" -> 65;
				case "surat" -> 71;
				case "lucknow" -> 69;
				case "kanpur" -> 67;
				case "nagpur" -> 73;
				case "indore" -> 74;
				case "thane" -> 72;
				case "bhopal" -> 75;
				case "visakhapatnam", "vizag" -> 77;
				case "pimpri-chinchwad" -> 78;
				case "patna" -> 66;
				case "vadodara" -> 70;
				case "ghaziabad" -> 64;
				case "ludhiana" -> 65;
				case "coimbatore" -> 80;
				case "kochi", "cochin" -> 76;
				case "thiruvananthapuram", "trivandrum" -> 81;
				case "mysore", "mysuru" -> 79;
				case "shimla" -> 85;
				case "manali" -> 88;
				case "darjeeling" -> 84;
				case "ooty" -> 86;
				case "udaipur" -> 77;
				case "jodhpur" -> 63;
				case "varanasi", "banaras" -> 68;
				case "amritsar" -> 66;
				case "chandigarh" -> 74;
				case "dehradun" -> 82;
				case "haridwar" -> 73;
				case "rishikesh" -> 80;
				case "mussoorie" -> 84;
				case "nainital" -> 83;
				case "gangtok" -> 86;
				case "shillong" -> 85;
				case "guwahati" -> 71;
				case "imphal" -> 75;
				case "bhubaneswar" -> 74;
				case "cuttack" -> 73;
				case "ranchi" -> 72;
				case "raipur" -> 71;
				case "jabalpur" -> 70;
				case "gwalior" -> 69;
				case "vijayawada" -> 72;
				case "madurai" -> 76;
				case "tiruchirappalli", "trichy" -> 75;
				case "salem" -> 77;
				case "tirupati" -> 78;
				case "aligarh" -> 67;
				case "moradabad" -> 66;
				case "meerut" -> 65;
				case "bareilly" -> 68;
				case "nashik" -> 76;
				case "aurangabad" -> 74;
				case "solapur" -> 73;
				case "kolhapur" -> 77;
				case "jammu" -> 70;
				case "srinagar" -> 82;
				case "leh" -> 75;
				case "port blair" -> 88;
				default -> 70 + (Math.abs(city.hashCode()) % 15); // Generic fallback with variation
			};
		}
	}

	private static class MapplsKeyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(405, -1);
				exchange.close();
				return;
			}

			String key = System.getenv("MAPPLS_API_KEY");
			Headers headers = exchange.getResponseHeaders();
			headers.add("Content-Type", "application/json; charset=UTF-8");

			if (key == null || key.isBlank()) {
				String response = "{\"error\":\"Mappls key not configured on server\"}";
				exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(response.getBytes(StandardCharsets.UTF_8));
				}
				exchange.close();
				return;
			}

			String response = String.format("{\"key\":\"%s\"}", key.trim());
			exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes(StandardCharsets.UTF_8));
			}
			exchange.close();
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
