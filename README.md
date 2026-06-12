# Vulnerable SAST Target — OWASP Top 10 (2021 & 2025)

> ⚠️ **WARNING: Intentionally vulnerable application for security testing / educational purposes only. DO NOT deploy in production.**

A Java application designed as a **SAST (Static Application Security Testing) target** that demonstrates all **10 OWASP Top 10 vulnerability categories** across two versions — the classic 2021 edition and the updated 2025 edition.

---

## Project Overview

| Property        | Value                                      |
|-----------------|--------------------------------------------|
| **Language**    | Java 11                                    |
| **Build Tool**  | Maven                                      |
| **Artifact ID** | `vulnerable-sast-target`                   |
| **Version**     | `1.0-SNAPSHOT`                             |
| **Main Class**  | `com.example.sasttest.Owasp2025WebApp`     |

---

## Applications

### 1. `VulnerableApp.java` — CLI Application (OWASP Top 10 2021)

A standalone command-line application demonstrating all 10 OWASP Top 10 (2021) vulnerability categories through direct method calls.

**File:** `VulnerableApp.java` (root)

### 2. `Owasp2025WebApp.java` — HTTP Web Server (OWASP Top 10 2025)

A built-in Java HTTP server (port 8080) with REST-style endpoints, covering the **updated OWASP Top 10 (2025)** categories including the new A10: Mishandling of Exceptional Conditions.

**File:** `src/main/java/com/example/sasttest/Owasp2025WebApp.java`

---

## Directory Structure

```
├── pom.xml
├── README.md
├── VulnerableApp.java                          ← CLI app (OWASP 2021)
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    └── sasttest/
                        └── Owasp2025WebApp.java  ← Web server app (OWASP 2025)
```

### Source Summary

| File | OWASP Version | Type | Vulnerabilities Demonstrated |
|------|---------------|------|------------------------------|
| `VulnerableApp.java` | 2021 | CLI | All 10 categories |
| `Owasp2025WebApp.java` | 2025 | HTTP Server | All 10 categories (updated) |

---

## Dependencies (`pom.xml`)

| Dependency                  | Version   | Purpose / Vulnerability Link              |
|-----------------------------|-----------|-------------------------------------------|
| `commons-collections`       | `3.2.1`   | A03:2025 — Known deserialization vuln (CVE-2015-7501) |
| `slf4j-api`                 | `1.7.32`  | Logging API                               |
| `slf4j-simple`              | `1.7.32`  | Simple logging implementation             |
| `sqlite-jdbc`               | `3.41.2.1`| A05:2025 — SQLite for SQL injection testing |

### Build Plugin

- **maven-assembly-plugin** (`3.6.0`) — Packages a fat JAR with all dependencies, including a `mainClass` manifest entry pointing to `Owasp2025WebApp`.

---

## OWASP Top 10 (2021) — `VulnerableApp.java`

| #   | OWASP Category                              | Method(s)                                  |
|-----|----------------------------------------------|--------------------------------------------|
| A01 | **Broken Access Control**                    | `readFile()` — Path Traversal              |
| A02 | **Cryptographic Failures**                   | `hashPassword()` — MD5 hashing             |
| A03 | **Injection**                                | `getUserData()` — SQL Injection            |
| A03 | **Injection**                                | `pingHost()` — OS Command Injection        |
| A04 | **Insecure Design**                          | `transferFunds()` — Missing validation     |
| A05 | **Security Misconfiguration**                | `parseXml()` — XXE (XML External Entities) |
| A06 | **Vulnerable & Outdated Components**         | `dummyComponentUsage()` — commons-collections 3.2.1 |
| A07 | **Identification & Authentication Failures** | `login()` — Hardcoded credentials          |
| A08 | **Software & Data Integrity Failures**       | `deserializeData()` — Insecure Deserialization |
| A09 | **Security Logging & Monitoring Failures**   | `processPayment()` — PII in logs, swallowed exceptions |
| A10 | **Server-Side Request Forgery (SSRF)**       | `fetchUrlContent()` — Unvalidated URL fetch |

---

## OWASP Top 10 (2025) — `Owasp2025WebApp.java`

> **Key changes from 2021:** A03 is now "Software Supply Chain Failures", A10 is "Mishandling of Exceptional Conditions" (new category).

| #   | OWASP Category                              | Endpoint / Method                          |
|-----|----------------------------------------------|--------------------------------------------|
| A01 | **Broken Access Control**                    | `/a01-access` — No role verification       |
| A02 | **Security Misconfiguration**                | `/a02-misconfig` — Missing security headers |
| A03 | **Software Supply Chain Failures**           | `/a03-supplychain` — commons-collections 3.2.1 |
| A04 | **Cryptographic Failures**                   | `/a04-crypto` — MD5 hashing                |
| A05 | **Injection**                                | `/a05-injection` — SQL Injection           |
| A06 | **Insecure Design**                          | `/a06-design` — Flawed business logic      |
| A07 | **Identification & Authentication Failures** | `/a07-auth` — Hardcoded credentials        |
| A08 | **Software or Data Integrity Failures**      | `/a08-integrity` — Insecure Deserialization |
| A09 | **Security Logging & Alerting Failures**     | `/a09-logging` — PII in logs               |
| A10 | **Mishandling of Exceptional Conditions**    | `/a10-exceptions` — Stack trace exposure, fail-open |

---

## Quick Start

### Build

```bash
mvn clean package
```

### Run — Web Server (2025)

```bash
java -jar target/vulnerable-sast-target-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Then visit: `http://localhost:8080/a05-injection?id=1`

### Run — CLI (2021)

```bash
mvn exec:java -Dexec.mainClass="com.example.sasttest.VulnerableApp"
```

---

## Suggested SAST Tools to Test

- **Semgrep** — `semgrep --config=auto src/`
- **SpotBugs** / **FindBugs**
- **SonarQube** — `mvn sonar:sonar`
- **Checkmarx**, **Fortify**, **Veracode**
- **OWASP Dependency-Check** — `mvn org.owasp:dependency-check-maven:check`
- **OWASP Dependency-Track** — for supply chain monitoring

---

## License

Educational / Testing purposes only.
