package com.example.myapplication;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.KeyguardManager;
import android.content.Intent;
import android.database.Cursor;
import androidx.biometric.BiometricManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Executor;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final int INTENT_AUTHENTICATE = 1;
    private boolean isBiometricEnabled;
    private boolean isBiometricPresent;
    private static final String TAG = "Browserstack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find call from contact button
        Button contactButton = (Button) findViewById(R.id.call_contact_button);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked");
                // Uri uri = Uri.parse("content://contacts");
                Intent contactIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                contactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(contactIntent, REQUEST_CODE);
            }
        });
        // Find edit view
        final TextView phoneNumberEditView = (TextView) findViewById(R.id.edit_text_phone_number);
        // Find call button
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked2");
                if(phoneNumberEditView.getText() != null) {
                    Uri number = Uri.parse("tel:" + phoneNumberEditView.getText());
                    Log.d(TAG, number.toString());
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }
            }
        });

        Button biometricAuth = (Button) findViewById(R.id.biometric_auth);
        biometricAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked2");
                showBiometricPrompt();
            }
        });

        Button passcodeAuth = (Button) findViewById(R.id.button3);
        passcodeAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked2");
                authenticatePasscode();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Bundle extras = data.getExtras();
                // String phoneNumber = extras.get("data").toString();
                Uri uri = data.getData();
                Log.e("ACT_RES", uri.toString());
                String[] projection = {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);
                Log.d("MAIN_ACTIVITY", "Selected number : " + number + " , name : " + name);

                // Find edit view
                final TextView phoneNumberEditView = (TextView) findViewById(R.id.edit_text_phone_number);
                phoneNumberEditView.setText(number);
            }
        }
    }

    private void checkIfBiometricIsSupported() {
        int id = BiometricManager.from(this).canAuthenticate();
        if (id == BiometricManager.BIOMETRIC_SUCCESS) {
            isBiometricEnabled = true;
            isBiometricPresent = true;
        } else if (id == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            Log.d(TAG, "No biometric features available on this device.");
            isBiometricEnabled = false;
            isBiometricPresent = false;
        } else if (id == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
            Log.d(TAG, "Biometric features are currently unavailable.");
            isBiometricEnabled = false;
            isBiometricPresent = false;
        } else if (id == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            Log.d(TAG, "he user hasn't associated any biometric credentials with their account.");
            isBiometricEnabled = false;
            isBiometricPresent = true;
        }
    }
    
    private void authenticatePasscode(){
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (km.isKeyguardSecure()) {
            Intent authIntent = km.createConfirmDeviceCredentialIntent("Confirm", "Confirm Passcode");
            startActivityForResult(authIntent, INTENT_AUTHENTICATE);
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        biometricAuthError();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        biometricAuthSuccessful();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        biometricAuthFailed();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Validation")
                .setSubtitle("This is is to validate using your biometric to check whether you are valid user to see the screen content")
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    private void biometricAuthError() {
        final TextView biometricText = (TextView) findViewById(R.id.biometric_status);
        Log.e(TAG, "onAuthenticationError Callback");
        biometricText.setText("Biometric Cancelled");
    }

    private void biometricAuthSuccessful() {
        final TextView biometricText = (TextView) findViewById(R.id.biometric_status);
        Log.d(TAG, "onAuthenticationSucceeded Callback");
        biometricText.setText("Biometric Validated Successfully");
    }

    private void biometricAuthFailed() {
        final TextView biometricText = (TextView) findViewById(R.id.biometric_status);
        Log.e(TAG, "onAuthenticationFailed Callback");
        biometricText.setText("Biometric Auth Failed");
    }
}
