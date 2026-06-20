// SPDX-License-Identifier: LicenseRef-AGPL-3.0-only-OpenSSL

package com.metallic.chiaki.session

import android.view.KeyEvent
import com.metallic.chiaki.lib.ControllerState

// Corrective key mapping for a DualSense delivered through the broken Fire OS / Android 11-12
// keylayout (see CONTROLLER_DIAGNOSIS.md). Pure on purpose so it can be unit-tested and can't
// silently regress.
object FireTvDualSenseMapping
{
	// What a received Android keycode should drive on this controller.
	sealed class Action
	{
		// a chiaki face / shoulder / system button (bit mask)
		data class Button(val mask: UInt): Action()
		// the physical L2 / R2 triggers, which arrive scrambled as digital BUTTON_L1 / BUTTON_R1
		object L2: Action()
		object R2: Action()
	}

	// Returns the corrected action for a received keycode, or null when the keycode isn't ours.
	fun actionFor(keyCode: Int, swapCrossMoon: Boolean): Action? = when(keyCode)
	{
		KeyEvent.KEYCODE_BUTTON_L1 -> Action.L2 // physical L2
		KeyEvent.KEYCODE_BUTTON_R1 -> Action.R2 // physical R2
		KeyEvent.KEYCODE_BUTTON_A -> Action.Button(if(swapCrossMoon) ControllerState.BUTTON_MOON else ControllerState.BUTTON_CROSS)
		KeyEvent.KEYCODE_BUTTON_B -> Action.Button(if(swapCrossMoon) ControllerState.BUTTON_CROSS else ControllerState.BUTTON_MOON)
		KeyEvent.KEYCODE_BUTTON_X -> Action.Button(if(swapCrossMoon) ControllerState.BUTTON_PYRAMID else ControllerState.BUTTON_BOX)
		KeyEvent.KEYCODE_BUTTON_C -> Action.Button(if(swapCrossMoon) ControllerState.BUTTON_BOX else ControllerState.BUTTON_PYRAMID) // Triangle
		KeyEvent.KEYCODE_BUTTON_Y -> Action.Button(ControllerState.BUTTON_L1) // physical L1
		KeyEvent.KEYCODE_BUTTON_Z -> Action.Button(ControllerState.BUTTON_R1) // physical R1
		KeyEvent.KEYCODE_BUTTON_L2 -> Action.Button(ControllerState.BUTTON_SHARE) // physical Create
		KeyEvent.KEYCODE_BUTTON_R2 -> Action.Button(ControllerState.BUTTON_OPTIONS) // physical Options
		KeyEvent.KEYCODE_BUTTON_SELECT -> Action.Button(ControllerState.BUTTON_L3) // physical L3
		KeyEvent.KEYCODE_BUTTON_START -> Action.Button(ControllerState.BUTTON_R3) // physical R3
		KeyEvent.KEYCODE_BUTTON_MODE -> Action.Button(ControllerState.BUTTON_PS) // physical PS
		KeyEvent.KEYCODE_BUTTON_THUMBL -> Action.Button(ControllerState.BUTTON_TOUCHPAD) // physical touchpad click
		else -> null
	}
}
