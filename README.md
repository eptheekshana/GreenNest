# Nboard - Student Boarding Management Platform

A specialized property management and boarding platform for NSBM Green University students to find, visit, and reserve boarding places online.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/eptheekshana/Nboard.git
   cd Nboard
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your actual credentials (database, DigitalOcean Spaces, etc.)
   ```

3. **Run the application**
   
   **Option A: Using the run script (recommended)**
   ```bash
   ./run.sh
   ```
   This script automatically loads environment variables from `.env` and starts the application.
   
   **Option B: Export environment variables manually**
   ```bash
   export $(cat .env | grep -v '^#' | xargs)
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - URL: `http://localhost:8080`
   - Admin Dashboard: `http://localhost:8080/admin`
   - Default Admin Login: `admin@nboard.com` / `admin123`

## 📋 Project Structure

```
├── src/main/java/com/horizonix/nboard/
│   ├── config/           # Spring configuration classes
│   ├── controller/       # REST and page controllers
│   ├── entity/          # JPA entities
│   ├── repository/      # Data access layer
│   └── service/         # Business logic
├── src/main/resources/
│   ├── application.properties  # Application configuration
│   ├── static/          # CSS, JS, images
│   └── templates/       # Thymeleaf HTML templates
└── pom.xml             # Maven dependencies
```

## 🔧 Configuration

### Application Properties

Key environment variables (see `.env.example`):

```properties
# Database
DATABASE_URL=jdbc:mysql://139.59.237.242:3306/nboard?createDatabaseIfNotExist=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
DATABASE_USERNAME=heroku_user
DATABASE_PASSWORD=password

# DigitalOcean Spaces (for file uploads)
DO_SPACES_KEY=your_key
DO_SPACES_SECRET=your_secret
DO_SPACES_BUCKET=nboard
```

## 🐳 Deployment

### Heroku Deployment

See [HEROKU_DEPLOYMENT.md](./HEROKU_DEPLOYMENT.md) for detailed instructions.

**Quick deploy:**
```bash
heroku create your-app-name
heroku config:set DATABASE_URL="jdbc:mysql://139.59.237.242:3306/nboard?createDatabaseIfNotExist=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true" --app your-app-name
heroku config:set DATABASE_USERNAME="heroku_user" --app your-app-name
heroku config:set DATABASE_PASSWORD="***" --app your-app-name
git push heroku main
```

### Environment Requirements

All environment variables have safe defaults. However, for production:

- Set `DATABASE_URL` to your production database
- Configure `DO_SPACES_KEY` and `DO_SPACES_SECRET` for file uploads
- Use strong database passwords

## 📦 Features

- **User Management**: Registration, login, profiles
- **Property Listings**: Browse, filter, and view boarding properties
- **Booking System**: Request and manage bookings
- **Admin Dashboard**: Manage users, properties, and bookings
- **Image Upload**: Upload property images (DigitalOcean Spaces or local)
- **Role-Based Access Control**: Admin, Owner, User roles

## 🔐 Security

- Spring Security with role-based authorization
- Password hashing with BCrypt
- CSRF protection
- Input validation and sanitization
- Secrets managed via environment variables (never committed to git)

## 📝 Fixed Issues

### Heroku Deployment Errors
- ✅ Fixed Maven compilation error (`TypeTag :: UNKNOWN`)
- ✅ Made environment variables optional with safe defaults
- ✅ Created `Procfile` for Heroku process management
- ✅ Enhanced `.gitignore` to prevent secret commits
- ⚠ If Heroku shows a generic error page, check the logs for MySQL auth issues like `Access denied for user ...`
- ⚠ Make sure the database user in `DATABASE_USERNAME` is allowed to connect from Heroku's network, and that `DATABASE_PASSWORD` matches the MySQL server

## 🔄 CI/CD

Automatic deployment is recommended via GitHub Actions. Configure:
1. Heroku API token as GitHub secret
2. GitHub Actions workflow to deploy on push

## 📚 Technology Stack

- **Backend**: Spring Boot 3.2.2
- **Database**: MySQL 8.0
- **Frontend**: Thymeleaf, Bootstrap, HTML5
- **Storage**: DigitalOcean Spaces (AWS S3 compatible)
- **Build**: Maven
- **Deployment**: Heroku, DigitalOcean

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -m "Add your feature"`
3. Push to branch: `git push origin feature/your-feature`
4. Open a pull request

## 📄 License

This project is private and owned by Horizonix.

## 📧 Support

For issues or questions, please open a GitHub issue or contact the development team.

---

**Note**: Never commit sensitive data (passwords, API keys) to git. Always use environment variables and `.gitignore`.


