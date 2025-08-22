package com.pine.pindroidpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pine.pindroidpp.ui.theme.PinDroidppTheme
import com.pine.pinedroid.db.Model
import com.pine.pinedroid.db.model
import com.pine.pinedroid.db.table
import com.pine.pinedroid.ui.message_box.MessageBox
import com.pine.pinedroid.utils.log
import com.pine.pinedroid.utils.toast
import com.pine.pinedroid.utils.ui.spw

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        table("DrivingQuestionsTable")
            .column(columnName = "id", type = "INTEGER", autoIncrease = true, primaryKey = true)
            .column(columnName = "question", type = "TEXT")
            .column(columnName = "answer", type = "TEXT")
            .createTable()

        val drivingQuestionsTable = model("DrivingQuestionsTable")
        drivingQuestionsTable["question"] = "What is your favorite color?"
        drivingQuestionsTable["answer"] = "What is your favorite color?"
        drivingQuestionsTable.save()

        var results = model("DrivingQuestionsTable").select()
        _root_ide_package_.com.pine.pinedroid.utils.log("results", results)
        results.forEach {
            results.save(it)
        }

        var result = model("DrivingQuestionsTable").find(1)
        log("result", result)
        result["question"] = "update"
        result.save()

        result = model("DrivingQuestionsTable").find(1)
        log("result", result)

        MessageBox.i().setListener { messageBoxChoose -> }.show("test" ,"te")

        enableEdgeToEdge()
        setContent {
            PinDroidppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {


                        Row {
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Row {
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Row {
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Row {
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Row {
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Row {
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        fontSize = 36.spw,
        modifier = modifier.clickable { toast("click") } // 添加点击事件
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PinDroidppTheme {
        Greeting("Android")
    }
}