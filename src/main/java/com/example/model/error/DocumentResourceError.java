package com.example.model.error;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Data
public class DocumentResourceError extends ResourceError {
    public DocumentResourceError(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public static final DocumentResourceError NOT_FOUND =
            new DocumentResourceError(1000001, "Document was not found.");

    public static final DocumentResourceError CANNOT_CHANGE_ID =
            new DocumentResourceError(1000002, "Cannot modify document id.");

    public static final DocumentResourceError CANNOT_CHANGE_CREATE_DATE =
            new DocumentResourceError(1000003, "Cannot modify document create date.");

    public static final DocumentResourceError CANNOT_CHANGE_LAST_EDIT_DATE =
            new DocumentResourceError(1000004, "Cannot modify document last edit date.");
}
