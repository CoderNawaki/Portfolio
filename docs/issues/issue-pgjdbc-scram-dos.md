Title: Security Vulnerability: PostgreSQL JDBC Driver (pgjdbc) - SCRAM-SHA-256 DoS
Repository: CoderNawaki/Portfolio
---
## Description
The PostgreSQL JDBC driver (pgjdbc) is vulnerable to a client-side Denial of Service (DoS) during SCRAM-SHA-256 authentication. A malicious server can send a very large iteration count in the SCRAM `server-first-message`, causing the client to consume excessive CPU resources during PBKDF2 computation.

## Vulnerability Details
- **Impact:** High (Denial of Service)
- **Affected Versions in Project:** `org.postgresql:postgresql:42.7.10`
- **Fixed Version:** `42.7.11`

## Action Plan
1. [ ] Create GitHub issue using `scripts/manage-issue.py`.
2. [ ] Update `build.gradle` to explicitly use `org.postgresql:postgresql:42.7.11`.
3. [ ] Verify the dependency resolution using `./gradlew dependencies`.
4. [ ] Run tests to ensure no regressions.
5. [ ] Create Pull Request linked to the issue.
