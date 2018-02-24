package com.github.hronom.dockerjsonlogconverter.components;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ApplicationBootstrapListener implements BootstrapListener {
    private final String googleAnalytics;
    private final String googleAdsense;

    @Autowired
    public ApplicationBootstrapListener(
        @Value(value = "classpath:data/google-analytics.html") Resource googleAnalyticsResource,
        @Value(value = "classpath:data/google-adsense.html") Resource googleAdsenseResource
    ) throws IOException {
        googleAnalytics =
            StreamUtils
                .copyToString(googleAnalyticsResource.getInputStream(), StandardCharsets.UTF_8);
        googleAdsense =
            StreamUtils
                .copyToString(googleAdsenseResource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Override
    public void modifyBootstrapFragment(BootstrapFragmentResponse bootstrapFragmentResponse) {
        // Add bootstrapFragmentResponse modification here.
    }

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        response.getDocument().head().append(googleAnalytics).append(googleAdsense);
    }
}
