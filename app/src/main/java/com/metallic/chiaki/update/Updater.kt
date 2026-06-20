// SPDX-License-Identifier: LicenseRef-AGPL-3.0-only-OpenSSL

package com.metallic.chiaki.update

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.metallic.chiaki.BuildConfig
import com.metallic.chiaki.common.fileProviderAuthority
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

// In-app self-update from GitHub Releases. The manifest/APK URLs must be publicly reachable, so
// this only does anything once the repo + releases are public (see ROADMAP "go public").
object Updater
{
	// Stable channel manifest, published as a release asset (GitHub serves /latest/download/).
	private const val MANIFEST_URL = "https://github.com/vargasvini/akichi/releases/latest/download/version.json"

	// A newer published build than the one running, with where to get it.
	data class Update(val versionCode: Int, val versionName: String, val apkUrl: String, val notes: String)

	// Blocking GET — run off the main thread. Returns an Update only if it is newer; null otherwise.
	fun check(): Update?
	{
		val json = JSONObject(httpGet(MANIFEST_URL))
		val versionCode = json.optInt("versionCode", 0)
		if(versionCode <= BuildConfig.VERSION_CODE)
			return null
		return Update(
			versionCode,
			json.optString("versionName"),
			json.optString("apkUrl"),
			json.optString("notes"))
	}

	// Blocking download, then hands the APK to the system installer. Same signing key as the
	// installed app, so it is an in-place update (the user just confirms once).
	fun downloadAndInstall(context: Context, update: Update)
	{
		val dir = File(context.cacheDir, "updates").apply { mkdirs() }
		val apk = File(dir, "akichi-update.apk")
		URL(update.apkUrl).openStream().use { input -> apk.outputStream().use { input.copyTo(it) } }
		val uri = FileProvider.getUriForFile(context, fileProviderAuthority, apk)
		context.startActivity(Intent(Intent.ACTION_VIEW).apply {
			setDataAndType(uri, "application/vnd.android.package-archive")
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
		})
	}

	private fun httpGet(urlStr: String): String =
		(URL(urlStr).openConnection() as HttpURLConnection).run {
			connectTimeout = 10000
			readTimeout = 10000
			setRequestProperty("Accept", "application/json")
			inputStream.bufferedReader().use { it.readText() }
		}
}
