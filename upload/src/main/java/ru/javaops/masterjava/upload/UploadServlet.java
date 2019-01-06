package ru.javaops.masterjava.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.model.User;

@WebServlet("/")
public class UploadServlet extends HttpServlet {

  private UserCasterByStax casterByStax = new UserCasterByStax();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    WebContext ctx = new WebContext(req, resp, req.getServletContext());
    ThymeleafListener.engine.process("index", ctx, resp.getWriter());
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    ServletFileUpload upload = new ServletFileUpload();
    WebContext ctx = new WebContext(req, resp, req.getServletContext());

    try {
      FileItemIterator iterator = upload.getItemIterator(req);
      while (iterator.hasNext()) {
        FileItemStream item = iterator.next();
        String name = item.getFieldName();
        if (!item.isFormField()) {
          try (InputStream stream = item.openStream()) {
            System.out.println("File field " + name + " with file name "
                + item.getName() + " detected.");
            // Process the input stream
            List<User> userList = casterByStax.proceesUsersByStax(stream);
            ctx.setVariable("users", userList);
            ThymeleafListener.engine.process("results", ctx, resp.getWriter());
            break;
          }

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      ThymeleafListener.engine.process("results", ctx, resp.getWriter());
    }

  }
}
