# 🐾 Microservices Zoo Management System

A distributed, container-ready application for managing users, animal species, and individual specimens — built using **Spring Boot**, **Java 21**, and **React**.  
This system follows **Domain-Driven Design (DDD)** and a **microservices architecture**, ensuring modularity, scalability, and maintainability.

---

## 📘 Overview

The project decomposes a monolithic zoo management application into **three autonomous microservices**:

- **user-service** – handles authentication, authorization, and user management.
- **animal-service** – manages animal species, including CRUD, filtering, statistics, and export.
- **exemplar-service** – manages individual animal records, integrating with animal-service through a REST proxy.

Each microservice:
- Has its **own database** (MySQL).
- Exposes **REST APIs** for communication.
- Can be deployed **independently** in Docker or Kubernetes.

The system is secured using **JWT authentication**, follows **DDD layering**, and includes **React SPA** frontend integration via an **API Gateway**.

---

## 🧱 System Architecture

### 🧩 Services

#### 1️⃣ user-service
- **Role:** Registration, authentication, CRUD, role-based access, and notifications.  
- **Stack:** Spring Boot, Spring Security, JWT, JavaMail.  
- **Data:** `users` table — stores id, username, email, password, user_type.

#### 2️⃣ animal-service
- **Role:** Manage animal species, filtering, and exporting data.  
- **Stack:** Spring Boot, Spring Data JPA, JFreeChart.  
- **Data:** `animals` table — id, name, category, diet_type, habitat, weight, age.

#### 3️⃣ exemplar-service
- **Role:** Manage animal instances, fetch species details via REST proxy.  
- **Stack:** Spring Boot, RestTemplate, PlantUML.  
- **Data:** `exemplars` table — id, animal_id, name, location, age, weight, notes.

---

## 🚀 Features

- **User Authentication & Authorization** using JWT  
- **CRUD Operations** for users, species, and exemplars  
- **Filtering & Pagination** for all entities  
- **Statistics Generation** (e.g., average weight and age by category)  
- **Data Export** in CSV, JSON, XML, DOCX formats  
- **Email Notifications** on profile or password changes  
- **Independent Databases** per microservice  

---

## 🧠 Design Patterns Used

| Pattern | Category | Location | Description |
|----------|-----------|-----------|--------------|
| **Singleton** | Creational | All Spring beans | Ensures single instance per context |
| **Facade** | Structural | `ExemplarFacade` | Simplifies access to business logic |
| **Builder** | Creational | DTOs | Fluent creation of immutable DTOs |
| **DTO (Data Transfer Object)** | Structural | All services | Decouples persistence from API layer |
| **Proxy** | Structural | `AnimalApiService` | REST client for inter-service calls |

---

## 🧮 Database Design (ERD Summary)

Each microservice owns its own schema:

- `users`: manages user credentials and roles  
- `animals`: stores animal species and related data  
- `exemplars`: stores animal instances (references species via `animal_id`, not FK to preserve decoupling)

---

## ⚙️ Tech Stack

| Component | Technology |
|------------|-------------|
| Backend | Java 21, Spring Boot 3.x |
| Frontend | React, React Router, react-i18next |
| Databases | MySQL (one per microservice) |
| API Testing | Postman, Newman |
| Unit Testing | JUnit, Mockito |
| Documentation | PlantUML |


---

## 🔐 Security

- Authentication via **JWT tokens**
- Role-based authorization (CLIENT, EMPLOYEE, MANAGER, ADMIN)
- Centralized validation via **API Gateway**
- Configurable CORS policy

---

## 🧩 Communication Flow

1. **Frontend** → **API Gateway** → microservice (based on route).  
2. **exemplar-service** → **animal-service** (via `AnimalApiService.fetchById(id)`).  
3. **user-service** is standalone (no internal calls).  

---

## 🧰 Development Steps

1. Initialize Spring Boot projects for each service.  
2. Define entities and JPA mappings (`User`, `Animal`, `Exemplar`).  
3. Configure JWT security and CORS policies.  
4. Implement Controllers for CRUD and Auth endpoints.  
5. Add Service and Facade layers with DDD separation.  
6. Implement DTOs and Builders using Lombok.  
7. Integrate export strategies (CSV, JSON, XML, DOCX).  
8. Implement REST Proxy (`AnimalApiService`).  
9. Test with Postman.  
10. Generate UML diagrams (classes, components, sequence).

---

## 🖥️ Frontend

- React Single Page Application (SPA)  
- Role-based UI for:
  - **Visitors:** View species and exemplars  
  - **Employees:** CRUD operations  
  - **Managers:** View statistics, export reports  
  - **Admins:** Manage users + email notifications  
- Fully internationalized with **react-i18next**.

---

## 🧩 Advantages

- Independent deployment and scaling per service  
- Clear separation of concerns and bounded contexts  
- Strong modularity for testing and maintenance  
- Easy integration with CI/CD and Docker environments  
- Secure, extensible, and production-ready architecture

---

