# Lama Nawaraj Portfolio

Spring Boot portfolio application for Lama Nawaraj, a full stack developer working mainly in Japan.

This project is used as both a recruiter-facing portfolio site and a demonstration of iterative full stack improvement with Java, Spring Boot, template rendering, frontend polish, and tested contact handling.

## Highlights

- **Full Stack Positioning**: Java-first backend focus with frontend delivery.
- **Dynamic Configuration**: All portfolio content (bio, projects, contact info) is managed through `application.properties`.
- **Architectural Excellence**: Layered architecture with dedicated services, properties-backed configuration, and global model advice.
- **Thymeleaf Fragments**: Reusable template components for a consistent UI across all pages.
- **Validated Contact Flow**: Structured API responses with server-side validation.
- **Admin Dashboard**: Protected area to review, search, and manage contact submissions.
- **Responsive UI**: Modern design with section navigation and progressive reveal effects.
- **Operational Visibility**: Prometheus metrics, Loki logs, Tempo traces, and a provisioned Grafana dashboard for runtime inspection.
- **Security & Resilience**: Redis-backed rate limiting with fallback to in-memory buckets.
- **Database Evolution**: Flyway-based schema migrations for reproducible database state.

## Tech Stack

- Java 17
- Spring Boot 4
- Gradle
- Thymeleaf (with Spring Security extras)
- HTML, CSS, JavaScript
- PostgreSQL (Production), H2 (Local/Testing)
- Redis (Rate limiting and caching)
- Flyway (Database migrations)
- Prometheus, Grafana, Loki, Promtail, Tempo, OpenTelemetry

## Project Structure

- `src/main/java/com/codernawaki/portfolio/`
  - `PortfolioProperties.java`: Configuration binding for portfolio content.
  - `PortfolioService.java`: Service to provide project and bio data.
  - `GlobalModelAttributeAdvice.java`: Provides common model attributes to all templates.
  - `ContactService.java`: Business logic for contact submissions.
- `src/main/resources/templates/`
  - `index.html`: Main portfolio page.
  - `fragments.html`: Reusable components (head, header, footer).
  - `login.html` & `admin/`: Protected admin areas.

## Run Locally

1. **Set Environment Variables**:
   The application requires admin credentials to start.
   ```bash
   export PORTFOLIO_ADMIN_USERNAME=your-admin-username
   export PORTFOLIO_ADMIN_PASSWORD=your-admin-password
   ```

2. **Run the Application**:
   ```bash
   ./gradlew bootRun
   ```

3. **Access the Site**:
   - Portfolio: `http://localhost:8081/`
   - Admin Login: `http://localhost:8081/login`

## Configuration

You can update the portfolio content without changing Java code by editing `src/main/resources/application.properties`.

Example:
```properties
portfolio.display-name=Lama Nawaraj
portfolio.role=Full Stack Developer
portfolio.bio=Your professional summary here...
portfolio.projects[0].slug=new-project
portfolio.projects[0].title=New Project
...
```

## Build And Test

```bash
./gradlew build
```

## Monitoring Stack

Run the application and observability stack with Docker Compose:

```bash
docker compose up -d --build
```

Key URLs:

- Portfolio app: `http://localhost:8081/`
- Prometheus: `http://localhost:9090/`
- Grafana: `http://localhost:3000/`
- Tempo API: `http://localhost:3200/`

Grafana provisions:

- `Prometheus` as the default metrics datasource
- `Loki` as the log datasource
- `Tempo` as the trace datasource
- `Portfolio Observability` dashboard in the `Portfolio` folder

Tracing:

- The application exports traces via OTLP to Tempo
- Logs include `traceId` so Grafana can jump from Loki log lines to Tempo traces

## Contact

- GitHub: https://github.com/CoderNawaki
- Email: lama.nawraj00@gmail.com
