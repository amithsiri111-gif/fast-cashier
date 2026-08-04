package com.example.data.remote

interface TokenProvider {
    /**
     * Returns the current authorization token, or null when no token is available.
     *
     * Do not store secrets or production credentials in source control.
     */
    fun getToken(): String?
}

object DefaultTokenProvider : TokenProvider {
    override fun getToken(): String? = null
}
