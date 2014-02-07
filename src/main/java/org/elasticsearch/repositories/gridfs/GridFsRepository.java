package org.elasticsearch.repositories.gridfs;

import org.elasticsearch.repositories.gridfs.blobstore.GridFsBlobStore;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStore;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.index.snapshots.IndexShardRepository;
import org.elasticsearch.repositories.RepositoryException;
import org.elasticsearch.repositories.RepositoryName;
import org.elasticsearch.repositories.RepositorySettings;
import org.elasticsearch.repositories.blobstore.BlobStoreRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


public class GridFsRepository extends BlobStoreRepository {

    public final static String TYPE = "gridfs";

    private final GridFsBlobStore blobStore;

    private final BlobPath basePath;

    private ByteSizeValue chunkSize;

    private boolean compress;


    /**
     * Constructs new BlobStoreRepository
     * @param name                 repository name
     * @param repositorySettings   repository settings
     * @param indexShardRepository an instance of IndexShardRepository
     * @param gridFsService and instance of GridFsService
     */
    @Inject
    protected GridFsRepository(RepositoryName name, RepositorySettings repositorySettings, IndexShardRepository indexShardRepository, GridFsService gridFsService) {
        super(name.getName(), repositorySettings, indexShardRepository);

        String database = repositorySettings.settings().get("database", componentSettings.get("database"));
        if (database == null) {
            throw new RepositoryException(name.name(), "No database defined for GridFS repository");
        }

        String bucket = repositorySettings.settings().get("bucket", "fs");
        String host = repositorySettings.settings().get("gridfs_host", "localhost");
        int port = repositorySettings.settings().getAsInt("gridfs_port", 27017);
        String username = repositorySettings.settings().get("gridfs_username");
        String password = repositorySettings.settings().get("gridfs_password");

        int concurrentStreams = repositorySettings.settings().getAsInt("concurrent_streams", componentSettings.getAsInt("concurrent_streams", 5));
        ExecutorService concurrentStreamPool = EsExecutors.newScaling(1, concurrentStreams, 5, TimeUnit.SECONDS, EsExecutors.daemonThreadFactory(settings, "[gridfs_stream]"));
        blobStore = new GridFsBlobStore(settings, gridFsService.mongoDB(host, port, database, username, password), bucket, concurrentStreamPool);
        this.chunkSize = repositorySettings.settings().getAsBytesSize("chunk_size", componentSettings.getAsBytesSize("chunk_size", null));
        this.compress = repositorySettings.settings().getAsBoolean("compress", componentSettings.getAsBoolean("compress", true));
        this.basePath = BlobPath.cleanPath();
    }

    @Override
    protected BlobStore blobStore() {
        return blobStore;
    }

    @Override
    protected BlobPath basePath() {
        return basePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ByteSizeValue chunkSize() {
        return chunkSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCompress() {
        return compress;
    }
}
