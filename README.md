# KawachYatri - Smart Tourist Safety System

A comprehensive safety platform for travelers in India with AI-powered monitoring, real-time weather integration, and emergency SOS features.

## 🌟 Key Features

- **Real-Time Weather Integration**: Live weather data for all Indian cities/villages via OpenWeatherMap API
- **AI Safety Indices**: Dynamic security and weather safety scores (0-100 scale)
- **Geo-Fencing**: Location-based safety monitoring with accurate GPS tracking (≤1km accuracy)
- **Smart Trip Setup**: Plan trips with destination-specific safety insights
- **Emergency SOS**: Quick emergency alerts to guardians and authorities
- **User Profiles**: Secure user data management and trip history
- **Mobile-First UI**: Responsive design with dark/light themes
- **Live Location Tracking**: Continuous GPS monitoring during trips

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Modern web browser

### Running the Server
```bash
javac backend.java
java backend
```

Server starts at: `http://localhost:8080`

## 🌤️ Weather API Setup

The system now uses **OpenWeatherMap API** for real-time weather data across all Indian locations.

### Get Started:
1. Sign up at [OpenWeatherMap](https://openweathermap.org/api) (free)
2. Get your API key from the dashboard
3. Add it to `backend.java` (line 210)
4. Restart the server

📖 **Detailed Instructions**: See [WEATHER_API_SETUP.md](WEATHER_API_SETUP.md)

**Without API Key**: System uses intelligent fallback values for 50+ major Indian cities.

## 📱 Pages

- `/login` - User registration/login
- `/home` - Dashboard with trip management
- `/trip-setup` - Configure new trips with AI safety estimates
- `/trip-progress` - Live trip monitoring
- `/quick-sos` - Emergency SOS without active trip
- `/profile` - User profile management

## 🔌 API Endpoints

### `/api/indices?city=<cityname>`
Get safety and weather indices for any Indian location.

**Example**:
```bash
curl "http://localhost:8080/api/indices?city=Mumbai"
```

**Response**:
```json
{
  "city": "Mumbai",
  "securityIndex": 68,
  "weatherIndex": 73
}
```

### `/api/user` (GET/POST)
Retrieve or update user profile data.

### `/api/sos` (POST)
Trigger emergency SOS alert.

## 🎨 Features in Detail

### Real-Time Weather Indices
- Temperature comfort analysis
- Humidity level assessment
- Wind speed monitoring
- Weather condition evaluation (clear, rain, storm, etc.)
- Automatic scoring (0-100, higher = better conditions)

### Location Services
- High-accuracy GPS detection (targeting ≤1km)
- Reverse geocoding for human-readable addresses
- Continuous tracking with status updates
- Mobile-responsive location controls

### Smart Trip Planning
- Date/time restrictions (no past dates)
- Geo-fence radius configuration
- AI-powered safety estimates for destinations
- Multi-traveler support
- Trip notes and mode selection

## 🛡️ Security

- Aadhaar masking (shows only last 4 digits)
- PIN hashing (SHA-256)
- Secure user data storage
- No sensitive data in localStorage

## 📂 Project Structure

```
teamnova/
├── backend.java              # Main server with API endpoints
├── packege/
│   ├── homePage.html         # Dashboard
│   ├── login.html            # Login/registration
│   ├── setuptrip.html        # Trip planning with AI
│   ├── tripprogress.html     # Live trip monitoring
│   ├── quickSOS.html         # Emergency SOS
│   ├── profile.html          # User profile
│   └── userData.json         # User data storage
├── WEATHER_API_SETUP.md      # Weather API documentation
└── README.md                 # This file
```

## 🔧 Configuration

### Theme
- Auto-detects device preference
- Persistent across sessions
- Toggle available on all pages

### Storage
- `localStorage.theme` - Theme preference
- `localStorage.profile_history_v1` - User profile
- `localStorage.activeTrip` - Current trip data

## 🌍 Coverage

Works for **all locations in India**:
- ✅ Major cities (Mumbai, Delhi, Bangalore, etc.)
- ✅ Tourist destinations (Goa, Shimla, Manali, etc.)
- ✅ Towns and villages
- ✅ Remote areas (with GPS coordinates fallback)

## 🤝 Contributing

This is a hackathon project for **Team Nova**. Contributions are welcome!

## 📄 License

Created for HACKATHONNOVA 2025

---

**Tech Stack**: Java (backend), Vanilla JS/HTML/CSS (frontend), OpenWeatherMap API (weather data)