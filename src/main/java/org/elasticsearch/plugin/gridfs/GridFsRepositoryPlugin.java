package org.elasticsearch.plugin.gridfs;

import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.repositories.RepositoriesModule;
import org.elasticsearch.repositories.gridfs.GridFsRepository;
import org.elasticsearch.repositories.gridfs.GridFsRepositoryModule;
import org.elasticsearch.repositories.gridfs.GridFsService;

import java.util.Collection;


public class GridFsRepositoryPlugin extends AbstractPlugin {

    private final Settings settings;

    public GridFsRepositoryPlugin(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String name() {
        return "gridfs-repository";
    }

    @Override
    public String description() {
        return "GridFS repository plugin";
    }

    @Override
    public Collection<Class<? extends Module>> modules() {
        Collection<Class<? extends Module>> modules = Lists.newArrayList();
        if (settings.getAsBoolean("gridfs.repository.enabled", true)) {
            modules.add(GridFsModule.class);
        }
        return modules;
    }

    @Override
    public Collection<Class<? extends LifecycleComponent>> services() {
        Collection<Class<? extends LifecycleComponent>> services = Lists.newArrayList();
        if (settings.getAsBoolean("gridfs.repository.enabled", true)) {
            services.add(GridFsService.class);
        }
        return services;
    }

    public void onModule(RepositoriesModule repositoriesModule) {
        if (settings.getAsBoolean("gridfs.repository.enabled", true)) {
            repositoriesModule.registerRepository(GridFsRepository.TYPE, GridFsRepositoryModule.class);
        }
    }
}
