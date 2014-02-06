package org.elasticsearch.plugin.gridfs;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.index.snapshots.IndexShardRepository;
import org.elasticsearch.index.snapshots.blobstore.BlobStoreIndexShardRepository;
import org.elasticsearch.repositories.Repository;
import org.elasticsearch.repositories.gridfs.GridFsRepository;
import org.elasticsearch.repositories.gridfs.GridFsService;

/**
 * GridFS repository module
 */
public class GridFsModule extends AbstractModule {

    public GridFsModule() {
        super();
    }

    @Override
    protected void configure() {
        bind(GridFsService.class).asEagerSingleton();
    }
}
