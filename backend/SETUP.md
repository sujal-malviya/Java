# GitHub Tracker - Backend

Java 21 Spring Boot backend for GitHub & Jira integration analytics.

## Quick Start

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- PostgreSQL 12+

### Setup

1. **Clone & Install Dependencies**
   ```bash
   cd backend
   mvn clean install
   ```

2. **Configure Environment Variables**
   - Copy `.env.example` to `.env.local`
   - Fill in your credentials:
   ```bash
   cp .env.example .env.local
   # Edit .env.local with your actual values
   ```

3. **Run the Application**
   ```bash
   # With Java 21
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`

## Environment Variables

All sensitive credentials are managed via environment variables. See `.env.example` for all required variables:

### Database
- `DATABASE_URL` - PostgreSQL connection URL
- `DATABASE_USERNAME` - Database user
- `DATABASE_PASSWORD` - Database password

### GitHub OAuth
- `GITHUB_CLIENT_ID` - GitHub OAuth application ID
- `GITHUB_CLIENT_SECRET` - GitHub OAuth application secret
- `GITHUB_REDIRECT_URI` - OAuth redirect URI

### Jira
- `JIRA_BASE_URL` - Jira instance URL
- `JIRA_EMAIL` - Jira user email
- `JIRA_API_TOKEN` - Jira API token

## API Endpoints

### GitHub
- `GET /api/github/me` - Get logged-in user
- `GET /api/github/repos` - Get user repositories
- `POST /api/github/commits` - Fetch commits
- `POST /api/github/prs/heatmap` - PR heatmap analytics
- `POST /api/github/merge/heatmap` - Merge heatmap analytics

### Jira
- `POST /api/jira/timeline` - Get timeline for Jira issue

## Security

⚠️ **IMPORTANT**
- **NEVER** commit credentials to version control
- Always use environment variables for secrets
- Use `.env.local` for local development (not committed)
- Rotate API tokens and passwords regularly

## Java 21 Features

This project is built with Java 21 LTS:
- Records for data classes
- Pattern matching enhancements
- Virtual threads support
- Improved performance

## Build

```bash
# Clean build
mvn clean package

# Build with tests
mvn clean verify

# Build without tests
mvn clean package -DskipTests
```

## Testing

```bash
mvn test
```

## Technologies

- **Spring Boot 3.3.5** - Application framework
- **Spring Data JPA** - Database ORM
- **PostgreSQL** - Database
- **Jackson** - JSON processing
- **RestTemplate** - HTTP client
- **Lombok** - Code generation

## License

Proprietary - GitHub Tracker
