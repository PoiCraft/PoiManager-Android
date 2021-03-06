@file:Suppress("unused")

package github.gggxbbb

import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class Http {
    companion object{
        private var cookieStore: HashMap<String, List<Cookie>> = HashMap()

        private val Client = OkHttpClient.Builder()
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val cookies = cookieStore[url.host]
                    return cookies ?: ArrayList()
                }
            })
            .pingInterval(40, TimeUnit.SECONDS)
            .build()

        fun post(url: String, body:Any, success: (Call, Response) -> Unit, error:(Call, IOException) -> Unit = {_,_ ->}){
            val request = Request.Builder()
                .url(url)
                .post(body as RequestBody)
                .build()
            val call = Client.newCall(request)
            call.enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {
                    error(call, e)
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    success(call, response)
                }

            })
        }

        fun get(url: String, success: (Call, Response) -> Unit, error: (Call, IOException) -> Unit = { _, _ ->}) {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            val call = Client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    error(call,e)
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    success(call,response)
                }
            })
        }

        fun ws(host:String, listener: WebSocketListener): WebSocket{
            val request = Request.Builder()
                .url("ws://${host}")
                .build()
            return Client.newWebSocket(request, listener)
        }

    }
}