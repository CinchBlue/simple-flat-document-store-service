package com.example.application;

import com.example.rest.resources.DocumentResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class SimpleServiceApplication extends ResourceConfig {
    public SimpleServiceApplication() {
        register(DocumentResource.class);
    }
}
