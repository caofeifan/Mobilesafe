package com.cff.mobilesafe.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.adapter.LockAdapter;
import com.cff.mobilesafe.adapter.UnlockAdapter;
import com.cff.mobilesafe.domain.AppLock;
import com.cff.mobilesafe.listener.OnRecyclerViewItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/28.
 */

public class LockFragment extends Fragment {

    private List<AppLock> appsLock;

    private LockFragment.OnFragmentInteractionListener1 mCallback;
    private RecyclerView mRecyclerView;
    private LockAdapter lockAdapter;
    private Activity activity;
    private TextView tv_unlock_count;

    public LockFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LockFragment.OnFragmentInteractionListener1) {
            mCallback = (LockFragment.OnFragmentInteractionListener1) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        System.out.println("onAttach 被调用了");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appsLock = new ArrayList<>();
        activity = getActivity();
        System.out.println("onCreate 被调用了");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("onCreateView 被调用了");
        View view = inflater.inflate(R.layout.fragment_lock, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rl_lock);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        tv_unlock_count = (TextView) view.findViewById(R.id.tv_unlock_count);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("onActivityCreated 被调用了");
        /**
         * 获取参数
         */
        if (getArguments().getSerializable("appsLock") !=null){
            appsLock = (List<AppLock>)getArguments().getSerializable("appsLock");
        }

        tv_unlock_count.setText("加锁软件("+appsLock.size()+")");

    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("onStart 被调用了");
        lockAdapter = new LockAdapter(activity, appsLock);
        mRecyclerView.setAdapter(lockAdapter);
        lockAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final String position) {

                /**
                 * 删除本Fragment的 appsUnLock
                 * 向外发送删除的appLock
                 * 改变Count计数
                 */
                new Thread(){
                    @Override
                    public void run() {
                        /**
                         * 暂停1000ms
                         */
                        SystemClock.sleep(500);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int i = Integer.parseInt(position);
                                AppLock appLock = appsLock.remove(i);
                                if (appLock != null) {
                                    lockAdapter.notifyListChanged(appsLock);
                                    if (mCallback != null) {
                                        mCallback.onFragmentInteraction1(appLock);
                                    }
                                }
                                boolean remove = appsLock.remove(Integer.valueOf(position));
                                if (remove){
                                    lockAdapter.notifyListChanged(appsLock);
                                }
                                tv_unlock_count.setText("加锁软件("+appsLock.size()+")");
                            }
                        });
                    }
                }.start();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * 更新数据
     * @param appLocks
     */
    public void updateView(List<AppLock> appLocks) {
        this.appsLock = appLocks;
    //    lockAdapter.notifyListChanged(appsLock);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener1 {
        // TODO: Update argument type and name
        void onFragmentInteraction1(AppLock appLock);
    }
}
