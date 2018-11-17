package com.example.storage;

import com.example.model.data.Document;
import com.example.rest.resources.DocumentResource;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;

public class HashMapDocumentStore implements DocumentStore {

    private HashMap<String, Document> store;

    @Inject
    public HashMapDocumentStore() {
        this.store = new HashMap<>();
    }

    public boolean contains(String id) {
        return store.containsKey(id);
    }

    public boolean put(String id, Document document) {
        boolean didOverwriteElement = store.containsKey(id);
        store.put(document.getId(), document);
        return didOverwriteElement;
    }

    public Optional<Document> get(String id) {
        return (store.containsKey(id)) ? Optional.ofNullable(store.get(id)) : Optional.empty();
    }
}
