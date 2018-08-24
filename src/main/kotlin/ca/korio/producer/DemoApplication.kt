package ca.korio.producer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink


@SpringBootApplication
@EnableBinding(Sink::class)
class LoggingConsumerApplication {

    @StreamListener(Sink.INPUT)
    fun handle(person: Person) {
        println("Received: $person")
    }

/*    class Person {
        var name: String? = null
        override fun toString(): String? {
            return this.name
        }
    }*/

    data class Person(
            var name: String? = null
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(LoggingConsumerApplication::class.java, *args)
        }
    }
}