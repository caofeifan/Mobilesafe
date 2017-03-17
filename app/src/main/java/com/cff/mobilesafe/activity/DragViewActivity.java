package com.cff.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DragViewActivity extends Activity {
    private static final String TAG = DragViewActivity.class.getSimpleName();
    private SharedPreferences mPref;
    @BindView(R.id.ivDrag)
    ImageView ivDrag;
    @BindView(R.id.tvTop)
    TextView tvTop;
    @BindView(R.id.tvBottom)
    TextView tvBottom;

    int startX;
    int startY;
    int winWidth;
    int winHeight;
    private int firstClickTime = 0;
    long[] mHits = new long[]{0,0};  //存储点击的时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        ButterKnife.bind(this);
        mPref = getSharedPreferences("config",MODE_PRIVATE);
        //屏幕大小
        Display display = getWindowManager().getDefaultDisplay();
        winWidth = display.getWidth();
        winHeight = display.getHeight();
        int lastX = mPref.getInt("startX",0);
        int lastY = mPref.getInt("startY",0);
        if (lastY>winHeight/2){
            //下面隐藏，上面显示
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        }else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }
        Log.i(TAG, "onCreate: ------------->"+lastX+"------------>"+lastY);
        ivDrag.layout(lastX,lastY,ivDrag.getWidth()+lastX,ivDrag.getHeight()+lastY);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();//布局对象
        layoutParams.leftMargin = lastX;//左边距
        layoutParams.topMargin = lastY;//上边距
        ivDrag.setLayoutParams(layoutParams);


        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 获取Down
                 *获取MOVE
                 * 计算x和y方向的偏移量
                 * 更新初始化位置坐标
                 * 获取UP
                 */
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;
                        //更新布局
                        int l = dx + ivDrag.getLeft();
                        int r = dx + ivDrag.getRight();
                        int t = dy + ivDrag.getTop();
                        int b = dy + ivDrag.getBottom();

                        if (l<0||r>winWidth||t<30||b>winHeight-30) break;
                        ivDrag.layout(l,t,r,b);


                        if (t>winHeight/2){
                            //下面隐藏，上面显示
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        }else {
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }

                        //初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();



                        break;
                    case MotionEvent.ACTION_UP:
                        mPref.edit().putInt("startX",ivDrag.getLeft()).commit();
                        mPref.edit().putInt("startY",ivDrag.getTop()).commit();
                        break;
                    default:break;
                }
                //位置已经处理好
                return false;
            }
        });

    }

    /**
     * 连续点击
     * @param view
     */
    @OnClick(R.id.ivDrag)
    public void ivOnClick(View view){
        //数组拷贝
        System.arraycopy(mHits,1,mHits,0,mHits.length-1);
        mHits[mHits.length-1] = System.currentTimeMillis();
        if (mHits[0] >= (System.currentTimeMillis()-500)){
            Toast.makeText(DragViewActivity.this,"连续点击啊",Toast.LENGTH_SHORT).show();
            //设置回归中间
        }
    }

}
