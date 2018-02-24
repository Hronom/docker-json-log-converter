package com.github.hronom.dockerjsonlogconverter.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {
    private final Log logger = LogFactory.getLog(getClass());

    private final Resource robotsResource;

    public MainController(@Value(value = "classpath:data/robots.txt") Resource robotsResource) {
        this.robotsResource = robotsResource;
    }

    @RequestMapping(value = {"/robots", "/robot", "/robot.txt", "/robots.txt", "/null"})
    public void robot(HttpServletResponse response) {
        try (InputStream resourceAsStream = robotsResource.getInputStream()) {
            response.addHeader("Content-disposition", "filename=robot.txt");
            response.setContentType("text/plain");
            IOUtils.copy(resourceAsStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            logger.error("Problem with displaying robot.txt", e);
        }
    }
}
