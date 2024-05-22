package rijve.shovon.eventmanagementsystem;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventList extends AppCompatActivity {
    private ListView lEvents;
    private ArrayList<Event> events;
    private CustomEventAdapter adapter;

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }
    @Override
    protected void onStart() {
        super.onStart();
        String[] keys = {"action", "id", "semester"};
        String[] values = {"restore", "2019260076", "2023-1"};
        httpRequest(keys, values);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        findViewById(R.id.exit).setOnClickListener(v -> {
            onDestroy();
            //finish();
        });
        // initialize list-reference by ListView object defined in XML
        lEvents = findViewById(R.id.listview);
// load events from database if there is any
        loadData();
    }

    private void showDialog(String message, String title,String key){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        KeyValueDB db = new KeyValueDB(EventList.this);
                        db.deleteDataByKey(key);
                        db.close();
                        dialog.cancel();
                        loadData();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert  = builder.create();
        alert.show();

    }

    private void loadData(){
        events = new ArrayList<>();
        KeyValueDB db = new KeyValueDB(this);
        Cursor rows = db.execute("SELECT * FROM key_value_pairs");
        if (rows.getCount() == 0) {
            return;
        }
//events = new Event[rows.getCount()];
        while (rows.moveToNext()) {
            String key = rows.getString(0);
            String eventData = rows.getString(1);
            String[] fieldValues = eventData.split("---");
            String name = fieldValues[0];
            String dateTime = fieldValues[1];
            String eventType = fieldValues[2];
            Event e = new Event(key, name,"", dateTime,"","", "", "", "",
                    eventType);
            events.add(e);
        }
        db.close();
        adapter = new CustomEventAdapter(this, events);
        lEvents.setAdapter(adapter);
// handle the click on an event-list item
        lEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int
                    position, long id) {

                Intent i = new Intent(EventList.this,CreateEvent.class);
                String value = db.getValueByKey(events.get(position).key);
                String values[] = value.split("---");
                i.putExtra("EVENT_KEY", events.get(position).key);

                startActivity(i);
            }
        });
// handle the long-click on an event-list item
        lEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int
                    position, long id) {
                //String message = "Do you want to delete event -"+events[position].name +"";
                String message = "Do you want to delete event -"+events.get(position).name +"";
                System.out.println(message);
                showDialog(message, "Delete Event", events.get(position).key);
//showDialog(message, &quot;Delete Event&quot;, events.get(position).key);
                return true;
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
                    System.out.println(data);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    System.out.println("Ok2");
                    updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    public void updateEventListByServerData(String data){
        System.out.println("found");
        try{
            JSONObject jo = new JSONObject(data);
            if(jo.has("events")){
                JSONArray ja = jo.getJSONArray("events");
                for(int i=0; i<ja.length(); i++){
                    JSONObject event = ja.getJSONObject(i);
                    String eventKey = event.getString("e_key");
                    String eventValue = event.getString("e_value");
                    System.out.println(eventValue);
                    String[] fieldValues = eventValue.split("---");
                    System.out.println(fieldValues);
//                    String name = fieldValues[0];
//                    String price = fieldValues[1];
//                    String eventType = fieldValues[2];
//                    String dateTime = fieldValues[3];
//                    String capacity = fieldValues[4];
//                    String budget = fieldValues[5];
//                    String email = fieldValues[6];
//                    String phone = fieldValues[7];
//                    String desc = fieldValues[8];




//                    Event e = new Event(eventKey, name,price, dateTime,capacity,budget, email, phone, desc, eventType);
//                    events.add(e);

// split eventValue to show in event list
                }
            }
        }catch(Exception e){}
    }

}


