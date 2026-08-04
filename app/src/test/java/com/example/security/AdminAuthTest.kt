package com.example.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminAuthTest {

    @Test
    fun `verifyPin returns false when no hash configured`() {
        AdminAuth.overrideHash = null

        assertFalse(AdminAuth.verifyPin("1234"))
    }

    @Test
    fun `verifyPin returns true for matching hash`() {
        AdminAuth.overrideHash = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4"

        assertTrue(AdminAuth.verifyPin("1234"))
    }

    @Test
    fun `verifyPin returns false for non matching pin`() {
        AdminAuth.overrideHash = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4"

        assertFalse(AdminAuth.verifyPin("0000"))
    }
}
