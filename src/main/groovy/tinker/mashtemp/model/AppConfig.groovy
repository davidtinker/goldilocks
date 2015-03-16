package tinker.mashtemp.model

/**
 * The persistent state of our application. This is stored in JSON on disk by {@link tinker.mashtemp.AppConfigRepo}.
 */
class AppConfig {

    String title
    List<Vessel> vessels = []

}
