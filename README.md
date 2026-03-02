# Nboard - Student Boarding Management Platform

> **University Project**: A specialized property management and boarding platform for NSBM Green University students to find, visit, and reserve boarding places online.

## 📚 Project Information

- **Project Name**: Nboard (Formerly GreenNest)
- **Developer**: eptheekshana
- **Institution**: NSBM Green University
- **Project Type**: University Submission
- **Year**: 2026

## 🎯 Project Overview

Nboard is a comprehensive web-based boarding management system that connects property owners with students looking for accommodation near NSBM Green University. The platform provides a secure, efficient way to:

- **For Students**: Search, filter, and book boarding places with detailed information and images
- **For Property Owners**: List and manage properties, handle booking requests, and update property details
- **For Administrators**: Oversee platform operations, manage users, approve properties, and monitor bookings

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (or DigitalOcean Managed Database)
- DigitalOcean Spaces account (for image storage)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/eptheekshana/Nboard.git
   cd Nboard
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your actual credentials:
   # - Database connection (MySQL)
   # - DigitalOcean Spaces credentials
   ```

3. **Run the application**
   
   **Option A: Using the run script (recommended)**
   ```bash
   chmod +x run.sh
   ./run.sh
   ```
   This script automatically loads environment variables from `.env` and starts the application.
   
   **Option B: Using Maven wrapper**
   ```bash
   # Export environment variables first
   export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)
   ./mvnw spring-boot:run
   ```
   
   **Option C: Build and run JAR**
   ```bash
   ./mvnw clean package
   java -jar target/nboard-0.0.1-SNAPSHOT.jar
   ```

4. **Access the application**
   - URL: `http://localhost:8080`
   - Admin Dashboard: `http://localhost:8080/admin`
   - Default Admin Login: `admin@nboard.com` / `admin123`

## 📋 Project Structure

```
Nboard/
├── src/main/java/com/horizonix/nboard/
│   ├── config/           # Configuration classes (Security, S3, etc.)
│   ├── controller/       # REST and page controllers
│   │   ├── admin/       # Admin controllers
│   │   ├── owner/       # Property owner controllers
│   │   └── user/        # Student user controllers
│   ├── entity/          # JPA entities (User, Property, Booking)
│   ├── repository/      # Data access layer (JPA repositories)
│   ├── service/         # Business logic services
│   └── NboardApplication.java  # Main application class
├── src/main/resources/
│   ├── application.properties  # Application configuration
│   ├── static/          # CSS, JavaScript, images
│   │   ├── css/
│   │   ├── js/
│   │   └── images/
│   └── templates/       # Thymeleaf HTML templates
│       ├── admin/       # Admin pages
│       ├── owner/       # Property owner pages
│       └── user/        # Student user pages
├── .env.example         # Example environment configuration
├── .gitignore          # Git ignore rules
├── pom.xml             # Maven dependencies
├── Procfile            # Heroku deployment configuration
└── README.md           # This file
```

## 🔧 Configuration

### Environment Variables

All configuration is done through environment variables defined in `.env` file. See `.env.example` for template.

**Database Configuration:**
```properties
DATABASE_URL=jdbc:mysql://host:port/database?useSSL=true&serverTimezone=UTC
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
```

**DigitalOcean Spaces (for file uploads):**
```properties
DO_SPACES_KEY=your_access_key
DO_SPACES_SECRET=your_secret_key
DO_SPACES_ENDPOINT=sgp1.digitaloceanspaces.com
DO_SPACES_REGION=sgp1
DO_SPACES_BUCKET=nboard
FILE_STORAGE_MODE=cloud  # Options: auto, local, cloud
```

**Server Configuration:**
```properties
PORT=8080  # Application port
```

### Security Configuration

- Spring Security is enabled by default
- Passwords are hashed using BCrypt
- Role-based access control (ADMIN, OWNER, USER)
- CSRF protection enabled
- All sensitive data is stored in environment variables

## 📦 Features

### Core Features

1. **User Management**
   - User registration with email verification
   - Secure login with Spring Security
   - Role-based access (Admin, Property Owner, Student User)
   - Profile management and password reset

2. **Property Listings**
   - Browse available boarding properties
   - Advanced filtering (price, location, amenities, gender)
   - Detailed property information with multiple images
   - Real-time availability status
   - Property verification by admin

