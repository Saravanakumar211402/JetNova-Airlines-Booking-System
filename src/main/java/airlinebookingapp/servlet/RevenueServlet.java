package airlinebookingapp.servlet;

import airlinebookingapp.dao.BookingDAO;
import airlinebookingapp.service.BookingService;
import airlinebookingapp.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/api/revenue")
public class RevenueServlet extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final BookingDAO bookingDAO = new BookingDAO();

    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
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
        try {
            double totalRevenue = bookingService.calculateRevenue();
            int confirmedCount = bookingService.confirmedCount();
            int cancelledCount = bookingService.cancelledCount();
            double cancelledRevenue = bookingDAO.calculateCancelledRevenue();

            String json = "{" +
                "\"totalRevenue\":" + totalRevenue + "," +
                "\"confirmedCount\":" + confirmedCount + "," +
                "\"cancelledCount\":" + cancelledCount + "," +
                "\"lostRevenue\":" + cancelledRevenue +
                "}";
            res.getWriter().write(JsonUtil.success(json));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write(JsonUtil.error("Error: " + e.getMessage()));
        }
    }
}
