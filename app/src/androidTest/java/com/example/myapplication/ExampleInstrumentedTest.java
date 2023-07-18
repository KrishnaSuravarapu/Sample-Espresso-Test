package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);
    private static final String PHONE_NUMBER = "1 234-567-890";
    private static final String DIALER_PACKAGE_NAME = "com.google.android.dialer";

    @Before
    public void afterActivityLaunched() {
        Intents.init();
    }

    @After
    public void afterActivityFinished() {
        Intents.release();
    }

    @Test
    public void stubIntentTest() {
        // Stub intent
        Intent intent = new Intent();
        intent.setData(Uri.parse("content://com.android.contacts/data/1"));
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
        intending(hasAction("android.intent.action.PICK")).respondWith(result);
//        intending(toPackage("com.android.contacts")).respondWith(result);

        // find the button and perform call action
        onView(withId(R.id.call_contact_button)).perform(click());

        onView(withId(R.id.button)).perform(click());
    }

    @Test
    public void validateIntentTest() {
        onView(withId(R.id.edit_text_phone_number))
                .perform(typeText(PHONE_NUMBER), closeSoftKeyboard());
        onView(withId(R.id.button)) .perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_DIAL),
                hasData("tel:" + PHONE_NUMBER),
                toPackage(DIALER_PACKAGE_NAME)));
    }

    @Test
    public void validateBiometricViaRunOnUIThread() {
        // Not possible if private method. Here we are only setting the text since it is publically available.
        // It internally the onauthsuccessful calls a private method, this wont be possible
        MainActivity act = mActivityRule.getActivity();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button biometricAuth = (Button) act.findViewById(R.id.biometric_auth);
                biometricAuth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final TextView biometricStatusText = (TextView) act.findViewById(R.id.biometric_status);
                        biometricStatusText.setText("Mocked Authentication Successful");
                    }
                });
            }
        });
        onView(withId(R.id.biometric_auth)) .perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateBiometricSuccessfulViaRunOnUIThreadwithPrivateMethod() {
        // If Biometric Authentication happens and they abstract the on successful method to a private one,
        // We can use Reflection to call the method and do the needful
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainActivity act = mActivityRule.getActivity();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Method method = null;
                try {
                    method = MainActivity.class.getDeclaredMethod("biometricAuthSuccessful");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                method.setAccessible(true);
                try {
                    method.invoke(act);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateBiometricErroredViaRunOnUIThreadwithPrivateMethod() {
        // If Biometric Authentication happens and they abstract the on successful method to a private one,
        // We can use Reflection to call the method and do the needful
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainActivity act = mActivityRule.getActivity();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Method method = null;
                try {
                    method = MainActivity.class.getDeclaredMethod("biometricAuthError");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                method.setAccessible(true);
                try {
                    method.invoke(act);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateBiometricFailedViaRunOnUIThreadwithPrivateMethod() {
        // If Biometric Authentication happens and they abstract the on successful method to a private one,
        // We can use Reflection to call the method and do the needful
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainActivity act = mActivityRule.getActivity();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Method method = null;
                try {
                    method = MainActivity.class.getDeclaredMethod("biometricAuthFailed");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                method.setAccessible(true);
                try {
                    method.invoke(act);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateBiometricViaIntendedFunction() {
        // Not working as it is not an intent call
        onView(withId(R.id.biometric_auth)).perform(click());
        intended(toPackage("android.hardware.biometrics.BiometricPrompt"));
    }

    @Test
    public void validateBiometricAfterPatching() {
        // Not working as it is not an intent call
        onView(withId(R.id.biometric_auth)) .perform(click());
        onView(withText(containsString("Pass"))).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}