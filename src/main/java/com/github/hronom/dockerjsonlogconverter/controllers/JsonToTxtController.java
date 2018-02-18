package com.github.hronom.dockerjsonlogconverter.controllers;

import com.github.hronom.dockerjsonlogconverter.components.ConvertingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Profile("!dev")
@RestController
public class JsonToTxtController {
    private final ConvertingService convertingService;

    @Autowired
    public JsonToTxtController(ConvertingService convertingService) {
        this.convertingService = convertingService;
    }

    @PostMapping(value = "/convert", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> toText(@RequestBody String json) throws IOException {
        return ResponseEntity.ok(convertingService.toTxt(json));
    }

    @PostMapping("/convertFile")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(convertingService.toTxt(file));
    }

    @RequestMapping(value = "/a", method = RequestMethod.POST)
    @ResponseBody
    public void downloadA(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        //response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getOriginalFilename() + ".converted.txt");
        //response.setHeader("Content-Length", String.valueOf(file.getSize()));
        convertingService.saveToTxt(file.getInputStream(), response.getWriter());
    }
}
