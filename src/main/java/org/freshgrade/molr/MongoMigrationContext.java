package org.freshgrade.molr;

import javax.annotation.PostConstruct;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.freshgrade.molr.interfaces.MongoMetadataRepository;
import org.freshgrade.molr.interfaces.MongoMigrator;
import org.freshgrade.molr.interfaces.MongoRepository;
import org.springframework.util.Assert;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ronbierman on 15-10-08.
 */
@Slf4j
@NoArgsConstructor
public class MongoMigrationContext {

    @Setter
    MongoMetadataRepository mongoMigrationRepository;
    
    @Setter
    MongoRepository mongoRepository;

    @Setter
    MongoMigrationConfiguration mongoMigratorConfigurer;


    @PostConstruct
    public void postConstruct() {
    	Assert.notNull(mongoMigratorConfigurer);
    	Assert.notNull(mongoMigrationRepository);
    	Assert.notNull(mongoRepository);
    }

    /**
     * Handles an oplog document by looking up a migrator in the mongoMigratorConfigurer.
     * 
     * If no migrator for the namespace is found the document will be ignored. Otherwise based on the operation the corresponding (UPDATE, INSERT or DELETE)
     * method on the migrator will be called.
     * 
     * Other operations are not supported and will be ignored. So are specific replication documents.
     * 
     * 
     * @param op
     */
    
    public void processOp(Document op) {
        log.trace("Handling: {}", op);
        String namespace = op.getString(MongoOplog.NAMESPACE);

        MongoMigrator migrator = mongoMigratorConfigurer.getMigrator(namespace);
        if (migrator == null) {
            log.trace("No migrator configured for namespace: {}", namespace);
            return;
        }

        String operation = op.getString(MongoOplog.OPERATION);
        
        switch (operation) {
            case MongoOplog.Operation.UPDATE:

                if ("repl.time".equals(op.get("ns")) || op.containsKey(MongoOplog.REPLICATION)) {

                } else {
                    Document updateDocument = op.get(MongoOplog.OBJECT, Document.class);
                    ObjectId updateObjectId = updateDocument.getObjectId("_id");

                    log.trace("Update document: {}, {}", updateObjectId, updateDocument);
                    migrator.handleUpdate(updateObjectId, updateDocument);
                }
                break;
            case MongoOplog.Operation.INSERT:
                Document insertDocument = op.get(MongoOplog.OBJECT, Document.class);
                ObjectId insertObjectId = insertDocument.getObjectId("_id");

                log.trace("Insert document: {}, {}", insertObjectId, insertDocument);
                migrator.handleInsert(insertObjectId, insertDocument);
                break;
            case MongoOplog.Operation.DELETE:
                Document deleteDocument = op.get(MongoOplog.OBJECT, Document.class);
                ObjectId deleteObjectId = deleteDocument.getObjectId("_id");

                log.trace("Delete document: {}", deleteObjectId);
                migrator.handleDelete(deleteObjectId);
                break;
            default:
                log.info("Mongo migrator is ignoring unsupported operation: {}", op);
                break;
        }

        updateTimestamp(op);

    }

    private OplogIdentifier updateTimestamp(Document document) {
        BsonTimestamp bsonTimestamp = document.get(MongoOplog.TIMESTAMP, BsonTimestamp.class);
        Integer timeStamp = bsonTimestamp.getTime();
        Integer increment = bsonTimestamp.getInc();

        OplogIdentifier oplogIdentifier = new OplogIdentifier(timeStamp, increment);

        return mongoMigrationRepository.updateLastTimestamp(oplogIdentifier);
    }

    /**
     * Updates the MongoMigrationRepository with the latest OplogIdentifier.
     * 
     * Throws an IllegalStateException in case no oplog record is found.
     */
    public OplogIdentifier updateToLastAvailableOplog() {

		OplogIdentifier lastOplog = mongoRepository.getLastOplog();
        if (lastOplog == null) {
            throw new IllegalStateException("No last oplog record found. Please make sure that there is a replica set configured");
        }
        
        log.info("Persisting the lastOplog");
        return mongoMigrationRepository.updateLastTimestamp(lastOplog);
    }
}
