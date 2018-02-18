package com.github.hronom.dockerjsonlogconverter.components;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Link;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadFileLink extends Link {
    private final Log logger = LogFactory.getLog(getClass());

    private final MessageSource messageSource;
    private final Path filePath;
    private final String uploadedFileName;

    public DownloadFileLink(MessageSource messageSource, Path filePath, String fileName) {
        super();
        this.messageSource = messageSource;
        this.filePath = filePath;
        this.uploadedFileName = fileName;
        setCaption(
            messageSource.getMessage(
                "download-file-link-caption",
                new String[] {fileName},
                this.getLocale()
            )
        );
        setDescription(
            messageSource.getMessage(
                "download-file-link-description",
                new String[] {fileName},
                this.getLocale()
            )
        );
        setTargetName("_blank");
    }

    @Override
    public void attach() {
        super.attach(); // Must call.

        StreamResource.StreamSource source = (StreamResource.StreamSource) () -> {
            try {
                return Files.newInputStream(filePath);
            } catch (IOException e) {
                logger.error("Error", e);
                return null;
            }
        };

        StreamResource resource = new StreamResource(source, uploadedFileName);

        resource.getStream().setParameter("Content-Disposition", "attachment;filename=\"" + uploadedFileName + "\"");
        resource.setMIMEType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        resource.setCacheTime(0);

        setResource(resource);
    }
}