3. **Booking System**
   - Online booking requests
   - Booking status tracking (pending, confirmed, rejected)
   - Email notifications (planned feature)
   - Booking history for students

4. **Property Owner Dashboard**
   - Add and edit property listings
   - Upload multiple property images
   - Manage booking requests
   - View property performance analytics
   - Edit and delete own properties

5. **Admin Dashboard**
   - User management (approve, suspend, delete)
   - Property verification and management
   - Booking oversight
   - Platform statistics and analytics
   - System configuration

6. **Image Management**
   - Multiple image upload per property
   - Cloud storage using DigitalOcean Spaces (S3-compatible)
   - Fallback to local storage if cloud not configured
   - Image optimization and compression

## 🌐 API Endpoints

### Public Endpoints

- `GET /` - Home page
- `GET /register` - User registration page
- `POST /register` - Process registration
- `GET /login` - Login page
- `GET /properties` - Browse properties (public view)

### User Endpoints (Authenticated)

- `GET /user/dashboard` - User dashboard
- `GET /user/profile` - View profile
- `POST /user/profile` - Update profile
- `GET /user/properties` - Browse and filter properties
- `GET /user/property/{id}` - View property details
- `POST /user/booking` - Create booking request
- `GET /user/bookings` - View my bookings

### Owner Endpoints (Property Owner Role)

- `GET /owner/dashboard` - Owner dashboard
- `GET /owner/add-property` - Add property form
- `POST /owner/add-property` - Submit new property
- `GET /owner/properties` - View my properties
- `GET /owner/edit-property/{id}` - Edit property form
- `POST /owner/edit-property/{id}` - Update property
- `DELETE /owner/delete-property/{id}` - Delete property
- `GET /owner/bookings` - View booking requests for my properties
- `POST /owner/booking/{id}/confirm` - Confirm booking
- `POST /owner/booking/{id}/reject` - Reject booking

### Admin Endpoints (Admin Role)

- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - Manage users
- `POST /admin/user/{id}/approve` - Approve user
- `POST /admin/user/{id}/suspend` - Suspend user
- `DELETE /admin/user/{id}` - Delete user
- `GET /admin/properties` - Manage properties
- `POST /admin/property/{id}/verify` - Verify property
- `DELETE /admin/property/{id}` - Delete property
- `GET /admin/bookings` - View all bookings
- `GET /admin/statistics` - Platform statistics

## 📝 Database Schema

### Main Entities

**User**
- id (Primary Key)
- email (Unique)
- password (Hashed)
- firstName, lastName
- phoneNumber
- role (ADMIN, OWNER, USER)
- status (ACTIVE, SUSPENDED, PENDING)
- createdAt, updatedAt

**Property**
- id (Primary Key)
- owner (Foreign Key to User)
- title, description
- address, city, district
- price, priceType (MONTHLY, DAILY)
- propertyType (ROOM, ANNEX, HOUSE)
- genderPreference (MALE, FEMALE, ANY)
- amenities (WiFi, Parking, AC, etc.)
- images (JSON array of image URLs)
- status (PENDING, VERIFIED, REJECTED)
- availability (AVAILABLE, BOOKED)
- createdAt, updatedAt

**Booking**
- id (Primary Key)
- property (Foreign Key to Property)
- user (Foreign Key to User)
- checkInDate, checkOutDate
- status (PENDING, CONFIRMED, REJECTED, CANCELLED)
- message (optional booking note)
- createdAt, updatedAt

## 🐳 Deployment

### Heroku Deployment

The application is configured for deployment on Heroku with the included `Procfile`.

**Prerequisites:**
- Heroku account
- Heroku CLI installed

**Deployment Steps:**

1. **Create Heroku app**
   ```bash
   heroku create nboard-app
   ```

2. **Set environment variables**
   ```bash
   heroku config:set DATABASE_URL="your_database_url" --app nboard-app
   heroku config:set DATABASE_USERNAME="your_username" --app nboard-app
   heroku config:set DATABASE_PASSWORD="your_password" --app nboard-app
   heroku config:set DO_SPACES_KEY="your_key" --app nboard-app
   heroku config:set DO_SPACES_SECRET="your_secret" --app nboard-app
   heroku config:set DO_SPACES_BUCKET="nboard" --app nboard-app
   ```

3. **Deploy application**
   ```bash
   git push heroku main
   ```

