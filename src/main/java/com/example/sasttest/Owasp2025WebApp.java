package com.example.sasttest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

/**
 * Intentionally vulnerable Web Application covering the OWASP Top 10 (2025).
 * DO NOT deploy this code in a production environment.
 */
public class Owasp2025WebApp {

    private static final Logger logger = LoggerFactory.getLogger(Owasp2025WebApp.class);
    private static final String DB_PATH = "jdbc:sqlite:web_test.db";

    public static void main(String[] args) throws Exception {
        setupDatabase();

        // Create a simple built-in Java web server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/a01-access", new A01BrokenAccessControlHandler());
        server.createContext("/a02-misconfig", new A02SecurityMisconfigHandler());
        server.createContext("/a03-supplychain", new A03SupplyChainHandler());
        server.createContext("/a04-crypto", new A04CryptographicFailuresHandler());
        server.createContext("/a05-injection", new A05InjectionHandler());
        server.createContext("/a06-design", new A06InsecureDesignHandler());
        server.createContext("/a07-auth", new A07AuthFailuresHandler());
        server.createContext("/a08-integrity", new A08IntegrityFailuresHandler());
        server.createContext("/a09-logging", new A09LoggingFailuresHandler());
        server.createContext("/a10-exceptions", new A10ExceptionalConditionsHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("OWASP 2025 Web Target running on http://localhost:8080");
        System.out.println("Try hitting endpoints like: http://localhost:8080/a05-injection?id=1");
    }

    private static void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_PATH);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id TEXT, name TEXT)");
            stmt.execute("INSERT INTO users (id, name) VALUES ('1', 'Alice'), ('2', 'Bob')");
        } catch (Exception e) {
            logger.error("DB Setup failed", e);
        }
    }

    /**
     * Helper to send HTTP responses
     */
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // ========================================================================
    // OWASP TOP 10 2025 ENDPOINT HANDLERS
    // ========================================================================

    /**
     * A01:2025 - Broken Access Control
     */
    static class A01BrokenAccessControlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // VULNERABILITY: No role verification check before serving an admin-only resource
            String response = "Admin Dashboard: Welcome Admin. Secret Data: XYZ123";
            sendResponse(exchange, 200, response);
        }
    }

    /**
     * A02:2025 - Security Misconfiguration
     */
    static class A02SecurityMisconfigHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // VULNERABILITY: Missing security headers (HSTS, CSP, etc.) and verbose server info
            exchange.getResponseHeaders().add("Server", "Outdated-Custom-Server/1.0");
            sendResponse(exchange, 200, "Security Misconfiguration: Missing critical security headers.");
        }
    }

    /**
     * A03:2025 - Software Supply Chain Failures
     */
    static class A03SupplyChainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // VULNERABILITY: Utilizing a severely outdated dependency from the POM (commons-collections 3.2.1)
            org.apache.commons.collections.map.HashedMap map = new org.apache.commons.collections.map.HashedMap();
            map.put("status", "Using vulnerable supply chain dependency");
            sendResponse(exchange, 200, "Created: " + map.get("status"));
        }
    }

    /**
     * A04:2025 - Cryptographic Failures
     */
    static class A04CryptographicFailuresHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // VULNERABILITY: MD5 is cryptographically broken
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash = md.digest("sample_password".getBytes());
                sendResponse(exchange, 200, "MD5 Hash generated with length: " + hash.length);
            } catch (Exception e) {
                sendResponse(exchange, 500, "Error");
            }
        }
    }

    /**
     * A05:2025 - Injection
     */
    static class A05InjectionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String id = query != null ? query.split("=")[1] : "1";
            StringBuilder result = new StringBuilder();

            try (Connection conn = DriverManager.getConnection(DB_PATH);
                 Statement stmt = conn.createStatement()) {
                // VULNERABILITY: SQL Injection via concatenated string instead of PreparedStatement
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = '" + id + "'");
                while (rs.next()) {
                    result.append("User: ").append(rs.getString("name")).append("\n");
                }
            } catch (Exception e) {
                result.append("DB Error");
            }
            sendResponse(exchange, 200, result.toString());
        }
    }

    /**
     * A06:2025 - Insecure Design
     */
    static class A06InsecureDesignHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // VULNERABILITY: Flawed business logic (missing constraints on quantity, allowing negative totals)
            int itemPrice = 50;
            int requestedQuantity = -5; // Simulated attacker input bypassing frontend checks
            int finalPrice = itemPrice * requestedQuantity;
            sendResponse(exchange, 200, "Order placed! Total charged to card: $" + finalPrice);
        }
    }

    /**
     * A07:2025 - Authentication Failures
     */
    static class A07AuthFailuresHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // VULNERABILITY: Hardcoded credentials & lack of rate limiting
            String hardcodedPass = "admin123";
            sendResponse(exchange, 200, "Auth check completed against hardcoded credential: " + hardcodedPass);
        }
    }

    /**
     * A08:2025 - Software or Data Integrity Failures
     */
    static class A08IntegrityFailuresHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // VULNERABILITY: Deserializing untrusted data without validation
            String base64Payload = "rO0ABXNyAA5qYXZhLmxhbmcuTG9uZzuwZ36DOP0FAgABSgAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAAAAAAew==";
            try {
                byte[] decoded = Base64.getDecoder().decode(base64Payload);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decoded));
                ois.readObject();
                sendResponse(exchange, 200, "Data deserialized.");
            } catch (Exception e) {
                sendResponse(exchange, 500, "Deserialization crashed");
            }
        }
    }

    /**
     * A09:2025 - Security Logging and Alerting Failures
     */
    static class A09LoggingFailuresHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // VULNERABILITY: Logging highly sensitive PII in clear text
            String fakeCreditCard = "4532-1111-2222-3333";
            logger.info("Payment processed for credit card: " + fakeCreditCard);
            sendResponse(exchange, 200, "Payment processed. Check logs.");
        }
    }

    /**
     * A10:2025 - Mishandling of Exceptional Conditions (NEW IN 2025)
     */
    static class A10ExceptionalConditionsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            boolean isAuthenticated = false;
            try {
                // Simulate an unexpected error condition during authentication (e.g. database goes offline)
                int unpredictableCrash = 10 / 0; 
                isAuthenticated = (unpredictableCrash == 1);
            } catch (Exception e) {
                // VULNERABILITY 1: Exposing the raw stack trace to the end-user
                e.printStackTrace(new PrintWriter(exchange.getResponseBody()));
                
                // VULNERABILITY 2: "Failing Open" - granting access because the exception wasn't handled securely
                isAuthenticated = true; 
            }

            if (isAuthenticated) {
                sendResponse(exchange, 200, "\nAccess Granted due to Mishandled Exception (Fail-Open).");
            }
        }
    }
}

