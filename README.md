# 🎵 TapIt - Musical Mobile Game

## 📌 Project Overview

**TapIt** is a musical tapping game developed in **Java** using **Android Studio**, offering rhythm-based gameplay where users tap falling tiles in sync with music.  
This project was created as part of the **Embedded Interfaces Programming - DevOps** module at **Le Mans University (M1 Computer Science)**.

🔗 **GitHub Repository**: [TapIT_Projet](https://github.com/sedraalhallak/TapIT_Projet.git)

---

## 🚀 API Documentation

The mobile app communicates with a **FastAPI** backend to manage songs.  
To launch the backend API:

```bash
uvicorn main:app --host 0.0.0.0 --port 8000
```
## 📱 Android Emulator API Access

If you are using an **Android Emulator**, access the API with:  
http://10.0.2.2:8000/

🌐 Otherwise, use your computer’s **local IP address** to access the server.

---

## 🎼 Available Endpoint

| Method | URL     | Description               |
|--------|---------|---------------------------|
| GET    | /songs  | Retrieve the list of songs |

---

## 🗃️ Local Database

**SQLite** is used to store:

- User profiles  
- Favorite songs  
- Best scores  

This ensures **offline support** and fast local access.

---

## ⚙️ Technical Choices Justification

- **Java + Android Studio**: Ensures high performance and broad compatibility  
- **FastAPI**: Provides lightweight, fast API development with async support  
- **SQLite**: Efficient and lightweight for offline local storage  
- **MVC Pattern**: Promotes separation of concerns and maintainability  
- **Git & GitHub**: Used for version control and team collaboration

---

## 🧩 Architecture and APIs Used

### 🧱 Architecture Overview

**Frontend:**

- **Language**: Java  
- **Framework**: Android SDK  
- **Pattern**: MVC (Model-View-Controller)  
  - **Model**: Manages user data, scores, and favorites  
  - **View**: XML layouts and UI elements  
  - **Controller**: Activities and Fragments  

**Backend:**

- **Framework**: FastAPI (Python)  
- **Handles**: Song metadata, API communication  

**Local Storage:**

- **Database**: SQLite  
- **Purpose**: Offline support for users' data  

**Communication:**

- **HTTP Libraries**: Retrofit or Volley  
- **Data Format**: JSON

---

## 🌟 Main Features

- ✅ **User Management**: Register, login, and edit profile (avatar, bio)  
- ✅ **Tutorial**: Interactive onboarding or skip to gameplay  
- ✅ **Game Modes**: Tap black tiles in sync with music  
- ✅ **Favorites**: Add or remove songs from favorites  
- ✅ **Quizzes**: Mini music-themed quiz (5 questions)  
- ✅ **Multilingual Support**: English 🇬🇧 / French 🇫🇷  
- ✅ **Background Music Toggle**: Enable or disable background music  
- ✅ **Score System**: Points and stars to reward performance  
- ✅ **Offline Mode**: Play and save scores/favorites without internet  

---

## 🔄 Workflow Overview

1. Users launch the app and are prompted to **register or log in**.  
2. They can follow an **optional tutorial** or skip directly to gameplay.  
3. After authentication, users access the **main interface** with a bottom menu:
   - Home  
   - Favorites  
   - Quiz  
   - Settings  
4. In **gameplay mode**, users tap black tiles in rhythm with the music.  
5. Users can **save scores**, **manage favorites**, and **change settings** anytime.  
6. Settings include **language toggle** (English/French) and **background music control**.

---

## 👩‍💻👨‍💻 Project Contributors

**Sedra Alhallak**  
**Rabia Allagui**

📍 *University of Le Mans – Master 1 Computer Science*  
📅 *April 2025*
