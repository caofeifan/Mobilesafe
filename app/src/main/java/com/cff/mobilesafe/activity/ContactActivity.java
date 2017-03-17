package com.cff.mobilesafe.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.adapter.ContactsAdapter;
import com.cff.mobilesafe.listener.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactActivity extends BaseActivity {

    private static final String TAG = ContactActivity.class.getSimpleName();
    private static final String VND = "vnd.android.cursor.item/";

    @BindView(R.id.rvContact)
    RecyclerView rvContact;

    private LinearLayoutManager mLayoutManager;
    private ContactsAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        List<Map<String,String>> list = readContact();
        if (list.size()!= 0 && list != null){
            for (int i = 0; i < list.size(); i++) {
                System.out.println("联系人姓名："+list.get(i).get("name"));
                int size = list.get(i).size();
                for (int j = 0; j < size-1; j++) {
                    System.out.println("联系人电话号码："+list.get(i).get("phoneNum"+j));
                }

            }

        }
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ContactsAdapter(list);
        rvContact.setLayoutManager(mLayoutManager);
        rvContact.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Intent intent = new Intent();
                intent.putExtra("phoneNum", ((TextView) view.findViewById(R.id.tvPhoneNum)).getText().toString());
                setResult(Setup3Activity.CODE_SELECT_CONTACT,intent);
                finish();
            }
        });

    }

    private List<Map<String,String>> readContact(){
        List<Map<String,String>> list = new ArrayList<>();
        //首先从raw_contact中读取联系人id
        //根据contact_id从data中获取相应的姓名和电话号码
        //通过，mimetype来区分联系人和电话号码
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Cursor cursor = getContentResolver().query(uri,new String[]{"contact_id"},null,null,null);
        if (cursor!= null){

            while (cursor.moveToNext()){
                String contactId = cursor.getString(0);
                Log.i(TAG, "readContact: contact_id:"+contactId);
                //实际上查询的是data_view视图；
                Uri dataUri = Uri.parse("content://com.android.contacts/data");
                Cursor dataCursor = getContentResolver().query(dataUri,new String[]{"data1","mimetype"},"contact_id = ?",new String[]{contactId},null);
                if (dataCursor != null){
                    Map<String,String> map = new HashMap<>();
                    while (dataCursor.moveToNext()) {
                        String args1 = dataCursor.getString(0);
                        String args2 = dataCursor.getString(1);
                        if (args2.substring(24).equals("name")){
                            map.put("name", args1);
                        }else if (args2.substring(24).equals("phone_v2")){
                            int i = 0;
                            while (true){
                                String str = map.get("phoneNum"+i);
                                if (TextUtils.isEmpty(str)){
                                    //添加一个，然后break
                                    map.put("phoneNum"+i, args1);
                                    System.out.println("----------");
                                    break;
                                }
                                i++;
                            }
                        }

                    }
                    list.add(map);
                    dataCursor.close();
                }
            }

        }
        cursor.close();
        return list;
    }
}
