GridFS repository plugin for Elasticsearch
==================================

[![Build Status](https://travis-ci.org/kzwang/elasticsearch-repository-gridfs.png?branch=master)](https://travis-ci.org/kzwang/elasticsearch-repository-gridfs)

In order to install the plugin, simply run: `bin/plugin -install com.github.kzwang/elasticsearch-repository-gridfs/1.0.0`.

|      GridFS Plugin          | elasticsearch         | Release date |
|-----------------------------|-----------------------|:------------:|
| 1.1.0-SNAPSHOT (master)     | 1.0.0                 |              |
| 1.0.0                       | 1.0.0                 | 2014-02-13   |
| 1.0.0.RC1                   | 1.0.0.RC2             | 2014-02-07   |



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

See [Snapshot And Restore](http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/modules-snapshots.html) for more information


## Settings
|  Setting                            |   Description
|-------------------------------------|------------------------------------------------------------
| database                            | GridFS database name in MongoDB. **Mandatory**
| gridfs_host                         | MongoDB host. Defaults to `localhost`
| gridfs_port                         | MongoDB port. Defaults to `27017`
| gridfs_username                     | MongoDB username, only required if Authentication in MongoDB is enabled
| gridfs_password                     | MongoDB password, only required if Authentication in MongoDB is enabled
| bucket                              | GridFS bucket name in MongoDB. Defaults to `fs`
| concurrent_streams                  | Throttles the number of streams (per node) preforming snapshot operation. Defaults to `5`
| compress                            | Turns on compression of the snapshot files. Defaults to `true`.
| max_restore_bytes_per_sec          | Throttles per node restore rate. Defaults to `20mb` per second.
| max_snapshot_bytes_per_sec         | Throttles per node snapshot rate. Defaults to `20mb` per second.