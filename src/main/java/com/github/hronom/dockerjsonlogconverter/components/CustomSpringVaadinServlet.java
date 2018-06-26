package com.github.hronom.dockerjsonlogconverter.components;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class CustomSpringVaadinServlet implements VaadinServiceInitListener {
    private final ApplicationBootstrapListener applicationBootstrapListener;

    @Autowired
    public CustomSpringVaadinServlet(ApplicationBootstrapListener applicationBootstrapListener) {
        this.applicationBootstrapListener = applicationBootstrapListener;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.addBootstrapListener(applicationBootstrapListener);
    }
}
