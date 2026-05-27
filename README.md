# MediCare — Smart Hospital Management System

MediCare is a production-ready, full-stack Hospital Management System featuring an integrated AI-powered Disease Prediction service. It enables patients to select symptoms, predict likely diseases using a Random Forest classifier, match with local specialists, and manage scheduled consultations, while allowing doctors and administrators to handle clinical profiles, patient files, and administrative statistics.

---

## Architecture Diagram (ASCII)

```
                     +---------------------------------------+
                     |         FRONTEND CLIENT PORTAL        |
                     |  (HTML5 / CSS3 / Vanilla Javascript)  |
                     +-------------------+-------------------+
                                         |
                                         | REST APIs
                                         | (Bearer Token Auth)
                                         v
                     +-------------------+-------------------+
                     |       SPRING BOOT BACKEND ENGINE      |
                     |   (Java 17, Spring Security 6, JWT)   |
                     +---------+-------------------+---------+
                               |                   |
                     Hibernate |                   | RestTemplate
                         JPA   |                   | (JSON payload)
                               v                   v
                     +---------+---------+   +-----+-----------------+
                     |   MYSQL 8 DATABASE|   | PYTHON FLASK ML ENGINE|
                     | (Relational Schema|   | (Scikit-Learn, Random |
                     |   & FKey indices) |   |  Forest Classifier)   |
                     +-------------------+   +-----------------------+
```

---

## Project Structure

```
medicare/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/medicare/
│       │   ├── MediCareApplication.java
│       │   ├── config/ (Security, CORS, JWT Configurations)
│       │   ├── entity/ (JPA Entities: Patient, Doctor, Appointment, MedicalRecord, Admin, RefreshToken)
│       │   ├── repository/ (JPA Repositories)
│       │   ├── service/ (Business logic handlers)
│       │   ├── controller/ (REST Controllers)
│       │   ├── dto/ (Validation DTOs: Login, Register, Book, Predict)
│       │   ├── security/ (JWT Filters & Providers)
│       │   └── exception/ (Global Exceptions Handler & ApiResponse wrapper)
│       └── resources/
│           └── application.properties
├── ml-service/
│   ├── app.py (Flask Server)
│   ├── model_trainer.py (Symptom-Disease Random Forest trainer)
│   ├── requirements.txt
│   ├── disease_model.pkl (Trained model)
│   └── symptoms_encoder.pkl (Feature encoder metadata)
├── database_schema.sql (MySQL initialization schema)
└── frontend/ (HTML, CSS, JS layout files)
```

---

## Default Admin Credentials

On backend startup, an admin account is automatically seeded in the database if empty:
* **Email**: `admin@medicare.com`
* **Password**: `AdminPass123!`
* **Role**: `ROLE_ADMIN`

---

## Prerequisites

1. **Java Development Kit (JDK) 17** or higher.
2. **Maven 3.8+** (or use packaged maven wrappers).
3. **Python 3.10+** with `pip` package manager.
4. **MySQL Server 8.0+** running locally.

---

## MySQL Setup Commands

Log in to your local MySQL Command Line interface and run the following statements:

```sql
-- Create and initialize the database schema
CREATE DATABASE medicare_db;

-- Select database
USE medicare_db;

-- Import schema using the provided SQL initialization file:
SOURCE c:/Users/likhi/Desktop/Medicare/database_schema.sql;
```

If you don't have MySQL CLI active, you can open your admin client (e.g. MySQL Workbench, phpMyAdmin) and run the contents of the `database_schema.sql` file located in the root of the project.

---

## Step-by-Step Run Instructions

### Step 1: Run the ML Service (Python Flask)
1. Navigate to the `ml-service` directory:
   ```bash
   cd ml-service
   ```
2. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Train the model and generate serialized assets (`.pkl`):
   ```bash
   python model_trainer.py
   ```
4. Run the Flask development server:
   ```bash
   python app.py
   ```
   *The ML service will be online on `http://localhost:5000`.*

### Step 2: Run the Backend Engine (Spring Boot)
1. Verify the database configurations inside `backend/src/main/resources/application.properties` (specifically username, password, and port matching your local MySQL installation).
2. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
3. Build and package the project:
   ```bash
   mvn clean package
   ```
4. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   *The Java API server will be online on `http://localhost:8080`.*
   *Swagger/OpenAPI docs are accessible at: `http://localhost:8080/swagger-ui/index.html`.*

### Step 3: Run the Frontend Portal (Vanilla JS)
Since the frontend consists of static assets, you can run them directly by opening `frontend/index.html` in any web browser. For a production-like experience, you can serve it using a lightweight dev server:
```bash
# Using NodeJS http-server
npx http-server -p 5500 ./frontend
# Or open in VS Code with the Live Server extension
```

---

## Sample API Request & Responses

### 1. User Login (Retrieve JWT Tokens)
```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email": "admin@medicare.com", "password": "AdminPass123!", "role": "ADMIN"}'
```
**Response**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.ey...",
    "refreshToken": "48b628fd-c878-4330-86c4-b78f8fc85779",
    "tokenType": "Bearer",
    "id": 1,
    "name": "System Admin",
    "email": "admin@medicare.com",
    "role": "ROLE_ADMIN"
  }
}
```

### 2. Predict Disease (Symptom Selection)
```bash
curl -X POST http://localhost:8080/api/predict/disease \
     -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"symptoms": ["cough", "chills", "high_fever", "breathlessness", "sweating"]}'
```
**Response**:
```json
{
  "success": true,
  "message": "Disease prediction generated successfully",
  "data": {
    "disease": "Pneumonia",
    "confidence": 0.94,
    "specialization": "Pulmonologist",
    "precautions": ["rest", "stay hydrated", "consult doctor", "antibiotic compliance"],
    "severity": "High"
  }
}
```
