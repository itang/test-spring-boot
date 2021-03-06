package demo.dev

import demo.ext.Logable
import redis.embedded.RedisServer

import demo.util

trait MockRedisServerable {
  def start(): Unit

  def stop(): Unit
}

class MockRedisServer(val port: Int) extends MockRedisServerable with Logable {

  val redisServer = new RedisServer(port)

  override def start(): Unit =
    if (!util.availablePort(port)) {
      logger.warn("端口{}已经被占用！", port)
    } else if (!redisServer.isActive) {
      redisServer.start()
      logger.info("Mock Redis start")

      Runtime.getRuntime.addShutdownHook(new Thread() {
        override def run(): Unit = {
          if (redisServer.isActive) {
            logger.info("Stop mock Redis Server")
            redisServer.stop()
          }
        }
      })
    }

  override def stop(): Unit =
    if (redisServer.isActive) {
      redisServer.stop()
      logger.info("Mock Redis stop")
    }
}
