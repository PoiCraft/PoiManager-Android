package github.poicraft.poimanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import github.gggxbbb.Http
import github.poicraft.poimanager.addpter.LogAdapter
import github.poicraft.poimanager.addpter.MLog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var wsClient: WebSocket
    private lateinit var adapter: LogAdapter

    private fun connect(){
        try {
            wsClient.close(101, ' '.toString())
        }catch (e: Exception){ }
        wsClient = Http.ws(
            host = "${server_address.text}/api/ws/cmd",
            listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    webSocket.send("{\"token\":\"${server_token.text}\"}")
                    wsClient = webSocket
                    super.onOpen(webSocket, response)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    val msg = JSONObject(text)
                    if (msg.getString("type")=="auth" && msg.getInt("code")==200){
                        val edit = getSharedPreferences("server", Context.MODE_PRIVATE).edit()
                        edit.putString("address", server_address.text.toString())
                        edit.putString("token", server_token.text.toString())
                        edit.apply()
                        Http.get("http://${server_address.text}/api/log/all?token=${server_token.text}",{ _, response ->
                            val logs = JSONObject(response.body!!.string()).getJSONArray("content")
                            for (i in 0 until logs.length()){
                                val log = logs.getJSONObject(i)
                                runOnUiThread {
                                    adapter.addLog(
                                        MLog(
                                            log_msg = log.getString("log"),
                                            log_type = log.getString("type")
                                        )
                                    )
                                }
                            }
                            runOnUiThread {
                                fab.hide()
                                cmd_send.visibility = View.VISIBLE
                                log_out.scrollToPosition(adapter.itemCount - 1)
                            }
                        })
                    }
                    runOnUiThread {
                        adapter.addLog(
                            MLog(
                                log_msg = msg.getString("msg"),
                                log_type = msg.getString("type")
                            )
                        )
                        log_out.scrollToPosition(adapter.itemCount - 1)
                    }

                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("ws", "stop")
                    runOnUiThread {
                        fab.show()
                        cmd_send.visibility = View.GONE
                    }
                    super.onClosed(webSocket, code, reason)
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("ws", "stop2")
                    runOnUiThread {
                        fab.show()
                        cmd_send.visibility = View.GONE
                    }
                    super.onClosing(webSocket, code, reason)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    runOnUiThread {
                        adapter.addLog(
                            MLog(
                                log_type = "ws",
                                log_msg = t.message.toString()
                            )
                        )
                        log_out.scrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val recManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        log_out.layoutManager = recManager

        adapter = LogAdapter()
        log_out.adapter = adapter

        val sp = getSharedPreferences("server", Context.MODE_PRIVATE)
        server_address.setText(sp.getString("address", ""))
        server_token.setText(sp.getString("token", ""))

        if (server_address.text.toString() != "" && server_token.text.toString() != ""){
            connect()
        }

        fab.setOnClickListener {
            Log.d("main", "clicked")
            connect()
        }

        cmd_send.setOnClickListener {
            if (cmd_in.text.isNotEmpty()){
                wsClient.send(cmd_in.text.toString())
                cmd_in.text.clear()
            }
        }
        }
    }
