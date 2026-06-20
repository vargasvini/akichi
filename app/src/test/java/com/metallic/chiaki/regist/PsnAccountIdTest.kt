// SPDX-License-Identifier: LicenseRef-AGPL-3.0-only-OpenSSL

package com.metallic.chiaki.regist

import org.junit.Assert.assertArrayEquals
import org.junit.Test

// Guards the PSN account-number -> 8-byte little-endian packing with a synthetic vector
// (72623859790382856 = 0x0102030405060708 -> little-endian 08 07 06 05 04 03 02 01).
class PsnAccountIdTest
{
	@Test fun packsAccountNumberLittleEndian()
	{
		val expected = byteArrayOf(0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01)
		assertArrayEquals(expected, psnAccountNumberToBytes(72623859790382856uL))
	}
}
