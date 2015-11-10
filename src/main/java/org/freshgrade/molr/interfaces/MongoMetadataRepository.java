package org.freshgrade.molr.interfaces;

import org.freshgrade.molr.OplogIdentifier;

/**
 * Repository to persist oplog meta-data.
 * 
 * 
 */
public interface MongoMetadataRepository {

	/**
	 * Save the last processed OplogIdentifier.
	 */
	public OplogIdentifier updateLastTimestamp(OplogIdentifier oplogIdentifier);

	/**
	 * Gets the OplogIdentifier of the last processed oplog record.
	 * 
	 * @return a new OplogIdentified Object.
	 */
	public OplogIdentifier getLastOplog();

}
