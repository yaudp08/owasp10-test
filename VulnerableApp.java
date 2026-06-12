package com.example.sasttest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intentionally vulnerable application covering OWASP Top 10 (2021).
 * DO NOT deploy this code in a production environment.
 */
public class VulnerableApp {

    private static final Logger logger = LoggerFactory.getLogger(VulnerableApp.class);

    // A02: Cryptographic Failures - Hardcoded AWS secrets
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";

    private final String dbPath = "jdbc:sqlite:test.db";

    /**
     * Database setup for testing purposes
     */
    public void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id TEXT, name TEXT)");
            stmt.execute("INSERT INTO users (id, name) VALUES ('1', 'Alice'), ('2', 'Bob')");
        } catch (Exception e) {
            logger.error("DB Setup failed", e);
        }
    }

    /**
     * A01: Broken Access Control - Path Traversal
     */
    public void readFile(String filename) throws IOException {
        // VULNERABILITY: User input directly dictates file paths
        File file = new File("data/" + filename);
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine();
            br.close();
        }
    }

    /**
     * A02: Cryptographic Failures - Weak Hashing Algorithm
     */
    public byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        // VULNERABILITY: MD5 is cryptographically broken
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(password.getBytes());
    }

    /**
     * A03: Injection - SQL Injection (SQLite)
     */
    public void getUserData(String userId) throws Exception {
        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement stmt = conn.createStatement()) {
            // VULNERABILITY: Concatenating input directly into a SQL query
            String query = "SELECT * FROM users WHERE id = '" + userId + "'";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("   Found User: " + rs.getString("name"));
            }
        }
    }

    /**
     * A03: Injection - OS Command Injection
     */
    public void pingHost(String hostname) throws IOException {
        // VULNERABILITY: OS command execution without sanitization
        Runtime.getRuntime().exec("ping -c 1 " + hostname);
    }

    /**
     * A04: Insecure Design - Missing Business Logic validation
     */
    public int transferFunds(int amountToTransfer) {
        int currentBalance = 1000;
        // VULNERABILITY: No check if 'amountToTransfer' is negative, allowing users to steal funds
        currentBalance -= amountToTransfer;
        return currentBalance;
    }

    /**
     * A05: Security Misconfiguration - XML External Entities (XXE)
     */
    public void parseXml(InputStream xmlStream) throws Exception {
        // VULNERABILITY: DocumentBuilderFactory does not disable XXE features
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(xmlStream);
    }

    /**
     * A06: Vulnerable and Outdated Components
     * Note: This is primarily tested by the SAST tool reading the pom.xml (commons-collections 3.2.1)
     */
    public void dummyComponentUsage() {
        // Simulating the presence of vulnerable third-party library usage
        org.apache.commons.collections.map.HashedMap map = new org.apache.commons.collections.map.HashedMap();
        map.put("key", "value");
    }

    /**
     * A07: Identification and Authentication Failures
     */
    public boolean login(String username, String password) {
        // VULNERABILITY: Hardcoded admin credentials & extremely weak password checking
        if ("admin".equals(username) && "password123".equals(password)) {
            return true;
        }
        return false;
    }

    /**
     * A08: Software and Data Integrity Failures - Insecure Deserialization
     */
    public Object deserializeData(byte[] serializedData) throws IOException, ClassNotFoundException {
        // VULNERABILITY: Deserializing untrusted data can lead to Remote Code Execution
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    /**
     * A09: Security Logging and Monitoring Failures
     */
    public void processPayment(String creditCardNumber) {
        // VULNERABILITY: Logging highly sensitive information (PII/PCI data) in plain text
        logger.info("Processing payment for credit card: " + creditCardNumber);
        
        try {
            if (creditCardNumber == null) throw new Exception("Invalid card");
        } catch (Exception e) {
            // VULNERABILITY: Empty catch block (swallowing exceptions completely) hides system failures
        }
    }

    /**
     * A10: Server-Side Request Forgery (SSRF)
     */
    public void fetchUrlContent(String targetUrl) throws IOException {
        // VULNERABILITY: Fetching an arbitrary URL provided by the user (allows scanning internal networks)
        URL url = new URL(targetUrl);
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        is.close();
    }

    public static void main(String[] args) {
        System.out.println("Starting comprehensive OWASP Top 10 SAST target...");
        VulnerableApp app = new VulnerableApp();

        System.out.println("\n[Setting up SQLite DB for Injection tests...]");
        app.setupDatabase();

        System.out.println("--- A01: Path Traversal ---");
        try { app.readFile("../../../etc/passwd"); } catch (Exception e) { /* Expected */ }

        System.out.println("--- A02: Cryptographic Failures ---");
        try { app.hashPassword("test"); } catch (Exception e) { /* Expected */ }

        System.out.println("--- A03: SQL Injection (SQLite) ---");
        try { 
            System.out.println("   Normal input:");
            app.getUserData("1");
            System.out.println("   Malicious input (1' OR '1'='1):");
            app.getUserData("1' OR '1'='1"); 
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println("--- A04: Insecure Design ---");
        int newBalance = app.transferFunds(-500);
        System.out.println("   Balance after transferring negative $500: $" + newBalance);

        System.out.println("--- A07: Authentication Failures ---");
        app.login("admin", "password123");

        System.out.println("--- A09: Logging Failures ---");
        app.processPayment("4532-1111-2222-3333");

        System.out.println("--- A10: SSRF ---");
        try { app.fetchUrlContent("http://169.254.169.254/latest/meta-data/"); } catch (Exception e) { /* Expected */ }

        System.out.println("\nExecution finished. Cleaning up Database...");
        new File("test.db").delete(); // Cleanup SQLite file
    }
}

