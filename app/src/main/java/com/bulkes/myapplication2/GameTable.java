package com.bulkes.myapplication2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by progr on 24.04.2016.
 */
public class GameTable  extends AppCompatActivity{
    private RecyclerView gameTableList;
    @Override
    public void onCreate(Bundle saveInstance)
    {
        super.onCreate(saveInstance);
        setContentView(R.layout.game_table_layout);
        gameTableList = (RecyclerView)findViewById(R.id.gameTableList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameTableList.setLayoutManager(linearLayoutManager);
        TableListAdapter tableListAdapter = new TableListAdapter(300);
        tableListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

            }
        });

        gameTableList.setAdapter(tableListAdapter);
        gameTableList.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy ){
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }
}

interface OnLoadMoreListener{
    void onLoadMore();
}

class TableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private ArrayList<String> itemsText;
    private int itemsCount;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public TableListAdapter(int itemsCount) {
        this.itemsCount = itemsCount;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item_layout, parent, false);
            return new ItemsViewHolder(view);
        }else if(viewType == VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_table_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemsViewHolder){
            ItemsViewHolder itemsViewHolder = (ItemsViewHolder)holder;
            itemsViewHolder.firstColumn.setText("a");
            itemsViewHolder.firstColumn.setText("b");
            itemsViewHolder.firstColumn.setText("c");
        }else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemsText.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.gameTableProgressBar);
        }
    }
    class ItemsViewHolder extends RecyclerView.ViewHolder{

        private TextView firstColumn;
        private TextView secondColumn;
        private TextView thirdColumn;
        public ItemsViewHolder(View itemView) {
            super(itemView);
            firstColumn = (TextView) itemView.findViewById(R.id.firstColumnText);
            secondColumn = (TextView) itemView.findViewById(R.id.secondColumnText);
            thirdColumn = (TextView) itemView.findViewById(R.id.thirdColumnText);
        }
    }
}
