package org.bravo.garden;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Volker
 */
public class Garden {

    public static void main(String args[]) throws InterruptedException {

        final Logger log = LogManager.getLogger(Garden.class);
        log.info("Garden Controller ... started.");

        final FloodDetection floodDetection = new FloodDetection();
        floodDetection.start();

        // keep program running until user aborts (CTRL-C)
        //noinspection InfiniteLoopStatement
        for (;;) {
            Thread.sleep(2000);
        }

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller
    }
}
