Title: Security Vulnerability: Spring Boot Actuator Unauthorized Access (CVE-2026-40976)
Repository: CoderNawaki/Portfolio
---
## Description
Spring Boot's default security filter chain in this project permits unauthorized access to all Actuator endpoints. While `/actuator/health` is often intended to be public for monitoring, other endpoints such as `/actuator/prometheus`, `/actuator/metrics`, and `/actuator/info` can expose sensitive internal state or metrics if not properly secured.

This vulnerability corresponds to **CVE-2026-40976** (CWE-862: Missing Authorization). An attacker can access these resources without any authentication, potentially leading to information disclosure.

## Vulnerability Details
- **Advisory ID:** CVE-2026-40976
- **CWE:** CWE-862 (Missing Authorization)
- **Impact:** Critical/High (Information Disclosure)
- **Affected File:** `src/main/java/com/codernawaki/portfolio/SecurityConfig.java`
- **Vulnerable Configuration:** `.requestMatchers("/", "/login", "/submitContactForm", "/actuator/**").permitAll()`

## Action Plan
1. [ ] Create GitHub issue using `scripts/manage-issue.py`.
2. [ ] Modify `SecurityConfig.java` to:
    - Permit access to `/actuator/health` and `/actuator/info` for all users.
    - Require `ROLE_ADMIN` for all other `/actuator/**` endpoints.
3. [ ] Verify the fix by running existing tests and potentially adding a new test case to ensure restricted access to sensitive actuator endpoints.
4. [ ] Create Pull Request linked to the issue.
