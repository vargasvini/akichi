// SPDX-License-Identifier: LicenseRef-AGPL-3.0-only-OpenSSL

package com.metallic.chiaki.regist

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.metallic.chiaki.R
import com.metallic.chiaki.databinding.ActivityPsnLoginBinding

// Sign in with PSN: shows Sony's login page, captures the OAuth redirect code, and resolves the
// Base64 Account ID, returned via EXTRA_ACCOUNT_ID. Manual entry remains the fallback.
class PsnLoginActivity: AppCompatActivity()
{
	companion object
	{
		const val EXTRA_ACCOUNT_ID = "account_id"
	}

	private lateinit var binding: ActivityPsnLoginBinding
	private var handled = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		binding = ActivityPsnLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.webView.settings.javaScriptEnabled = true
		binding.webView.settings.domStorageEnabled = true
		binding.webView.webViewClient = object: WebViewClient()
		{
			override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
				maybeHandleRedirect(request.url.toString())

			override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?)
			{
				if(maybeHandleRedirect(url))
					view.stopLoading()
			}
		}
		binding.webView.loadUrl(PsnAuth.loginUrl)
	}

	// Returns true once the redirect carrying the code is seen, so the WebView stops navigating.
	private fun maybeHandleRedirect(url: String): Boolean
	{
		if(handled || !url.startsWith(PsnAuth.REDIRECT_PAGE))
			return false
		handled = true
		val code = Uri.parse(url).getQueryParameter("code")
		if(code.isNullOrEmpty())
			fail()
		else
			fetchAccountId(code)
		return true
	}

	private fun fetchAccountId(code: String)
	{
		binding.webView.visibility = View.GONE
		binding.progressBar.visibility = View.VISIBLE
		Thread {
			val accountId = try { PsnAuth.fetchAccountId(code) } catch(e: Exception) { null }
			runOnUiThread {
				if(isFinishing)
					return@runOnUiThread
				if(accountId != null)
				{
					setResult(RESULT_OK, Intent().putExtra(EXTRA_ACCOUNT_ID, accountId))
					finish()
				}
				else
					fail()
			}
		}.start()
	}

	private fun fail()
	{
		Toast.makeText(this, R.string.psn_login_failed, Toast.LENGTH_LONG).show()
		setResult(RESULT_CANCELED)
		finish()
	}
}
