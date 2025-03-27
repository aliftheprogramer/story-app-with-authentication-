package com.example.myapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.helper.DataDummy
import com.example.myapplication.helper.MainDispatcherRule
import com.example.myapplication.response.ListStoryItem
import com.example.myapplication.adapter.StoryAdapter
import com.example.myapplication.helper.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var storyViewModel: StoryViewModel

    @Before
    fun setUp() {
        storyViewModel = StoryViewModel(authRepository)
    }

    @Test
    fun `when get Stories Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectData = MutableLiveData<PagingData<ListStoryItem>>()
        expectData.value = data

        val token = "dummy_token"
        Mockito.`when`(authRepository.getPagedStories(token)).thenReturn(expectData)

        val actualStory: PagingData<ListStoryItem> = storyViewModel.getPagedStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])

        // Verify that the method was called
        Mockito.verify(authRepository).getPagedStories(token)
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data

        Mockito.`when`(authRepository.getPagedStories("dummy_token")).thenReturn(expectedQuote)

        val actualQuote: PagingData<ListStoryItem> = storyViewModel.getPagedStories("dummy_token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualQuote)

        assertEquals(0, differ.snapshot().size)

        Mockito.verify(authRepository).getPagedStories("dummy_token")
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), prevKey = 0, nextKey = 1)
    }

    companion object {
        fun snapshot(items : List<ListStoryItem>) : PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}