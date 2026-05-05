Title: Apache Tomcat: Sensitive Information Exposure (Kubernetes Bearer Token) in Logs
Repository: CoderNawaki/Portfolio
---
### 🛡️ Vulnerability Report
**Severity:** High
**Dependency:** [ tomcat-embed-core ]
**Vulnerability:** Insertion of Sensitive Information into Log File

### 📝 Description
A vulnerability in the cloud membership for clustering component of Apache Tomcat exposes the Kubernetes bearer token by inserting it into log files. This issue affects Apache Tomcat versions 11.0.0-M1 through 11.0.20.

### 🛠️ Fix Plan
1. [x] Update Tomcat embedded version to `11.0.21` or higher.
2. [x] Update `build.gradle` to enforce the secure version.
3. [x] Run local tests to ensure no regressions.
4. [x] Verify dependency tree.

### ✅ Acceptance Criteria
- [x] tomcat-embed-core version is 11.0.21 or higher.
- [x] Application builds and starts successfully.
