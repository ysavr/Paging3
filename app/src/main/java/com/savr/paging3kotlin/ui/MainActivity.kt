package com.savr.paging3kotlin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.savr.paging3kotlin.R
import com.savr.paging3kotlin.utils.visibleWhen
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var movieJob: Job? = null
    private val movieAdapter: MainAdapter by lazy {
        MainAdapter()
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdapter()
        fetchMovie()

        movie_retry_button.setOnClickListener {
            movieAdapter.retry()
        }
    }

    private fun initAdapter() {
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.adapter = movieAdapter.withLoadStateHeaderAndFooter(
            header = StateAdapter { movieAdapter.retry() },
            footer = StateAdapter { movieAdapter.retry() }
        )

        movieAdapter.addLoadStateListener { loadState ->
            loadState.refresh.let {
                movie_error_msg_textView.visibleWhen(it is LoadState.Error)
                movie_retry_button.visibleWhen(it is LoadState.Error)
                movie_progressBar.visibleWhen(it is LoadState.Loading)
                recyclerView.visibleWhen(it is LoadState.NotLoading)

                if (it is LoadState.Error) {
                    movie_error_msg_textView.text = it.error.localizedMessage
                }
            }
        }
    }

    @ExperimentalPagingApi
    private fun fetchMovie() {
        movieJob?.cancel()
        movieJob = lifecycleScope.launch {
//            viewModel.movieList().collectLatest {
//                movieAdapter.submitData(it)
//            }
            viewModel.fetchMovie().collectLatest {
                movieAdapter.submitData(it)
            }
        }
    }
}
