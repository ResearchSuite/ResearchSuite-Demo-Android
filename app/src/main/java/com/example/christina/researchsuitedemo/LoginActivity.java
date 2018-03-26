package com.example.christina.researchsuitedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;

import com.curiosityhealth.ls2sdk.rs.LS2LoginStep;
import com.example.christina.researchsuitedemo.studyManagement.RSFileAccess;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;

import java.util.Date;

/**
 * Created by christinatsangouri on 3/23/18.
 */

public class LoginActivity extends PinCodeActivity {

    public static final int REQUEST_CODE_SIGN_IN  = 31473;

    public static final int REQUEST_CODE_PASSCODE = 41473;

    public static final String LOG_IN_STEP_IDENTIFIER = "login step identifier";
    public static final String LOG_IN_TASK_IDENTIFIER = "login task identifier";
    public static final String PASS_CODE_TASK_IDENTIFIER = "passcode task identifier";

    private Button log_in_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //load login screen here
        super.setContentView(R.layout.activity_onboarding);

        this.log_in_button = (Button) findViewById(R.id.log_in_button);
        this.log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInClicked(view);
            }
        });
    }

    @Override
    public void onDataAuth()
    {
        if(StorageAccess.getInstance().hasPinCode(this))
        {
            super.onDataAuth();
        }
        else // allow onboarding if no pincode
        {
            onDataReady();
        }
    }


    //pass code MUST be set when we launch this
    private void showLogInStep() {

        LS2LoginStep logInStep = new LS2LoginStep(
                LoginActivity.LOG_IN_STEP_IDENTIFIER,
                "Sign In",
                "Please enter your credentials to sign in.",
                null
        );

        OrderedTask task = new OrderedTask(LoginActivity.LOG_IN_TASK_IDENTIFIER, logInStep);
        startActivityForResult(ViewTaskActivity.newIntent(this, task),
                REQUEST_CODE_SIGN_IN);
    }

    public void logInClicked(View view)
    {

        //TODO: Fix for passcode step

        showLogInStep();


    }

    private void skipToMainActivity()
    {
        startMainActivity();
    }

    private void startMainActivity()
    {

        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if(requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

            Boolean isLoggedIn = (Boolean) result.getStepResult(LoginActivity.LOG_IN_STEP_IDENTIFIER).getResult();

            RSFileAccess.getInstance().setSignedInDate(this, new Date());


            if (isLoggedIn) {
                skipToMainActivity();
            }

        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
