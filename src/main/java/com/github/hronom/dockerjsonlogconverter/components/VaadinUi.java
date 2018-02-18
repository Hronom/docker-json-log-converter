package com.github.hronom.dockerjsonlogconverter.components;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Theme("valo")
@SpringUI
//@Title("docker-json-log-converter")
public class VaadinUi extends UI {
    private final Log logger = LogFactory.getLog(getClass());

    private final ConvertingService convertingService;
    private final MessageSource messageSource;

    private final Label mainLabel;
    private final TextArea inputTextArea;
    private final Button convertButton;
    private final Label orLabel;
    private final Upload upload;
    private final ProgressBar progressBar;
    private final TextArea outputTextArea;

    private volatile Path sourceTempFilePath;
    private volatile Path targetTempFilePath;
    private volatile OutputStream outputStream;

    @Autowired
    public VaadinUi(
        ConvertingService convertingService,
        MessageSource messageSource,
        @Value("${spring.application.name}") String appName
    ) {
        this.convertingService = convertingService;
        this.messageSource = messageSource;

        this.getPage().setTitle(appName);

        mainLabel = new Label(getMessageLocalized("main-label"), ContentMode.HTML);
        mainLabel.setSizeUndefined();

        inputTextArea = new TextArea();
        inputTextArea.setSizeFull();

        convertButton = new Button(getMessageLocalized("convert-button"));
        convertButton.setSizeUndefined();

        orLabel = new Label(getMessageLocalized("or-label"), ContentMode.HTML);
        orLabel.setSizeUndefined();
        orLabel.setHeight(100, Unit.PERCENTAGE);

        upload = new Upload();
        upload.setButtonCaption(getMessageLocalized("upload"));
        upload.setSizeUndefined();

        progressBar = new ProgressBar();
        progressBar.setWidth(100, Unit.PERCENTAGE);
        progressBar.setVisible(false);

        outputTextArea = new TextArea();
        outputTextArea.setSizeFull();
    }

    @Override
    protected void init(VaadinRequest request) {
        HorizontalLayout manipulationLayout = new HorizontalLayout();
        manipulationLayout.addComponent(convertButton);
        manipulationLayout.addComponent(orLabel);
        manipulationLayout.addComponent(upload);
        manipulationLayout.addComponent(progressBar);
        manipulationLayout.setSizeUndefined();

        HorizontalLayout resultLayout = new HorizontalLayout();
        resultLayout.setSizeFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponent(mainLabel);
        mainLayout.addComponent(inputTextArea);
        mainLayout.addComponent(manipulationLayout);
        mainLayout.addComponent(resultLayout);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(inputTextArea, 1.0f);
        mainLayout.setExpandRatio(manipulationLayout, 0.0f);
        mainLayout.setExpandRatio(resultLayout, 1.0f);
        setContent(mainLayout);

        inputTextArea.setPlaceholder(getMessageLocalized("input-text-area"));
        inputTextArea.setValueChangeMode(ValueChangeMode.LAZY);

        upload.setReceiver((Upload.Receiver) (filename, mimeType) -> {
            try {
                sourceTempFilePath = Files.createTempFile("", "_source");
                outputStream = Files.newOutputStream(sourceTempFilePath);
                return outputStream;
            } catch (IOException e) {
                Notification.show(
                    getMessageLocalized("notification-error"),
                    Notification.Type.ERROR_MESSAGE
                );
                logger.error("Error", e);
                return null;
            }
        });
        upload.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(Upload.StartedEvent event) {
                progressBar.reset();
                progressBar.setVisible(true);
            }
        });
        upload.addProgressListener(new Upload.ProgressListener() {
            long progress;
            @Override
            public void updateProgress(long readBytes, long contentLength) {
                long newProgress = (100 * readBytes) / contentLength;
                if (progress != newProgress) {
                    progress = newProgress;
                    progressBar.setValue(progress * 0.01f);
                }
            }
        });
        upload.addSucceededListener((Upload.SucceededListener) event -> {
            try {
                targetTempFilePath = Files.createTempFile("", "_processed");
                convertingService.saveToTxt(sourceTempFilePath, targetTempFilePath);

                resultLayout.removeAllComponents();
                DownloadFileLink downloadFileLink = new DownloadFileLink(
                    messageSource,
                    targetTempFilePath,
                    event.getFilename() + ".txt"
                );
                resultLayout.addComponent(downloadFileLink);
                progressBar.setVisible(false);
            } catch (IOException e) {
                Notification.show(
                    getMessageLocalized("notification-error"),
                    Notification.Type.ERROR_MESSAGE
                );
                logger.error("Error", e);
            }
        });

        convertButton.addClickListener((Button.ClickListener) event -> {
            try {
                resultLayout.removeAllComponents();
                String json = inputTextArea.getValue();
                String result = convertingService.toTxt(json);
                outputTextArea.setValue(result);
                resultLayout.addComponent(outputTextArea);
            } catch (Exception e) {
                Notification.show(
                    getMessageLocalized("notification-error"),
                    Notification.Type.ERROR_MESSAGE
                );
                logger.error("Error", e);
            }
        });
        outputTextArea.setPlaceholder(getMessageLocalized("output-text-area"));
        outputTextArea.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private String getMessageLocalized(String key) {
        return messageSource.getMessage(key, new Object[0], this.getLocale());
    }
}