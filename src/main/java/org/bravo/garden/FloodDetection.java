package org.bravo.garden;


import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class FloodDetection {

    private final static Logger log = LogManager.getLogger(FloodDetection.class);

    private Notification notification;
    private Config config;

    public void start() {
        config = new Config();
        notification = new Notification(config.loadConfig());
        notification.sendNotification("Gartensteuerung gestartet", "Die Gartensteuerung wurde um "+new Date()+" gestartet.");

        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput pegelSchalter = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

        // create and register gpio pin listener
        pegelSchalter.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState().isHigh()) {
                    notification.sendNotification("HOCHWASSER!!!", "Pegelstand im Brunnen hat um "+new Date()+" den Maximalstand erreicht!");
                    log.info("ACHTUNG Hochwasser! Pegelschalter" + event.getPin() + " meldet " + event.getState());
                }
                else {
                    notification.sendNotification("Hochwasser Entwarnung", "Pegelstand im Brunnen hat um "+new Date()+" wieder Normalstand erreicht!");
                    log.info("Hochwasser Entwarnung! Pegelschalter" + event.getPin() + " meldet " + event.getState());
                }
            }
        });

    }
}
