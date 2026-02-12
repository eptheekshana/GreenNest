# Nboard - Environment Configuration Guide

## Setting Up Environment Variables

This application uses environment variables to securely manage sensitive credentials. Follow the steps below to configure your environment.

---

## Local Development Setup

### 1. Create a `.env` file
Copy the `.env.example` file and rename it to `.env`:
```bash
cp .env.example .env
```

### 2. Fill in your credentials
Edit the `.env` file with your actual credentials:
```properties
DATABASE_URL=jdbc:mysql://your-database-host:25060/defaultdb?sslMode=REQUIRED
DATABASE_USERNAME=doadmin
DATABASE_PASSWORD=your-actual-password

DO_SPACES_KEY=your-actual-access-key
DO_SPACES_SECRET=your-actual-secret-key
DO_SPACES_ENDPOINT=sgp1.digitaloceanspaces.com
DO_SPACES_REGION=sgp1
DO_SPACES_BUCKET=nboard
```

### 3. Run the application
The application will automatically load environment variables from the `.env` file.

**Note:** The `.env` file is excluded from Git to prevent exposing sensitive credentials.

---

## Heroku Deployment

### Setting Environment Variables on Heroku

You need to configure the environment variables in Heroku for production deployment:

```bash
# Set Database credentials
heroku config:set DATABASE_PASSWORD=your-database-password

# Set DigitalOcean Spaces credentials
heroku config:set DO_SPACES_KEY=your-spaces-access-key
heroku config:set DO_SPACES_SECRET=your-spaces-secret-key
heroku config:set DO_SPACES_ENDPOINT=sgp1.digitaloceanspaces.com
heroku config:set DO_SPACES_REGION=sgp1
heroku config:set DO_SPACES_BUCKET=nboard
```

Or use the Heroku Dashboard:
1. Go to your app's Settings tab
2. Click "Reveal Config Vars"
3. Add each environment variable manually

---

## GitHub Actions / CI/CD

If using GitHub Actions, add these as repository secrets:
1. Go to Settings → Secrets and variables → Actions
2. Add each variable as a repository secret
3. Reference them in your workflow files

---

## Required Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `DATABASE_URL` | MySQL database connection URL | Yes |
| `DATABASE_USERNAME` | Database username | Yes |
| `DATABASE_PASSWORD` | Database password | Yes |
| `DO_SPACES_KEY` | DigitalOcean Spaces access key | Yes |
| `DO_SPACES_SECRET` | DigitalOcean Spaces secret key | Yes |
| `DO_SPACES_ENDPOINT` | Spaces endpoint (default: sgp1.digitaloceanspaces.com) | No |
| `DO_SPACES_REGION` | Spaces region (default: sgp1) | No |
| `DO_SPACES_BUCKET` | Spaces bucket name (default: nboard) | No |

---

## Security Best Practices

✅ **DO:**
- Keep `.env` file local and never commit it
- Use different credentials for development and production
- Rotate credentials regularly
- Use Heroku Config Vars or similar for production

❌ **DON'T:**
- Commit credentials to Git
- Share your `.env` file
- Use production credentials in development
- Hardcode secrets in source code

---

## Troubleshooting

### Application fails to start
- Ensure all required environment variables are set
- Check that database credentials are correct
- Verify DigitalOcean Spaces credentials are valid

### GitHub push blocked
- If GitHub blocks your push due to secrets, ensure you've:
  1. Removed hardcoded credentials from `application.properties`
  2. Added `.env` to `.gitignore`
  3. Not committed the `.env` file

### Heroku deployment fails
- Verify all Config Vars are set in Heroku Dashboard
- Check Heroku logs: `heroku logs --tail`
- Ensure `system.properties` specifies Java 17

