package com.example.data

import com.example.data.remote.AuthInterceptor
import com.example.data.remote.TokenProvider
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.Protocol
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteAuthInterceptorTest {

    private class StaticTokenProvider(private val token: String?) : TokenProvider {
        override fun getToken(): String? = token
    }

    @Test
    fun `authorization header is not added when token is absent`() {
        val interceptor = AuthInterceptor(StaticTokenProvider(null))
        val request = Request.Builder().url("https://api.example.com/test").build()
        val chain = TestInterceptorChain(request)

        interceptor.intercept(chain)

        assertNull(chain.capturedRequest.header("Authorization"))
        assertEquals("application/json", chain.capturedRequest.header("Accept"))
        assertEquals("application/json", chain.capturedRequest.header("Content-Type"))
    }

    @Test
    fun `authorization header is added when token exists`() {
        val interceptor = AuthInterceptor(StaticTokenProvider("abc123"))
        val request = Request.Builder().url("https://api.example.com/test").build()
        val chain = TestInterceptorChain(request)

        interceptor.intercept(chain)

        assertEquals("Bearer abc123", chain.capturedRequest.header("Authorization"))
        assertEquals("application/json", chain.capturedRequest.header("Accept"))
        assertEquals("application/json", chain.capturedRequest.header("Content-Type"))
    }

    private class TestInterceptorChain(private val request: Request) : okhttp3.Interceptor.Chain {
        lateinit var capturedRequest: Request

        override fun request(): Request = request

        override fun proceed(request: Request): Response {
            capturedRequest = request
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(null, ""))
                .build()
        }

        override fun connection() = null
    }
}
