package tinker.mashtemp

import tinker.mashtemp.model.AppConfig

import javax.inject.Inject

/**
 * All the operations our app supports.
 */
class AppService {

    private final AppConfigRepo repo

    @Inject
    AppService(AppConfigRepo repo) {
        this.repo = repo
    }

    AppConfig getAppConfig() { repo.load() }
}
