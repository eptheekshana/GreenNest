# Nboard - University Project Submission Guide

## 📦 Submission Package Contents

This repository contains all required elements for the university project submission:

1. ✅ **GitHub Repository** (Public)
   - Repository URL: https://github.com/eptheekshana/Nboard
   - Clear commit history showing development progress
   - Organized project structure with proper naming conventions

2. ✅ **README.md**
   - Comprehensive project documentation
   - Setup and installation instructions
   - API endpoints documentation
   - Technology stack overview
   - Database schema information

3. ✅ **Postman Collection**
   - File: `Nboard.postman_collection.json`
   - Complete API endpoint collection
   - Includes Authentication, User, Owner, and Admin APIs
   - Ready to import and test

## 🎓 Project Details

- **Project Name**: Nboard (Student Boarding Management Platform)
- **Developer**: eptheekshana
- **Institution**: NSBM Green University
- **Academic Year**: 2026
- **Project Type**: Full-Stack Web Application

## 🚀 How to Run the Project

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- DigitalOcean MySQL Database (provided)
- DigitalOcean Spaces account (for image storage)

### Setup Steps

1. **Clone Repository**
   ```bash
   git clone https://github.com/eptheekshana/Nboard.git
   cd Nboard
   ```

2. **Configure Environment**
   ```bash
   # Copy example environment file
   cp .env.example .env
   
   # Edit .env file with your credentials
   # Database and DigitalOcean Spaces credentials are pre-configured
   ```

3. **Run Application**
   ```bash
   # Using run script (recommended)
   chmod +x run.sh
   ./run.sh
   
   # OR using Maven
   ./mvnw spring-boot:run
   ```

4. **Access Application**
   - URL: http://localhost:8080
   - Admin Login: `admin@nboard.com` / `admin123`
   - Owner Login: `owner@nboard.com` / `owner123`
   - User Login: `user@nboard.com` / `user123`

## 📝 Testing the Application

### Using Postman

1. **Import Collection**
   - Open Postman
   - Click "Import" button
   - Select file: `Nboard.postman_collection.json`
   - Collection will be imported with all endpoints

2. **Configure Base URL**
   - In Postman, go to Collection Variables
   - Set `base_url` to `http://localhost:8080` (for local)
   - Or set to your deployed URL (e.g., Heroku app URL)

3. **Test Endpoints**
   - Start with Authentication → Login
   - Test User endpoints (student functions)
   - Test Owner endpoints (property management)
   - Test Admin endpoints (platform management)

### Manual Testing

**Test Scenario 1: Student User Flow**
1. Register new account
2. Login as student
3. Browse properties
4. View property details
5. Submit booking request
6. Check booking status

**Test Scenario 2: Property Owner Flow**
1. Login as owner
2. Add new property with images
3. View my properties
4. Edit property details
5. Review booking requests
6. Confirm/reject bookings

**Test Scenario 3: Admin Flow**
1. Login as admin
2. View platform statistics
3. Manage users (approve/suspend)
4. Verify properties
5. Monitor all bookings

## 🗄️ Database Configuration

The application uses **DigitalOcean Managed MySQL Database**:

- **Host**: db-mysql-sgp1-89626-do-user-27328078-0.k.db.ondigitalocean.com
- **Port**: 25060
- **Database**: defaultdb
- **SSL**: Required
- **Connection**: Configured via environment variables

Database credentials are stored securely in `.env` file (not committed to Git).

## 🖼️ Image Storage

The application uses **DigitalOcean Spaces** for image storage:

- **Endpoint**: sgp1.digitaloceanspaces.com
- **Region**: Singapore (sgp1)
- **Bucket**: nboard
- **Access**: S3-compatible API

## 🏗️ Architecture Overview

### Technology Stack

**Backend:**
- Spring Boot 3.2.2
- Spring Security 6.2.1
- Spring Data JPA
- Hibernate ORM

**Frontend:**
- Thymeleaf Template Engine
- Bootstrap 5
- JavaScript/jQuery
- Font Awesome Icons

**Database:**
- MySQL 8.0 (DigitalOcean Managed)

**Storage:**
- DigitalOcean Spaces (S3-compatible)

**Build & Deployment:**
- Maven
- Heroku
- Git/GitHub

### Key Features Implemented

1. **User Authentication & Authorization**
   - Registration with email validation
   - Secure login (Spring Security)
   - Role-based access control (ADMIN, OWNER, USER)
   - Password hashing (BCrypt)

