Title: Spring Security fails to include path in path matching of xml auth rules
Repository: CoderNawaki/Portfolio
---
### 🛡️ Vulnerability Report
**Severity:** Critical / High
**Dependency:** [ spring-security-config ]
**CVE:** [ CVE-2026-22754]

### 📝 Description
Vulnerability in Spring Spring Security. If an application uses <sec:intercept-url servlet-path="/servlet-path" pattern="/endpoint/**"/> to define the servlet path for computing a path matcher, then the servlet path is not included and the related authorization rules are not exercised. This can lead to an authorization bypass. This issue affects Spring Security: from 7.0.0 through 7.0.4.

### 🛠️ Fix Plan
1. [x] Identify latest stable version
2. [x] Update `build.gradle`
3. [x] Run local tests to ensure no regressions
4. [x] Verify dependency tree

### ✅ Acceptance Criteria
- [x] Vulnerability no longer shows in dependency scan
- [x] Application builds and starts successfully
