package org.freshgrade.molr;

import lombok.Getter;

import org.freshgrade.molr.interfaces.MongoMigrator;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configuration class  
 * Responsible for keeping a mapping between a namespace and a MongoMigrator
 */
public class MongoMigrationConfiguration {

    @Getter
    private final String database;

    private Map<String, MongoMigrator> mongoMigrators = new HashMap<>();

    public MongoMigrationConfiguration(String database) {
        Assert.notNull(database);
        
        this.database = database;
    }

    /**
     * Register a new MongoMigrator
     *
     * @param migrator The MongoMigrator
     */
    public void registerMigrator(MongoMigrator migrator){
        Assert.notNull(migrator);

        this.mongoMigrators.put(database + "." + migrator.getCollectionName(), migrator);
    }

    /**
     * Get a MongoMigrator based on the namespace
     *
     * @param namespace The namespace to get a MongoMigrator for.
     * @return
     */
    public MongoMigrator getMigrator(String namespace){
        Assert.notNull(namespace);

        return this.mongoMigrators.get(namespace);
    }

    /**
     * Get list of all collections that have a migrator connected to them.
     * @return
     */
    public List<String> getCollections() {
    	return Collections.unmodifiableList(new LinkedList<>(this.mongoMigrators.keySet()));
    }
}
