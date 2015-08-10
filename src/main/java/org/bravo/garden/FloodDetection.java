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
    private boolean state;
    private boolean flooding;

    public void start() {
        config = new Config();
        notification = new Notification(config.loadConfig());
        notification.sendNotification("Gartensteuerung gestartet", "Die Gartensteuerung wurde um "+new Date()+" gestartet.");
        flooding = false;

        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput pegelSchalter = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

        // create and register gpio pin listener
        pegelSchalter.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(final GpioPinDigitalStateChangeEvent event) {
                if (event.getState().isHigh()) {
                    state = true;
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    if (state == true) { // if state is still high after 5 seconds, send notification
                                        notification.sendNotification("HOCHWASSER!!!", "Pegelstand im Brunnen hat um " + new Date() + " den Maximalstand erreicht!");
                                        log.info("ACHTUNG Hochwasser! Pegelschalter" + event.getPin() + " meldet " + event.getState());
                                        flooding = true;
                                    }
                                    else {
                                        log.info("Fehlalarm.");
                                        flooding = false;
                                    }
                                }
                            },
                            5000
                    );
                }
                else {
                    state = false;
                    if (flooding == true) {
                        notification.sendNotification("Hochwasser Entwarnung", "Pegelstand im Brunnen hat um " + new Date() + " wieder Normalstand erreicht!");
                        log.info("Hochwasser Entwarnung! Pegelschalter" + event.getPin() + " meldet " + event.getState());
                        flooding = false;
                    }
                }
            }
        });

    }
}
