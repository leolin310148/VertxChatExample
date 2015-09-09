package me.leolin.client

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import java.util.Scanner

/**
 * @author leolin
 */
fun main(args: Array<String>) {
    val VERTX = Vertx.vertx()
    VERTX.deployVerticle(ClientVerticle())
}

class ClientVerticle : AbstractVerticle() {

    override fun start() {

        val client = vertx.createNetClient()

        client.connect(2000, "localhost", { result ->
            val socket = result.result()
            socket.handler { buffer ->
                val message = buffer.getString(0, buffer.length())
                println(message)
            }

            Thread{
                val scanner = Scanner(System.`in`)
                while (scanner.hasNext()) {
                    val message = scanner.next()
                    socket.write(Buffer.buffer().appendString(message))
                }
            }.start()

        })


    }
}