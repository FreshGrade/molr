package org.freshgrade.molr;


import com.mongodb.client.MongoDatabase;

import static org.hamcrest.core.Is.is;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.freshgrade.molr.interfaces.MongoMigrator;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;


public class MongoMigrationConfigurationTest {

	public static final String DATABASE_NAME = "test_database";
	public static final String MIGRATOR_COLLECTION_NAME = "collection_name";
	
	private MongoMigrationConfiguration test = new MongoMigrationConfiguration(DATABASE_NAME);
	

	@Test
	public void getCollections() {
		MongoMigrator migrator = new MockMongoMigrator(MIGRATOR_COLLECTION_NAME);
		
		test.registerMigrator(migrator);
		List<String> collections = test.getCollections();
		assertThat(collections.size(), is(1));
		
		String collection = collections.get(0);
		assertThat(collection, is(DATABASE_NAME + "." + MIGRATOR_COLLECTION_NAME));
	}

	@Test
	public void getDatabase() {
		assertThat(test.getDatabase(), is(DATABASE_NAME));
	}

	@Test
	public void getAndRegisterMigrator() {
		MongoMigrator migrator = new MockMongoMigrator(MIGRATOR_COLLECTION_NAME);
		
		test.registerMigrator(migrator);
		assertThat(test.getMigrator(DATABASE_NAME + "." + MIGRATOR_COLLECTION_NAME) , is(migrator));
	}

	
	final static class MockMongoMigrator implements MongoMigrator {
		private String collectionName;

		MockMongoMigrator(String collectionName){
			this.collectionName = collectionName;
		}
		@Override
		public void handleFullSync(MongoDatabase mongoClient) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void handleInsert(ObjectId objectId, Document document) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void handleUpdate(ObjectId objectId, Document document) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void handleDelete(ObjectId op) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getCollectionName() {
			return collectionName;
		}
		
	}
	
}
