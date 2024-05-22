package rijve.shovon.eventmanagementsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout namell,emailll,phonell,pass2ll;
    private TextView showtxt,toggletxt,tittletxt;
    private EditText uname,uemail,upass,upass2,uphone,user_id;
    private CheckBox remUserid,remLogin;
    private Button uGo,uExit,uToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        SharedPreferences sp = getSharedPreferences("useraccount",MODE_PRIVATE);
        SharedPreferences.Editor speditor = sp.edit();
        String userexist = sp.getString("userid","").toString();
        boolean isrememberedLogin = sp.getBoolean("isremLogin",false);
        boolean isrememberedId = sp.getBoolean("isremUserid",false);

        if(!userexist.isEmpty() && isrememberedLogin ){
            Intent I = new Intent(this,CreateEvent.class);
            String userid = userexist;
            String email = sp.getString("email","").toString();
            Bundle bundle = new Bundle();
            bundle.putString( "email",email);
            bundle.putString( "userid",userid);

            I.putExtras(bundle);
            startActivity(I);
            finish();
        }
        setContentView(R.layout.activity_main);
        namell = findViewById(R.id.namell);
        emailll = findViewById(R.id.emailll);
        phonell = findViewById(R.id.phonell);
        pass2ll = findViewById(R.id.pass2ll);
        showtxt = findViewById(R.id.showtxt);
        tittletxt = findViewById(R.id.tittletxt);
        uname = findViewById(R.id.name);
        uemail = findViewById(R.id.mail);
        upass = findViewById(R.id.pass);
        upass2 = findViewById(R.id.pass2);
        uphone = findViewById(R.id.phone);
        user_id = findViewById(R.id.user_id);
        remUserid = findViewById(R.id.remuserid);
        remLogin = findViewById(R.id.remlogin);
        uToggle = findViewById(R.id.toggle);
        uExit = findViewById(R.id.exit);
        uGo = findViewById(R.id.go);

        if(!userexist.isEmpty()){
            namell.setVisibility(View.GONE);
            emailll.setVisibility(View.GONE);
            phonell.setVisibility(View.GONE);
            pass2ll.setVisibility(View.GONE);
            uToggle.setText("Signup");
            showtxt.setText("Don't have an account?");
            tittletxt.setText("Login");
            if(isrememberedId){
                user_id.setText(userexist);
                remUserid.setChecked(true);
            }
        }



        uToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btntxt = uToggle.getText().toString();
                System.out.println(btntxt);

                if(btntxt.equalsIgnoreCase("Login")){
                    namell.setVisibility(v.GONE);
                    emailll.setVisibility(v.GONE);
                    phonell.setVisibility(v.GONE);
                    pass2ll.setVisibility(v.GONE);
                    uToggle.setText("Signup");
                    showtxt.setText("Don't have an account?");
                    tittletxt.setText("Login");
                    if(isrememberedId){
                        user_id.setText(userexist);
                        remUserid.setChecked(true);
                    }
                }
                else{
                    namell.setVisibility(v.VISIBLE);
                    emailll.setVisibility(v.VISIBLE);
                    phonell.setVisibility(v.VISIBLE);
                    pass2ll.setVisibility(v.VISIBLE);
                    uToggle.setText("Login");
                    showtxt.setText("Already have an account?");
                    tittletxt.setText("Signup");
                }
            }
        });

        uExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt = uToggle.getText().toString();
                if(txt.equalsIgnoreCase("Login")){
                    //get info...Do check...Store...
                    //
                    String userid = user_id.getText().toString();
                    String name = uname.getText().toString();
                    String pass = upass.getText().toString();
                    String pass2 = upass2.getText().toString();
                    String phone = uphone.getText().toString();
                    String  email= uemail.getText().toString();
                    boolean isremUserid = remUserid.isChecked();
                    boolean isremLogin = remLogin.isChecked();
                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || userid.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                    else if (!pass.equals(pass2)) {
                        Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                    else{


                        speditor.putString("userid",userid);
                        speditor.putString("name",name);
                        speditor.putString("pass",pass);
                        speditor.putString("phone",phone);
                        speditor.putString("email",email);
                        speditor.putBoolean("isremUserid", isremUserid);
                        speditor.putBoolean("isremLogin", isremLogin);
                        //complete the full..
                        speditor.apply();
                        System.out.println("Signup done");
                        // go to another page....
                        goToNewEventActivity();
                        //finish();
                    }
//
                }
                else{
                    //get info...
                    String userid = user_id.getText().toString().trim();
                    String pass = upass.getText().toString().trim();
                    boolean isremUserid = remUserid.isChecked();
                    boolean isremLogin = remLogin.isChecked();


                    String saveduserId = sp.getString("userid","");
                    String savedPass = sp.getString("pass","");
                    speditor.putBoolean("isremUserid", isremUserid);
                    speditor.putBoolean("isremLogin", isremLogin);
                    //complete the full..
                    speditor.apply();

                    if (!userid.equals(saveduserId) || !pass.equals(savedPass)) {
                        Toast.makeText(MainActivity.this, "Invalid user ID or password", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Go to new event activity
                        goToNewEventActivity();
                        System.out.println("Login done");
                        //finish(); // close this activity
                    }


                }
                System.out.println("ok");
            }
        });
    }

    private void goToNewEventActivity() {
        // Start new event activity
        Intent I = new Intent(this,CreateEvent.class);
        Bundle bundle = new Bundle();
        String  email= uemail.getText().toString();
        String userid = user_id.getText().toString();
        bundle.putString( "email",email);
        bundle.putString( "userid",userid);

        I.putExtras(bundle);
        startActivity(I);
        finish();
        //startActivity(MainActivity.newIntent(this));
    }
}