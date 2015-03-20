package tinker.mashtemp.model

/**
 * The persistent state of our application with transient fields for the dynamic state (current temp of vessels etc.).
 * The non-transient fields are stored in JSON on disk by {@link tinker.mashtemp.AppConfigRepo}.
 */
class AppState {

    String title
    List<Vessel> vessels = []

    transient Date updated

}
