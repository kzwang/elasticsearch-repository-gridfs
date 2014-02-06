package org.elasticsearch.repositories.gridfs.blobstore;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStore;
import org.elasticsearch.common.blobstore.ImmutableBlobContainer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;

import java.util.concurrent.Executor;


public class GridFsBlobStore extends AbstractComponent implements BlobStore {

    private final Executor executor;

    private final int bufferSizeInBytes;

    private final GridFS gridFS;

    public GridFsBlobStore(Settings settings, MongoClient mongoClient, String dbName, String bucket, Executor executor) {
        super(settings);
        this.executor = executor;

        DB db = mongoClient.getDB(dbName);
        gridFS = new GridFS(db, bucket);

        this.bufferSizeInBytes = (int) settings.getAsBytesSize("buffer_size", new ByteSizeValue(100, ByteSizeUnit.KB)).bytes();
    }

    public GridFS gridFS() {
        return gridFS;
    }

    public Executor executor() {
        return executor;
    }

    public int bufferSizeInBytes() {
        return bufferSizeInBytes;
    }

    @Override
    public ImmutableBlobContainer immutableBlobContainer(BlobPath path) {
        return new GridFsImmutableBlobContainer(path, this);
    }

    @Override
    public void delete(BlobPath path) {
        gridFS().remove(new BasicDBObject("filename", "/^" + path.buildAsString("/") + "/"));
    }

    @Override
    public void close() {

    }
}
