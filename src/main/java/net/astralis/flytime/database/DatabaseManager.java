package net.astralis.flytime.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import net.astralis.flytime.models.PlayerModel;
import org.bson.Document;

import java.util.UUID;

public class DatabaseManager {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public DatabaseManager(String uri, String dbName, String collectionName) {
        this.mongoClient = MongoClients.create(uri);
        this.database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
    }

    public void savePlayer(UUID uuid, PlayerModel model) {
        Document doc = new Document("uuid", uuid.toString())
                .append("flyTime", model.getFlyTime());
        
        collection.replaceOne(new Document("uuid", uuid.toString()), doc, new ReplaceOptions().upsert(true));
    }

    public PlayerModel loadPlayer(UUID uuid) {
        Document doc = collection.find(new Document("uuid", uuid.toString())).first();
        if (doc == null) {
            return null;
        }
        return new PlayerModel(doc.getLong("flyTime"));
    }

    public void close() {
        mongoClient.close();
    }
}