4. **Open application**
   ```bash
   heroku open --app nboard-app
   ```

**Note**: The `system.properties` file specifies Java 17 for Heroku runtime.

### DigitalOcean App Platform

Alternative deployment option using DigitalOcean:

1. Connect your GitHub repository
2. Configure environment variables in App Platform settings
3. Deploy automatically on push

## 📚 Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.2
- **Language**: Java 17
- **Security**: Spring Security 6.2.1
- **Database**: MySQL 8.0 (DigitalOcean Managed Database)
- **ORM**: Hibernate / JPA
- **Build Tool**: Maven 3.6+

### Frontend
- **Template Engine**: Thymeleaf
- **CSS Framework**: Bootstrap 5
- **JavaScript**: Vanilla JS, jQuery
- **Icons**: Font Awesome

### Infrastructure
- **Cloud Storage**: DigitalOcean Spaces (S3-compatible)
- **Hosting**: Heroku / DigitalOcean App Platform
- **Database**: DigitalOcean Managed MySQL
- **Version Control**: Git / GitHub

### Key Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Thymeleaf
- MySQL Connector
- AWS SDK S3 (for DigitalOcean Spaces)
- Lombok

## 🔐 Security Best Practices

This project implements several security measures:

1. **Environment Variables**: All sensitive data (passwords, API keys) stored in `.env` file
2. **Git Security**: `.env` file excluded from version control via `.gitignore`
3. **GitHub Secret Scanning**: Protected against accidental credential commits
4. **Password Hashing**: BCrypt encryption for all passwords
5. **CSRF Protection**: Enabled by default in Spring Security
6. **Input Validation**: Server-side validation for all user inputs
7. **SQL Injection Prevention**: JPA/Hibernate parameterized queries
8. **Role-Based Access**: Fine-grained authorization controls

**⚠️ Important**: Never commit sensitive credentials to GitHub. Use `.env` file locally and environment variables in production.

## 🧪 Testing

### Manual Testing

Test accounts (configured in database):

- **Admin**: `admin@nboard.com` / `admin123`
- **Owner**: `owner@nboard.com` / `owner123`
- **User**: `user@nboard.com` / `user123`

### Test Scenarios

1. **User Registration & Login**
   - Register new account
   - Login with credentials
   - Access role-specific dashboards

2. **Property Management**
   - Add new property with images
   - Edit property details
   - Delete property
   - View property listings

3. **Booking Flow**
   - Browse properties
   - Submit booking request
   - Owner reviews and confirms/rejects
   - View booking history

4. **Admin Functions**
   - Verify new properties
   - Manage users
   - View platform statistics

## 🤝 Contribution & Development

### Git Workflow

This project follows a standard Git workflow:

```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "feat: description of changes"

# Push to repository
git push origin feature/your-feature-name
```

### Commit Message Convention

Follow conventional commits:

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code formatting
- `refactor:` - Code refactoring
- `test:` - Adding tests
- `chore:` - Maintenance tasks

Example:
```
feat: add property edit functionality for owners
fix: resolve image upload error on DigitalOcean Spaces
docs: update README with API endpoints
```

## 📄 License & Academic Integrity

This project is developed as a university submission for NSBM Green University.

**Academic Use**: This code is provided for educational purposes and academic review.

**Copyright**: © 2026 eptheekshana. All rights reserved.

## 📧 Contact & Support

- **Developer**: eptheekshana
- **GitHub**: [@eptheekshana](https://github.com/eptheekshana)
- **Project Repository**: [Nboard](https://github.com/eptheekshana/Nboard)

For issues, questions, or feedback:
1. Open a GitHub issue
2. Contact via university email

## 📖 Postman Collection

A Postman collection with all API endpoints is included in the repository:
- File: `Nboard.postman_collection.json`
- Import into Postman to test all endpoints
- Includes authentication, user, owner, and admin APIs

## ✅ Submission Checklist

- [x] GitHub repository with commit history
- [x] README.md with comprehensive documentation
- [x] Postman collection for API testing
- [x] Environment variables properly configured
- [x] Sensitive data excluded from repository
- [x] Application deployable on Heroku/DigitalOcean
- [x] All core features implemented and tested

---

**Note**: This README provides complete documentation for setup, configuration, deployment, and usage of the Nboard platform. For additional information, refer to inline code comments and documentation in the source files.


