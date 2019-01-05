package ru.javaops.masterjava.upload;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.thymeleaf.TemplateEngine;

@WebListener
public class ThymeleafListener implements ServletContextListener {

  public static TemplateEngine engine;

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    engine = ThymeleafAppUtil.getTemplateEngine(servletContextEvent.getServletContext());
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {

  }
}
