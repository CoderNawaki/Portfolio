# Lama Nawaraj Portfolio

Spring Boot portfolio application for Lama Nawaraj, a full stack developer working mainly in Japan.

This project is used as both a recruiter-facing portfolio site and a demonstration of iterative full stack improvement with Java, Spring Boot, template rendering, frontend polish, and tested contact handling.

## Highlights

- full stack developer positioning with Java-first backend focus
- template-driven Spring Boot homepage instead of a static-only site
- featured project case studies for backend and frontend work
- validated contact flow with structured API responses
- responsive UI with section navigation and progressive reveal effects
- test coverage for the contact controller flow

## Tech Stack

- Java 17 source compatibility
- Spring Boot 4
- Gradle
- Thymeleaf
- HTML, CSS, JavaScript
- H2 runtime dependency

## Project Structure

- `src/main/java/com/codernawaki/portfolio/`
  - controllers, contact service, models, and portfolio view data
- `src/main/resources/templates/`
  - Thymeleaf templates
- `src/main/resources/static/`
  - CSS, JavaScript, and static assets
- `src/test/java/com/codernawaki/portfolio/`
  - application and controller tests

## Run Locally

From the repository root:

```bash
export PORTFOLIO_ADMIN_USERNAME=your-admin-username
export PORTFOLIO_ADMIN_PASSWORD=your-admin-password
./gradlew bootRun
```

The app runs locally at:

```text
http://localhost:8081/
```

Admin login:

```text
http://localhost:8081/login
```

## Build And Test

```bash
./gradlew build
```

## Current Portfolio Focus

This portfolio currently emphasizes:

- Java and Spring Boot backend delivery
- frontend implementation with HTML, CSS, JavaScript, and React experience
- design documentation, testing, and release-oriented development
- Japanese and English communication in engineering environments

## Featured Projects

### WebKintaiSystem

Business-facing attendance platform built with Java Servlet and JSP architecture, focused on workflow reliability, layered design, and operational use cases.

Repository:

- Private repository, available for discussion as a case study

### YouTube Clone

React-based frontend product clone covering search, discovery, API integration, and testing with React Testing Library, MSW, and Playwright.

Repository:

- https://github.com/CoderNawaki/youtube_clone

### Portfolio

This repository, documenting the transition from a simple static page into a more structured full stack portfolio application.

Repository:

- https://github.com/CoderNawaki/Portfolio

## Contact

- GitHub: https://github.com/CoderNawaki
- Email: lama.nawraj00+work@gmail.com

## Notes

- `plan.md` is intentionally kept local and ignored from Git.
- The application currently uses port `8081` to avoid local conflicts with `8080`.
- The app now requires `PORTFOLIO_ADMIN_USERNAME` and `PORTFOLIO_ADMIN_PASSWORD` to start.
