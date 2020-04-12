package db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MongoMethods {
    private static final Logger LOGGER = LogManager.getLogger(MongoMethods.class.getName());

    public BasicDBObject deleteDbDocuments(String keyParameter, String keyParameters) {
        BasicDBObject query = new BasicDBObject();
        switch (keyParameter) {
            case "createdByProjectId":
                query.put("createdByProject", new BasicDBObject("$eq", keyParameters));
                LOGGER.info("Delete document from DB with projectIds=" + keyParameters);
                break;
            case "projectId":
                query.put("_id.proj", new BasicDBObject("$eq", keyParameters));
                LOGGER.info("Delete document from DB with projectIds=" + keyParameters);
                break;
        }
        return query;
    }

    public void deleteActionsFromDb1ByProjectId(String dbUrl, String createdByProjectId, String projectId, String projectIds) {
        MongoDbClient mongoDbClient = new MongoDbClient();
        DBCollection collection = mongoDbClient.createConnection(dbUrl, "[dbName1]", "[collectionName1]"); //changed as private customer info
        LOGGER.info("Delete data from [dbName1] [collectionName1]"); //changed as private customer info
        collection.remove(deleteDbDocuments(createdByProjectId, projectIds));
        collection = mongoDbClient.createConnection(dbUrl, "[dbName1]", "[collectionNane2]"); //changed as private customer info
        LOGGER.info("Delete data from [dbName1] [collectionName2]"); //changed as private customer info
        collection.remove(deleteDbDocuments(projectId, projectIds));
        collection = mongoDbClient.createConnection(dbUrl, "[dbName1]", "[collectionName3]"); //changed as private customer info
        LOGGER.info("Delete data from [dbName1] [collectionName3]"); //changed as private customer info
        collection.remove(deleteDbDocuments(projectId, projectIds));
    }

    public void deleteActionsFromDb2ByProjectId(String dbUrl, String createdByProjectId, String projectIds) {
        MongoDbClient mongoDbClient = new MongoDbClient();
        DBCollection collection = mongoDbClient.createConnection(dbUrl, "[dbName2]", "[collectionName1]"); //changed as private customer info
        LOGGER.info("Delete data from [dbName2] [collectionName1]"); //changed as private customer info
        collection.remove(deleteDbDocuments(createdByProjectId, projectIds));
    }
}
