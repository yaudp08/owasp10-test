# Vulnerable SAST Target — OWASP Top 10 (2021)

> ⚠️ **WARNING: Intentionally vulnerable application for security testing / educational purposes only. DO NOT deploy in production.**

A Java application designed as a **SAST (Static Application Security Testing) target** that demonstrates all **10 OWASP Top 10 (2021)** vulnerability categories.

---

## Project Overview

| Property        | Value                                      |
|-----------------|--------------------------------------------|
| **Language**    | Java 11                                    |
| **Build Tool**  | Maven                                      |
| **Artifact ID** | `vulnerable-sast-target`                   |
| **Version**     | `1.0-SNAPSHOT`                             |
| **Main Class**  | `com.example.sasttest.VulnerableApp`       |

---

## Directory Structure

```
src/
└── main/
    └── java/
        └── com/
            └── example/
                └── sasttest/
                    └── VulnerableApp.java          ← Main application (all vulnerabilities)
```

### Source Tree

```
src/
├── main/
│   └── java/
│       └── com/
│           └── example/
│               └── sasttest/
│                   └── VulnerableApp.java
```

- **Total Java files:** 1
- **Total subfolders:** 5 (`src`, `src/main`, `src/main/java`, `src/main/java/com`, `src/main/java/com/example`, `src/main/java/com/example/sasttest`)

---

## Dependencies (`pom.xml`)

| Dependency                  | Version   | Purpose / Vulnerability Link              |
|-----------------------------|-----------|-------------------------------------------|
| `commons-collections`       | `3.2.1`   | A06 — Known deserialization vuln (CVE-2015-7501) |
| `slf4j-api`                 | `1.7.32`  | Logging API                               |
| `slf4j-simple`              | `1.7.32`  | Simple logging implementation             |
| `sqlite-jdbc`               | `3.41.2.1`| A03 — SQLite for SQL injection testing    |

### Build Plugin

- **maven-assembly-plugin** (`3.6.0`) — Packages a fat JAR with all dependencies, including a `mainClass` manifest entry.

---

## OWASP Top 10 Vulnerabilities Covered

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

## Quick Start

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/vulnerable-sast-target-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or directly:

```bash
mvn exec:java -Dexec.mainClass="com.example.sasttest.VulnerableApp"
```

---

## Suggested SAST Tools to Test

- **Semgrep** — `semgrep --config=auto src/`
- **Bandit** (Python equivalent) / **FindBugs** / **SpotBugs**
- **SonarQube** — `mvn sonar:sonar`
- **Checkmarx**, **Fortify**, **Veracode**
- **OWASP Dependency-Check** — `mvn org.owasp:dependency-check-maven:check`

---

## License

Educational / Testing purposes only.

