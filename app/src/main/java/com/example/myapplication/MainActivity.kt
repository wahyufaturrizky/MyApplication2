package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.UserApi
import com.example.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class LoginReqModel(
    var username: String,
    var password: String,
)

data class UserModel(
    var profile: LoginReqModel
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

fun handleLogin(username: String, password: String) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://klikyou-das-api.demo-kota.com/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(UserApi::class.java)
    val data = object {
        val username = username
        val password = password
    }

    val call: Call<LoginReqModel?>? = api.login(data);

    call!!.enqueue(object: Callback<LoginReqModel?> {
        override fun onResponse(call: Call<LoginReqModel?>, response: Response<LoginReqModel?>) {
            Log.d("@Main@", "success!" + response)
            if(response.isSuccessful) {

            }
        }

        override fun onFailure(call: Call<LoginReqModel?>, t: Throwable) {
            Log.e("Main", "Failed mate " + t.message.toString())
        }
    })

}

@Composable
fun Greeting() {

    var username by remember {
        mutableStateOf("superadmin@klikyou.com")
    }
    var password by remember {
        mutableStateOf("KI1k..YOu/?-2024")
    }


    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
            ) {
                Button(onClick = { handleLogin(username, password) },
                    modifier = Modifier
                        .fillMaxWidth(),) {
                    Text(
                        text = "LOGIN",
                    )
                }

            }
        },
    ) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),) {

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                placeholder = {
                    Text(text = "Type username")
                },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = {
                    Text(text = "Type password")
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Face else Icons.Filled.AccountBox
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = {
                    /*TODO*/
                    passwordVisible = !passwordVisible
                    }) {
                        Icon(imageVector = image, contentDescription = description)

                    }

                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting()
    }
}