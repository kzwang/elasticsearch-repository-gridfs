package org.elasticsearch.repositories.gridfs;

import com.mongodb.MongoClient;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import java.net.UnknownHostException;


public class GridFsService extends AbstractLifecycleComponent<GridFsService> {

    private MongoClient client;

    @Inject
    public GridFsService(Settings settings) {
        super(settings);
    }

    public synchronized MongoClient client(String host, int port) {
        if (client != null) {
            return client;
        }

        try {
            client = new MongoClient(host, port);
            return client;
        } catch (UnknownHostException e) {
            throw new ElasticsearchIllegalArgumentException("Unknown host", e);
        }
    }

    @Override
    protected void doStart() throws ElasticsearchException {

    }

    @Override
    protected void doStop() throws ElasticsearchException {

    }

    @Override
    protected void doClose() throws ElasticsearchException {
        if (client != null) {
            client.close();
        }
    }
}
