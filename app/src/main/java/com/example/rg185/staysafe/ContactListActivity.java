package com.example.rg185.staysafe;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by rg185 on 2017-07-09.
 */

public class ContactListActivity extends AppCompatActivity {


    ArrayList<String> contactList;
    ArrayAdapter<String> adapter;
    EditText inputText;
    Button addButton;
    ListView contactLV;
    DatabaseHelper databaseHelper;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_main);
        inputText = (EditText) findViewById(R.id.inputText);
        addButton = (Button) findViewById(R.id.addButton);
        contactLV = (ListView) findViewById(R.id.contactList);
        databaseHelper = new DatabaseHelper(this);

        populateContactLV();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(inputText.getText().toString().equals(""))) {
                    contactList.add(inputText.getText().toString());
                    addData(inputText.getText().toString());
                    inputText.setText("");
                    adapter.notifyDataSetChanged();
                }
                else if (inputText.getText().toString().equals("")) {
                    Toast.makeText(ContactListActivity.this, "Please Enter a Valid Entry", Toast.LENGTH_SHORT).show();
                }
            }
        });


        contactLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                adapter.remove(adapter.getItem(pos));

                Log.v("long clicked","pos: " + pos);
                return true;
            }
        });

    }

    public void addData(String newContact){
        boolean insertData = databaseHelper.addData(newContact);
        if(insertData)
            Toast.makeText(this, "Trusted Contact Successfully Added!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Could Not Add Trusted Contact (ERROR)!", Toast.LENGTH_SHORT).show();

    }

    private void populateContactLV() {

        Log.d(TAG, "Populating Trusted Contacts List");
        Cursor data = databaseHelper.getData();
        contactList = new ArrayList<>();

        while (data.moveToNext()){
            contactList.add(data.getString(1));
        }
        adapter = new ArrayAdapter<String>(ContactListActivity.this, android.R.layout.simple_list_item_1, contactList);
        contactLV.setAdapter(adapter);

    }
}
