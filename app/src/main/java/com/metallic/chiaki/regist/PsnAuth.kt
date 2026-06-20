// SPDX-License-Identifier: LicenseRef-AGPL-3.0-only-OpenSSL

package com.metallic.chiaki.regist

import android.util.Base64
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// PSN OAuth helper: turns the redirect "code" (captured after the user logs in on Sony's page)
// into the Base64 Account ID. Endpoints/client are the public Remote Play ones (same as chiaki).
object PsnAuth
{
	const val CLIENT_ID = "ba495a24-818c-472b-b12d-ff231c1b5745"
	private const val CLIENT_SECRET = "mvaiZkRsAsI1IBkY"
	const val REDIRECT_PAGE = "https://remoteplay.dl.playstation.net/remoteplay/redirect"
	private const val TOKEN_URL = "https://auth.api.sonyentertainmentnetwork.com/2.0/oauth/token"
	private const val SCOPE = "psn:clientapp referenceDataService:countryConfig.read pushNotification:webSocket.desktop.connect sessionManager:remotePlaySession.system.update"

	// The Sony login page to open in a WebView.
	val loginUrl: String get() =
		"https://auth.api.sonyentertainmentnetwork.com/2.0/oauth/authorize" +
			"?service_entity=urn:service-entity:psn&response_type=code&client_id=$CLIENT_ID" +
			"&redirect_uri=$REDIRECT_PAGE&scope=${enc(SCOPE)}" +
			"&request_locale=en_US&ui=pr&service_logo=ps&layout_type=popup&smcid=remoteplay&prompt=always&PlatformPrivacyWs1=minimal&"

	// Blocking network calls — run off the main thread. Returns the Base64 Account ID.
	fun fetchAccountId(redirectCode: String): String
	{
		val tokenBody = "grant_type=authorization_code&code=$redirectCode&scope=${enc(SCOPE)}&redirect_uri=$REDIRECT_PAGE&"
		val accessToken = JSONObject(postForm(TOKEN_URL, tokenBody)).optString("access_token")
		if(accessToken.isEmpty())
			throw IOException("No access token in response")
		val userId = JSONObject(get("$TOKEN_URL/$accessToken")).optString("user_id")
		val number = userId.toULongOrNull() ?: throw IOException("Invalid user_id: $userId")
		return Base64.encodeToString(psnAccountNumberToBytes(number), Base64.NO_WRAP)
	}

	private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

	private val basicAuth get() =
		"Basic " + Base64.encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray(), Base64.NO_WRAP)

	private fun postForm(urlStr: String, body: String): String =
		open(urlStr).run {
			requestMethod = "POST"
			setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
			doOutput = true
			outputStream.use { it.write(body.toByteArray()) }
			readBody()
		}

	private fun get(urlStr: String): String =
		open(urlStr).run {
			requestMethod = "GET"
			setRequestProperty("Accept", "application/json")
			readBody()
		}

	private fun open(urlStr: String) = (URL(urlStr).openConnection() as HttpURLConnection).apply {
		setRequestProperty("Authorization", basicAuth)
		connectTimeout = 15000
		readTimeout = 15000
	}

	private fun HttpURLConnection.readBody(): String
	{
		val ok = responseCode in 200..299
		val text = (if(ok) inputStream else errorStream)?.bufferedReader()?.use { it.readText() } ?: ""
		if(!ok)
			throw IOException("HTTP $responseCode")
		return text
	}
}
