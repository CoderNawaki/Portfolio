Title: Security Vulnerability: Spring Boot DevTools Remote Secret Timing Attack (CVE-2026-40972)
Repository: CoderNawaki/Portfolio
---
## Description
Spring Boot DevTools remote secret comparison is vulnerable to timing attacks. An attacker on the same network as the remote application can utilize a timing attack to discover the remote secret. This could result in the attacker determining the secret and uploading changed classes, achieving remote code execution (RCE).

Additionally, this upgrade addresses **CVE-2026-40976** (Authorization Bypass in Actuator) which was previously partially mitigated with a manual configuration change.

## Vulnerability Details
- **Advisory ID:** CVE-2026-40972
- **Severity:** High (RCE potential)
- **Impact:** Remote Code Execution via DevTools secret recovery.
- **Fixed Version:** `4.0.6`

## Action Plan
1. [ ] Create GitHub issue using `scripts/manage-issue.py`.
2. [ ] Upgrade Spring Boot from `4.0.5` to `4.0.6` in `build.gradle`.
3. [ ] Verify the dependency resolution using `./gradlew dependencies`.
4. [ ] Run tests to ensure no regressions.
5. [ ] Create Pull Request linked to the issue.
