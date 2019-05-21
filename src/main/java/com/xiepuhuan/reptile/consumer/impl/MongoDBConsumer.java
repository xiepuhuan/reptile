package com.xiepuhuan.reptile.consumer.impl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xiepuhuan.reptile.config.MongoDBConfig;
import com.xiepuhuan.reptile.exception.UnsupportedObjectException;
import com.xiepuhuan.reptile.model.Result;
import com.xiepuhuan.reptile.utils.ArgUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 * @author xiepuhuan
 */
public class MongoDBConsumer extends AbstractCloseableBufferConsumer {

    private final MongoDBConfig mongoDBConfig;

    private final MongoClient mongoClient;

    private final MongoDatabase mongoDatabase;

    private final Map<String, MongoCollection<Document>> collectionMap;

    private final Map<MongoCollection<Document>, List<Document>> collectionDocumentMap;

    public MongoDBConsumer(MongoDBConfig config, int flushInterval) {
        super(flushInterval);
        ArgUtils.notNull(config, "mongoDBConfig");
        config.check();
        this.mongoDBConfig = config;

        MongoClientSettings.Builder settingsBuilder =  MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(String.format("mongodb://%s:%d", config.getIp(), config.getPort())))
                .applyToConnectionPoolSettings(builder -> builder.maxSize(config.getMaxConnectionSize()).minSize(config.getMinConnectionSize()));

        if (config.getUsername() != null) {
            settingsBuilder.credential(MongoCredential.createCredential(config.getUsername(), config.getDatabase(), config.getPassword().toCharArray()));
        }

        this.mongoClient = MongoClients.create(settingsBuilder.build());
        this.mongoDatabase = mongoClient.getDatabase(config.getDatabase());
        this.collectionMap = new HashMap<>();
        this.collectionDocumentMap = new HashMap<>();
    }

    public MongoDBConsumer(MongoDBConfig config) {
        this(config, DEFAULT_FLUSH_INTERVAL);
    }


    public MongoDBConsumer(int flushInterval) {
        this(MongoDBConfig.DEFAULT_MONGODB_CONFIG, flushInterval);
    }

    public MongoDBConsumer() {
        this(MongoDBConfig.DEFAULT_MONGODB_CONFIG, DEFAULT_FLUSH_INTERVAL);
    }

    @Override
    protected void flush(Result[] buffer, int position) {

        if (position <= 0) {
            return;
        }

        for (int i = 0; i < position; ++i) {
            String collectionName;
            if ((collectionName = buffer[i].getExtendedField(Result.MONGODB_DATABASE_COLLECTION_NAME)) == null) {
                throw new UnsupportedObjectException("Object types must be MongoResult or its subclasses");
            }

            MongoCollection<Document> collection;

            if ((collection = collectionMap.get(collectionName)) == null) {
                collection = mongoDatabase.getCollection(collectionName);
                collectionMap.put(collectionName, collection);

                collectionDocumentMap.put(collection, new ArrayList<>());
            }

            List<Document> documents = collectionDocumentMap.get(collection);

            documents.add(new Document(buffer[i].getResults()));
        }

        for (Map.Entry<MongoCollection<Document>, List<Document>> entry : collectionDocumentMap.entrySet()) {
            entry.getKey().insertMany(entry.getValue());
        }

        collectionDocumentMap.values().forEach(List::clear);
    }

    @Override
    public synchronized void close() throws IOException {
        flush();
        mongoClient.close();
    }

    public MongoDBConfig getMongoDBConfig() {
        return mongoDBConfig;
    }
}
