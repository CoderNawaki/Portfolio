Title: Apache Commons Lang3: Uncontrolled Recursion (StackOverflowError) in ClassUtils
Repository: CoderNawaki/Portfolio
---
### 🛡️ Vulnerability Report
**Severity:** High
**Dependency:** [ org.apache.commons:commons-lang3 ]
**Vulnerability:** Uncontrolled Recursion (StackOverflowError)

### 📝 Description
Uncontrolled Recursion vulnerability in Apache Commons Lang. The methods `ClassUtils.getClass(...)` can throw `StackOverflowError` on very long inputs. This can cause an application to stop. This issue affects versions from 3.0 before 3.18.0.

### 🛠️ Fix Plan
1. [x] Update `commons-lang3` version to `3.18.0` or higher (3.19.0 recommended).
2. [x] Update `build.gradle` to enforce the secure version.
3. [x] Run local tests to ensure no regressions.
4. [x] Verify dependency tree.

### ✅ Acceptance Criteria
- [x] `commons-lang3` version is 3.18.0 or higher.
- [x] Application builds and starts successfully.
