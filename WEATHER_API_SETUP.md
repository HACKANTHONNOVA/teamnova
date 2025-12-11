# Weather API Setup Guide

## Overview
The KawachYatri backend now integrates with **OpenWeatherMap API** to provide real-time, accurate weather indices for **all cities and villages in India**. This replaces the previous demo/hardcoded values with live data.

## Features
- ✅ Real-time weather data for any location in India
- ✅ Supports all cities, towns, and villages
- ✅ Calculates weather safety index (0-100) based on:
  - Temperature comfort (optimal: 20-30°C)
  - Humidity levels (optimal: 40-60%)
  - Wind speed
  - Weather conditions (clear, rain, thunderstorm, etc.)
- ✅ Intelligent fallback system for 50+ major Indian cities
- ✅ Free tier: 1,000 API calls per day

## Setup Instructions

### Step 1: Sign Up for OpenWeatherMap
1. Visit [OpenWeatherMap API](https://openweathermap.org/api)
2. Click **"Sign Up"** (top right corner)
3. Fill in your details:
   - Email address
   - Username
   - Password
4. Verify your email address

### Step 2: Get Your API Key
1. Log in to your OpenWeatherMap account
2. Navigate to **"API keys"** section (or go to https://home.openweathermap.org/api_keys)
3. You'll see a default API key already generated
4. Copy your API key (format: `1a2b3c4d5e6f7g8h9i0j`)
5. ⚠️ **Note**: It may take 10-30 minutes for your API key to activate

### Step 3: Add API Key to Backend
1. Open `backend.java`
2. Find the `generateWeatherIndex()` method (around line 210)
3. Replace this line:
   ```java
   String apiKey = "YOUR_API_KEY_HERE";
   ```
   with:
   ```java
   String apiKey = "YOUR_ACTUAL_API_KEY";
   ```

### Step 4: Test the Integration
1. Restart your backend server
2. Open your browser and navigate to:
   ```
   http://localhost:8080/api/indices?city=Mumbai
   ```
3. You should see real weather data:
   ```json
   {
     "city": "Mumbai",
     "securityIndex": 68,
     "weatherIndex": 73
   }
   ```

## How the Weather Index Works

The system fetches live data from OpenWeatherMap and calculates a **0-100 weather safety index**:

### Scoring Components:

1. **Temperature** (max +15 points)
   - Optimal: 20-30°C → +15
   - Good: 15-35°C → +10
   - Acceptable: 10-40°C → +5
   - Extreme: <10°C or >40°C → -10

2. **Humidity** (max +10 points)
   - Optimal: 40-60% → +10
   - Good: 30-70% → +5
   - Poor: >80% or <20% → -10

3. **Wind Speed** (max +5 points)
   - Calm: <5 m/s → +5
   - Strong: >15 m/s → -10

4. **Weather Condition** (max +10 points)
   - Clear → +10
   - Clouds → +5
   - Rain/Drizzle → -15
   - Thunderstorm/Snow → -25
   - Mist/Fog → -5

**Base Score:** 70 points

### Example Calculations:
- **Perfect Day**: Clear sky, 25°C, 50% humidity, 3 m/s wind → **100 points**
- **Good Day**: Partly cloudy, 28°C, 60% humidity → **85 points**
- **Moderate**: Cloudy, 33°C, 70% humidity → **70 points**
- **Poor**: Heavy rain, 22°C, 85% humidity → **45 points**

## Coverage

### With API Key:
- ✅ All Indian cities (e.g., Mumbai, Delhi, Bangalore)
- ✅ All towns (e.g., Shimla, Darjeeling, Ooty)
- ✅ All villages (e.g., Khajuraho, Hampi, Mandu)
- ✅ Any location name recognized by OpenWeatherMap

### Without API Key (Fallback):
The system includes pre-configured weather indices for 50+ major Indian cities:
- Major metros: Mumbai, Delhi, Bangalore, Chennai, Kolkata
- Tourist destinations: Goa, Shimla, Manali, Ooty, Darjeeling
- Heritage cities: Jaipur, Agra, Varanasi, Udaipur
- And many more...

## API Limits

**Free Tier** (what you get by default):
- 1,000 API calls per day
- 60 API calls per minute
- Current weather data
- No credit card required

If you need more:
- **Startup Plan**: $40/month - 100,000 calls/month
- **Developer Plan**: $120/month - 1,000,000 calls/month
- More info: https://openweathermap.org/price

## Troubleshooting

### "Weather API error: HTTP 401"
- Your API key is invalid or not activated yet
- Wait 10-30 minutes after signup
- Check that you copied the key correctly

### "Weather API error: HTTP 404"
- City name not found
- Try alternate spellings (e.g., "Bengaluru" vs "Bangalore")
- System will use fallback values automatically

### System uses fallback values
- Check that you replaced `YOUR_API_KEY_HERE` with your actual key
- Restart the backend server after adding the key
- Verify API key is active in OpenWeatherMap dashboard

## Example API Responses

### Request:
```
GET /api/indices?city=Goa
```

### Response with API (live data):
```json
{
  "city": "Goa",
  "securityIndex": 85,
  "weatherIndex": 82
}
```
*Weather index based on current conditions: 28°C, clear sky, 65% humidity*

### Response without API (fallback):
```json
{
  "city": "Goa",
  "securityIndex": 85,
  "weatherIndex": 82
}
```
*Weather index based on seasonal averages*

## Benefits of Real Weather Data

1. **Accuracy**: Live conditions instead of static values
2. **Dynamic**: Updates reflect current weather changes
3. **Comprehensive**: Works for any Indian location
4. **Smart Alerts**: Better trip planning based on real conditions
5. **Seasonal Variation**: Automatic adjustment for monsoons, summers, winters

## Security Note

⚠️ **Important**: Never commit your API key to public repositories!

For production:
- Use environment variables:
  ```java
  String apiKey = System.getenv("OPENWEATHER_API_KEY");
  ```
- Or use a configuration file (not tracked in git):
  ```java
  // config.properties
  openweather.api.key=your_key_here
  ```

## Support

- OpenWeatherMap Docs: https://openweathermap.org/api
- API Support: https://openweathermap.org/faq
- KawachYatri Backend: Check backend.java comments

---

**Last Updated**: December 2025  
**API Version**: OpenWeatherMap 2.5  
**Coverage**: All India (cities, towns, villages)
