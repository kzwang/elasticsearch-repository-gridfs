package org.elasticsearch.repositories.gridfs;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import java.net.UnknownHostException;


public class GridFsService extends AbstractLifecycleComponent<GridFsService> {

    private DB mongoDB;

    @Inject
    public GridFsService(Settings settings) {
        super(settings);
    }

    public synchronized DB mongoDB(String host, int port, String databaseName, String username, String password) {
        if (mongoDB != null) {
            return mongoDB;
        }

        try {
            MongoClient client = new MongoClient(host, port);
            mongoDB = client.getDB(databaseName);
            if (username != null && password != null){
                mongoDB.authenticate(username, password.toCharArray());
            }
            return mongoDB;
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
        if (mongoDB != null) {
            mongoDB.getMongo().close();
        }
    }
}