//public class EventList extends AppCompatActivity {
//    private ListView lEvents;
//    private ArrayList<Event> events;
//    private CustomEventAdapter adapter;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_event_list);
//
//        findViewById(R.id.exit).setOnClickListener(v -> {
//            finish();
//        });
//        events = new ArrayList<>();
//        // initialize list-reference by ListView object defined in XML
//        lEvents = findViewById(R.id.listview);
//        adapter = new CustomEventAdapter(this, events);
//        lEvents.setAdapter(adapter);
//// handle the click on an event-list item
//        lEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view, int
//                    position, long id) {
//// String item = (String) parent.getItemAtPosition(position);
//                System.out.println(position);
////                Intent i = new Intent(EventList.this,CreateEvent.class);
////                String value = db.getValueByKey(events.get(position).key);
////                String values[] = value.split("---");
////                String email = values[6];
////                String user_id = values[9];
////                i.putExtra("EVENT_KEY", events.get(position).key);
////                i.putExtra("userid",user_id);
////                i.putExtra("email",email);
////                startActivity(i);
//            }
//        });
//// handle the long-click on an event-list item
//
//// load events from database if there is any
//        loadData();
//    }
//
//    protected void onRestart() {
//        super.onRestart();
//        loadData();
//    }
//
//    protected void onStart() {
//        super.onStart();
//        String[] keys = {"action", "id", "semester"};
//        String[] values = {"restore", "2019260076", "2023-1"};
//        httpRequest(keys, values);
//    }
//
//    private void loadData(){
//        events.clear();
//        KeyValueDB db = new KeyValueDB(this);
//        Cursor rows = db.execute("SELECT * FROM key_value_pairs");
//        if (rows.getCount() == 0) {
//            return;
//        }
////events = new Event[rows.getCount()];
//        while (rows.moveToNext()) {
//            String key = rows.getString(0);
//            String eventData = rows.getString(1);
//            String[] fieldValues = eventData.split("---");
//            String name = fieldValues[0];
//            String price = fieldValues[1];
//            String eventType = fieldValues[2];
//            String dateTime = fieldValues[3];
//            String capacity = fieldValues[4];
//            String budget = fieldValues[5];
//            String email = fieldValues[6];
//            String phone = fieldValues[7];
//            String desc = fieldValues[8];
//
//            Event e = new Event(key, name,price, dateTime,capacity,budget, email, phone, desc, eventType);
//            events.add(e);
//        }
//        db.close();
//        adapter.notifyDataSetChanged();
//    }
//
//    private void showDialog(String message, String title, String key) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setMessage(message);
//        builder.setTitle(title);
//        builder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Util.getInstance().deleteByKey(EventList.this,key);
//                dialogInterface.cancel();
//                loadData();
//                adapter.notifyDataSetChanged();
//            }
//        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//            }
//        });
//
//        AlertDialog alert=builder.create();
//        alert.show();
//    }
//
//    private void httpRequest(final String keys[],final String values[]){
//        new AsyncTask<Void,Void,String>(){
//            @Override
//            protected String doInBackground(Void... voids) {
//                List<NameValuePair> params=new ArrayList<NameValuePair>();
//                for (int i=0; i<keys.length; i++){
//                    params.add(new BasicNameValuePair(keys[i],values[i]));
//                }
//                String url= "https://www.muthosoft.com/univ/cse489/index.php";
//                String data="";
//                try {
//                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
//                    System.out.println(data);
//                    return data;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//            protected void onPostExecute(String data){
//                if(data!=null){
//                    System.out.println(data);
//                    System.out.println("Ok2");
//                    updateEventListByServerData(data);
//                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
//                }
//            }
//        }.execute();
//    }
//    public void updateEventListByServerData(String data){
//        System.out.println("found");
//        try{
//            JSONObject jo = new JSONObject(data);
//            if(jo.has("events")){
//                JSONArray ja = jo.getJSONArray("events");
//                for(int i=0; i<ja.length(); i++){
//                    JSONObject event = ja.getJSONObject(i);
//                    String eventKey = event.getString("e_key");
//                    String eventValue = event.getString("e_value");
//                    System.out.println(eventValue);
//                    String[] fieldValues = eventValue.split("---");
//                    System.out.println(fieldValues);
//                    String name = fieldValues[0];
//                    String price = fieldValues[1];
//                    String eventType = fieldValues[2];
//                    String dateTime = fieldValues[3];
//                    String capacity = fieldValues[4];
//                    String budget = fieldValues[5];
//                    String email = fieldValues[6];
//                    String phone = fieldValues[7];
//                    String desc = fieldValues[8];
//
//
//
//
//                    Event e = new Event(eventKey, name,price, dateTime,capacity,budget, email, phone, desc, eventType);
//                    events.add(e);
//
//// split eventValue to show in event list
//                }
//            }
//        }catch(Exception e){}
//    }
//}