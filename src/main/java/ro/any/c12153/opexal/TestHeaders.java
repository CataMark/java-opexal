package ro.any.c12153.opexal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ro.any.c12153.opexal.bkg.AppSingleton;

/**
 *
 * @author catalin
 */
@WebServlet(name = "TestHeaders", urlPatterns = {"/thead"})
public class TestHeaders extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        try ( PrintWriter out = response.getWriter()) {            
            try {
                if (!AppSingleton.CHILD_APP) throw new Exception("Called from parent app!");
                
                out.println("Remote address: ".concat(Optional.ofNullable(request.getRemoteAddr()).orElse("")).concat("<br/>"));
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null){
                    while(headerNames.hasMoreElements()){
                        String header = headerNames.nextElement();
                        out.println(header.concat(": ").concat(Optional.ofNullable(request.getHeader(header)).orElse("")).concat("<br/>"));
                    }
                }
                out.println("Server name: ".concat(request.getServerName()));
            } catch (Exception ex) {
                out.println(ex.getMessage());
            }
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
