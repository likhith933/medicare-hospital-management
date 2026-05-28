# 🏥 MediCare — Smart Hospital Management System

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green?style=for-the-badge&logo=springboot)
![Python](https://img.shields.io/badge/Python-3.10-blue?style=for-the-badge&logo=python)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Flask](https://img.shields.io/badge/Flask-2.0-black?style=for-the-badge&logo=flask)
![Live](https://img.shields.io/badge/Frontend-Live-brightgreen?style=for-the-badge&logo=netlify)

> A production-ready, full-stack Hospital Management System with AI-powered Disease Prediction built using Java Spring Boot, Python Flask ML microservice, and a modern JavaScript frontend.

🌐 **Live Demo:** [https://medicaremanage.netlify.app](https://medicaremanage.netlify.app)

🤖 **ML API:** [https://medicare-hospital-management.onrender.com](https://medicare-hospital-management.onrender.com)

📁 **GitHub:** [https://github.com/likhith933/medicare-hospital-management](https://github.com/likhith933/medicare-hospital-management)

---

## 📸 Screenshots

### 🏠 Landing Page & Login
![Landing Page](screenshots/landing.png)

### 📝 Patient Registration
![Register](screenshots/register.png)

### 👤 Patient Dashboard
![Patient Dashboard](screenshots/patient-dashboard.png)

### 🤖 AI Disease Predictor — Symptom Selection
![Disease Predictor](screenshots/disease-predictor.png)

### 🧠 AI Prediction Result
![Prediction Result](screenshots/prediction-result.png)

### 📅 Book Appointment
![Book Appointment](screenshots/book-appointment.png)

### 📋 Medical History
![Medical History](screenshots/medical-history.png)

### 🛡️ Admin Dashboard
![Admin Dashboard](screenshots/admin-dashboard.png)

### 👨‍⚕️ Doctor Dashboard
![Doctor Dashboard](screenshots/doctor-dashboard.png)

---

## ✨ Features

### 👤 Patient Portal
- Self-registration and JWT-secured login
- AI-powered disease prediction from 50+ symptoms
- Appointment booking with doctor slot selection
- Appointment cancellation and rescheduling
- View complete medical history and prescriptions

### 👨‍⚕️ Doctor Portal
- Secure doctor login with specialization profile
- View all assigned patient appointments
- Mark consultations as complete
- File diagnosis, prescription, and lab reports

### 🛡️ Admin Portal
- Hospital statistics dashboard (patients, doctors, appointments)
- Register new doctors with specialization and time slots
- Manage patient directory
- View all hospital appointments

### 🤖 AI Disease Predictor
- Random Forest Classifier trained on 2500+ samples
- Maps 132 symptoms to 42 diseases
- Returns disease name, confidence score, severity
- Recommends doctor specialization automatically
- Java Spring Boot calls Python Flask via REST

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    MEDICARE SYSTEM                       │
│                                                         │
│  ┌──────────────┐    ┌──────────────┐    ┌───────────┐ │
│  │   Frontend   │    │  Spring Boot │    │  Python   │ │
│  │  HTML/CSS/JS │───▶│   Backend    │───▶│  Flask ML │ │
│  │  Port: 5500  │    │  Port: 8080  │    │ Port:5000 │ │
│  └──────────────┘    └──────┬───────┘    └───────────┘ │
│                             │                           │
│                      ┌──────▼───────┐                  │
│                      │   MySQL DB   │                  │
│                      │  Port: 3306  │                  │
│                      └──────────────┘                  │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| Frontend | HTML5, CSS3, Vanilla JS | User Interface |
| Backend | Java 17, Spring Boot 3.0 | REST API Server |
| Security | Spring Security 6, JWT | Authentication |
| Database | MySQL 8.0 | Data Persistence |
| ML Service | Python 3.10, Flask | Disease Prediction API |
| ML Model | Scikit-learn Random Forest | Disease Classification |
| Data Processing | Pandas, NumPy | Dataset Generation |
| API Docs | SpringDoc OpenAPI | Swagger UI |
| Deployment | Netlify, Render | Cloud Hosting |

---

## 🗄️ Database Schema

```sql
patients        → id, name, age, email, password, phone, blood_group
doctors         → id, name, specialization, experience, email, password, available_slots
appointments    → id, patient_id, doctor_id, date, time_slot, status, symptoms
medical_records → id, patient_id, doctor_id, diagnosis, prescription, lab_reports
admins          → id, name, email, password, role
refresh_tokens  → id, token, user_email, expiry_date, user_role
```

---

## 🔌 REST API Endpoints

### Authentication
```
POST   /api/auth/register              → Patient self-registration
POST   /api/auth/login                 → Login (returns JWT token)
POST   /api/auth/refresh               → Refresh access token
POST   /api/auth/logout                → Logout
```

### Doctors
```
GET    /api/doctors/all                → List all doctors
GET    /api/doctors/{specialization}   → Filter by specialization
GET    /api/doctors/{id}/slots         → Get available time slots
POST   /api/doctors/register           → Register doctor (Admin only)
```

### Appointments
```
POST   /api/appointments/book              → Book appointment
PUT    /api/appointments/cancel/{id}       → Cancel appointment
PUT    /api/appointments/reschedule/{id}   → Reschedule appointment
PUT    /api/appointments/complete/{id}     → Mark complete (Doctor)
GET    /api/appointments/patient/{id}      → Patient appointments
GET    /api/appointments/doctor/{id}       → Doctor appointments
```

### Medical Records
```
POST   /api/medical-records/add            → Add medical record
GET    /api/medical-records/{patientId}    → Get patient records
PUT    /api/medical-records/{id}           → Update record
```

### Disease Prediction
```
POST   /api/predict/disease    → Predict disease from symptoms
```

### Admin
```
GET    /api/admin/stats                → Hospital statistics
GET    /api/admin/appointments/all     → All appointments
DELETE /api/admin/patient/{id}         → Delete patient
```

---

## 🤖 ML Disease Prediction

```python
# Input
{ "symptoms": ["fever", "cough", "fatigue", "headache"] }

# Output
{
  "disease": "Pneumonia",
  "confidence": 0.91,
  "specialization": "Pulmonologist",
  "severity": "High",
  "precautions": ["rest", "stay hydrated", "consult doctor"]
}
```

**Model Details:**
- Algorithm: Random Forest Classifier
- Dataset: 2500 samples
- Features: 132 symptoms
- Classes: 42 diseases
- Accuracy: ~92%

---

## ⚙️ Setup Instructions

### Prerequisites
```
Java 17, Maven 3.9+, Python 3.10+, MySQL 8.0+
```

### Step 1: Clone Repository
```bash
git clone https://github.com/likhith933/medicare-hospital-management.git
cd medicare-hospital-management
```

### Step 2: Setup MySQL
```sql
CREATE DATABASE medicare_db;
source database_schema.sql;
```

### Step 3: Configure Backend
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/medicare_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
ml.service.url=http://localhost:5000
```

### Step 4: Start ML Service
```bash
cd ml-service
pip install -r requirements.txt
python model_trainer.py
python app.py
```

### Step 5: Start Backend
```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

### Step 6: Start Frontend
```bash
cd frontend
python -m http.server 5500
# Open http://localhost:5500
```

---

## 🔑 Default Credentials

| Role | Email | Password |
|---|---|---|
| Admin | admin@medicare.com | AdminPass123! |
| Patient | Register via UI | Your choice |
| Doctor | Added by Admin | Set by Admin |

---

## 📁 Project Structure

```
medicare/
├── database_schema.sql
├── README.md
├── screenshots/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/medicare/
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── exception/
│       ├── repository/
│       ├── security/
│       └── service/
├── ml-service/
│   ├── app.py
│   ├── model_trainer.py
│   ├── disease_model.pkl
│   └── requirements.txt
└── frontend/
    ├── index.html
    ├── register.html
    ├── patient-dashboard.html
    ├── doctor-dashboard.html
    ├── admin-dashboard.html
    ├── book-appointment.html
    ├── disease-predictor.html
    ├── medical-history.html
    ├── css/styles.css
    └── js/
        ├── api.js
        ├── auth.js
        ├── appointments.js
        └── predictor.js
```

---

## 🔒 Security

- BCrypt password encoding
- JWT Access + Refresh tokens
- Role-based endpoint protection
- CORS configured
- Global exception handling

---

## 💼 Resume Highlights

```
MediCare – Smart Hospital Management System
Live: medicaremanage.netlify.app
GitHub: github.com/likhith933/medicare-hospital-management

• Built secure REST API with Java 17 & Spring Boot 3,
  implementing JWT authentication and role-based access
  control for Patient, Doctor, and Admin roles

• Integrated Python Flask ML microservice using Random
  Forest Classifier predicting 42 diseases from 132
  symptoms (~92% accuracy) via inter-service REST calls

• Designed normalized MySQL schema with 6 relational
  tables with foreign key constraints and indexes

• Implemented full appointment lifecycle management
  (book, reschedule, cancel, complete) with @Transactional

• Documented 20+ REST endpoints via SpringDoc OpenAPI

Tech: Java 17 · Spring Boot 3 · Spring Security · JWT ·
      MySQL · Python · Flask · Scikit-learn · HTML · CSS · JS
```

---

<div align="center">
  <p>Built with ❤️ by <a href="https://github.com/likhith933">Likhith</a></p>
  <p>⭐ Star this repo if you find it helpful!</p>
</div>
