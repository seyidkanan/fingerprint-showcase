package com.seyidkanan.fingetscantesting;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.seyidkanan.fingetscantesting.fingerprint.FingerprintManagerCompat;

import static android.hardware.fingerprint.FingerprintManager.FINGERPRINT_ERROR_CANCELED;
import static android.hardware.fingerprint.FingerprintManager.FINGERPRINT_ERROR_LOCKOUT;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private FingerprintManagerCompat.AuthenticationCallback authenticationCallback;

    private CancellationSignal cancellationSignal;
    private FingerprintManagerCompat fingerprintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        if (Build.VERSION.SDK_INT >= 23) {
            cancellationSignal = new CancellationSignal();

            authenticationCallback
                    = new FingerprintManagerCompat.AuthenticationCallback() {
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    if (errMsgId == FINGERPRINT_ERROR_CANCELED) {
                        stopScaning();
                        startScanning();
                    } else if (errMsgId == FINGERPRINT_ERROR_LOCKOUT) {
                        //5 fail scan and u can continiu after 30 seconds
                    }
                    setText("onAuthenticationError: " + "id=" + errMsgId + "\tmes=" + errString);
                }

                @Override
                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                    setText("onAuthenticationHelp: " + helpString);
                }

                @Override
                public void onAuthenticationFailed() {
                    setText("onAuthenticationFailed: FingerprintNotRecognized");
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    setText("onAuthenticationSucceeded");
                }
            };

            fingerprintManager = FingerprintManagerCompat.from(this);

            Log.e("kanan", "isHardwareDetected=" + fingerprintManager.isHardwareDetected() +
                    "\thasEnrolledFingerprints=" + fingerprintManager.hasEnrolledFingerprints());

            if (fingerprintManager.isHardwareDetected() &&
                    fingerprintManager.hasEnrolledFingerprints()) {
                checkFingerprint();
            } else {
                setText("Hardware cannot detect");
            }
        }
    }

    public void setText(String text) {
        textView.setText(text);
    }

    private void checkFingerprint() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                fingerprintManager = FingerprintManagerCompat.from(this);
                if (fingerprintManager.isHardwareDetected() &&
                        fingerprintManager.hasEnrolledFingerprints()) {

                }
                fingerprintManager.authenticate(null,
                        0,
                        cancellationSignal,
                        authenticationCallback,
                        null);
            } catch (Exception e) {
                setText("Exception");
                e.printStackTrace();
            }
        }
    }

    private void startScanning() {
        cancellationSignal = new CancellationSignal();
        checkFingerprint();
    }

    private void stopScaning() {
        if (isFingerScannerAvailableAndSet()) {
            cancellationSignal = null;
        }
    }

    public void buttonClickScan(View view) {
        checkFingerprint();
    }

    public boolean isFingerScannerAvailableAndSet() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(FingerprintNormalActivity.this, "User hasn't granted permission to use Fingerprint", Toast.LENGTH_LONG).show();
            return false;
        }
        if (fingerprintManager == null) {
//            Toast.makeText(FingerprintNormalActivity.this,
//                    "mFingerprintManager is null",
//                    Toast.LENGTH_LONG).show();
//            tvstatus.setText("mFingerprintManager is null");
            return false;
        }
        if (!fingerprintManager.isHardwareDetected()) {
//            Toast.makeText(this,
//                    "fingerprint hardware not present or not functional",
//                    Toast.LENGTH_LONG).show();
//            tvstatus.setText("fingerprint hardware not present or not functional");
            return false;
        }
        if (!fingerprintManager.hasEnrolledFingerprints()) {
//            Toast.makeText(this,
//                    "no fingerprint enrolled/saved",
//                    Toast.LENGTH_LONG).show();
//            tvstatus.setText("no fingerprint enrolled/saved");
            return false;
        }
        return true;
    }

}
