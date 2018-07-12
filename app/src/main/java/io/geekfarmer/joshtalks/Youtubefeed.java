package io.geekfarmer.joshtalks;


import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.prof.youtubeparser.Parser;
import com.prof.youtubeparser.models.videos.Video;

import java.util.ArrayList;

public class Youtubefeed extends Fragment {

    private RecyclerView mRecyclerView;
    private VideoAdapter vAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private int totalElement;
    private String nextToken;
    private final String CHANNEL_ID = "UCTlnaHHQ75zlDg_fLr7tGEg";
    //TODO: delete
    private final String API_KEY = "AIzaSyBTkbbNGXwqTUronIcTn6NMMgoWTg_oNtY";

    public static final String TITLE = "YOUTUBE";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View youtube = inflater.inflate(R.layout.activity_youtubefeed, container, false);


        progressBar = youtube.findViewById(R.id.progressBar);
        fab = youtube.findViewById(R.id.fab);

        mRecyclerView = youtube.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout = youtube.findViewById(R.id.container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.canChildScrollUp();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                vAdapter.clearData();
                vAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(true);
                loadVideo();
            }
        });

        if (!isNetworkAvailable()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.alert_message)
                    .setTitle(R.string.alert_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    getActivity().finish();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        } else if (isNetworkAvailable())
            loadVideo();

        //show the fab on the bottom of recycler view
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                if (lastVisible == totalElement - 1)
                    fab.setVisibility(View.VISIBLE);
                else
                    fab.setVisibility(View.GONE);

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //load more data
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parser parser = new Parser();
                if (nextToken != null) {
                    String url = parser.generateMoreDataRequest(CHANNEL_ID, 20, Parser.ORDER_DATE, API_KEY, nextToken);
                    parser.execute(url);
                    parser.onFinish(new Parser.OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(ArrayList<Video> list, String nextPageToken) {

                            //update the adapter with the new data
                            vAdapter.getList().addAll(list);
                            totalElement = vAdapter.getItemCount();
                            nextToken = nextPageToken;
                            vAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "New video added!", Toast.LENGTH_SHORT).show();
                            fab.setVisibility(View.GONE);
                            mRecyclerView.scrollBy(0, 1000);
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(getActivity(), "Error while loading data. Please retry", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Unable to load data. Please retry", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return youtube;
    }



    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void loadVideo() {

        if (!mSwipeRefreshLayout.isRefreshing())
            progressBar.setVisibility(View.VISIBLE);

        Parser parser = new Parser();
        String url = parser.generateRequest(CHANNEL_ID, 20, Parser.ORDER_DATE, API_KEY);

        parser.execute(url);
        parser.onFinish(new Parser.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(ArrayList<com.prof.youtubeparser.models.videos.Video> list, String nextPageToken) {
                //list is an ArrayList with all video's item
                //set the adapter to recycler view
                vAdapter = new VideoAdapter(list, R.layout.yt_row, getActivity());
                mRecyclerView.setAdapter(vAdapter);
                totalElement = vAdapter.getItemCount();
                nextToken = nextPageToken;
                vAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onError() {
                Toast.makeText(getActivity(), "Error while loading data. Please retry", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onResume() {

        super.onResume();
        if (vAdapter != null)
            vAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (vAdapter != null)
            vAdapter.clearData();
    }


}

