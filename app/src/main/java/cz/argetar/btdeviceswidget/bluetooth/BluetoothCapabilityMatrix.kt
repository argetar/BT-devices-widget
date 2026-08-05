package cz.argetar.btdeviceswidget.bluetooth

/** Product-level profiles, deliberately independent of Android framework types. */
enum class DeviceProfile {
    AUDIO_A2DP,
    HEADSET_HANDSFREE,
    LE_AUDIO,
    HID_HOST,
    APP_OWNED_GATT,
    GENERIC_BONDED,
}

enum class StateObservation {
    PROFILE_PROXY_AND_BROADCAST,
    APP_GATT_CALLBACK,
    ACL_BROADCAST_BEST_EFFORT,
}

enum class ToggleAvailability {
    NO_PUBLIC_TOGGLE_API_26_TO_35,
    APP_OWNED_GATT_SESSION_ONLY,
    PUBLIC_DEVICE_TOGGLE_API_37_ASSOCIATED,
}

data class ProfileCapability(
    val profile: DeviceProfile,
    val stateObservation: StateObservation,
    val api26To35Toggle: ToggleAvailability,
    val api37PlusCandidate: ToggleAvailability,
    val mvpSelectable: Boolean,
)

/**
 * Static feasibility facts used by product/configuration code later.
 *
 * This does not call Android APIs and does not enable hidden/reflection paths.
 */
object BluetoothCapabilityMatrix {
    val profiles: Map<DeviceProfile, ProfileCapability> = listOf(
        ProfileCapability(
            DeviceProfile.AUDIO_A2DP,
            StateObservation.PROFILE_PROXY_AND_BROADCAST,
            ToggleAvailability.NO_PUBLIC_TOGGLE_API_26_TO_35,
            ToggleAvailability.PUBLIC_DEVICE_TOGGLE_API_37_ASSOCIATED,
            mvpSelectable = true,
        ),
        ProfileCapability(
            DeviceProfile.HEADSET_HANDSFREE,
            StateObservation.PROFILE_PROXY_AND_BROADCAST,
            ToggleAvailability.NO_PUBLIC_TOGGLE_API_26_TO_35,
            ToggleAvailability.PUBLIC_DEVICE_TOGGLE_API_37_ASSOCIATED,
            mvpSelectable = true,
        ),
        ProfileCapability(
            DeviceProfile.LE_AUDIO,
            StateObservation.PROFILE_PROXY_AND_BROADCAST,
            ToggleAvailability.NO_PUBLIC_TOGGLE_API_26_TO_35,
            ToggleAvailability.PUBLIC_DEVICE_TOGGLE_API_37_ASSOCIATED,
            mvpSelectable = false,
        ),
        ProfileCapability(
            DeviceProfile.HID_HOST,
            StateObservation.ACL_BROADCAST_BEST_EFFORT,
            ToggleAvailability.NO_PUBLIC_TOGGLE_API_26_TO_35,
            ToggleAvailability.PUBLIC_DEVICE_TOGGLE_API_37_ASSOCIATED,
            mvpSelectable = false,
        ),
        ProfileCapability(
            DeviceProfile.APP_OWNED_GATT,
            StateObservation.APP_GATT_CALLBACK,
            ToggleAvailability.APP_OWNED_GATT_SESSION_ONLY,
            ToggleAvailability.APP_OWNED_GATT_SESSION_ONLY,
            mvpSelectable = false,
        ),
        ProfileCapability(
            DeviceProfile.GENERIC_BONDED,
            StateObservation.ACL_BROADCAST_BEST_EFFORT,
            ToggleAvailability.NO_PUBLIC_TOGGLE_API_26_TO_35,
            ToggleAvailability.PUBLIC_DEVICE_TOGGLE_API_37_ASSOCIATED,
            mvpSelectable = true,
        ),
    ).associateBy { it.profile }
}
