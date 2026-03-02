package com.example.doctors.viewmodel

import android.content.Context
import com.example.doctors.model.User
import com.example.doctors.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.*

class UserViewModelTest {

    private lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel

    // Static mocks for Firebase
    private lateinit var mockedAuth: MockedStatic<FirebaseAuth>
    private lateinit var mockedDb: MockedStatic<FirebaseDatabase>

    // Mock Objects
    private val mockContext: Context = mock()
    private val firebaseAuthMock: FirebaseAuth = mock()
    private val firebaseDbMock: FirebaseDatabase = mock()
    private val dbRefMock: DatabaseReference = mock()

    @Before
    fun setup() {
        // Start static mocking
        mockedAuth = mockStatic(FirebaseAuth::class.java)
        mockedDb = mockStatic(FirebaseDatabase::class.java)

        // Configure static mock returns
        mockedAuth.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(firebaseAuthMock)
        mockedDb.`when`<FirebaseDatabase> { FirebaseDatabase.getInstance() }.thenReturn(firebaseDbMock)

        // Setup DB reference
        whenever(firebaseDbMock.reference).thenReturn(dbRefMock)

        // Initialize objects
        repository = mock()
        viewModel = UserViewModel(repository)
    }

    @After
    fun tearDown() {
        // Close static mocks
        mockedAuth.close()
        mockedDb.close()
    }

    @Test
    fun `fetchCurrentUser success updates state correctly`() {
        // Arrange: Prepare mock data
        val mockUser = User(
            userId = "123",
            firstName = "Angela",
            lastName = "Pradhan",
            email = "test@doctor.com"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, User?) -> Unit>(0)
            callback(true, mockUser)
            null
        }.`when`(repository).getCurrentUser(any())

        // Act: Call the method
        viewModel.fetchCurrentUser()

        // Assert: Verify state updates
        assertFalse(viewModel.state.isLoading)
        assertEquals(mockUser, viewModel.state.currentUser)
        assertEquals(null, viewModel.state.error)

        verify(repository).getCurrentUser(any())
    }

    @Test
    fun `fetchCurrentUser failure updates error message`() {
        // Arrange: Simulate failure
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, User?) -> Unit>(0)
            callback(false, null)
            null
        }.`when`(repository).getCurrentUser(any())

        // Act
        viewModel.fetchCurrentUser()

        // Assert: Verify error state
        assertFalse(viewModel.state.isLoading)
        assertEquals("Failed to fetch user", viewModel.state.error)
    }

    @Test
    fun `updateUserDetails triggers database call`() {
        // Arrange: Mock auth and DB structure
        val mockFirebaseUser: FirebaseUser = mock()
        whenever(firebaseAuthMock.currentUser).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn("user123")

        val usersChild: DatabaseReference = mock()
        val uidChild: DatabaseReference = mock()
        whenever(dbRefMock.child("Users")).thenReturn(usersChild)
        whenever(usersChild.child("user123")).thenReturn(uidChild)

        // Mock Task for async operations
        val mockTask: com.google.android.gms.tasks.Task<Void> = mock()
        whenever(uidChild.updateChildren(any())).thenReturn(mockTask)
        whenever(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
        whenever(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

        // Act
        viewModel.updateUserDetails("Angela Pradhan", "angela123", "9800000000", mockContext)

        // Assert: Verify database interactions
        verify(dbRefMock).child("Users")
        verify(usersChild).child("user123")
    }
}