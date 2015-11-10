package org.freshgrade.molr;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.freshgrade.molr.interfaces.MongoRepository;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;

/**
 * Created by ronbierman on 15-10-08.
 */
public class OplogTailer implements Runnable {

    final MongoMigrationContext mongoMigratorContext;
    
    final MongoRepository mongoRepository;


    public OplogTailer(MongoMigrationContext mongoMigrationContext, MongoRepository mongoRepository) {
        Assert.notNull(mongoMigrationContext);
        Assert.notNull(mongoRepository);

        this.mongoMigratorContext = mongoMigrationContext;
        this.mongoRepository = mongoRepository;
    }

    @Override
    public void run() {
    	OplogIdentifier lastOplog = mongoRepository.getLastOplog();
    	
        BasicDBObject timeQuery = getTimeQuery(lastOplog);
        try(MongoCursor<Document> oplogDocuments = mongoRepository.openOplog(timeQuery).iterator()){

            oplogDocuments.forEachRemaining(document -> {
                mongoMigratorContext.processOp(document);
            });
        }
    }

    /**
     * Query to return all documents in the oplog that are &gt; lastTimeStamp.
     * @param lastOplog 
     *
     * @return
     */
    private BasicDBObject getTimeQuery(OplogIdentifier lastOplog) {
        BasicDBObject result = new BasicDBObject();
        BsonTimestamp timestamp = new BsonTimestamp(lastOplog.getTime(), lastOplog.getInc());

        result.append(MongoOplog.TIMESTAMP, new BasicDBObject("$gt", timestamp));
        return result;
    }

}
