// SPDX-License-Identifier: LicenseRef-AGPL-3.0-only-OpenSSL

package com.metallic.chiaki.session

import android.view.KeyEvent
import com.metallic.chiaki.lib.ControllerState
import com.metallic.chiaki.session.FireTvDualSenseMapping.Action
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Pins the corrective Fire OS DualSense mapping so the flagship controller fix can't silently
// regress. Mirrors the evidence table in CONTROLLER_DIAGNOSIS.md.
class FireTvDualSenseMappingTest
{
	private fun button(keyCode: Int) =
		(FireTvDualSenseMapping.actionFor(keyCode, swapCrossMoon = false) as Action.Button).mask

	@Test fun faceButtonsStayCorrect()
	{
		assertEquals(ControllerState.BUTTON_CROSS, button(KeyEvent.KEYCODE_BUTTON_A))
		assertEquals(ControllerState.BUTTON_MOON, button(KeyEvent.KEYCODE_BUTTON_B))
		assertEquals(ControllerState.BUTTON_BOX, button(KeyEvent.KEYCODE_BUTTON_X))
	}

	@Test fun triangleIsNotPs()
	{
		// the headline symptom: Triangle used to open the PS menu
		assertEquals(ControllerState.BUTTON_PYRAMID, button(KeyEvent.KEYCODE_BUTTON_C))
	}

	@Test fun shouldersAreCorrected()
	{
		assertEquals(ControllerState.BUTTON_L1, button(KeyEvent.KEYCODE_BUTTON_Y))
		assertEquals(ControllerState.BUTTON_R1, button(KeyEvent.KEYCODE_BUTTON_Z))
	}

	@Test fun triggersArriveAsDigitalL2R2()
	{
		assertEquals(Action.L2, FireTvDualSenseMapping.actionFor(KeyEvent.KEYCODE_BUTTON_L1, false))
		assertEquals(Action.R2, FireTvDualSenseMapping.actionFor(KeyEvent.KEYCODE_BUTTON_R1, false))
	}

	@Test fun createOptionsAndStickClicks()
	{
		assertEquals(ControllerState.BUTTON_SHARE, button(KeyEvent.KEYCODE_BUTTON_L2))   // Create
		assertEquals(ControllerState.BUTTON_OPTIONS, button(KeyEvent.KEYCODE_BUTTON_R2)) // Options
		assertEquals(ControllerState.BUTTON_L3, button(KeyEvent.KEYCODE_BUTTON_SELECT))
		assertEquals(ControllerState.BUTTON_R3, button(KeyEvent.KEYCODE_BUTTON_START))
	}

	@Test fun touchpadAndPs()
	{
		assertEquals(ControllerState.BUTTON_TOUCHPAD, button(KeyEvent.KEYCODE_BUTTON_THUMBL))
		assertEquals(ControllerState.BUTTON_PS, button(KeyEvent.KEYCODE_BUTTON_MODE))
	}

	@Test fun swapCrossMoonSwapsFaceButtons()
	{
		val crossKey = FireTvDualSenseMapping.actionFor(KeyEvent.KEYCODE_BUTTON_A, swapCrossMoon = true) as Action.Button
		assertEquals(ControllerState.BUTTON_MOON, crossKey.mask)
	}

	@Test fun unknownKeyIsNotOurs()
	{
		assertNull(FireTvDualSenseMapping.actionFor(KeyEvent.KEYCODE_SPACE, false))
	}
}
