package com.example.wt


import com.example.wt.repository.UserAuthRepoImpl
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class UserAuthUniTest {
    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockTask: Task<AuthResult>

    private lateinit var AuthUserRepo: UserAuthRepoImpl

    @Captor
    private lateinit var captor: ArgumentCaptor<OnCompleteListener<AuthResult>>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        AuthUserRepo = UserAuthRepoImpl(mockAuth)
    }
    @Test
    fun testRegister_Successful() {
        val email = "test@example.com"
        val password = "testPassword"
        var expectedResult = "Initial Value" // Define the initial value

        // Mocking task to simulate successful registration
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(mockTask)

        // Define a callback that updates the expectedResult
        val callback = { success: Boolean, message: String? ->
            expectedResult = message ?: "Callback message is null"
        }

        // Call the function under test
        AuthUserRepo.signup(email, password, callback)

        verify(mockTask).addOnCompleteListener(captor.capture())
        captor.value.onComplete(mockTask)

        // Assert the result
        assertEquals("Registration Successful", expectedResult)
    }
}