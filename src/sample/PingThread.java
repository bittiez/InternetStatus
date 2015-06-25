package sample;

import javafx.application.Platform;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by tad on 6/21/2015.
 */
public class PingThread implements Runnable {
    Main main;
    InetAddress inet;
    public boolean running = true;
    public PingThread(Main main) {
        this.main = main;
        try {
//            inet = InetAddress.getByName("8.8.8.8");
            inet = InetAddress.getByName("192.168.1.7");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //do ping stuff
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        if(isWindows) {
            ProcessBuilder processBuilder = new ProcessBuilder("ping", isWindows ? "-n" : "-c", "1", "8.8.8.8");
            Process proc = null;



            while (running) {
                long startTime = System.nanoTime();
                try {
                    proc = processBuilder.start();
                    proc.waitFor(1, TimeUnit.SECONDS);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long endTime = System.nanoTime();

                long duration = (endTime - startTime);
                long duration2 = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS);
                duration2 = duration2 > 999 ? -1 : duration2;

                final long dur = duration2;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        main.addToChart(dur);
                    }
                });
                try {
                    Thread.sleep((1000 - duration2) > -1 ? 1000 - duration2 : 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
