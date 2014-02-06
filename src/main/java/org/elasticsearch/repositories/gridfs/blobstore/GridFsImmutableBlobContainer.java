package org.elasticsearch.repositories.gridfs.blobstore;

import com.mongodb.gridfs.GridFSInputFile;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.ImmutableBlobContainer;
import org.elasticsearch.common.blobstore.support.BlobStores;

import java.io.IOException;
import java.io.InputStream;


public class GridFsImmutableBlobContainer extends AbstractGridFsBlobContainer implements ImmutableBlobContainer {

    protected GridFsImmutableBlobContainer(BlobPath path, GridFsBlobStore blobStore) {
        super(path, blobStore);
    }

    @Override
    public void writeBlob(final String blobName, final InputStream is, final long sizeInBytes, final WriterListener listener) {
        blobStore.executor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    blobStore.gridFS().remove(buildKey(blobName));  // need to remove old file if already exist
                    GridFSInputFile file = blobStore.gridFS().createFile(is, buildKey(blobName));
                    file.save();
                    listener.onCompleted();
                } catch (Exception e) {
                    listener.onFailure(e);
                }
            }
        });
    }

    @Override
    public void writeBlob(String blobName, InputStream is, long sizeInBytes) throws IOException {
        BlobStores.syncWriteBlob(this, blobName, is, sizeInBytes);
    }
}
