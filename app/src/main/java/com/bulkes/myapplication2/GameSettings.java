package com.bulkes.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by progr on 11.04.2016.
 */
public class GameSettings extends AppCompatActivity {
    private RecyclerView colorsList;
    private ColorItemsAdapter adapter;
    private Activity activity;
    private float movePixels;
    private float itemWidth;
    private float pad;
    private float colorItemsPadding;
    private int currentColorPosition;
    private EditText nameUser;
    private CheckBox checkBlackBG;
    private Window window;
    public static final String NICKNAME = "nickname";
    public static final String USER_COLOR_NUM = "usercolornum";
    public static final String BLACK_BG = "usercolor";

    private String nickName;
    public static int userColorNum;
    private boolean isBlackBG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.animator.activity_down_up_enter, R.animator.activity_down_up_exit);
        setContentView(R.layout.game_settings_layout);
        window = getWindow();
        //---------------------------------Determine UI changes-------------------------------############
        if(Build.VERSION.SDK_INT>=19 && deviceImmersiveSupport()) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAGS_CHANGED,WindowManager.LayoutParams.FLAGS_CHANGED);
            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_IMMERSIVE) == 0) {
                        setFullScreenMode();
                    }
                }
            });
        }
        //----------------------------------Init Last Settings--------------------------------############
        Intent lastSettings = this.getIntent();
        nickName = lastSettings.getStringExtra(NICKNAME);
        userColorNum = lastSettings.getIntExtra(USER_COLOR_NUM, 1);
        isBlackBG = lastSettings.getBooleanExtra(BLACK_BG, false);
        //----------------------------------Processing--------------------------------############
        currentColorPosition = userColorNum;
        activity = this;
        movePixels = 0;
        colorItemsPadding = getResources().getDimension(R.dimen.colors_items_padding);
        itemWidth = getResources().getDimension(R.dimen.color_item_width) + colorItemsPadding;
        pad = (getResources().getDimension(R.dimen.recycle_view_width) - itemWidth) / 2;
        LinearLayoutManager lManager = new LinearLayoutManager(this);
        lManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        adapter = new ColorItemsAdapter(Settings.UsersBulkColors.length, currentColorPosition);
        colorsList = (RecyclerView) findViewById(R.id.colorsList);
        colorsList.setLayoutManager(lManager);
        colorsList.addItemDecoration(new SpacesItemDecoration((int) colorItemsPadding));
        colorsList.setHasFixedSize(true);
        colorsList.setAdapter(adapter);
        if (currentColorPosition != -1) scrollToPosition(currentColorPosition);
        colorsList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                synchronized (this) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int newPosition = Math.round((movePixels + pad) / itemWidth);
                        scrollToPosition(newPosition);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                movePixels += dx;
            }
        });
        findViewById(R.id.previousColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColorPosition--;
                scrollToPosition(currentColorPosition);
            }
        });
        findViewById(R.id.nextColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColorPosition++;
                scrollToPosition(currentColorPosition);
            }
        });
        nameUser = (EditText) findViewById(R.id.settingsNameField);
        nameUser.setText(nickName);
        nameUser.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (nameUser.getText().toString().length() != 0) {
                        nickName = nameUser.getText().toString();
                    }
                    nameUser.clearFocus();
                }
                return false;
            }
        });
        checkBlackBG = (CheckBox) findViewById(R.id.checkBlackBg);
        checkBlackBG.setChecked(isBlackBG);
        findViewById(R.id.checkBlackBg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    isBlackBG = true;
                    Settings.GameFieldColor = Color.BLACK;
                } else {
                    isBlackBG = false;
                    Settings.GameFieldColor = Color.WHITE;
                }
            }
        });
        findViewById(R.id.closeSettingsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivityResult();
                finish();
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.v("onWindowFocusChanged","onWindowFocusChanged");
    }

