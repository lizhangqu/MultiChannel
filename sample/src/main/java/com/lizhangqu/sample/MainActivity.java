package com.lizhangqu.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mButton;
    private String mCacheChannel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.channel);
        mButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.channel) {
            if (mCacheChannel == null) {
                mCacheChannel = getChannel(this);
            }
            Toast.makeText(this, "channel:" + mCacheChannel, Toast.LENGTH_LONG).show();
        }
    }

    public static final String DEFAULT = "default";

    public static String getChannel(Context context) {
        if (context == null) {
            return null;
        }
        ByteArrayOutputStream bos = null;
        InputStream inputStream = null;
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(context.getPackageCodePath());
            ZipEntry entry = jarFile.getEntry("META-INF/channel.data");
            if (entry == null) {
                return DEFAULT;
            }
            inputStream = jarFile.getInputStream(entry);

            if (inputStream == null) {
                return DEFAULT;
            }

            bos = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int read = -1;

            while ((read = inputStream.read(data)) != -1) {
                bos.write(data, 0, read);
            }

            String channel = new String(bos.toByteArray(), "UTF-8");
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (jarFile != null) {
                    jarFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return DEFAULT;
    }


}
