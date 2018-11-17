package com.example.model.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Our "simple data structure" for POJO (plain old java objects) that models our document data structure.
 *
 * The @ annotations are used for hooking on code to automatically generate our getters/setters/constructors as well as
 * help Jackson figure out how to serialize this object to JSON format and back.
 */
@XmlRootElement
@Data
@Builder
@AllArgsConstructor
public class Document {
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXXX")
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXXX")
    private LocalDateTime lastEditDate;
    private Map<String, String> attributes;

    public Document() {
        this.id = UUID.randomUUID().toString();
        this.createDate = LocalDateTime.now();
        this.lastEditDate = LocalDateTime.now();
        this.attributes = new HashMap<>();
    }

    public Document(Map<String, String> attributes) {
        this.id = UUID.randomUUID().toString();
        this.createDate = LocalDateTime.now();
        this.lastEditDate = LocalDateTime.now();
        this.attributes = attributes;
    }
}
