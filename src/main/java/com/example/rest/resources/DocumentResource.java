package com.example.rest.resources;

import com.example.model.data.Document;
import com.example.model.error.DocumentResourceError;
import com.example.storage.DocumentStore;
import com.example.storage.HashMapDocumentStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * DocumentResource (mostly exposed at "/document" path) manages the REST API for "documents" in our service.
 *
 * It makes use of a Jackson ObjectMapper to perform serialization.
 *
 * Logging uses SLF4J backed by Log4j2 in order to accomplish more "enterprise-style" logging over raw output to
 * streams.
 */
@Path("/")
@Builder
@AllArgsConstructor
@Singleton
public class DocumentResource {

    private static Logger LOG = LoggerFactory.getLogger(DocumentResource.class);
    private static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    /**
     * The document store object to use to persist our data.
     *
     * TODO: Replace this with an implementation that is a persistent database so our data doesn't disappear everytime
     * we shutdown the service!
     */
    private DocumentStore documentStore;

    public DocumentResource() {
        this.documentStore = new HashMapDocumentStore();
    }

    /**
     * Gets a document by id.
     * @param documentId The document id.
     * @return The Document as JSON.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/documents/{documentId}")
    public Response getDocument(
            @PathParam("documentId") String documentId) {

        LOG.info("GET Document {} requested at {}", documentId, LocalDateTime.now());
        Optional<Document> maybeDocument = documentStore.get(documentId);
        if (!maybeDocument.isPresent()) {
            return Response.status(204)
                    .entity(DocumentResourceError.NOT_FOUND)
                    .build();
        } else {
            Document document = documentStore.get(documentId).get();
            LOG.info("GET Document {} succeeded at {}", document.getId(), LocalDateTime.now());
            return Response.ok(document)
                    .build();
        }
    }

    /**
     * Updates attributes on the document.
     * @param documentId The id of the document to update.
     * @param info The URI info (used to get the query parameters).
     * @return The response with the status code.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/documents/{documentId}")
    public Response updateDocumentFields(
            @PathParam("documentId") String documentId,
            @Context UriInfo info) {
        MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
        LOG.info("POST for document {} requested at {}", documentId, LocalDateTime.now());

        // Get the document or send NOT FOUND as a response.
        Optional<Document> optionalDocument = documentStore.get(documentId);
        if (!optionalDocument.isPresent()) {
            return Response.status(404).entity(DocumentResourceError.NOT_FOUND).build();
        }
        Document document = documentStore.get(documentId).get();

        // Validate that we are not changing the createDate, lastEditedDate, or id
        LOG.debug("query parameters: {}", queryParameters);
        if (queryParameters.containsKey("id")) {
            return Response.status(403).entity(DocumentResourceError.CANNOT_CHANGE_ID).build();
        } else if (queryParameters.containsKey("lastEditDate")) {
            return Response.status(403).entity(DocumentResourceError.CANNOT_CHANGE_LAST_EDIT_DATE).build();
        } else if (queryParameters.containsKey("createDate")) {
            return Response.status(403).entity(DocumentResourceError.CANNOT_CHANGE_CREATE_DATE).build();
        }

        // Map the attributes onto the document.
        queryParameters.forEach((k, v) -> {
            document.getAttributes().put(k, v.get(0));
        });

        LOG.info("POST for document {} succeeded at {}", documentId, LocalDateTime.now());
        return Response.accepted().build();
    }

    /**
     * Puts a document directly into the document store. If it overwrites, it returns OK instead of CREATED.
     * @param requestBody The raw request body of the HTTP request that is expected to be JSON.
     * @return The response with the status code.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/documents")
    public Response putDocument(
            String requestBody) {
        LOG.info("PUT createDocument requested at {}", LocalDateTime.now());
        Document document = null;
        try {
            document = new ObjectMapper().readValue(requestBody, Document.class);
        } catch (IOException e) {
            LOG.error("Failed to deserialize payload request: ", e);
            return Response.status(400).build();
        }
        LOG.debug("Document: {}", document);

        boolean didOverwriteData = documentStore.put(document.getId(), document);

        LOG.info("PUT createDocument succeeded at {}", LocalDateTime.now());
        return didOverwriteData
                ? Response.ok().build()
                : Response.created(URI.create("/documents/" + document.getId())).build();
    }


    /**
     * Creates a new document with random attribute 'dummy-data'. Is not idempotent.
     * @param requestBody The request body which is expected to be JSON.
     * @return The response detailing success or failure.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/documents-random")
    public Response putDocumentWithRandomDummyData(
            String requestBody) {
        LOG.info("POST createRandomDocument requested at {}", LocalDateTime.now());
        Document document = new Document();
        document.getAttributes().put("dummy-data", UUID.randomUUID().toString());
        LOG.debug("Document: {}", document);

        boolean didOverwriteData = documentStore.put(document.getId(), document);

        LOG.info("POST createRandomDocument succeeded at {}", LocalDateTime.now());
        return didOverwriteData
                ? Response.ok().build()
                : Response.created(URI.create("/documents/" + document.getId())).build();
    }
}
