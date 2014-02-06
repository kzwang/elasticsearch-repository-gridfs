package org.elasticsearch.repositories.gridfs;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.index.snapshots.IndexShardRepository;
import org.elasticsearch.index.snapshots.blobstore.BlobStoreIndexShardRepository;
import org.elasticsearch.repositories.Repository;

/**
 * GridFS repository module
 */
public class GridFsRepositoryModule extends AbstractModule {

    public GridFsRepositoryModule() {
        super();
    }

    @Override
    protected void configure() {
        bind(Repository.class).to(GridFsRepository.class).asEagerSingleton();
        bind(IndexShardRepository.class).to(BlobStoreIndexShardRepository.class).asEagerSingleton();
    }
}
