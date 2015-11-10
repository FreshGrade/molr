package org.freshgrade.molr;

/**
 * Utility class to define static fields related to the oplog.
 */
public class MongoOplog {
    public static final String TIMESTAMP = "ts";
    public static final String VERSION = "v";
    public static final String OPERATION = "op";
    public static final String OBJECT = "o";
    public static final String QUERY = "o2";
    public static final String NAMESPACE= "ns";
    public static final String NAMESPACE_SEPARATOR = ".";

    public static final String UNIQUE_ID = "h";

    public static class Operation {
        public static final String INSERT = "i";
        public static final String UPDATE = "u";
        public static final String DELETE = "d";
        public static final String COMMAND = "c";
        public static final String DATABASE = "db";
        public static final String NO_OPS = "n";
    }

    public static final String REPLICATION = "fromMigrate";

    public static final String OPLOG_COLLECTION = "oplog.rs";
    public static final String OPLOG_DATABASE= "local";

}
