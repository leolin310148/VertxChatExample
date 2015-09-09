package me.leolin.server

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author leolin
 */

fun main(args: Array<String>) {
    val VERTX = Vertx.vertx()
    VERTX.deployVerticle(TcpServerVeticle())
}


class TcpServerVeticle : AbstractVerticle() {
    val atomicInteger = AtomicInteger(0)
    val users = Collections.synchronizedMap(hashMapOf<Int, NetSocket>())
    override fun start() {

        val server = vertx.createNetServer()

        server.connectHandler { socket ->
            val userId = atomicInteger.incrementAndGet()
            users.forEach {
                it.getValue().write(Buffer.buffer().appendString("User : $userId connected"))
            }

            users.put(userId, socket)


            socket.handler { buffer ->
                val message = buffer.getString(0, buffer.length())
                users.filter { it.getKey() != userId }.forEach {
                    it.getValue().write(Buffer.buffer().appendString("$userId : $message"))
                }
            }

            socket.closeHandler {
                users.remove(userId)
                users.forEach {
                    it.getValue().write(Buffer.buffer().appendString("User : $userId left"))
                }
            }

            socket.write(Buffer.buffer().appendString("Your are login as id : $userId"))
        }

        server.listen(2000)
    }
}
