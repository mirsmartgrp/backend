package nl.fontys.exercise;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;



public class MainActivity extends Activity
{
    private BackendSender sender;
    private ConnectionListener receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sender = new BackendSender(this);
        receiver = new ConnectionListener(new BackendReceiver(),this);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button);

        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            BackendSenderTizen.sendString("Hallo Welt!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        Button button2 = (Button)findViewById(R.id.button2);

        button2.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                            sender.setReceiverAndroid();
                            sender.sendExerciseData("Android Data Hallo!");
                    }
                }
        );
    }

    public void updateText(String text)
    {
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(text);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}