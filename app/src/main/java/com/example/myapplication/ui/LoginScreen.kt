/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myapplication.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.MyApplicationScreen
import com.example.myapplication.api.UserApi
import com.example.myapplication.ui.theme.MyApplicationTheme
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileOutputStream

data class LoginReqModel(
    var email: String,
    var password: String,
)

fun saveToInternalStorage(data: String, context: Context) {
    val fos: FileOutputStream =
        context.openFileOutput("userProfile.txt", Context.MODE_PRIVATE)
    fos.write(data.toByteArray())
    fos.flush()
    fos.close()
}

fun handleLogin(email: String, password: String, setIsLoading: (Boolean) -> Unit, setResDataLogin: (String) -> Unit, navController: NavHostController) {
    setIsLoading(true)

    val interceptor = HttpLoggingInterceptor()
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://klikyou-das-api.demo-kota.com/api/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(UserApi::class.java)
    val data = LoginReqModel(email, password)

    val call: Call<Any?>? = api.login(data)

    call!!.enqueue(object: Callback<Any?> {
        override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
            Log.i("@response", response.body().toString())
            setResDataLogin(response.body().toString())
            setIsLoading(false)
            navController.navigate(MyApplicationScreen.Home.name)

        }

        override fun onFailure(call: Call<Any?>, t: Throwable) {
            setIsLoading(false)
            Log.e("Main", "Failed mate " + t.message.toString())
        }
    })

}

@Composable
fun LoginScreen(
    context: Context,
    navController: NavHostController,
    ) {

    var email by remember {
        mutableStateOf("superadmin@klikyou.com")
    }
    var password by remember {
        mutableStateOf("Kl1k..Y0u/?-2024")
    }

    val (isLoading, setIsLoading) = remember { mutableStateOf(false) }
    val (resDataLogin, setResDataLogin) = remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    if(resDataLogin.isNotEmpty()) {
        saveToInternalStorage(resDataLogin, context)
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Column {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("email") },
                placeholder = {
                    Text(text = "Type email")
                },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
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

        Column {
            Button(
                onClick = { handleLogin(email, password, setIsLoading, setResDataLogin, navController)},
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
            ) {
                Text(
                    text = if(isLoading) "LOADING..." else "LOGIN",
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    MyApplicationTheme {
        LoginScreen(
            context = LocalContext.current,
            navController = rememberNavController()
        )
    }
}