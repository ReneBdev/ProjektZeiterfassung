package com.example.projektzeiterfassung;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class MainController {
    @FXML
    private Label errorLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label stoppedTimeLabel;
    @FXML
    private Button timerButton;
    @FXML
    private TextField projectTextField;
    @FXML
    private TextField paketTextField;
    @FXML
    private TextField employeeTextField;
    @FXML
    private Label projectLabel;
    @FXML
    private Label paketLabel;
    @FXML
    private Label employeeLabel;

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private LocalTime startTime;
    private int hours = 0,
                minutes = 0;

    @FXML
    private void initialize() {
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = () -> updateTimer();

                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Update Thread interrupted.");
                    }
                    Platform.runLater(updater);
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    @FXML
    protected void onStartTimerButton() {
        if (startTime == null) {
            if (isFormFilled()) {
                disableTextFields(true);
                startTime = LocalTime.now();
                timerButton.setText("Stop Time");
            }
        } else {
            startTime = null;
            disableTextFields(false);
            stoppedTimeLabel.setText(finalTime());
            stoppedTimeLabel.setOpacity(1);
            projectLabel.setText(projectTextField.getText());
            paketLabel.setText(paketTextField.getText());
            employeeLabel.setText(employeeTextField.getText());
            timerButton.setText("Start Time");
        }
    }

    @FXML
    protected void onFinishTimeClicked() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(
            projectLabel.getText() + "\t\t" +
                paketLabel.getText() + "\t\t" +
                employeeLabel.getText() + "\t\t\t" +
                LocalDate.now().format(dateFormat) + "\t\t\t" +
                finalTime()
        );
        clipboard.setContent(content);
    }

    private void updateTimer() {
        if (startTime != null) {
            hours = LocalTime.now().getHour()-startTime.getHour();
            int time = hours*60 + LocalTime.now().getMinute() - startTime.getMinute();
            minutes = ((time % 60) / 15) * 15;
            timeLabel.setText(finalTime());
        } else {
            timeLabel.setText(LocalTime.now().format(timeFormat));
        }
    }

    private boolean isFormFilled() {
        if (projectTextField.getText().matches("\\d{4}")
                && paketTextField.getText().matches("\\w+")
                && employeeTextField.getText().length() == 3
                && employeeTextField.getText().matches("[A-z]{3}")
        ) {
            errorLabel.setOpacity(0);
            return true;
        } else {
            errorLabel.setOpacity(1);
            return false;
        }
    }

    private void disableTextFields(boolean disable) {
            projectTextField.setDisable(disable);
            paketTextField.setDisable(disable);
            employeeTextField.setDisable(disable);
    }

    private String finalTime() {
        return String.format("%02d,%02d",hours, minutes);
    }


}