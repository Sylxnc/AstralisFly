package net.astralis.flytime.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import net.astralis.flytime.models.PlayerModel;
import org.bson.Document;

import java.util.UUID;

public class MongoPlayerStorage implements PlayerStorage {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> collection;

    public MongoPlayerStorage(String uri, String databaseName, String collectionName) {
        this.mongoClient = MongoClients.create(uri);
        MongoDatabase database = this.mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
    }

    @Override
    public PlayerModel loadPlayer(UUID uuid) {
        Document document = collection.find(new Document("uuid", uuid.toString())).first();
        if (document == null) {
            return null;
        }

        Long flyTime = document.getLong("flyTime");
        return new PlayerModel(flyTime == null ? 0L : flyTime);
    }

    @Override
    public void savePlayer(UUID uuid, PlayerModel model) {
        Document document = new Document("uuid", uuid.toString())
                .append("flyTime", model.getFlyTime());
        collection.replaceOne(new Document("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}

