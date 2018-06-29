package com.github.hronom.dockerjsonlogconverter.components;

import com.github.hronom.dockerjsonlogconverter.components.docker.json.log.ConvertingService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.StartedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import elemental.css.CSSStyleDeclaration;

@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@Route("")
public class RootComponent extends Div implements LocaleChangeObserver {
    private final Log logger = LogFactory.getLog(getClass());

    private final ConvertingService convertingService;

    private volatile Path sourceTempFilePath = null;
    private volatile Path targetTempFilePath = null;
    private volatile OutputStream outputStream = null;

    @Autowired
    public RootComponent(
        ConvertingService convertingService,
        @Value("${spring.application.name}") String appName
    ) throws IOException {
        this.convertingService = convertingService;

        setTitle(appName);

        Label mainLabel = new Label(getTranslation("main-label"));
        mainLabel.setSizeUndefined();

        TextArea inputTextArea = new TextArea();
        inputTextArea.setSizeFull();
        //inputTextArea.setWordWrap(false);
        inputTextArea.setHeight(250 + CSSStyleDeclaration.Unit.PX);
        inputTextArea.setPlaceholder(getTranslation("input-text-area"));
        inputTextArea.setValueChangeMode(ValueChangeMode.ON_CHANGE);

        Button convertButton = new Button(getTranslation("convert-button"));
        convertButton.setSizeUndefined();

        Label orLabel = new Label(getTranslation("or-label"));
        orLabel.setSizeUndefined();
        orLabel.setHeight(100 + CSSStyleDeclaration.Unit.PCT);

        Label uploadLabel = new Label(getTranslation("upload"));
        uploadLabel.setSizeUndefined();

        Upload upload = new Upload();
        upload.setDropLabel(uploadLabel);
        //upload.setButtonCaption(getMessageLocalized("upload"));
        upload.setSizeUndefined();

        Label uploadProgressLabel = new Label();
        uploadProgressLabel.setWidth(100 + CSSStyleDeclaration.Unit.PCT);
        uploadProgressLabel.setVisible(false);

        TextArea outputTextArea = new TextArea();
        outputTextArea.setSizeFull();
        //outputTextArea.setWordWrap(false);
        outputTextArea.setHeight(250 + CSSStyleDeclaration.Unit.PX);
        outputTextArea.setPlaceholder(getTranslation("output-text-area"));
        outputTextArea.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        outputTextArea.setReadOnly(true);

        DisqusComponent disqusComponent = new DisqusComponent();
        disqusComponent.setSizeFull();

        VerticalLayout manipulationLayout = new VerticalLayout();
        manipulationLayout.add(convertButton);
        manipulationLayout.add(orLabel);
        manipulationLayout.add(upload);
        manipulationLayout.setSizeUndefined();

        HorizontalLayout resultLayout = new HorizontalLayout();
        resultLayout.setSizeFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(mainLabel);
        mainLayout.add(inputTextArea);
        mainLayout.add(manipulationLayout);
        mainLayout.add(resultLayout);
        mainLayout.add(disqusComponent);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(1.0f, inputTextArea);
        mainLayout.setFlexGrow(0.0f, manipulationLayout);
        mainLayout.setFlexGrow(1.0f, resultLayout);
        mainLayout.setFlexGrow(1.0f, disqusComponent);

        add(mainLayout);

        upload.setReceiver((Receiver) (filename, mimeType) -> {
            try {
                clean();
                sourceTempFilePath = Files.createTempFile("", "_source");
                outputStream = Files.newOutputStream(sourceTempFilePath);
                return outputStream;
            } catch (IOException e) {
                Notification.show(
                    getTranslation("notification-error")
                );
                logger.error("Error", e);
                return null;
            }
        });
        upload.addStartedListener((ComponentEventListener<StartedEvent>) event -> {
            resultLayout.removeAll();

            inputTextArea.clear();

            uploadProgressLabel
                .setText(getTranslation("upload-progress-label", event.getFileName()));
            resultLayout.add(uploadProgressLabel);

            uploadProgressLabel.setVisible(true);
        });
        upload.addSucceededListener((ComponentEventListener<SucceededEvent>) event -> {
            try {
                targetTempFilePath = Files.createTempFile("", "_processed");
                convertingService.saveToTxt(sourceTempFilePath, targetTempFilePath);

                resultLayout.removeAll();

                String fileName = event.getFileName() + ".txt";
                StreamResource resource =
                    new StreamResource(fileName, (InputStreamFactory) () -> {
                    try {
                        return Files.newInputStream(targetTempFilePath);
                    } catch (IOException e) {
                        logger.error("Error", e);
                        return null;
                    }
                });
                Anchor downloadLink = new Anchor(resource, getTranslation("download-file-link-caption", fileName));
                downloadLink.getElement().setAttribute("download", true);

                resultLayout.add(downloadLink);
                uploadProgressLabel.setVisible(false);
            } catch (IOException e) {
                Notification.show(
                    getTranslation("notification-error")
                );
                logger.error("Error", e);
            }
        });
        convertButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            try {
                resultLayout.removeAll();
                String json = inputTextArea.getValue();
                String result = convertingService.toTxt(json);
                outputTextArea.setValue(result);
                resultLayout.add(outputTextArea);
            } catch (Exception e) {
                Notification.show(
                    getTranslation("notification-error")
                );
                logger.error("Error", e);
            }
        });
    }

//    @Override
//    public void detach() {
//        super.detach();
//        try {
//            clean();
//        } catch (Exception e) {
//            logger.error("Error", e);
//        }
//    }

    private void clean() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
        if (sourceTempFilePath != null) {
            Files.deleteIfExists(sourceTempFilePath);
        }
        if (targetTempFilePath != null) {
            Files.deleteIfExists(targetTempFilePath);
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
//        link.setText(
//            getTranslation("root.navigate_to_component"));
    }
}