package org.freshgrade.molr;

import com.mongodb.client.MongoDatabase;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.freshgrade.molr.interfaces.MongoMigrationClient;
import org.freshgrade.molr.interfaces.MongoMetadataRepository;
import org.freshgrade.molr.interfaces.MongoMigrator;
import org.freshgrade.molr.interfaces.MongoRepository;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.List;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by ronbierman on 15-10-08.
 */
@Slf4j
public class MongoMigrationExecuter {

    @Setter
    MongoMigrationContext mongoMigratorContext;

    @Setter
    MongoMigrationConfiguration mongoMigratorConfigurer;

    @Setter
    MongoMetadataRepository mongoMigratorRepository;

    @Setter
    MongoRepository mongoRepository;

    @Setter
    MongoMigrationClient mongoMigratorClient;

    private SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();

    private ListenableFuture<?> oplogTailer;

    public MongoMigrationExecuter(){
        simpleAsyncTaskExecutor.setThreadGroupName("MongoMigrationExecuter");
        simpleAsyncTaskExecutor.setDaemon(true);
    }

    
    @PostConstruct
    public void postConstruct() {
    	Assert.notNull(mongoMigratorContext);
    	Assert.notNull(mongoMigratorConfigurer);
    	Assert.notNull(mongoMigratorRepository);
    	Assert.notNull(mongoMigratorClient);
    	
        // Fire up the migration engines.
        startMongoMigrator();
    }

    @PreDestroy
    public void destroy() {
        log.info("Stopping mongo migration executor");
        if(oplogTailer != null){
            oplogTailer.cancel(true);
        }
    }

    private void startMongoMigrator() {
        log.info("Initializing mongo migration executor");

        OplogIdentifier lastOplog = mongoMigratorRepository.getLastOplog();
        
        //Check if we're resuming a migration run or starting a fresh one.
        if(lastOplog == null){
            log.info("No last timestamp available. Starting a full import before tailing the oplog. This might take a while...");
            startFullImport();
        } else {
            log.info("Resuming from last known timestamp: {}", lastOplog);
            resumeImport(lastOplog);
        }
    }

    private void resumeImport(OplogIdentifier lastOplog) {

        if(mongoRepository.oplogExists(lastOplog)) {
            log.info("No oplog rollover detected. Ready to tail the oplog.");
            startOplogTail(lastOplog);
        } else {
            log.warn("Last timestamp not available in the oplog. Please remove last time to force a full sync if you're sure you want to do that");

            //TODO: Decide if we want to automatically do a full sync again?
            throw new IllegalStateException();
        }
    }

    private void startOplogTail(OplogIdentifier lastAvailableOplog) {
        log.info("Starting the oplog tailer from: {}", lastAvailableOplog);
        OplogTailer oplogTailer = new OplogTailer(mongoMigratorContext, mongoRepository);
        this.oplogTailer = simpleAsyncTaskExecutor.submitListenable(oplogTailer);
        this.oplogTailer.addCallback(new SuccessCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
        		log.info("Oplog trailer finished");
            }
        }, new FailureCallback() {
            @Override
            public void onFailure(Throwable ex) {
                log.warn("Oplog trailer failed with exception: {}", ex.getMessage());
                ex.printStackTrace();
                            }
        });
        log.info("oplogTailer: {}", oplogTailer);
    }

    private void startFullImport() {
        List<String> migrators = mongoMigratorConfigurer.getCollections();

        OplogIdentifier lastAvailableOplog = mongoMigratorContext.updateToLastAvailableOplog();

        migrators.forEach((namespace) -> {
            log.info("Starting full import for namespace: {}", namespace);
            long startTime = System.currentTimeMillis();

            MongoMigrator mongoMigrator = mongoMigratorConfigurer.getMigrator(namespace);
            MongoDatabase database = mongoMigratorClient.getMongoClient().getDatabase(mongoMigratorConfigurer.getDatabase());
            mongoMigrator.handleFullSync(database);

            long endTime = System.currentTimeMillis();
            log.info("Full sync done for {} in: {}ms", namespace, endTime - startTime);
        });

        startOplogTail(lastAvailableOplog);
    }

}
