package org.vaadin.addon.grid;

import java.io.File;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.vaadin.addon.grid.demo.GriddemoApplication;

import com.vaadin.terminal.gwt.server.ApplicationServlet;

public class TestServer {

    private static final int PORT = 9998;

    public static void main(String[] args) throws Exception {
        Server server = new Server();

        final Connector connector = new SelectChannelConnector();

        connector.setPort(PORT);
        server.setConnectors(new Connector[] { connector });

        final WebAppContext context = new WebAppContext();
        final ServletHolder servletHolder = new ServletHolder(ApplicationServlet.class);
        servletHolder.setInitParameter("widgetset", "org.vaadin.addon.grid.demo.widgetset.GridDemoWidgetset");
        servletHolder.setInitParameter("application", GriddemoApplication.class.getName());

        File file = new File("./target/testwebapp");
        context.setWar(file.getPath());
        context.setContextPath("/");

        context.addServlet(servletHolder, "/*");
        server.setHandler(context);

        server.start();
        server.join();
    }

}
