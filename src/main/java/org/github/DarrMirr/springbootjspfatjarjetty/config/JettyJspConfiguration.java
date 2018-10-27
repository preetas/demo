package org.github.DarrMirr.springbootjspfatjarjetty.config;

import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JettyJspConfiguration extends AbstractConfiguration {

    @Override
    public void preConfigure(WebAppContext context) throws Exception {
        context.setResourceBase(getJspResourceUrl());
    }

    private String getJspResourceUrl() throws IOException {
        return new ClassPathResource("META-INF/resources/").getURI().toASCIIString();
    }
}