2. **Property Management**
   - CRUD operations for properties
   - Multiple image upload
   - Property filtering and search
   - Property verification workflow

3. **Booking System**
   - Booking request submission
   - Request approval/rejection
   - Booking history tracking
   - Status management

4. **Admin Dashboard**
   - User management
   - Property verification
   - Platform statistics
   - System monitoring

5. **Security Features**
   - CSRF protection
   - Input validation
   - SQL injection prevention
   - Secure password storage
   - Environment-based configuration

## 📊 Database Schema

### Entities

**User Table**
- User ID (PK)
- Email (Unique)
- Password (Encrypted)
- First Name, Last Name
- Phone Number
- Role (ADMIN/OWNER/USER)
- Status (ACTIVE/SUSPENDED/PENDING)
- Timestamps

**Property Table**
- Property ID (PK)
- Owner ID (FK → User)
- Title, Description
- Address, City, District
- Price, Price Type
- Property Type
- Gender Preference
- Amenities
- Images (JSON)
- Status (PENDING/VERIFIED/REJECTED)
- Availability
- Timestamps

**Booking Table**
- Booking ID (PK)
- Property ID (FK → Property)
- User ID (FK → User)
- Check-in Date, Check-out Date
- Status (PENDING/CONFIRMED/REJECTED/CANCELLED)
- Message
- Timestamps

## 🔐 Security Considerations

### Implemented Security Measures

1. **Environment Variables**
   - All sensitive credentials in `.env` file
   - `.env` excluded from Git via `.gitignore`
   - `.env.example` provided as template

2. **GitHub Secret Scanning**
   - Repository configured to prevent credential commits
   - Secrets are flagged before push

3. **Application Security**
   - Spring Security with CSRF protection
   - BCrypt password encryption
   - Role-based authorization
   - Input validation and sanitization
   - Prepared statements (JPA) prevent SQL injection

4. **Production Best Practices**
   - Environment-based configuration
   - Secure database connections (SSL)
   - HTTPS recommended for deployment

## 🌐 Deployment

### Live Deployment Options

**Option 1: Heroku**
```bash
heroku create nboard-app
heroku config:set DATABASE_URL="..." --app nboard-app
git push heroku main
```

**Option 2: DigitalOcean App Platform**
- Connect GitHub repository
- Configure environment variables
- Auto-deploy on push

### Environment Variables for Production

Required environment variables:
```bash
DATABASE_URL=jdbc:mysql://...
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
DO_SPACES_KEY=...
DO_SPACES_SECRET=...
DO_SPACES_BUCKET=nboard
FILE_STORAGE_MODE=cloud
PORT=8080
```

## 📖 Documentation Files

1. **README.md** - Main project documentation
2. **SUBMISSION_GUIDE.md** - This file (submission instructions)
3. **Nboard.postman_collection.json** - API testing collection
4. **.env.example** - Environment configuration template
5. **Procfile** - Heroku deployment configuration
6. **system.properties** - Java version specification

## 🎯 Learning Outcomes

This project demonstrates:

1. **Full-Stack Development**
   - Backend: Spring Boot, JPA, Security
   - Frontend: Thymeleaf, Bootstrap, JavaScript
   - Database: MySQL with proper schema design

2. **Cloud Integration**
   - DigitalOcean Managed Database
   - DigitalOcean Spaces (object storage)
   - Heroku deployment

3. **Software Engineering Practices**
   - MVC architecture pattern
   - RESTful API design
   - Version control with Git
   - Environment-based configuration
   - Security best practices

4. **Project Management**
   - Proper documentation
   - Clear commit history
   - API documentation (Postman)
   - Deployment ready

## ✅ Pre-Submission Checklist

- [x] All code committed to GitHub
- [x] README.md is comprehensive and up-to-date
- [x] Postman collection created and tested
- [x] Environment variables properly configured
- [x] .env file excluded from repository
- [x] Application compiles without errors
- [x] Application runs successfully
- [x] All core features implemented
- [x] Database properly configured
- [x] Image upload working (DigitalOcean Spaces)
- [x] Security measures implemented
- [x] Clear commit history maintained

## 📧 Support

For any questions or issues:
- **Developer**: eptheekshana
- **GitHub**: https://github.com/eptheekshana
- **Repository**: https://github.com/eptheekshana/Nboard

---

**Note**: This project was developed as a university submission for NSBM Green University, 2026. All code is original work by the developer.

