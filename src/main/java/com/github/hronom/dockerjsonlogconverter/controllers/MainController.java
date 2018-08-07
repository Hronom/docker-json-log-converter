package com.github.hronom.dockerjsonlogconverter.controllers;

import com.github.hronom.dockerjsonlogconverter.components.docker.json.log.ConvertingService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {
    private final Log logger = LogFactory.getLog(getClass());

    private final Resource robotsResource;

    private final ConvertingService convertingService;

    @Autowired
    public MainController(
        @Value(value = "classpath:data/robots.txt") Resource robotsResource,
        ConvertingService convertingService
    ) {
        this.robotsResource = robotsResource;
        this.convertingService = convertingService;
    }

    @RequestMapping(value = {"/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(
        Model model,
        @ModelAttribute("processTextForm") ProcessTextForm processTextForm
    ) throws IOException {
        if (StringUtils.hasText(processTextForm.getJsonInput())) {
            model.addAttribute(
                "processTextResult",
                convertingService.toTxt(processTextForm.getJsonInput())
            );
        }
        return "index";
    }

    @PostMapping(value = {"/processFile"})
    public String processFile(
        Model model,
        @ModelAttribute("processTextForm") ProcessTextForm processTextForm,
        @RequestParam("file") MultipartFile file,
        HttpServletResponse response
    ) throws IOException {
        String fileName = file.getOriginalFilename() + ".txt";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        convertingService.saveToTxt(file.getInputStream(), response.getWriter());
        return "index";
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
