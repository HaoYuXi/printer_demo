package com.printer.app;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.os.Environment;
import android.Manifest;
import android.content.pm.PackageManager;

import com.tx.printlib.Const;
import com.tx.printlib.UsbPrinter;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private UsbPrinter mUsbPrinter;

    private class MyThread extends Thread {
        @Override
        public void run() {
            mUsbPrinter = new UsbPrinter(getApplicationContext());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsbPrinter = new UsbPrinter(getApplicationContext());
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UsbDevice dev = getCorrectDevice();
                if (dev != null) {
                    if(mUsbPrinter.open(dev)) {
                        final long stat1 = mUsbPrinter.getStatus();
                        final long stat2 = mUsbPrinter.getStatus2();
                        mUsbPrinter.close();
                        showMessage(String.format("%04XH, %04XH", stat1, stat2));
                    } else
                        showMessage("failed to open device");
                } else
                    showMessage("no device");
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UsbDevice dev = getCorrectDevice();
                if (dev != null && mUsbPrinter.open(dev)) {
                    mUsbPrinter.init();
                    mUsbPrinter.doFunction(Const.TX_FONT_ULINE, Const.TX_ON, 0);
                    mUsbPrinter.outputStringLn("This is Font A with underline.");
                    mUsbPrinter.doFunction(Const.TX_SEL_FONT, Const.TX_FONT_B, 0);
                    mUsbPrinter.doFunction(Const.TX_FONT_ULINE, Const.TX_OFF, 0);
                    mUsbPrinter.doFunction(Const.TX_FONT_BOLD, Const.TX_ON, 0);
                    mUsbPrinter.outputStringLn("This is Font B with bold.");
                    mUsbPrinter.resetFont();
                    mUsbPrinter.doFunction(Const.TX_ALIGN, Const.TX_ALIGN_CENTER, 0);
                    mUsbPrinter.outputStringLn("center");
                    mUsbPrinter.doFunction(Const.TX_ALIGN, Const.TX_ALIGN_RIGHT, 0);
                    mUsbPrinter.outputStringLn("right");
                    mUsbPrinter.doFunction(Const.TX_ALIGN, Const.TX_ALIGN_LEFT, 0);
                    mUsbPrinter.doFunction(Const.TX_FONT_ROTATE, Const.TX_ON, 0);
                    mUsbPrinter.outputStringLn("left & rotating");
                    mUsbPrinter.resetFont();
                    mUsbPrinter.doFunction(Const.TX_CHINESE_MODE, Const.TX_ON, 0);
                    mUsbPrinter.outputStringLn("中文");
                    mUsbPrinter.doFunction(Const.TX_FONT_SIZE, Const.TX_SIZE_3X, Const.TX_SIZE_2X);
                    mUsbPrinter.doFunction(Const.TX_UNIT_TYPE, Const.TX_UNIT_MM, 0);
                    mUsbPrinter.doFunction(Const.TX_HOR_POS, 20, 0);
                    mUsbPrinter.outputStringLn("放大Abc");
                    mUsbPrinter.resetFont();
                    mUsbPrinter.doFunction(Const.TX_FEED, 30, 0);
                    mUsbPrinter.outputStringLn("feed 30mm");
                    mUsbPrinter.doFunction(Const.TX_BARCODE_HEIGHT, 15, 0);
                    mUsbPrinter.printBarcode(Const.TX_BAR_UPCA, "12345678901");
                    //mUsbPrinter.printImage("/storage/sdcard0/a1.png");
                    //mUsbPrinter.printImage(getExternalFilesDir(null).getPath()+"/../../../../a1.png");
                    //mUsbPrinter.printImage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()+"/a1.png");
                    mUsbPrinter.printImage("/storage/emulated/0/a1.png");
                    mUsbPrinter.doFunction(Const.TX_UNIT_TYPE, Const.TX_UNIT_PIXEL, 0);
                    mUsbPrinter.doFunction(Const.TX_FEED, 140, 0);
                    mUsbPrinter.doFunction(Const.TX_CUT, Const.TX_PURECUT_FULL, 0);
                    mUsbPrinter.close();
                }
            }
        });
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    private UsbDevice getCorrectDevice() {
        final UsbManager usbMgr = (UsbManager)getSystemService(Context.USB_SERVICE);
        final Map<String, UsbDevice> devMap = usbMgr.getDeviceList();
        if(devMap.size() == 0)
            Log.w(LOG_TAG, "no device");
        for(String name : devMap.keySet()) {
            Log.v(LOG_TAG, "check device: " + name);
            if (UsbPrinter.checkPrinter(devMap.get(name)))
                return devMap.get(name);
        }
        return null;
    }

    private void showMessage(String msg) {
        ((TextView)findViewById(R.id.textView)).setText(msg);
    }
}
