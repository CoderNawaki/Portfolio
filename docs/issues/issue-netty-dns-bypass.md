Title: Security Vulnerability: Netty DNS Codec Input Validation Bypass (CVE-2026-42579)
Repository: CoderNawaki/Portfolio
---
## Description
Netty has a DNS Codec Input Validation Bypass vulnerability (CVE-2026-42579). It affects both the encoder and decoder in the `netty-codec-dns` module.

- **Encoder:** Fails to validate RFC 1035 domain name constraints (null bytes, label lengths, total length), potentially leading to DNS cache poisoning or domain validation bypass.
- **Decoder:** Fails to validate label lengths and allows unbounded memory allocation, potentially leading to Denial of Service (DoS).

## Vulnerability Details
- **Advisory ID:** CVE-2026-42579
- **Impact:** High (DNS Cache Poisoning, DoS)
- **Affected Version in Project:** `4.2.12.Final`
- **Fixed Version:** `4.2.13.Final`

## Action Plan
1. [ ] Create GitHub issue using `scripts/manage-issue.py`.
2. [ ] Update `build.gradle` to enforce `io.netty:netty-bom:4.2.13.Final`.
3. [ ] Verify the dependency resolution using `./gradlew dependencies`.
4. [ ] Run tests to ensure no regressions.
5. [ ] Create Pull Request linked to the issue.
