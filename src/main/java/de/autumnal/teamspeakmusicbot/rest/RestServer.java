package de.autumnal.teamspeakmusicbot.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class RestServer {
    private final Server server;

    public RestServer(){
        server = new Server(8080);

        ServletContextHandler ctx =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/*");
        serHol.setInitOrder(0);
        serHol.setInitParameter("jersey.config.server.provider.classnames",
                BotRestApiV1.class.getCanonicalName());
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop(){
        server.destroy();
    }
}
