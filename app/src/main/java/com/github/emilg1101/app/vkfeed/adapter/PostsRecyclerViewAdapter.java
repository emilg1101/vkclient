package com.github.emilg1101.app.vkfeed.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.emilg1101.app.vkfeed.liker.Liker;
import com.github.emilg1101.app.vkfeed.PostActivity;
import com.github.emilg1101.app.vkfeed.R;
import com.github.emilg1101.app.vkfeed.models.Post;
import com.github.emilg1101.app.vkfeed.repository.PostRepository;
import com.github.emilg1101.app.vkfeed.repository.AuthorsRepository;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private PostRepository posts;
    private AuthorsRepository authorsRepository;
    private OnLoadMoreListener onLoadMoreListener;
    private Liker liker;
    private Context context;

    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;

    private boolean isLoading;

    public PostsRecyclerViewAdapter(Context context, RecyclerView recyclerView, PostRepository posts) {
        this.context = context;
        this.posts = posts;
        authorsRepository = AuthorsRepository.getInstance();
        liker = new Liker();
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_post, parent, false);
            return new PostViewHolder(v);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PostViewHolder) {
            final PostViewHolder postViewHolder = (PostViewHolder) holder;

            final Post post = posts.get(position);
            int id = post.getSourceId();

            Date date = new Date(post.getDate()*1000L);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");

            Picasso.get().load(authorsRepository.get(id).getPhoto()).into(postViewHolder.authorImageView);
            postViewHolder.authorTextView.setText(authorsRepository.get(id).getName());
            postViewHolder.dateTextView.setText(sdf.format(date));
            postViewHolder.postTextView.setText(post.getText());
            postViewHolder.likeTextView.setText(String.valueOf(post.getLikesCount()));

            if (post.isUserLikes()) {
                postViewHolder.likeImageView.setImageResource(R.drawable.like);
            }

            postViewHolder.likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean liked = post.isUserLikes();
                    liker.like(liked, post.getSourceId(), post.getPostId(), new Liker.LikeListener() {
                        @Override
                        public void like(int count) {
                            postViewHolder.likeTextView.setText(String.valueOf(count));
                        }
                    });
                    postViewHolder.likeImageView.setImageResource(liked?R.drawable.like_outline:R.drawable.like);
                    post.setUserLikes(!liked);
                }
            });

            postViewHolder.postCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostActivity.class);
                    intent.putExtra("post", (Parcelable) post);
                    context.startActivity(intent);
                }
            });

        }

        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return posts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.postCardView) CardView postCardView;

        @BindView(R.id.likeImageView) ImageView likeImageView;
        @BindView(R.id.authorImageView) CircleImageView authorImageView;

        @BindView(R.id.authorTextView) TextView authorTextView;
        @BindView(R.id.dateTextView) TextView dateTextView;
        @BindView(R.id.postTextView) TextView postTextView;
        @BindView(R.id.likeTextView) TextView likeTextView;

        PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressBar) ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
