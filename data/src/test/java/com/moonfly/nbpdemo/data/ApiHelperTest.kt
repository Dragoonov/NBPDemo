package com.moonfly.nbpdemo.data

import com.moonfly.nbpdemo.domain.repository.Response
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException


class ApiHelperTest {

    @Test
    fun `should successfully return result of call`() = runTest {
        //Given
        val mockResponse = mockk<retrofit2.Response<String>> {
            every { isSuccessful } returns true
            every { body() } returns "Ok"
        }
        val call: suspend () -> retrofit2.Response<String> = mockk {
            coEvery { this@mockk.invoke() } returns mockResponse
        }
        val successMapper: suspend (String) -> Response<String> = mockk {
            coEvery { this@mockk.invoke("Ok") } returns Response.Success("Ok")
        }

        //When
        val result = ApiHelper.executeCallWithErrorHandling(call, successMapper)

        //Then
        assertEquals("Ok", (result as? Response.Success)?.body)
        coVerify { call() }
        coVerify { successMapper("Ok") }
    }

    @Test
    fun `should return http error when response is not successful`() = runTest {
        //Given
        val mockResponse = mockk<retrofit2.Response<String>> {
            every { isSuccessful } returns false
            every { body() } returns "mockk"
            every { code() } returns 404
            every { errorBody()?.string() } returns "HTTP error"
        }
        val call: suspend () -> retrofit2.Response<String> = mockk {
            coEvery { this@mockk.invoke() } returns mockResponse
        }
        val successMapper: suspend (String) -> Response<String> = mockk()

        //When
        val result = ApiHelper.executeCallWithErrorHandling(call, successMapper)

        //Then
        assertEquals(404, (result as? Response.Error.HttpError)?.code)
        assertEquals(
            Throwable("HTTP error").message,
            (result as? Response.Error.HttpError)?.throwable?.message
        )
        coVerify { call() }
        coVerify(exactly = 0) { successMapper(any()) }
    }

    @Test
    fun `should return unknown error when response is not successful`() = runTest {
        //Given
        val mockResponse = mockk<retrofit2.Response<String>> {
            every { isSuccessful } returns false
            every { body() } returns "mockk"
            every { code() } returns 500
        }
        val call: suspend () -> retrofit2.Response<String> = mockk {
            coEvery { this@mockk.invoke() } returns mockResponse
        }
        val successMapper: suspend (String) -> Response<String> = mockk()

        //When
        val result = ApiHelper.executeCallWithErrorHandling(call, successMapper)

        //Then
        assert(result is Response.Error.UnknownError)
        coVerify { call() }
        coVerify(exactly = 0) { successMapper(any()) }
    }

    @Test
    fun `should return network error when call fails`() = runTest {
        //Given
        val call: suspend () -> retrofit2.Response<String> = mockk {
            coEvery { this@mockk.invoke() } throws IOException("Network error")
        }
        val successMapper: suspend (String) -> Response<String> = mockk()

        //When
        val result = ApiHelper.executeCallWithErrorHandling(call, successMapper)

        //Then
        assertEquals("Network error", (result as? Response.Error.NetworkError)?.throwable?.message)
        coVerify { call() }
        coVerify(exactly = 0) { successMapper(any()) }
    }

    @Test
    fun `should return unknown error when call fails with unknown reason`() = runTest {
        //Given
        val call: suspend () -> retrofit2.Response<String> = mockk {
            coEvery { this@mockk.invoke() } throws Exception("Error")
        }
        val successMapper: suspend (String) -> Response<String> = mockk()

        //When
        val result = ApiHelper.executeCallWithErrorHandling(call, successMapper)

        //Then
        assertEquals("Error", (result as? Response.Error.UnknownError)?.throwable?.message)
        coVerify { call() }
        coVerify(exactly = 0) { successMapper(any()) }
    }

}