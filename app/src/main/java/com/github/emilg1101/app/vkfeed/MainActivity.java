package com.github.emilg1101.app.vkfeed;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.github.emilg1101.app.vkfeed.adapter.OnLoadMoreListener;
import com.github.emilg1101.app.vkfeed.adapter.PostsRecyclerViewAdapter;
import com.github.emilg1101.app.vkfeed.loader.OnPostsLoadListener;
import com.github.emilg1101.app.vkfeed.loader.PostsLoader;
import com.github.emilg1101.app.vkfeed.models.Author;
import com.github.emilg1101.app.vkfeed.models.Post;
import com.github.emilg1101.app.vkfeed.repository.PostRepository;
import com.github.emilg1101.app.vkfeed.repository.AuthorsRepository;
import com.github.emilg1101.app.vkfeed.utils.InternetConnect;
import com.vk.sdk.VKSdk;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnPostsLoadListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, InternetConnect.OnConnectListener {

    private PostRepository postRepository;
    private PostsLoader postsLoader;
    private AuthorsRepository authorsRepository;
    private InternetConnect internetConnect;

    @BindView(R.id.postsRecyclerView) public RecyclerView postsRecyclerView;
    @BindView(R.id.swipeRefreshLayout) public SwipeRefreshLayout swipeRefreshLayout;

    private PostsRecyclerViewAdapter postsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!VKSdk.isLoggedIn()) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
        }
        setContentView(R.layout.ac_main);
        ButterKnife.bind(this);

        PostRepository.load(this);
        AuthorsRepository.load(this);

        postRepository = PostRepository.getInstance();
        authorsRepository = AuthorsRepository.getInstance();
        postsLoader = new PostsLoader();
        internetConnect = new InternetConnect(this);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initRecyclerView();

        if (postRepository.size() == 0 || authorsRepository.size() == 0) {
            postsLoader.load(postRepository.getStartFrom());
        }

        initListeners();
    }

    public void initRecyclerView() {
        postsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        postsRecyclerView.setLayoutManager(linearLayoutManager);
        postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(this, postsRecyclerView, postRepository);
        postsRecyclerView.setAdapter(postsRecyclerViewAdapter);
    }

    public void initListeners() {
        postsLoader.setOnPostsLoadListener(this);
        internetConnect.setOnConnectListener(this);
        postsRecyclerViewAdapter.setOnLoadMoreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPostsLoad(List<Post> posts, Map<Integer, Author> authors, String nextFrom) {
        authorsRepository.addAll(authors);
        postRepository.addAll(posts);
        postRepository.setStartFrom(nextFrom);
        postsRecyclerViewAdapter.notifyDataSetChanged();
        postsRecyclerViewAdapter.setLoaded();

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        AuthorsRepository.save(MainActivity.this);
        PostRepository.save(MainActivity.this);
    }

    @Override
    public void onRefresh() {
        if (internetConnect.hasConnection()) {
            postRepository.clear();
            postsRecyclerViewAdapter.notifyDataSetChanged();
            postsLoader.load("");
        }
    }

    @Override
    public void onLoadMore() {
        postRepository.add(null);
        postsRecyclerViewAdapter.notifyItemInserted(postRepository.size() - 1);
        postRepository.remove(postRepository.size() - 1);
        postsRecyclerViewAdapter.notifyItemRemoved(postRepository.size());
        postsLoader.load(postRepository.getStartFrom());
    }

    @Override
    public void onConnected(boolean connected) {
        if (connected) {
            getSupportActionBar().setTitle(R.string.str_news);
        } else {
            getSupportActionBar().setTitle(R.string.str_waiting);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                VKSdk.logout();
                startActivity(new Intent(MainActivity.this, SigninActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        internetConnect.removeOnConnectListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        internetConnect.setOnConnectListener(this);
    }
}