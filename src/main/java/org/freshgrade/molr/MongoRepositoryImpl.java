package org.freshgrade.molr;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.freshgrade.molr.interfaces.MongoMigrationClient;
import org.freshgrade.molr.interfaces.MongoRepository;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class MongoRepositoryImpl implements MongoRepository {
	private final MongoClient mongoClient;
	

	public MongoRepositoryImpl(MongoMigrationClient mongoMigrationClient) {
		Assert.notNull(mongoMigrationClient);
	
		mongoClient = mongoMigrationClient.getMongoClient();
	}
	
	@Override
	public boolean oplogExists(OplogIdentifier oplogIdentifier) {
		return getOplog().count() != 0;
	}

	@Override
	public FindIterable<Document> openOplog(BasicDBObject timeQuery) {
		return getOplog()
			.find(timeQuery)
	        .noCursorTimeout(true)
	        .cursorType(CursorType.TailableAwait)
	        .sort(new BasicDBObject("$natural", 1));
	}

	@Override
	public OplogIdentifier getLastOplog() {
		try(MongoCursor<Document> cursor = getOplog()
				.find()
				.sort(new BasicDBObject("$natural", -1))
				.limit(1)
				.iterator()){
			if(cursor.hasNext()) {
				return toOplogIdentifier(cursor.next());
			} else {
				return null;
			}
		}
	}
	
    private OplogIdentifier toOplogIdentifier(Document lastOplog) {
        BsonTimestamp bsonTimestamp = lastOplog.get(MongoOplog.TIMESTAMP, BsonTimestamp.class);
        Integer timeStamp = bsonTimestamp.getTime();
        Integer increment = bsonTimestamp.getInc();

        return new OplogIdentifier(timeStamp, increment);
    }
    
	private MongoCollection<Document> getOplog() {
		return mongoClient.getDatabase(MongoOplog.OPLOG_DATABASE).getCollection(MongoOplog.OPLOG_DATABASE);
	}

}