/*
    public static int getSizeDP(int realSize)
    {
        float scale = activity.getResources().getDisplayMetrics().density;
        return  (int) (realSize*scale + 0.5f);
    }
*/

    @Override
    protected void onStart() {
        setFullScreenMode();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void scrollToPosition(int newPosition) {
        int itemCount = adapter.getItemCount();
        if (itemCount > 3) {
            if (newPosition <= 0) {
                newPosition = 1;
            } else if (newPosition >= itemCount - 1) {
                newPosition--;
            }
        }
        if (newPosition != currentColorPosition)
            currentColorPosition = newPosition;
        float pixelsToScroll = (itemWidth * (newPosition) - pad) - movePixels;
        if (pixelsToScroll != 0)
            colorsList.smoothScrollBy((int) pixelsToScroll, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.animator.activity_down_up_close_enter, R.animator.activity_down_up_close_exit);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setActivityResult();
            finish();
            return false;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            setFullScreenMode();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setActivityResult() {
        Intent activityResult = new Intent();
        activityResult.putExtra(NICKNAME, nickName);
        activityResult.putExtra(USER_COLOR_NUM, userColorNum);
        activityResult.putExtra(BLACK_BG, isBlackBG);
        activity.setResult(2, activityResult);
    }

    private boolean deviceImmersiveSupport()
    {
        try{
            int id = getResources().getIdentifier("config_enableTranslucentDecor","bool","android");
            if(id == 0){return false;}
            else{
                boolean enable = getResources().getBoolean(id);
                return  enable;
            }
        }catch(Exception e){return false;}
    }

    private void setFullScreenMode() {
        if(Build.VERSION.SDK_INT < 19) {
                window.getDecorView().setSystemUiVisibility(View.GONE);
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if(deviceImmersiveSupport()) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}

class ColorItemsAdapter extends RecyclerView.Adapter<ColorItemsAdapter.ViewHolder>
{
    private static final int VIEW_TYPE_PAD = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private int numItems;
    public static int currentSelectedItem;

    public ColorItemsAdapter(int mNumItems) {
        this.numItems = mNumItems;
        this.currentSelectedItem = -1;
    }

    public ColorItemsAdapter(int mNumItems, int currentSelectedItem) {
        this.numItems = mNumItems;
        this.currentSelectedItem = currentSelectedItem;
    }

    @Override
    public int getItemCount() {
        return numItems + 2;//start and end pad
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == getItemCount()-1) {return VIEW_TYPE_PAD;}
        else
            return VIEW_TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.select_color_item, parent, false);
            return new ViewHolder(v);
        }
        else {
            FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.select_color_item, parent, false);
            return new ViewHolder(v, true);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            holder.backgroundShape.setColor(Settings.UsersBulkColors[position-1]);
            holder.itemView.setBackground(holder.backgroundShape);
            holder.imageView.setVisibility(position == currentSelectedItem?View.VISIBLE:View.GONE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        GradientDrawable backgroundShape;
        public ViewHolder(final FrameLayout itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.selected_icon);
            backgroundShape = new GradientDrawable();
            backgroundShape.setShape(GradientDrawable.OVAL);
            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }
        public ViewHolder(final FrameLayout itemView, boolean notUsed) {super(itemView);}

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position>0 && position < getItemCount()-1) {
                int temp = currentSelectedItem;
                currentSelectedItem = position;
                notifyItemChanged(temp);
                notifyItemChanged(position);
                GameSettings.userColorNum = position;
                Settings.UserDefaultColor = Settings.UsersBulkColors[position-1];
            }
        }
    }
}

class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;
    public SpacesItemDecoration(int space) {
        this.space = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) <= 0)
            outRect.left = space/2;
        else
            outRect.left = space;
        if(parent.getChildAdapterPosition(view)>=parent.getAdapter().getItemCount()-1)
            outRect.right = space/2;
        else
            outRect.right = 0;
        outRect.bottom = 0;
        outRect.top = 0;
    }
}
