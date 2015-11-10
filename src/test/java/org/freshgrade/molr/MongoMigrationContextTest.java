package org.freshgrade.molr;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.freshgrade.molr.interfaces.MongoMetadataRepository;
import org.freshgrade.molr.interfaces.MongoMigrator;
import org.freshgrade.molr.interfaces.MongoRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import mockit.Expectations;
import mockit.Mocked;


public class MongoMigrationContextTest {
	public static final String TEST_NAMESPACE = "test";

    @Mocked
    MongoMetadataRepository mongoMigrationRepository;
    
    @Mocked
    MongoRepository mongoRepository;

    @Mocked
    MongoMigrationConfiguration mongoMigratorConfigurer;
    
    @Mocked
    MongoMigrator mongoMigrator;
	
	MongoMigrationContext context = new MongoMigrationContext();
	
	@Before
	public void before() {
		context.mongoMigrationRepository = mongoMigrationRepository;
		context.mongoRepository = mongoRepository;
		context.mongoMigratorConfigurer = mongoMigratorConfigurer;
	}
	
	@Test
	public void ignoreUnkownNamespace() {
		ObjectId objectId = new ObjectId();
		Document document = createDocument(objectId, MongoOplog.Operation.INSERT);
		document.put(MongoOplog.NAMESPACE, TEST_NAMESPACE);
	}

	@Test
	public void procestsOpInsert() {
		ObjectId objectId = new ObjectId();
		Document document = createDocument(objectId, MongoOplog.Operation.INSERT);
		
		new Expectations(){{
			mongoMigratorConfigurer.getMigrator(TEST_NAMESPACE);
			returns(mongoMigrator);
			
			mongoMigrator.handleInsert(objectId, (Document) document.get(MongoOplog.OBJECT));
		}};
		
		context.processOp(document);
	}
	
	@Test
	public void procestsOpUpdate() {
		ObjectId objectId = new ObjectId();
		Document document = createDocument(objectId, MongoOplog.Operation.UPDATE);
		
		new Expectations(){{
			mongoMigratorConfigurer.getMigrator(TEST_NAMESPACE);
			returns(mongoMigrator);
			
			mongoMigrator.handleUpdate(objectId, (Document) document.get(MongoOplog.OBJECT));
		}};
		
		context.processOp(document);
	}
	
	@Test
	public void procestsOpDelete() {
		ObjectId objectId = new ObjectId();
		Document document = createDocument(objectId, MongoOplog.Operation.DELETE);
				
		new Expectations(){{
			mongoMigratorConfigurer.getMigrator(TEST_NAMESPACE);
			returns(mongoMigrator);
			
			mongoMigrator.handleDelete(objectId);
		}};
		
		context.processOp(document);
	}
	
	private Document createDocument(ObjectId objectId, String operation) {
		Document object = new Document();
		object.put("_id", objectId);
		
		Document document = new Document();
		document.put(MongoOplog.NAMESPACE, TEST_NAMESPACE);
		document.put(MongoOplog.OPERATION, operation);
		document.put(MongoOplog.TIMESTAMP, new BsonTimestamp());
		document.put(MongoOplog.OBJECT, object);
		
		return document;
	}

	@Test(expected = IllegalStateException.class)
	public void updateToLastAvailableOplogThrowsException() {
		new Expectations(){{
			mongoRepository.getLastOplog();
			returns(null);
		}};
		
		context.updateToLastAvailableOplog();
	}
	
	@Test()
	public void updateToLastAvailableOplog() {
		OplogIdentifier oplogIdentifier = new OplogIdentifier(0, 0);
		new Expectations(){{	
			mongoRepository.getLastOplog();
			returns(oplogIdentifier);
			
			mongoMigrationRepository.updateLastTimestamp(oplogIdentifier);
			returns(oplogIdentifier);
		}};
		
		assertThat(context.updateToLastAvailableOplog(), is(oplogIdentifier));
	}

}
