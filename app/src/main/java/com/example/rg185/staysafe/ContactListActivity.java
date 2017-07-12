package com.example.rg185.staysafe;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
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
    private static final String TAG = "ContactListActivity";
    int listSize = 0;
    String allNumbers = "";


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
                    listSize++;
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

                String contact = adapter.getItem(pos).toString();
                Cursor data = databaseHelper.getId(contact);
                int contactID = -1;
                while(data.moveToNext()){
                    contactID = data.getInt(0);
                }
                if(contactID > -1){
                    Log.v(TAG, "onContactClick: The ID is: " + contactID);
                }
                else {
                    Toast.makeText(ContactListActivity.this, "No ID associated with that contact", Toast.LENGTH_SHORT).show();
                }
                databaseHelper.deleteContact(contactID, contact);
                adapter.remove(adapter.getItem(pos));
                listSize--;
                Log.v(TAG, "The list size is " + contactList.size());
                Toast.makeText(ContactListActivity.this, "Contact Successfully Deleted", Toast.LENGTH_SHORT).show();
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

    public void phoneNumbers(String issue, String myAddress){

        databaseHelper.getAllContacts();

        /*ArrayList<String> tempList = contactList;
        Log.v(TAG, tempList.size() + "");
        int len = tempList.size();

        for (int x = 0; x < tempList.size(); x++ ){

            String number = tempList.get(x).toString();
            number = number.substring(number.indexOf("#") + 1);

            if(issue.equals("heartattack")) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "I'm having a heart attack. I am currently at " + myAddress + ".", null, null);
            }

            else if (issue.equals("stroke")){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "I'm having a stroke. I am currently at " + myAddress + ".", null, null);
            }

            else if (issue.equals("bleeding")){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "I'm bleeding. I am currently at " + myAddress + ".", null, null);
            }

            else if (issue.equals("breathing")){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "I have trouble breathing. I am currently at " + myAddress + ".", null, null);
            }

            else if (issue.equals("heatstroke")){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "I'm having a heatstroke. I am currently at " + myAddress + ".", null, null);
            }

            else if (issue.equals("brokenbone")){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "I have a broken bone. I am currently at " + myAddress + ".", null, null);
            }

            else if (issue.equals("poisoning")){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "I'm poisoned. I am currently at " + myAddress + ".", null, null);
            }
        }
        Toast.makeText(this, "Your trusted contacts have been contacted. Please wait.", Toast.LENGTH_SHORT).show();
*/
    }

}
