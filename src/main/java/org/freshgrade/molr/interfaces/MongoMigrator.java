package org.freshgrade.molr.interfaces;

import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Created by ronbierman on 15-10-08.
 */
public interface MongoMigrator {
    /**
     * This will be called in case there's no previous migration run and allows
     * you to do an initial full sync before accepting individual oplog tails
     *
     */
    public void handleFullSync(MongoDatabase mongoClient);

    public void handleInsert(ObjectId objectId, Document document);

    public void handleUpdate(ObjectId objectId, Document document);

    public void handleDelete(ObjectId op);

    public String getCollectionName();
}
