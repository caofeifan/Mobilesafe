package com.cff.mobilesafe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.adapter.CallSafeAdapter;
import com.cff.mobilesafe.dao.BlackNumberDao;
import com.cff.mobilesafe.domain.BlackNumberInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallSafeActivity extends BaseActivity {
    private static final String TAG = CallSafeActivity.class.getSimpleName();
    //正在滚动,空闲状态
    public static final int SCROLL_STATE_IDLE = 0;

    //正在被外部拖拽,一般为用户正在用手指滚动
    public static final int SCROLL_STATE_DRAGGING = 1;

    //自动滚动开始
    public static final int SCROLL_STATE_SETTLING = 2;


    private static final int TAG_CHECK_SCROLL_UP = -1;
    private static final int TAG_CHECK_SCROLL_DOWN = 1;
    private static final int CODE_INIT_DATA = 0002;
    private static final int CODE_ADD_DATA = 0001;

    @BindView(R.id.list_view)
    RecyclerView list_view;
    @BindView(R.id.ll_pb)
    LinearLayout mLinearLayout;
    @BindView(R.id.tv_null)
    TextView tv_null;
    private BlackNumberDao blackNumberDao;
    private List<BlackNumberInfo> blackNumberInfos;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CODE_INIT_DATA:
                    //设置Adapter
                    adapter = new CallSafeAdapter(CallSafeActivity.this,blackNumberInfos);
                    list_view.setAdapter(adapter);
                    adapter.loadingFinished();
                    mLinearLayout.setVisibility(View.INVISIBLE);
                    if (blackNumberInfos.size() == 0){
                        tv_null.setVisibility(View.VISIBLE);
                    }
                    break;
                case CODE_ADD_DATA:
                    adapter.addData(0,newDatas);
                    adapter.loadingFinished();
                    break;
            }

        }
    };
    private CallSafeAdapter adapter;
    private List<BlackNumberInfo> newDatas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        ButterKnife.bind(this);
        initUI();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //获取数据
                blackNumberDao = new BlackNumberDao(CallSafeActivity.this);
                blackNumberInfos = blackNumberDao.findAll();
                SystemClock.sleep(3000);
                Message msg = Message.obtain();
                msg.what = CODE_INIT_DATA;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        mLinearLayout.setVisibility(View.VISIBLE);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list_view.setLayoutManager(linearLayoutManager);
        list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "-----------onScrolled-----------");
                Log.i(TAG, "dx: " + dx);
                Log.i(TAG, "dy: " + dy);
                Log.i(TAG, "CHECK_SCROLL_UP: " + recyclerView.canScrollVertically(TAG_CHECK_SCROLL_UP));
                Log.i(TAG, "CHECK_SCROLL_DOWN: " + recyclerView.canScrollVertically(TAG_CHECK_SCROLL_DOWN));
                if (dy>0 && !recyclerView.canScrollVertically(TAG_CHECK_SCROLL_DOWN)){
                    //加载更多
                    adapter.setLoading(true);
                    adapter.notifyDataSetChanged();
                    addData();
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i(TAG, "-----------onScrollStateChanged-----------");
                Log.i(TAG, "newState: " + newState);
            }
        });
    }

    public void addData(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                //获取数据
                blackNumberDao = new BlackNumberDao(CallSafeActivity.this);
                newDatas = blackNumberDao.findPart(0,10);
                SystemClock.sleep(3000);
                Message msg = Message.obtain();
                msg.what = CODE_ADD_DATA;
                mHandler.sendMessage(msg);
            }
        }.start();
    }
}
