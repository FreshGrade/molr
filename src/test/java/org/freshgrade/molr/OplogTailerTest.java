package org.freshgrade.molr;

import java.util.ArrayList;

import org.bson.Document;
import org.freshgrade.molr.interfaces.MongoRepository;
import org.freshgrade.molr.test.ListBackedCursor;
import org.freshgrade.molr.test.ListBackedFindIterable;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;

import mockit.Expectations;
import mockit.Mocked;

public class OplogTailerTest {
	@Mocked
	MongoMigrationContext mongoMigrationContext;

	@Mocked
	MongoRepository mongoRepository;

	@Test
	public void testRunOplogTailer() {

		new Expectations(){{
			OplogIdentifier oplogIdentifier = new OplogIdentifier(1, 1);
			
			mongoRepository.getLastOplog();
			returns(oplogIdentifier);
			
			mongoRepository.openOplog((BasicDBObject) any);
			returns(oplogResponse());
			
			mongoMigrationContext.processOp((Document) any); times=1;
		}

		private FindIterable<Document> oplogResponse() {
			ArrayList<Document> list = new ArrayList<>();
			list.add(new Document());
			ListBackedCursor<Document> listBackedCursor = new ListBackedCursor<>(list);
			ListBackedFindIterable<Document> result = new ListBackedFindIterable<>(listBackedCursor);
					
			return result;
		}};
		
		OplogTailer oplogTailer = new OplogTailer(mongoMigrationContext, mongoRepository);
		oplogTailer.run();
	}
}
