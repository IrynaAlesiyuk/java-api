package db;

import com.mongodb.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.UnknownHostException;

public class MongoDbClient {
    private static final Logger LOGGER = LogManager.getLogger(MongoDbClient.class.getName());

    private MongoClient mongoClient;

    public DBCollection createConnection(String dbUrl, String dbName, String dbCollectionName) {
        LOGGER.info("Create connection to '" + dbName + "' DB to '" + dbCollectionName + "' to dbCollectionName");
        try {
            mongoClient = new MongoClient(new MongoClientURI(dbUrl));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB database = mongoClient.getDB(dbName);
        return database.getCollection(dbCollectionName);
    }

    public void closeConnection() {
        mongoClient.close();
        LOGGER.info("Connection to DB is closed");
    }
}
