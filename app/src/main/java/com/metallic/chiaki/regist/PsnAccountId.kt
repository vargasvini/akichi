// SPDX-License-Identifier: LicenseRef-AGPL-3.0-only-OpenSSL

package com.metallic.chiaki.regist

// Packs a PSN account number (the 19-digit value) into 8 little-endian bytes — the binary
// account-id format the Remote Play registration expects. Kept pure for unit testing.
fun psnAccountNumberToBytes(number: ULong): ByteArray =
	ByteArray(8) { i -> ((number shr (8 * i)) and 0xFFuL).toByte() }
