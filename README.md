
# 🗺️ Map & Navigation Application (Android)

An Android-based map and navigation application built using **OpenStreetMap (OSM)** and **OSRM**, providing real-time location tracking, destination search, route navigation, re-routing, and offline route support implemented without using Google Maps APIs.

---

##  Project Overview

This project aims to design and implement a **lightweight navigation system** similar to Google Maps using open-source mapping technologies. The application allows users to search for destinations, visualize routes, track live location, detect route deviations, and dynamically re-route when necessary.

The system is designed with **MVVM architecture**, ensuring clean separation of concerns and scalability.

---

##  Key Features

* 📍 **Real-Time User Location Tracking**
* 🔎 **Destination Search using Nominatim API**
* 🛣️ **Route Calculation using OSRM**
* 🧭 **Dynamic Turn & Re-routing Logic**
* 📏 **Remaining Distance Calculation**
* 💾 **Offline Route Support**
* 📌 **Map Tap to Set Destination**
* 🔄 **Automatic Route Reset on New Destination**
* 🧠 **MVVM Architecture (ViewModel + LiveData)**

---

## 🧱 Tech Stack

| Component       | Technology               |
| --------------- | ------------------------ |
| Language        | Java                     |
| Maps            | OpenStreetMap (OSMDroid) |
| Routing         | OSRM                     |
| Search          | Nominatim                |
| Architecture    | MVVM                     |
| UI              | XML (ConstraintLayout)   |
| Networking      | HttpURLConnection        |
| Offline Storage | Local Repository         |
| Location        | Android GPS              |

---

## 🏗️ Architecture

The application follows the **MVVM (Model–View–ViewModel)** pattern:

```
UI (MainActivity)
        ↓
ViewModel (MapViewModel)
        ↓
Repository (RouteRepository)
        ↓
Network APIs (OSRM / Nominatim)
```

* **MainActivity** handles UI and user interactions
* **MapViewModel** manages navigation logic and state
* **Repository** handles offline route storage
* **Services** manage network-based APIs

---

## 🔄 Navigation Logic Flow

1. User location is continuously updated
2. Destination is selected via search or map tap
3. Route is fetched from OSRM
4. Polyline is drawn on the map
5. App checks user distance from next route point:

   * If within range → continue
   * If far away → **re-route automatically**
6. Remaining distance is calculated and displayed

---

## 🌐 APIs Used

### 🔹 Nominatim (Search)

* Converts place names into geographic coordinates
* Supports nearest-location search

### 🔹 OSRM (Routing)

* Provides optimized routing paths
* Returns encoded polylines for route drawing

---

## 📶 Offline Mode

* When internet is unavailable:

  * App attempts to load a previously saved route
  * If available, navigation continues offline
  * Otherwise, user is notified

---

## 🚀 How to Run the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/FabihaRizvi/Maps-and-Navigations.git
   ```
2. Open in **Android Studio**
3. Sync Gradle
4. Run on a **real Android device** (GPS required)

---

## ⚠️ Notes

* Internet connection is required for:
  * Searching new destinations
  * Fetching routes
* Offline routing works only for previously saved destinations
* No Google Maps or proprietary APIs are used

---

