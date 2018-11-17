package com.example.storage;


import com.example.model.data.Document;

import java.util.Optional;

public interface DocumentStore {
    public boolean contains(String id);
    public boolean put(String id, Document document);
    public Optional<Document> get(String id);
}
