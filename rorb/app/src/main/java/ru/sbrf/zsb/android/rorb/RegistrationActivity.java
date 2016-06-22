package ru.sbrf.zsb.android.rorb;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import ru.sbrf.zsb.android.helper.Utils;

/**
 * Created by Администратор on 17.06.2016.
 */
public class RegistrationActivity extends Activity {

    public final static String REG_EXP_SBERBANK_EMAIL = "^\\w+([+-.']\\w+)*@sberbank\\.ru";
    public final static String REG_EXP_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\S]{8,}$";

    private  String email;
    private  String password;
    private  String confirmPassword;

    private  EditText emailInput;
    private  EditText passwordInput;
    private  EditText confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button regSubmit = (Button) findViewById(R.id.btnRegistrationSubmit);
        regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitInputValue();
                if(isValidRegistrationData()){
                    Toast.makeText(RegistrationActivity.this,"Registration is Succesed!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void InitInputValue(){
        emailInput = (EditText) findViewById(R.id.txtEmail);
        passwordInput = (EditText) findViewById(R.id.txtPassword);
        confirmPasswordInput = (EditText) findViewById(R.id.txtConfirmPassword);

        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        confirmPassword = confirmPasswordInput.getText().toString();
    }

    private boolean isValidRegistrationData(){
        boolean result = false;
        if(email == null || email.trim().isEmpty()){
            emailInput.setError("Поле Email не может быть пустым!");
            return result;
        }
        if(password == null || password.trim().isEmpty()){
            passwordInput.setError("Пароль не может быть пустым!");
            return result;
        }
        if(confirmPassword == null || confirmPassword.trim().isEmpty()){
            confirmPasswordInput.setError("Подтверждение пароля не может быть пустым!");
            return result;
        }
        if (!isValidRegExEmail()){
            emailInput.setError("Неверный Email! Необходимо указать Email @sberbank.ru");
            return result;
        }

        if (!isValidRegExPassword()){
            passwordInput.setError("Неверный формат пароля!");
            return result;
        }

        if (!Objects.equals(password, confirmPassword)){
            confirmPasswordInput.setError("Пароль и его подтверждение должны совпадать.");
            return result;
        }

        return !result;
    }

    private boolean isValidRegExEmail(){
        return Utils.regExMatches(email, REG_EXP_SBERBANK_EMAIL);
    }
    private boolean isValidRegExPassword(){
        return Utils.regExMatches(password, REG_EXP_PASSWORD);
    }
}
