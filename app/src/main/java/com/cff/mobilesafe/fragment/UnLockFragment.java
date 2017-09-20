package com.cff.mobilesafe.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.adapter.UnlockAdapter;
import com.cff.mobilesafe.domain.AppInfo;
import com.cff.mobilesafe.domain.AppLock;
import com.cff.mobilesafe.engine.AppInfos;
import com.cff.mobilesafe.listener.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UnLockFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UnLockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnLockFragment extends Fragment {
    private static final String TAG = UnLockFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mCallback;
    private View view;
    /**
     * Context
     */
    private FragmentActivity activity;
    private RecyclerView mRecyclerView;
    private List<AppLock> appsUnLock;

    private UnlockAdapter unlockAdapter;
    private TextView tv_unlock_count;

    public UnLockFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UnLockFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UnLockFragment newInstance(String param1, String param2) {
        UnLockFragment fragment = new UnLockFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "--------------onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mCallback = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--------------onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        /**
         * 获取上下文
         */
        activity = getActivity();
        appsUnLock = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "--------------onCreateView");
        view = inflater.inflate(R.layout.fragment_un_lock, null, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rl_unlock);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        tv_unlock_count = (TextView) view.findViewById(R.id.tv_unlock_count);
        return view;
    }

    /**
     * 当Activity的onCreate方法返回时调用，初始化数据使用
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
/**
 * 获取参数
 */
        if (getArguments().getSerializable("appsUnLock") != null) {
            appsUnLock = (List<AppLock>) getArguments().getSerializable("appsUnLock");
            Log.i(TAG, "--------------onActivityCreated-----"+appsUnLock.size());
        }

        tv_unlock_count.setText("未加锁软件(" + appsUnLock.size() + ")");
    }

    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "--------------onStart"+appsUnLock.size());
        unlockAdapter = new UnlockAdapter(activity, appsUnLock);
        mRecyclerView.setAdapter(unlockAdapter);
        unlockAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final String position) {

                new Thread(){
                    @Override
                    public void run() {
                        SystemClock.sleep(500);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * 删除本Fragment的 appsUnLock
                                 * 向外发送删除的appLock
                                 * 改变Count计数
                                 */

                                int i = Integer.parseInt(position);
                                AppLock appLock = appsUnLock.remove(i);
                                if (appLock != null) {
                                    unlockAdapter.notifyListChanged(appsUnLock);
                                    if (mCallback != null) {
                                        mCallback.onFragmentInteraction(appLock);
                                    }
                                }
                                tv_unlock_count.setText("未加锁软件(" + appsUnLock.size() + ")");
                            }
                        });
                    }
                }.start();




            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onListItemClick(AppLock appLock) {
        if (mCallback != null) {
            mCallback.onFragmentInteraction(appLock);
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(AppLock appLocks);
    }

    /**
     * 当该Fragment的视图被移除时调用
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 当Fragment与Activity关联被取消时调用
     */
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
        this.appsUnLock = appLocks;
        /*unlockAdapter.notifyListChanged(appsUnLock);*/

    }


}
