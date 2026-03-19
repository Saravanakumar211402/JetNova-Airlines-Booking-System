package airlinebookingapp.servlet;

import airlinebookingapp.exception.FlightFullException;
import airlinebookingapp.exception.FlightNotFoundException;
import airlinebookingapp.model.Flight;
import airlinebookingapp.service.FlightService;
import airlinebookingapp.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@WebServlet("/api/flights/*")
public class FlightServlet extends HttpServlet {

    private final FlightService flightService = new FlightService();

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        setCorsHeaders(res);
        String pathInfo = req.getPathInfo(); // e.g. /F101 or null

        try {
            // GET /api/flights/{id}
            if (pathInfo != null && pathInfo.length() > 1) {
                String id = pathInfo.substring(1);
                Flight flight = flightService.getAvailabFlight(id);
                res.getWriter().write(JsonUtil.success(JsonUtil.toJson(flight)));
                return;
            }

            String source = req.getParameter("source");
            String destination = req.getParameter("destination");
            String available = req.getParameter("available");
            String sort = req.getParameter("sort");

            // GET /api/flights?source=X&destination=Y
            if (source != null && destination != null) {
                List<Flight> flights = flightService.searchByRoute(source, destination);
                res.getWriter().write(JsonUtil.success(JsonUtil.toJsonArray(flights)));
                return;
            }

            // GET /api/flights?available=true
            if ("true".equalsIgnoreCase(available)) {
                List<Flight> all = flightService.getAllFlights();
                List<Flight> avail = all.stream()
                        .filter(f -> f.getAvailableSeats() > 0).toList();
                res.getWriter().write(JsonUtil.success(JsonUtil.toJsonArray(avail)));
                return;
            }

            // GET /api/flights?sort=price
            if ("price".equalsIgnoreCase(sort)) {
                List<Flight> all = flightService.getAllFlights();
                List<Flight> sorted = all.stream()
                        .sorted(Comparator.comparingDouble(Flight::getPrice)).toList();
                res.getWriter().write(JsonUtil.success(JsonUtil.toJsonArray(sorted)));
                return;
            }

            // GET /api/flights — all flights
            List<Flight> flights = flightService.getAllFlights();
            res.getWriter().write(JsonUtil.success(JsonUtil.toJsonArray(flights)));

        } catch (FlightNotFoundException e) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write(JsonUtil.error(e.getMessage()));
        } catch (FlightFullException e) {
            res.setStatus(HttpServletResponse.SC_CONFLICT);
            res.getWriter().write(JsonUtil.error(e.getMessage()));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write(JsonUtil.error("Internal server error: " + e.getMessage()));
        }
    }
}
