package com.github.emilg1101.app.vkfeed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.emilg1101.app.vkfeed.adapter.PhotosRecyclerViewAdapter;
import com.github.emilg1101.app.vkfeed.liker.Liker;
import com.github.emilg1101.app.vkfeed.models.Post;
import com.github.emilg1101.app.vkfeed.repository.AuthorsRepository;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    @BindView(R.id.authorImageView) public CircleImageView authorImageView;
    @BindView(R.id.likeImageView) public ImageView likeImageView;

    @BindView(R.id.authorTextView) public TextView authorTextView;
    @BindView(R.id.dateTextView) public TextView dateTextView;
    @BindView(R.id.postTextView) public TextView postTextView;
    @BindView(R.id.likeTextView) public TextView likeTextView;

    @BindView(R.id.attachmentsRecyclerView) public RecyclerView attachmentsRecyclerView;

    private Post post;
    private AuthorsRepository authorsRepository;
    private Liker liker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_post);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.post);

        post = getIntent().getParcelableExtra("post");
        authorsRepository = AuthorsRepository.getInstance();
        liker = new Liker();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        attachmentsRecyclerView.setLayoutManager(linearLayoutManager);
        attachmentsRecyclerView.setNestedScrollingEnabled(false);

        init();
    }

    private void init() {
        int id = post.getSourceId();

        Date date = new Date(post.getDate()*1000L);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");

        Picasso.get().load(authorsRepository.get(id).getPhoto()).into(authorImageView);
        authorTextView.setText(authorsRepository.get(id).getName());
        dateTextView.setText(sdf.format(date));
        postTextView.setText(post.getText());
        likeTextView.setText(String.valueOf(post.getLikesCount()));

        if (post.isUserLikes()) {
            likeImageView.setImageResource(R.drawable.like);
        }

        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean liked = post.isUserLikes();
                liker.like(liked, post.getSourceId(), post.getPostId(), new Liker.LikeListener() {
                    @Override
                    public void like(int count) {
                        likeTextView.setText(String.valueOf(count));
                    }
                });
                likeImageView.setImageResource(liked?R.drawable.like_outline:R.drawable.like);
                post.setUserLikes(!liked);
            }
        });

        PhotosRecyclerViewAdapter photosRecyclerViewAdapter = new PhotosRecyclerViewAdapter(post.getPhotos());
        attachmentsRecyclerView.setAdapter(photosRecyclerViewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}