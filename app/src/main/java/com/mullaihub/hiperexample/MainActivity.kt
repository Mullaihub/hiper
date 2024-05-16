package com.mullaihub.hiperexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mullaihub.hiper.http.Hiper
import com.mullaihub.hiper.util.*
import com.mullaihub.hiperexample.ui.theme.HiperExampleTheme
import java.net.InetSocketAddress
import java.net.Proxy

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Hiper.getInstance().async().get(
            url = "https://httpbin.org/ip",
        ) { response ->
            Log.d("hello", "Response: ${response.text}")
        }


        setContent {
            HiperExampleTheme {
                val context = LocalContext.current
                val stateDB = remember { StateDB(context, name="test_store") }
                val textState by stateDB.getString("name").collectAsState(initial = "hello, world")
                val launcher = rememberActivityResult {
                    context.toast("Permission: $it")
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box {
                        Column {
                            Text(text = textState, style = MaterialTheme.typography.h5)
                            Button(onClick = {
                                async {
                                    stateDB.put("name", "jeeva : ${(0..10).random()}")
                                }
                            }) {
                                Text(text = "Click me!")
                            }
                            debug("hello")

                            Button(onClick = {
                                launcher.requestPermission(android.Manifest.permission.CAMERA)
                            }) {
                                Text(text = "Request permission")
                            }

                            Greeting(name = "Android")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    LaunchedEffect(true) {
        debug("this is amazing...")
    }

    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HiperExampleTheme {
        Greeting("Android")
    }
}