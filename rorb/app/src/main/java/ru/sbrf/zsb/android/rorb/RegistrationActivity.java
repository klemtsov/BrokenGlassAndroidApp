package ru.sbrf.zsb.android.rorb;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

import ru.sbrf.zsb.android.exceptions.UserInsertDbException;
import ru.sbrf.zsb.android.exceptions.UserRegistrationException;
import ru.sbrf.zsb.android.helper.Utils;

/**
 * Created by Администратор on 17.06.2016.
 */
public class RegistrationActivity extends Activity implements AsyncHandleResult  {

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
                    Boolean result = false;
                    UserRegistrationModel userData = new UserRegistrationModel();
                    userData.setEmail(email);
                    userData.setPassword(password);
                    userData.setConfirmPassword(password);
                    AsyncTask<UserRegistrationModel, Void, Void> task =  new RegistrationTask().execute(userData);
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

    @Override
    public void onReciveResponseHandler(Object value) {
            if ((Boolean) value) {
                Intent intent = new Intent(RegistrationActivity.this, InfoPopupActivity.class);
                intent.putExtra("TEXT_INFO_MESSAGE", "Регистрация прошла успешно! " +
                        "На указанный Email было направлено письмо для подверждение вашего Email. " +
                        "Перед началом работы с приложением вам необходимо подтвердить Email.");
                startActivity(intent);
            }

    }

    public class RegistrationTask extends AsyncTask<UserRegistrationModel, Void, Void> {
        String textToast = null;
        Boolean result = null;
        AsyncHandleResult delegateResult = null;
        @Override
        protected Void doInBackground(UserRegistrationModel... params) {
            UserContext userContext = UserContext.getCurrentUserContext(RegistrationActivity.this);
            textToast = null;
            result = null;
                try {
                    userContext.RegistrationUser(params[0]);
                    result = true;
                }
                catch (Exception e){
                    textToast = "Ошибка регистрации! На сервере возникли проблемы!";
                }
            result = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(textToast != null){
                Toast.makeText(RegistrationActivity.this, textToast, Toast.LENGTH_LONG).show();
            }
            delegateResult.onReciveResponseHandler(result);
        }
    }
}
