import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class WelcomeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String safe = escapeHtml(username);

        out.println("<!DOCTYPE html><html><head><title>Welcome Back!</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; margin: 0; padding: 0; }");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #667eea, #764ba2); min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".card { background: white; padding: 40px; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); max-width: 420px; width: 100%; text-align: center; }");
        out.println(".emoji { font-size: 3rem; margin-bottom: 16px; }");
        out.println("h1 { color: #333; font-size: 1.8rem; margin-bottom: 12px; }");
        out.println("p { color: #666; margin-bottom: 28px; font-size: 1rem; }");
        out.println(".badge { display: inline-block; background: #f0f0ff; color: #667eea; padding: 4px 14px; border-radius: 20px; font-size: 0.85rem; margin-bottom: 24px; }");
        out.println("a { display: inline-block; padding: 12px 32px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 8px; font-size: 1rem; transition: opacity 0.3s; }");
        out.println("a:hover { opacity: 0.9; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<div class='emoji'>🎉</div>");
        out.println("<h1>Welcome back, " + safe + "!</h1>");
        out.println("<span class='badge'>Returning visitor</span>");
        out.println("<p>We remembered you from your last visit.</p>");
        out.println("<a href='" + request.getContextPath() + "/logout'>Log out</a>");
        out.println("</div></body></html>");
    }

    private String escapeHtml(String input) {
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
