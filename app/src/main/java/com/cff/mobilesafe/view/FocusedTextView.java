package com.cff.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 80755 on 2017/2/23.
 * 获取焦点的TextView
 */

public class FocusedTextView extends TextView {

    public FocusedTextView(Context context) {
        super(context);
    }
    //有属性时使用
    public FocusedTextView(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    //有style样式
    public FocusedTextView(Context context, AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    /**
     * 表示是否获取焦点
     * 跑马灯要运行，首先要调用此方法，有焦点才会有效果
     * 所以不管实际上textView有焦点，都强制返回true，让跑马灯认为有焦点
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
