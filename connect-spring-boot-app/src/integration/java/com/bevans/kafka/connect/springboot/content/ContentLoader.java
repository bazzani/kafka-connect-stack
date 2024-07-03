package com.bevans.kafka.connect.springboot.content;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ContentLoader {
    public String getStringFromResource(Resource resource) throws ContentLoadException {
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ContentLoadException("Unable to locate resource", e);
        }
    }
}
