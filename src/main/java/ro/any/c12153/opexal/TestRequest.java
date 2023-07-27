package ro.any.c12153.opexal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
@WebServlet(name = "TestRequest", urlPatterns = {"/treq"})
public class TestRequest extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        HttpURLConnection connection = null;        
        try ( PrintWriter out = response.getWriter()) {
            try {
                if (AppSingleton.CHILD_APP) throw new Exception("Called from child app!");
                
                String childAppURL = Optional.ofNullable(request.getServletContext().getInitParameter("ro.any.c12153.CHILD_APP_URL"))
                        .orElseThrow(() -> new Exception("CHILD_APP_ADRESS_NOT_FOUND"));
                URL url = new URL(childAppURL.concat("/thead"));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();

                if (connection.getResponseCode() <= 0 || connection.getResponseCode() > 299 )
                    throw new Exception("CHILD_APP_RESPONSE: " + connection.getResponseMessage());
                
                try(BufferedReader breader = new BufferedReader(new InputStreamReader(connection.getInputStream()));){
                    String line;
                    while ((line = breader.readLine()) != null)
                        out.println(line);
                }
            } catch (Exception ex) {
                out.println(ex.getMessage());
            } finally {
                connection.disconnect();
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
