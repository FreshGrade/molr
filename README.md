# molr
Mongo oplog reader library


## Enable opslog
The MongoDB oplog allows you to keep track of changes that have happened on your database in real-time. This is a very useful tool that isn't offered out of the box with a single server instance. You can follow these steps to enable to oplog on a standalone MongoDB instance.

Look up the mongod.conf you're using. This depends on how you installed mongo but you could look in the following places:
* /etc/mongod.conf
* /usr/local/etc/mongod.conf

Add the following:
```
replSet=rs0
oplogSize=1024
```
This will give your MongoDB server a replica set identity of rs0 and will allow your oplog to grow to 1024mb. You can tune these parameters to suit.

To complete the process, restart your MongoDB daemon and open a shell. You just need to issue rs.initiate() on the local database:
```
MongoDB shell version: 2.6.1
connecting to: test
> use local
switched to db local
> rs.initiate()
{
   "info2" : "no configuration explicitly specified -- making one",
   "me" : "mongo:27017",
   "info" : "Config now saved locally.  Should come online in about a minute.",
      "ok" : 1
   }
> show collections
me
oplog.rs
startup_log
system.indexes
system.replset
```

## Usage of the library.

TODO...