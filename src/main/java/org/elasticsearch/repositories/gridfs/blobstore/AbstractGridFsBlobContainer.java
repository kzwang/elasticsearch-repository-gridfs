package org.elasticsearch.repositories.gridfs.blobstore;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.blobstore.BlobMetaData;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.support.AbstractBlobContainer;
import org.elasticsearch.common.blobstore.support.PlainBlobMetaData;
import org.elasticsearch.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class AbstractGridFsBlobContainer extends AbstractBlobContainer {

    protected final GridFsBlobStore blobStore;

    protected final String keyPath;


    protected AbstractGridFsBlobContainer(BlobPath path, GridFsBlobStore blobStore) {
        super(path);
        this.blobStore = blobStore;
        String keyPath = path.buildAsString("/");
        if (!keyPath.isEmpty()) {
            keyPath = keyPath + "/";
        }
        this.keyPath = keyPath;
    }

    @Override
    public boolean blobExists(String blobName) {
        return blobStore.gridFS().findOne(buildKey(blobName)) != null;
    }

    @Override
    public void readBlob(final String blobName, final ReadBlobListener listener) {
        blobStore.executor().execute(new Runnable() {
            @Override
            public void run() {
                InputStream is;
                try {
                    GridFSDBFile object = blobStore.gridFS().findOne(buildKey(blobName));
                    is = object.getInputStream();
                } catch (Exception e) {
                    listener.onFailure(e);
                    return;
                }
                byte[] buffer = new byte[blobStore.bufferSizeInBytes()];
                try {
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        listener.onPartial(buffer, 0, bytesRead);
                    }
                    listener.onCompleted();
                } catch (Exception e) {
                    try {
                        is.close();
                    } catch (IOException e1) {
                        // ignore
                    }
                    listener.onFailure(e);
                }
            }
        });
    }

    @Override
    public boolean deleteBlob(String blobName) throws IOException {
        blobStore.gridFS().remove(buildKey(blobName));
        return true;
    }

    @Override
    public ImmutableMap<String, BlobMetaData> listBlobsByPrefix(@Nullable String blobNamePrefix) throws IOException {
        ImmutableMap.Builder<String, BlobMetaData> blobsBuilder = ImmutableMap.builder();

        List<GridFSDBFile> files;
        if (blobNamePrefix != null) {
            files = blobStore.gridFS().find(new BasicDBObject("filename", "/^" + buildKey(blobNamePrefix) + "/"));
        } else {
            files = blobStore.gridFS().find(new BasicDBObject("filename", "/^" + keyPath + "/"));
        }
         if (files != null && !files.isEmpty()) {
            for (GridFSDBFile file : files) {
                String name = file.getFilename().substring(keyPath.length());
                blobsBuilder.put(name, new PlainBlobMetaData(name, file.getLength()));
            }
        }
        return blobsBuilder.build();
    }

    @Override
    public ImmutableMap<String, BlobMetaData> listBlobs() throws IOException {
        return listBlobsByPrefix(null);
    }

    protected String buildKey(String blobName) {
        return keyPath + blobName;
    }
}
