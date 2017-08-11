package com.hitanshudhawan.popcorn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hitanshudhawan.popcorn.R;
import com.hitanshudhawan.popcorn.adapters.MovieBriefsSmallAdapter;
import com.hitanshudhawan.popcorn.network.ApiClient;
import com.hitanshudhawan.popcorn.network.ApiInterface;
import com.hitanshudhawan.popcorn.network.movies.MovieBrief;
import com.hitanshudhawan.popcorn.network.movies.NowShowingMoviesResponse;
import com.hitanshudhawan.popcorn.network.movies.PopularMoviesResponse;
import com.hitanshudhawan.popcorn.network.movies.TopRatedMoviesResponse;
import com.hitanshudhawan.popcorn.network.movies.UpcomingMoviesResponse;
import com.hitanshudhawan.popcorn.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllMoviesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<MovieBrief> mMovies;
    private MovieBriefsSmallAdapter mMoviesAdapter;

    private boolean pagesOver = false;
    private int presentPage = 1;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    private Call<NowShowingMoviesResponse> mNowShowingMoviesCall;
    private Call<PopularMoviesResponse> mPopularMoviesCall;
    private Call<UpcomingMoviesResponse> mUpcomingMoviesCall;
    private Call<TopRatedMoviesResponse> mTopRatedMoviesCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent receivedIntent = getIntent();
        final int movieType = receivedIntent.getIntExtra(Constant.VIEW_ALL_MOVIES_TYPE, -1);

        switch (movieType) {
            case Constant.NOW_SHOWING_MOVIES_TYPE:
                setTitle("Now Showing Movies");
                break;
            case Constant.POPULAR_MOVIES_TYPE:
                setTitle("Popular Movies");
                break;
            case Constant.UPCOMING_MOVIES_TYPE:
                setTitle("Upcoming Movies");
                break;
            case Constant.TOP_RATED_MOVIES_TYPE:
                setTitle("Top Rated Movies");
                break;
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_view_all);
        mMovies = new ArrayList<>();
        mMoviesAdapter = new MovieBriefsSmallAdapter(ViewAllMoviesActivity.this, mMovies);
        mRecyclerView.setAdapter(mMoviesAdapter);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewAllMoviesActivity.this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    loadMovies(movieType);
                    loading = true;
                }

            }
        });

        loadMovies(movieType);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNowShowingMoviesCall != null) mNowShowingMoviesCall.cancel();
        if (mPopularMoviesCall != null) mPopularMoviesCall.cancel();
        if (mUpcomingMoviesCall != null) mUpcomingMoviesCall.cancel();
        if (mTopRatedMoviesCall != null) mTopRatedMoviesCall.cancel();
    }

    private void loadMovies(int movieType) {
        if (pagesOver) return;

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        switch (movieType) {
            case Constant.NOW_SHOWING_MOVIES_TYPE:
                mNowShowingMoviesCall = apiService.getNowShowingMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage, "US");
                mNowShowingMoviesCall.enqueue(new Callback<NowShowingMoviesResponse>() {
                    @Override
                    public void onResponse(Call<NowShowingMoviesResponse> call, Response<NowShowingMoviesResponse> response) {
                        if (!response.isSuccessful()) {
                            mNowShowingMoviesCall = call.clone();
                            mNowShowingMoviesCall.enqueue(this);
                            return;
                        }

                        mMovies.addAll(response.body().getResults());
                        mMoviesAdapter.notifyDataSetChanged();
                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<NowShowingMoviesResponse> call, Throwable t) {

                    }
                });
                break;
            case Constant.POPULAR_MOVIES_TYPE:
                mPopularMoviesCall = apiService.getPopularMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage, "US");
                mPopularMoviesCall.enqueue(new Callback<PopularMoviesResponse>() {
                    @Override
                    public void onResponse(Call<PopularMoviesResponse> call, Response<PopularMoviesResponse> response) {
                        if (!response.isSuccessful()) {
                            mPopularMoviesCall = call.clone();
                            mPopularMoviesCall.enqueue(this);
                            return;
                        }

                        mMovies.addAll(response.body().getResults());
                        mMoviesAdapter.notifyDataSetChanged();
                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<PopularMoviesResponse> call, Throwable t) {

                    }
                });
                break;
            case Constant.UPCOMING_MOVIES_TYPE:
                mUpcomingMoviesCall = apiService.getUpcomingMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage, "US");
                mUpcomingMoviesCall.enqueue(new Callback<UpcomingMoviesResponse>() {
                    @Override
                    public void onResponse(Call<UpcomingMoviesResponse> call, Response<UpcomingMoviesResponse> response) {
                        if (!response.isSuccessful()) {
                            mUpcomingMoviesCall = call.clone();
                            mUpcomingMoviesCall.enqueue(this);
                            return;
                        }

                        mMovies.addAll(response.body().getResults());
                        mMoviesAdapter.notifyDataSetChanged();
                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<UpcomingMoviesResponse> call, Throwable t) {

                    }
                });
                break;
            case Constant.TOP_RATED_MOVIES_TYPE:
                mTopRatedMoviesCall = apiService.getTopRatedMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage, "US");
                mTopRatedMoviesCall.enqueue(new Callback<TopRatedMoviesResponse>() {
                    @Override
                    public void onResponse(Call<TopRatedMoviesResponse> call, Response<TopRatedMoviesResponse> response) {
                        if (!response.isSuccessful()) {
                            mTopRatedMoviesCall = call.clone();
                            mTopRatedMoviesCall.enqueue(this);
                            return;
                        }

                        mMovies.addAll(response.body().getResults());
                        mMoviesAdapter.notifyDataSetChanged();
                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<TopRatedMoviesResponse> call, Throwable t) {

                    }
                });
                break;
        }
    }

}