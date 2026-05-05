# Security Vulnerability: Jackson Core (GHSA-2m67-wjpj-xhg9)

## Description
Jackson Core is vulnerable to a "Document length constraint bypass" in blocking, async, and DataInput parsers. This allows oversized JSON documents to be accepted without throwing a `StreamConstraintsException`, potentially leading to Denial of Service (DoS) via resource exhaustion.

## Vulnerability Details
- **Advisory ID:** GHSA-2m67-wjpj-xhg9
- **Impact:** Critical/High (Denial of Service)
- **Affected Versions in Project:** `tools.jackson.core:jackson-core:3.1.0`
- **Fixed Version:** `3.1.1`

## Action Plan
1. [x] Update `build.gradle` to enforce `tools.jackson.core:jackson-core:3.1.1`.
2. [x] Run `./gradlew dependencies` to verify the resolution.
3. [x] Run tests to ensure no regressions.
