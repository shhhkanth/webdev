import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StudentServlet extends HttpServlet {

    // POST /register  → insert new student
    // POST /students  → update existing student
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("update".equals(action)) {
            updateStudent(req, res);
        } else {
            insertStudent(req, res);
        }
    }

    // GET /students          → list all students (JSON)
    // GET /students?delete=id → delete student
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String deleteId = req.getParameter("delete");
        if (deleteId != null) {
            deleteStudent(deleteId, req, res);
            return;
        }
        listStudents(req, res);
    }

    // ── INSERT ────────────────────────────────────────────────────────────────
    private void insertStudent(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String name       = sanitize(req.getParameter("name"));
        String regno      = sanitize(req.getParameter("regno"));
        String email      = sanitize(req.getParameter("email"));
        String phone      = sanitize(req.getParameter("phone"));
        String dob        = sanitize(req.getParameter("dob"));
        String gender     = sanitize(req.getParameter("gender"));
        String course     = sanitize(req.getParameter("course"));
        String department = sanitize(req.getParameter("department"));
        String semester   = sanitize(req.getParameter("semester"));
        String address    = sanitize(req.getParameter("address"));

        String sql = "INSERT INTO students (name,regno,email,phone,dob,gender,course,department,semester,address) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, regno);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, dob);
            ps.setString(6, gender);
            ps.setString(7, course);
            ps.setString(8, department);
            ps.setString(9, semester);
            ps.setString(10, address);
            ps.executeUpdate();

            res.sendRedirect(req.getContextPath() + "/manage.html?success=registered");

        } catch (SQLIntegrityConstraintViolationException e) {
            res.sendRedirect(req.getContextPath() + "/index.html?error=duplicate_regno");
        } catch (SQLException e) {
            res.sendRedirect(req.getContextPath() + "/index.html?error=db");
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    private void updateStudent(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String id         = sanitize(req.getParameter("id"));
        String name       = sanitize(req.getParameter("name"));
        String regno      = sanitize(req.getParameter("regno"));
        String email      = sanitize(req.getParameter("email"));
        String phone      = sanitize(req.getParameter("phone"));
        String dob        = sanitize(req.getParameter("dob"));
        String gender     = sanitize(req.getParameter("gender"));
        String course     = sanitize(req.getParameter("course"));
        String department = sanitize(req.getParameter("department"));
        String semester   = sanitize(req.getParameter("semester"));
        String address    = sanitize(req.getParameter("address"));

        String sql = "UPDATE students SET name=?,regno=?,email=?,phone=?,dob=?,gender=?,"
                   + "course=?,department=?,semester=?,address=? WHERE id=?";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, regno);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, dob);
            ps.setString(6, gender);
            ps.setString(7, course);
            ps.setString(8, department);
            ps.setString(9, semester);
            ps.setString(10, address);
            ps.setInt(11, Integer.parseInt(id));
            ps.executeUpdate();

            res.sendRedirect(req.getContextPath() + "/manage.html?success=updated");

        } catch (SQLException e) {
            res.sendRedirect(req.getContextPath() + "/manage.html?error=db");
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    private void deleteStudent(String id, HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE id=?")) {

            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
            res.sendRedirect(req.getContextPath() + "/manage.html?success=deleted");

        } catch (SQLException | NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/manage.html?error=db");
        }
    }

    // ── LIST (JSON) ───────────────────────────────────────────────────────────
    private void listStudents(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id,name,regno,email,phone,dob,gender,course,department,semester,address FROM students ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {

            out.print("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) out.print(",");
                first = false;
                out.print("{");
                out.print("\"id\":"         + rs.getInt("id")                    + ",");
                out.print("\"name\":\""     + esc(rs.getString("name"))    + "\",");
                out.print("\"regno\":\""    + esc(rs.getString("regno"))   + "\",");
                out.print("\"email\":\""    + esc(rs.getString("email"))   + "\",");
                out.print("\"phone\":\""    + esc(rs.getString("phone"))   + "\",");
                out.print("\"dob\":\""      + esc(rs.getString("dob"))     + "\",");
                out.print("\"gender\":\""   + esc(rs.getString("gender"))  + "\",");
                out.print("\"course\":\""   + esc(rs.getString("course"))  + "\",");
                out.print("\"department\":\"" + esc(rs.getString("department")) + "\",");
                out.print("\"semester\":\"" + esc(rs.getString("semester")) + "\",");
                out.print("\"address\":\""  + esc(rs.getString("address")) + "\"");
                out.print("}");
            }
            out.print("]");

        } catch (SQLException e) {
            out.print("[]");
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private String sanitize(String val) {
        return (val == null) ? "" : val.trim();
    }

    private String esc(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
}
