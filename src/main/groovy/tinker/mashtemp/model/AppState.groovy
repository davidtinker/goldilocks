package tinker.mashtemp.model

/**
 * The persistent state of our application with transient fields for the dynamic state (current temp of vessels etc.).
 * The non-transient fields are stored in JSON on disk by {@link tinker.mashtemp.SetupRepo}.
 */
class AppState {

    String title
    List<Vessel> vessels = []

    String tempUnit = "C"   // or F

    transient Date updated

    /**
     * Convert temps from C to tempUnit.
     */
    void toTempUnit() {
        if (tempUnit == 'F') {
        }
    }

}
