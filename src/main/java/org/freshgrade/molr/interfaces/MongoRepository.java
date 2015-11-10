package org.freshgrade.molr.interfaces;

import org.bson.Document;
import org.freshgrade.molr.OplogIdentifier;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;


/**
 * Repository to interface with the mongo database.
 */
public interface MongoRepository {

	/**
	 * Checks if the oplog exists in the mongo database.
	 * 
	 * @param oplogIdentifier
	 *            The oplogIdentifier to check for.
	 * @return <code>true</code> in case the document exists. Otherwise
	 *         <code>false</code>
	 */
	public boolean oplogExists(OplogIdentifier oplogIdentifier);

	/**
	 * Opens an cursor to the oplog that's waiting forever and will add new data
	 * 
	 * @param timeQuery The 
	 * @return
	 */
	public FindIterable<Document> openOplog(BasicDBObject timeQuery);

	/**
	 * Gets the last oplogIdentifier in the oplog
	 * @return
	 */
	public OplogIdentifier getLastOplog();
}
