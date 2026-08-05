package cz.argetar.btdeviceswidget.bluetooth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BluetoothActionDecisionTest {
    private fun input(
        sdk: Int = 34,
        permission: Boolean = true,
        profile: DeviceProfile = DeviceProfile.AUDIO_A2DP,
        state: ObservedConnectionState = ObservedConnectionState.DISCONNECTED,
        method: AvailableMethod = AvailableMethod.STATE_QUERY_ONLY,
        associated: Boolean = false,
        bonded: Boolean = true,
    ) = BluetoothActionInput(
        sdkInt = sdk,
        adapterAvailable = true,
        bluetoothEnabled = true,
        connectPermissionGranted = permission,
        bonded = bonded,
        companionAssociated = associated,
        profile = profile,
        state = state,
        availableMethod = method,
    )

    @Test
    fun `android 12 plus without permission opens permission flow`() {
        assertEquals(
            BluetoothActionPlan.PermissionMissing("android.permission.BLUETOOTH_CONNECT"),
            BluetoothActionDecision.decide(input(permission = false)),
        )
    }

    @Test
    fun `android 14 audio profile has state but no public toggle`() {
        assertEquals(
            BluetoothActionPlan.OpenBluetoothSettings("no_public_system_profile_toggle"),
            BluetoothActionDecision.decide(input()),
        )
    }

    @Test
    fun `hidden profile method remains disabled`() {
        assertEquals(
            BluetoothActionPlan.OpenBluetoothSettings("hidden_api_strategy_disabled"),
            BluetoothActionDecision.decide(
                input(method = AvailableMethod.HIDDEN_PROFILE_TOGGLE_DISABLED),
            ),
        )
    }

    @Test
    fun `future api 37 public toggle requires companion association`() {
        val denied = BluetoothActionDecision.decide(
            input(sdk = 37, method = AvailableMethod.PUBLIC_DEVICE_TOGGLE_API_37),
        )
        assertEquals(
            BluetoothActionPlan.OpenBluetoothSettings("companion_association_required"),
            denied,
        )
        val allowed = BluetoothActionDecision.decide(
            input(
                sdk = 37,
                method = AvailableMethod.PUBLIC_DEVICE_TOGGLE_API_37,
                associated = true,
            ),
        )
        assertEquals(
            BluetoothActionPlan.DirectToggle(
                ToggleAction.CONNECT,
                AvailableMethod.PUBLIC_DEVICE_TOGGLE_API_37,
            ),
            allowed,
        )
    }

    @Test
    fun `app owned gatt session can connect and disconnect only its session`() {
        val connect = BluetoothActionDecision.decide(
            input(
                profile = DeviceProfile.APP_OWNED_GATT,
                method = AvailableMethod.APP_OWNED_GATT_SESSION,
            ),
        )
        assertEquals(
            BluetoothActionPlan.DirectToggle(
                ToggleAction.CONNECT,
                AvailableMethod.APP_OWNED_GATT_SESSION,
            ),
            connect,
        )
        val disconnect = BluetoothActionDecision.decide(
            input(
                profile = DeviceProfile.APP_OWNED_GATT,
                state = ObservedConnectionState.CONNECTED,
                method = AvailableMethod.APP_OWNED_GATT_SESSION,
            ),
        )
        assertEquals(
            BluetoothActionPlan.DirectToggle(
                ToggleAction.DISCONNECT,
                AvailableMethod.APP_OWNED_GATT_SESSION,
            ),
            disconnect,
        )
    }

    @Test
    fun `generic unknown state is display only`() {
        assertEquals(
            BluetoothActionPlan.StateOnly("no_reliable_per_device_profile_state"),
            BluetoothActionDecision.decide(
                input(
                    profile = DeviceProfile.GENERIC_BONDED,
                    state = ObservedConnectionState.UNKNOWN,
                    method = AvailableMethod.NONE,
                ),
            ),
        )
    }

    @Test
    fun `transition state never starts second toggle`() {
        assertEquals(
            BluetoothActionPlan.WaitForTransition(ObservedConnectionState.CONNECTING),
            BluetoothActionDecision.decide(
                input(state = ObservedConnectionState.CONNECTING),
            ),
        )
    }

    @Test
    fun `matrix keeps mvp audio first and has no api 26 to 35 public toggle`() {
        assertTrue(BluetoothCapabilityMatrix.profiles.getValue(DeviceProfile.AUDIO_A2DP).mvpSelectable)
        assertEquals(
            ToggleAvailability.NO_PUBLIC_TOGGLE_API_26_TO_35,
            BluetoothCapabilityMatrix.profiles.getValue(DeviceProfile.AUDIO_A2DP).api26To35Toggle,
        )
    }
}
