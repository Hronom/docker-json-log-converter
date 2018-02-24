package com.github.hronom.dockerjsonlogconverter.components;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.spring.server.SpringVaadinServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;

@Component("vaadinServlet")
public class CustomSpringVaadinServlet extends SpringVaadinServlet {
    private final ApplicationBootstrapListener applicationBootstrapListener;

    @Autowired
    public CustomSpringVaadinServlet(ApplicationBootstrapListener applicationBootstrapListener) {
        this.applicationBootstrapListener = applicationBootstrapListener;
    }

    @Override
    protected void servletInitialized() throws ServletException {
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException {
                event.getSession().addBootstrapListener(applicationBootstrapListener);
            }
        });
        super.servletInitialized();
    }
}
