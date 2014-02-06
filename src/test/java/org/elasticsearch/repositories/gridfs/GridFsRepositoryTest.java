package org.elasticsearch.repositories.gridfs;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.snapshots.SnapshotState;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.hamcrest.Matchers.*;

@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numNodes = 2)
public class GridFsRepositoryTest extends ElasticsearchIntegrationTest {

    @Test
    public void test_snapshot_using_gridfs() {
        MongoServer server = new MongoServer(new MemoryBackend());
        InetSocketAddress serverAddress = server.bind();
        int port = serverAddress.getPort();

        String repositoryName = randomAsciiOfLengthBetween(10, 50).toLowerCase();
        String indexName = randomAsciiOfLengthBetween(10, 50).toLowerCase();
        String snapshotName = randomAsciiOfLengthBetween(10, 50).toLowerCase();

        client().admin().cluster().preparePutRepository(repositoryName).setType("gridfs")
                .setSettings(ImmutableSettings.builder()
                        .put("database", "test")
                        .put("bucket", "test")
                        .put("gridfs_host", "localhost")
                        .put("gridfs_port", port)
                )
                .get();


        client().admin().indices().prepareCreate(indexName).get();  // create index

        IndicesExistsResponse existsResponse1 = client().admin().indices().prepareExists(indexName).get();
        assertThat(existsResponse1.isExists(), equalTo(true));  // check index exist

        client().admin().cluster().prepareCreateSnapshot(repositoryName, snapshotName).setWaitForCompletion(true).get();  // create a snapshot

        client().admin().indices().prepareDelete(indexName).get();  // delete index

        IndicesExistsResponse existsResponse2 = client().admin().indices().prepareExists(indexName).get();
        assertThat(existsResponse2.isExists(), equalTo(false));  // make sure index been deleted


        GetSnapshotsResponse getSnapshotsResponse1 = client().admin().cluster().prepareGetSnapshots(repositoryName).addSnapshots(snapshotName).get();
        assertThat(getSnapshotsResponse1.getSnapshots().get(0).state(), equalTo(SnapshotState.SUCCESS)); // check index has been created successfully


        client().admin().cluster().prepareRestoreSnapshot(repositoryName, snapshotName).setWaitForCompletion(true).get();  // restore the previous snapshot

        IndicesExistsResponse existsResponse3 = client().admin().indices().prepareExists(indexName).get();
        assertThat(existsResponse3.isExists(), equalTo(true));  // make sure index has been restored


        client().admin().cluster().prepareDeleteSnapshot(repositoryName, snapshotName).get();  // delete snapshot

        GetSnapshotsResponse getSnapshotsResponse2 = client().admin().cluster().prepareGetSnapshots(repositoryName).get();
        assertThat(getSnapshotsResponse2.getSnapshots(), emptyIterable());  // make sure snapshot has been deleted

        server.shutdownNow();
    }
}
