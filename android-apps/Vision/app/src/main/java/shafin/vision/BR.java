package shafin.vision;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;


public class BR extends Setting {

    public static final int BUFFER_LENGTH = 1024, PORT = 60499, SOCKET_TIMEOUT = 10000;
   // public String hostName="103.49.170.147" ;




    private volatile boolean run = false;
    private byte[] buffer;


    private static final String[] COMMANDS = { "reset", "start", "stop" };

    private View view;
    private ImageView imageView;

    private ByteArrayOutputStream byteArrayOutputStream;
    private DatagramSocket datagramSocket;
    private DatagramPacket[] datagramPackets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_br);

        Toast.makeText(getApplicationContext(),"ip Address is :"+getIpAddress(),Toast.LENGTH_LONG).show();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
bb();
        imageView = (ImageView) findViewById(R.id.imageView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }).start();

       // findViewById(R.id.p).setVisibility(View.GONE);
    }

    public BR(){


        buffer= new byte[BUFFER_LENGTH];
        datagramPackets = new DatagramPacket[3];

        byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_LENGTH);
        try {
            for (int i = 0; i < datagramPackets.length; i++) {
                switch (i) {
                    case 0:
                        datagramPackets[i] = new DatagramPacket(buffer, 0, buffer.length, InetAddress.getByName(getIpAddress()), PORT);

                        break;
                    case 1:
                    case 2:
                        datagramPackets[i] = new DatagramPacket(COMMANDS[i].getBytes(), 0, COMMANDS[i].length(), InetAddress.getByName(getIpAddress()), PORT);

                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception exception) {
            Log.d("Exception", exception.getStackTrace().toString());
        }



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stop();
    }

    public void start() {
        if (!run) {
            try {
                datagramSocket = new DatagramSocket();		// creating datagram-socket might throw exception...
                datagramSocket.setSoTimeout(SOCKET_TIMEOUT);
                datagramSocket.send(datagramPackets[1]);

                byteArrayOutputStream.reset();

                run = true;

                while (run) {
                    try {
                        datagramSocket.receive(datagramPackets[0]);

                        if (datagramPackets[0].getLength() > 0) {
                            if (datagramPackets[0].getLength() == COMMANDS[0].length()) {
                                String response = new String(datagramPackets[0].getData(),
                                        datagramPackets[0].getOffset(),
                                        datagramPackets[0].getOffset() +
                                                datagramPackets[0].getLength());

                                if (response.equals(COMMANDS[0])) {
                                    try {
                                        byte[] imageData = byteArrayOutputStream.toByteArray();

                                      //  if (byteArrayOutputStream != null){findViewById(R.id.p).setVisibility(View.GONE);}

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                                        Update(RotateBitmap(bitmap, 90.0f));

                                        Log.d("Exception", "running...");
                                    }
                                    catch (Exception exception) {
                                        Log.d("Exception", exception.toString());
                                        exception.printStackTrace();
                                    }

                                    byteArrayOutputStream.reset();

                                    System.gc();
                                }
                            }
                            else {
                                byteArrayOutputStream.write(datagramPackets[0].getData(), datagramPackets[0].getOffset(), datagramPackets[0].getOffset() + datagramPackets[0].getLength());
                            }
                        }
                    }
                    catch (SocketTimeoutException socketTimeoutException) {
                        Thread.sleep(100);

                        datagramSocket.send(datagramPackets[1]);		// sends "start"...
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                datagramSocket.send(datagramPackets[2]);		// sends "stop"...
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }

            datagramSocket.close();
        }
    }

    public void stop() {
        if (run) {
            run = false;
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void Update(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

public void bb() {

    buffer = new byte[BUFFER_LENGTH];
    datagramPackets = new DatagramPacket[3];

    byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_LENGTH);
    try {
        for (int i = 0; i < datagramPackets.length; i++) {
            switch (i) {
                case 0:
                    datagramPackets[i] = new DatagramPacket(buffer, 0, buffer.length, InetAddress.getByName(getIpAddress()), PORT);

                    break;
                case 1:
                case 2:
                    datagramPackets[i] = new DatagramPacket(COMMANDS[i].getBytes(), 0, COMMANDS[i].length(), InetAddress.getByName(getIpAddress()), PORT);

                    break;
                default:
                    break;
            }
        }
    } catch (Exception exception) {
        Log.d("Exception", exception.getStackTrace().toString());
    }

}

}


