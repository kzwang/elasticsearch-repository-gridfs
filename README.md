GridFS repository plugin for Elasticsearch
==================================

[![Build Status](https://travis-ci.org/kzwang/elasticsearch-gridfs-repository.png?branch=master)](https://travis-ci.org/kzwang/elasticsearch-gridfs-repository)



|      GridFS Plugin          | elasticsearch         | Release date |
|-----------------------------|-----------------------|:------------:|
| 1.0.0-SNAPSHOT (master)     | 1.0.0.RC2             |              |



## Create Repository
```
    $ curl -XPUT 'http://localhost:9200/_snapshot/my_backup' -d '{
        "type": "gridfs",
        "settings": {
            "gridfs_host": "localhost",
            "database": "mongo",
            "compress": true
        }
    }'
```

See [Snapshot And Restore](http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/modules-snapshots.html) for more informations


## Settings
|  Setting                            |   Description
|-------------------------------------|------------------------------------------------------------
| gridfs_host                         | MongoDB host. Defaults to `localhost`
| gridfs_port                         | MongoDB port. Defaults to `27017`
| database                            | GridFS database name in MongoDB. **Mandatory**
| bucket                              | GridFS bucket name in MongoDB. Defaults to `fs`
| concurrent_streams                  | Throttles the number of streams (per node) preforming snapshot operation. Defaults to 5
| compress                            | Turns on compression of the snapshot files. Defaults to `true`.
| max_restore_bytes_per_sec           | Throttles per node restore rate. Defaults to `20mb` per second.
| max_snapshot_bytes_per_sec          | Throttles per node snapshot rate. Defaults to `20mb` per second.