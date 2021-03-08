package com.example.mojaaplikacija;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private OutputStream outputStream;
    private InputStream inStream;
    private BluetoothAdapter mBlueAdapter;
    private BluetoothSocket socket = null;
    public static String module_address = "00:18:E4:35:4D:6D";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bluetooth telefona
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    @SuppressLint("SetTextI18n")
    public void connectDevices(View v) {
        TextView connect_txt = findViewById(R.id.connect_textView);
        TextView send_txt = findViewById(R.id.send_textView);
        boolean target_found = false;

        if (socket != null && socket.isConnected()) {
            connect_txt.setText("Vec je konektovan :)");
            return;
        }

        if (mBlueAdapter.isEnabled()) {
            Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();

            for (BluetoothDevice device : devices) {
                if (device.getAddress().equals(module_address)) {
                    target_found = true;
                    ParcelUuid[] uuids = device.getUuids();

                    // kreiranje socketa i dohvatanje I/O strimova.
                    try {
                        socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        socket.connect();

                        outputStream = socket.getOutputStream();
                        inStream = socket.getInputStream();

                        if (socket.isConnected()) {
                            connect_txt.setText("Uspesno konektovao :)");
                            send_txt.setText("");
                        } else
                            connect_txt.setText("Neuspesno konektovao :(");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!target_found)
                connect_txt.setText("Modul nije uparen :(");
        } else
            connect_txt.setText("Adapter nedostupan");

    }

    @SuppressLint("SetTextI18n")
    public void send(View v) throws IOException {
        EditText t = findViewById(R.id.message_source);
        TextView send_txt = findViewById(R.id.send_textView);

        String message = t.getText().toString() + "\n";
        if (socket != null && socket.isConnected())
            // TODO Pukne kada se nakon konektovanja blutut iskljuci.
            // TODO Pop-up tekst kada posaljes nesto.
            outputStream.write(message.getBytes());
        else
            send_txt.setText("Modul nije konektovan");

    }

}