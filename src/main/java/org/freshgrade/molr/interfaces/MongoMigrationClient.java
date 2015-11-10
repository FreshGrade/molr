package org.freshgrade.molr.interfaces;

import com.mongodb.MongoClient;

/**
 * Interface to provide access to the Mongo database to migrate a collection
 * from.
 */
public interface MongoMigrationClient {
	public MongoClient getMongoClient();
}
