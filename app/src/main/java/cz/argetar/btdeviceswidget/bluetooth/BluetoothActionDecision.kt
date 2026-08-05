package cz.argetar.btdeviceswidget.bluetooth

enum class ObservedConnectionState {
    CONNECTED,
    CONNECTING,
    DISCONNECTED,
    DISCONNECTING,
    UNKNOWN,
}

enum class ToggleAction { CONNECT, DISCONNECT }

enum class AvailableMethod {
    STATE_QUERY_ONLY,
    APP_OWNED_GATT_SESSION,
    PUBLIC_DEVICE_TOGGLE_API_37,
    HIDDEN_PROFILE_TOGGLE_DISABLED,
    NONE,
}

data class BluetoothActionInput(
    val sdkInt: Int,
    val adapterAvailable: Boolean,
    val bluetoothEnabled: Boolean,
    val connectPermissionGranted: Boolean,
    val bonded: Boolean,
    val companionAssociated: Boolean,
    val profile: DeviceProfile,
    val state: ObservedConnectionState,
    val availableMethod: AvailableMethod,
)

sealed interface BluetoothActionPlan {
    data class DirectToggle(
        val action: ToggleAction,
        val method: AvailableMethod,
    ) : BluetoothActionPlan

    data class StateOnly(val reason: String) : BluetoothActionPlan
    data class OpenBluetoothSettings(val reason: String) : BluetoothActionPlan
    data class PermissionMissing(val permission: String) : BluetoothActionPlan
    data class WaitForTransition(val state: ObservedConnectionState) : BluetoothActionPlan
    data class Unsupported(val reason: String) : BluetoothActionPlan
}

/** Pure policy for a future widget action handler. No Android or hidden API calls. */
object BluetoothActionDecision {
    fun decide(input: BluetoothActionInput): BluetoothActionPlan {
        if (!input.adapterAvailable) {
            return BluetoothActionPlan.Unsupported("bluetooth_not_supported")
        }
        if (input.sdkInt >= 31 && !input.connectPermissionGranted) {
            return BluetoothActionPlan.PermissionMissing("android.permission.BLUETOOTH_CONNECT")
        }
        if (!input.bluetoothEnabled) {
            return BluetoothActionPlan.OpenBluetoothSettings("bluetooth_disabled")
        }
        if (!input.bonded && input.profile != DeviceProfile.APP_OWNED_GATT) {
            return BluetoothActionPlan.Unsupported("selected_device_not_bonded")
        }
        if (input.state == ObservedConnectionState.CONNECTING ||
            input.state == ObservedConnectionState.DISCONNECTING
        ) {
            return BluetoothActionPlan.WaitForTransition(input.state)
        }

        val desired = when (input.state) {
            ObservedConnectionState.CONNECTED -> ToggleAction.DISCONNECT
            ObservedConnectionState.DISCONNECTED -> ToggleAction.CONNECT
            else -> null
        }

        if (input.availableMethod == AvailableMethod.APP_OWNED_GATT_SESSION &&
            input.profile == DeviceProfile.APP_OWNED_GATT && desired != null
        ) {
            return BluetoothActionPlan.DirectToggle(desired, input.availableMethod)
        }

        if (input.availableMethod == AvailableMethod.PUBLIC_DEVICE_TOGGLE_API_37 &&
            input.sdkInt >= 37 && input.companionAssociated && desired != null
        ) {
            return BluetoothActionPlan.DirectToggle(desired, input.availableMethod)
        }

        if (input.profile == DeviceProfile.GENERIC_BONDED &&
            input.state == ObservedConnectionState.UNKNOWN
        ) {
            return BluetoothActionPlan.StateOnly("no_reliable_per_device_profile_state")
        }

        return BluetoothActionPlan.OpenBluetoothSettings(
            when (input.availableMethod) {
                AvailableMethod.PUBLIC_DEVICE_TOGGLE_API_37 -> "companion_association_required"
                AvailableMethod.HIDDEN_PROFILE_TOGGLE_DISABLED -> "hidden_api_strategy_disabled"
                else -> "no_public_system_profile_toggle"
            }
        )
    }
}
