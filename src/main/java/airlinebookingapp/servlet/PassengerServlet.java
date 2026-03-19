package airlinebookingapp.servlet;

import airlinebookingapp.dao.PassengerDAO;
import airlinebookingapp.model.Passenger;
import airlinebookingapp.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/api/passengers/*")
public class PassengerServlet extends HttpServlet {

    private final PassengerDAO passengerDAO = new PassengerDAO();

    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        setCorsHeaders(res);
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String id = pathInfo.substring(1);
            Optional<Passenger> p = passengerDAO.findById(id);
            if (p.isPresent()) {
                res.getWriter().write(JsonUtil.success(JsonUtil.toJson(p.get())));
            } else {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write(JsonUtil.error("Passenger not found"));
            }
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(JsonUtil.error("Passenger ID required"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        setCorsHeaders(res);
        try {
            String body = readBody(req);
            String name = extractJson(body, "name");
            String email = extractJson(body, "email");
            String phoneStr = extractJson(body, "phone");
            int phone = 0;
            try { phone = phoneStr != null ? Integer.parseInt(phoneStr.trim()) : 0; } catch (NumberFormatException ignored) {}

            Passenger p = new Passenger(name, email, phone);
            passengerDAO.savePassenger(p);
            res.setStatus(HttpServletResponse.SC_CREATED);
            res.getWriter().write(JsonUtil.success(JsonUtil.toJson(p)));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write(JsonUtil.error("Error: " + e.getMessage()));
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = req.getReader()) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private String extractJson(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        idx += search.length();
        while (idx < json.length() && (json.charAt(idx) == ':' || json.charAt(idx) == ' ')) idx++;
        if (idx >= json.length()) return null;
        if (json.charAt(idx) == '"') {
            int end = json.indexOf('"', idx + 1);
            if (end == -1) return null;
            return json.substring(idx + 1, end);
        } else {
            int end = idx;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) end++;
            return json.substring(idx, end);
        }
    }
}
