Title: Thymeleaf Server-Side Template Injection (SSTI) in expression execution
Repository: CoderNawaki/Portfolio
---
### 🛡️ Vulnerability Report
**Severity:** Critical
**Dependency:** [ thymeleaf ]
**CVE:** [ CVE-2025-30310 ]

### 📝 Description
A security bypass vulnerability exists in the expression execution mechanisms of Thymeleaf up to and including 3.1.4.RELEASE. The library fails to properly neutralize specific constructs that allow dangerous expressions to be executed in sandboxed contexts. If an application developer passes unsanitized variables containing such expressions to the template engine, it can result in Server-Side Template Injection (SSTI).

### 🛠️ Fix Plan
1. [x] Update Thymeleaf version to `3.1.5.RELEASE`
2. [x] Update `build.gradle` to enforce the secure version
3. [x] Run local tests to ensure no regressions
4. [x] Verify dependency tree

### ✅ Acceptance Criteria
- [x] Vulnerability no longer shows in dependency scan
- [x] Application builds and starts successfully
- [x] Templates render correctly
