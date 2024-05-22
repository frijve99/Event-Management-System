package rijve.shovon.eventmanagementsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateEvent extends AppCompatActivity {



    private EditText mUseridEditText,mPrice,mCapacity,mDescrip,mbudget,mEventname,mDate,mPhone;
    private EditText mEmailEditText;
    private RadioButton mIndoor,mOutdoor,mOnline;
    private  String existingKey="";
    private Button save,cancel;
    private boolean exist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_create_event);

        mEmailEditText = findViewById(R.id.email);
        mPrice = findViewById(R.id.price);
        mCapacity = findViewById(R.id.capacity);
        mDescrip = findViewById(R.id.desc);
        mbudget = findViewById(R.id.budget);
        mOnline = findViewById(R.id.online);
        mIndoor = findViewById(R.id.indoor);
        mOutdoor = findViewById(R.id.outdoor);
        mEventname = findViewById(R.id.name);
        mDate = findViewById(R.id.date);
        save = findViewById(R.id.save);
        mPhone = findViewById(R.id.phone);




        Intent I = this.getIntent();
        String user_id = I.getStringExtra("userid");
        String email = I.getStringExtra("email");


        mEmailEditText.setText(email);
        System.out.println(email);

        if(I.hasExtra("EVENT_KEY")){
            existingKey = I.getStringExtra("EVENT_KEY");
            KeyValueDB db = new KeyValueDB(CreateEvent.this);
            String value = db.getValueByKey(existingKey);
            if(!value.isEmpty()){
                String values[] = value.split("---");
                mEventname.setText(values[0]);
                mEmailEditText.setText(values[6]);
                mPrice.setText(values[1]);
                mDate.setText(values[3]);
                mCapacity.setText(values[4]);
                mbudget.setText(values[5]);
                mPhone.setText(values[7]);
                mDescrip.setText(values[8]);
            }
        }

        findViewById(R.id.cancel).setOnClickListener(v -> {
            Intent show = new Intent(this,EventList.class);
            startActivity(show);
            finish();
        });

        save.setOnClickListener(v -> {
            //Collect info..
            String eventname = mEventname.getText().toString().trim();
            String price = mPrice.getText().toString().trim();
            String capacity = mCapacity.getText().toString().trim();
            String desc = mDescrip.getText().toString().trim();
            String budget = mbudget.getText().toString().trim();
            String date = mDate.getText().toString().trim();
            String phone = mPhone.getText().toString().trim();
            String type="";

            if(!mIndoor.getText().toString().isEmpty()){
                type = mIndoor.getText().toString();
            }
            else if(!mOnline.getText().toString().isEmpty()){
                type = mOnline.getText().toString();
            }
            else if(!mOutdoor.getText().toString().isEmpty()){
                type = mOutdoor.getText().toString();
            }


            if(eventname.isEmpty() || price.isEmpty() || capacity.isEmpty() || desc.isEmpty() || budget.isEmpty() || type.isEmpty()){
                // give notice of error..
                Toast.makeText(CreateEvent.this, "FilL all the forms", Toast.LENGTH_SHORT).show();
                return;
            }


            String Value = eventname+"---"+price+"---"+type+"---"+date+"---"+ capacity +"---"+budget+"---"+email+"---"+phone+"---"+desc+"---"+user_id;


            if(existingKey.length()>0){
                KeyValueDB db = new KeyValueDB(CreateEvent.this);
                db.updateValueByKey(existingKey,Value);
                db.close();
                finish();
            }
            else{
                //procced for saving into database..
                String Key =  eventname + System.currentTimeMillis();
                KeyValueDB db = new KeyValueDB(CreateEvent.this);
                db.insertKeyValue(Key,Value);
                db.close();

                Intent show = new Intent(this,EventList.class);
                startActivity(show);
//                System.out.println("DOne");
//                String value = db.getValueByKey(Key);
//                System.out.println(value);

                String[] keys = {"action", "id", "semester", "key", "event"};
                String[] values = {"backup", "2019260076", "2023-1",Key, Value};
                httpRequest(keys, values);
                finish();
            }

        });

    }

    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    System.out.println("OK");

                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }




}