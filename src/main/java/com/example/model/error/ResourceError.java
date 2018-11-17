package com.example.model.error;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A generic resource error class that is used for 4xx/5xx responses.
 */
@XmlRootElement
@Data
public class ResourceError {
    private final int errorCode;
    private final String errorMessage;

    public ResourceError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
