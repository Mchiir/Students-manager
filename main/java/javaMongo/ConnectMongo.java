package javaMongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class ConnectMongo {
	private static ConnectMongo instance = null;
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	
	 private ConnectMongo() {
	        try {
	            mongoClient = MongoClients.create("mongodb://localhost:27017");
	            database = mongoClient.getDatabase("students");
	            collection = database.getCollection("learners");
	            
	            System.out.println("MongoDB Connected to 'learners' collection in 'students' database");
	        } catch (Exception e) {
	            System.out.println("Failed to connect to MongoDB: " + e.getMessage());
	        }
	    }

	 public static ConnectMongo getInstance() {
	        if (instance == null) {
	            instance = new ConnectMongo();
	        }
	        return instance;
	}
	 
	public MongoClient getMongoClient() {
		return mongoClient;
	}
	
	public MongoCollection<Document> getCollection() {
        return collection;
    }
}