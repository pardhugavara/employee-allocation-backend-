# Resource Allocation / Project Staffing System

A full-stack demo application built with **Java 17 + Spring Boot** (backend) and **Angular 17** (frontend), using **MySQL** as the database.

---

## Architecture

```
statestreet/
├── backend/          # Spring Boot REST API (port 8080)
├── frontend/         # Angular 17 SPA (port 4200)
└── README.md
```

## Features

### Admin Dashboard (PMO / Delivery Manager)
- **Jira-style Kanban Board** with 3 columns: PENDING → IN_PROGRESS → FINISHED
- Add employees (triggers email with profile completion link)
- Add skills, create projects
- Allocate employees to projects
- Drag employees between status columns
- Track profile completion status

### Employee Self-Service (via email link)
- Receives a unique tokenized link via email
- Completes profile: mobile, address, experience, skills
- Single-use token with 48-hour expiry
- Auto-updates status to IN_PROGRESS on submission
- Changes immediately reflected in Admin Dashboard

---

## Prerequisites

- **Java 17** (JDK)
- **Node.js 18+** & **npm**
- **MySQL 8** (via MySQL Workbench or standalone)
- **Angular CLI** (`npm install -g @angular/cli@17`)

---

## Database Setup

1. Open **MySQL Workbench**
2. Create the database (JPA auto-DDL will create tables):
```sql
CREATE DATABASE IF NOT EXISTS resource_allocation;
```
3. Update credentials in `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=root
```

---

## Backend Setup (Spring Boot)

```bash
cd backend

# Build & run
./mvnw spring-boot:run
# OR if Maven is installed globally:
mvn spring-boot:run
```

The API will start at **http://localhost:8080**

### Email Configuration

Update `application.properties` with your SMTP settings:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

> **Note:** For Gmail, enable 2FA and generate an [App Password](https://myaccount.google.com/apppasswords). If email fails, the employee is still created (error is logged).

---

## Frontend Setup (Angular 17)

```bash
cd frontend

# Install dependencies
npm install

# Start development server
ng serve
```

The app will open at **http://localhost:4200**

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/employees` | Add employee (admin) |
| GET | `/api/employees` | List all employees |
| GET | `/api/employees/status/{status}` | Filter by status |
| GET | `/api/employees/{id}` | Get employee by ID |
| PUT | `/api/employees/{id}/status` | Update employee status |
| POST | `/api/employees/profile/validate-token` | Validate email token |
| PUT | `/api/employees/profile/update` | Employee self-service update |
| POST | `/api/skills` | Add skill |
| GET | `/api/skills` | List all skills |
| POST | `/api/projects` | Add project |
| GET | `/api/projects` | List all projects |
| POST | `/api/allocations` | Allocate employee to project |
| GET | `/api/allocations` | List all allocations |

---

## Frontend Routes

| Route | Description |
|-------|-------------|
| `/admin/dashboard` | Kanban board (main dashboard) |
| `/admin/add-employee` | Add new employee form |
| `/admin/add-skill` | Add skill form |
| `/admin/add-project` | Add project form |
| `/admin/allocate` | Allocate employee to project |
| `/employee/profile?token=UUID` | Employee self-service profile form |

---

## End-to-End Flow

1. Admin adds employee → Email sent with tokenized link
2. Employee clicks link → Profile form opens (no login)
3. Employee fills details & submits
4. Profile marked complete → Status moves to IN_PROGRESS
5. Admin dashboard immediately reflects the change
6. Admin can allocate employee to project
7. Admin can move employee to FINISHED when project completes

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2, Spring Data JPA, Lombok |
| Frontend | Angular 17, Angular Material, Standalone Components |
| Database | MySQL 8 |
| Email | Spring Mail (Java Mail Sender) |
| Build | Maven (backend), npm + Angular CLI (frontend) |
