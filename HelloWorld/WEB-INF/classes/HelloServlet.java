import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class HelloServlet extends HttpServlet {

    // On GET (direct visit to /hello or app root), check session/cookie and route
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String existingName = getNameFromSessionOrCookie(request);

        if (existingName != null) {
            response.sendRedirect(request.getContextPath() + "/welcome");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }
    }

    // On POST (form submission), validate and route
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("uname");

        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        username = username.trim();

        String existingName = getNameFromSessionOrCookie(request);

        if (existingName != null) {
            // Name already known — go straight to welcome
            response.sendRedirect(request.getContextPath() + "/welcome");
            return;
        }

        // New user — store in session and cookie, then show greeting
        HttpSession session = request.getSession();
        session.setAttribute("username", username);

        Cookie cookie = new Cookie("username", username);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        cookie.setHttpOnly(true);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        response.addCookie(cookie);

        response.sendRedirect(request.getContextPath() + "/greet");
    }

    private String getNameFromSessionOrCookie(HttpServletRequest request) {
        // Check session first
        HttpSession session = request.getSession(false);
        if (session != null) {
            String name = (String) session.getAttribute("username");
            if (name != null) return name;
        }

        // Fall back to cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("username".equals(c.getName()) && !c.getValue().isEmpty()) {
                    // Restore into session
                    HttpSession newSession = request.getSession();
                    newSession.setAttribute("username", c.getValue());
                    return c.getValue();
                }
            }
        }

        return null;
    }
}
