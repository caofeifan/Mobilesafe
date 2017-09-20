package com.cff.mobilesafe.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.adapter.TaskMAnagerAdapter;
import com.cff.mobilesafe.domain.TaskInfo;
import com.cff.mobilesafe.engine.TaskInfoParser;
import com.cff.mobilesafe.listener.OnItemTaskClickListener;
import com.cff.mobilesafe.utils.SystemInfoUitl;
import com.cff.mobilesafe.view.FocusedTextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskManagerActivity extends BaseActivity {
    private static final String TAG = TaskManagerActivity.class.getSimpleName();

    private static final int CODE_TASKINFO = 001;

    @BindView(R.id.tv_task_process_count)
    TextView tv_task_process_count;
    @BindView(R.id.tv_task_memory)
    TextView tv_task_memory;

    @BindView(R.id.rv_taskInfo)
    RecyclerView rv_taskInfo;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTasks;
    private List<TaskInfo> sysTasks;
    private List<TaskInfo> deleteTasks;
    private TaskMAnagerAdapter adapter;
    private int processCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        ButterKnife.bind(this);
        initUI();
        initData();
    }


    private void initUI() {
        processCount = SystemInfoUitl.getProcessCount(this);
        tv_task_process_count.setText("运行中的进程：（" + processCount + "个)");
        long availMem = SystemInfoUitl.getAvailMem(this);
        long totalMem = SystemInfoUitl.getTotalMem(this);
        tv_task_memory.setText("剩余/总内存：（" + Formatter.formatFileSize(this, availMem) + "/" +
                Formatter.formatFileSize(this, totalMem) + ")");

        userTasks = new ArrayList<>();
        sysTasks = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_taskInfo.setLayoutManager(layoutManager);

        /**
         * 读取配置文件
         */
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuffer sb = new StringBuffer();
            String line = reader.readLine();
            for (char c : line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            long l = Long.parseLong(String.valueOf(sb));
            Log.i(TAG, "initUI: -----" + Formatter.formatFileSize(this, l * 1024));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_TASKINFO:
                    for (TaskInfo taskInfo : taskInfos) {
                        if (taskInfo.isUserApp()) {
                            userTasks.add(taskInfo);
                        } else {
                            sysTasks.add(taskInfo);
                        }
                    }
                    adapter = new TaskMAnagerAdapter(TaskManagerActivity.this, userTasks, sysTasks);
                    adapter.setOnItemClickListener(new OnItemTaskClickListener() {
                        @Override
                        public void onItemClick(View view, String data) {
                            int position = Integer.valueOf(data);
                            if (position < userTasks.size() + 1) {
                                TaskInfo taskInfo = userTasks.get(position - 1);
                                if (taskInfo.isChecked()) {
                                    taskInfo.setChecked(false);
                                } else {
                                    taskInfo.setChecked(true);
                                }
                                userTasks.set(position - 1, taskInfo);
                            } else {
                                TaskInfo taskInfo = sysTasks.get(position - userTasks.size() - 2);
                                if (taskInfo.isChecked()) {
                                    taskInfo.setChecked(false);
                                } else {
                                    taskInfo.setChecked(true);
                                }
                                sysTasks.set(position - userTasks.size() - 2, taskInfo);
                            }
                            //通知adapter数据变化
                            adapter.notifyDataChanged(userTasks, sysTasks);
                        }
                    });
                    rv_taskInfo.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);
                Message msg = new Message();
                msg.what = CODE_TASKINFO;
                handler.sendMessage(msg);

            }
        }.start();

    }

    /**
     * 清理进程
     */
    public void killProcess() {
        deleteTasks = new ArrayList<>();

        int totalCount = 0;
        for (TaskInfo taskInfo : userTasks) {
            //不能删除本程序
            if (taskInfo.getPackageName() != getPackageName()) {
                if (taskInfo.isChecked()) {
                    //杀死该进程
                    TaskInfoParser.killProcess(this, taskInfo.getPackageName());
                    deleteTasks.add(taskInfo);
                    totalCount++;
                }
            } else {
                Toast.makeText(TaskManagerActivity.this, "无法删除本进程", Toast.LENGTH_SHORT).show();
            }

        }
        for (TaskInfo taskInfo : sysTasks) {
            if (taskInfo.isChecked()) {
                //杀死该进程
                TaskInfoParser.killProcess(this, taskInfo.getPackageName());
                deleteTasks.add(taskInfo);
                totalCount++;

            }
        }
/**
 *
 */
        for (TaskInfo taskInfo : deleteTasks) {
            if (taskInfo.isUserApp()) {
                userTasks.remove(taskInfo);
            } else {
                sysTasks.remove(taskInfo);
            }
        }
        processCount -= totalCount;
        tv_task_process_count.setText("进程数：（"+processCount+")");
        //解除delteTaskInfo
        deleteTasks = null;
        Toast.makeText(TaskManagerActivity.this, "共关闭了" + totalCount + "个进程", Toast.LENGTH_SHORT).show();
        adapter.notifyDataChanged(userTasks, sysTasks);
    }


}
