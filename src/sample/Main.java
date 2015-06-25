package sample;

import javafx.application.Application;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static sample.AnchorTools.bottomleft;
import static sample.AnchorTools.right;
import static sample.AnchorTools.topleft;

public class Main extends Application {
    public Label status;
    public Label avgPing, successfulPing, failedPing, percentSuccessful;
    public XYChart.Series series;
    public ArrayList<XYChart.Data> chart;
    public static int chartLimit = 20;
    public int curNum = 0;
    public NumberAxis xAxis;
    public NumberAxis yAxis;
    public float successful = 0, failed = 0;
    DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                           @Override
                                           public void handle(WindowEvent e) {
                                               Platform.exit();
                                               System.exit(0);
                                           }
                                       });

        chart = new ArrayList<XYChart.Data>();

        primaryStage.setTitle("Internet Status");
        AnchorPane mainUI = new AnchorPane();
        Parent root = mainUI;

        mainUI.getChildren().add(bottomleft(new Label("Internet Status: "), 0, 0));
        status = new Label("Unknown");
        bottomleft(status, 0, 95);
        mainUI.getChildren().add(status);
        avgPing = new Label("-1");
        mainUI.getChildren().add(bottomleft(new Label("Average Ping: "), 20, 0));
        mainUI.getChildren().add(bottomleft(avgPing,20, 95));
        successfulPing = new Label(String.valueOf(successful));
        failedPing = new Label(String.valueOf(failed));
        percentSuccessful = new Label("Successful Ping %: ~");
        bottomleft(successfulPing, 60, 95);
        bottomleft(failedPing, 40, 95);
        bottomleft(percentSuccessful, 80, 0);

        mainUI.getChildren().add(bottomleft(new Label("Successful Pings: "), 60, 0));
        mainUI.getChildren().add(bottomleft(new Label("Failed Pings: "), 40, 0));
        mainUI.getChildren().add(successfulPing);
        mainUI.getChildren().add(failedPing);
        mainUI.getChildren().add(percentSuccessful);

        xAxis = new NumberAxis(0, 20, 1);
        yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        //xAxis.setLabel("Round Trip");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("Ping Round Trip Time(ms)");
        //defining a series
        series = new XYChart.Series();
        lineChart.setAnimated(false);
        series.setName("Ping Status");

        Thread ping = new Thread(new PingThread(this));
        ping.start();

        lineChart.getData().add(series);
        topleft(lineChart, 0, 0);
        right(lineChart, 0);
        mainUI.getChildren().add(lineChart);

        primaryStage.getIcons().add(new Image("InternetStatusIcon.png"));

        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }

    public void addToChart(double ping){
        chart.add(new XYChart.Data(curNum, ping));
        while(chart.size() > chartLimit)
            chart.remove(0);
        curNum++;

        //series = new XYChart.Series();
        series.getData().clear();

//        for (int i = 0; i < series.getData().size(); i++) {
//            series.getData().remove(i);
//        }

        series.getData().addAll(chart);
        xAxis.setLowerBound(curNum > 20 ? curNum - 20 : 0);
        xAxis.setUpperBound(curNum > 20 ? curNum - 1 : 20);

        status.setText(ping > -1 ? "Online" : "Offline");

        int avg = 0, tot = 0;
        for(XYChart.Data p : chart){
            tot += Double.parseDouble(p.getYValue().toString());
        }
        avg = tot / chart.size();
        avgPing.setText(String.valueOf(avg));

        if(ping > -1) {
            successful++;
            successfulPing.setText(String.valueOf((int)successful));
        }
        else {
            failed++;
            failedPing.setText(String.valueOf((int)failed));
        }

        if(failed > 0)
            percentSuccessful.setText("Successful Ping Percent: " + df.format((successful / (failed + successful)) * 100) + "%");
        else
            percentSuccessful.setText("Successful Ping Percent: 100%");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
