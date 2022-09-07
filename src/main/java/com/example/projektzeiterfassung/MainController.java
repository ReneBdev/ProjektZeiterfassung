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
    @FXML
    private Label startTimeLabel;

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private LocalTime startTime;
    private int hours = 0,
                minutes = 0;

    @FXML
    private void initialize() {
        // using a Thread to update the Timer each second
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

    /**
     * Controls the button to start the timer
     * switches between start and stop mode
     */
    @FXML
    protected void onStartTimerButton() {
        if (startTime == null) { // Time started
            if (isFormFilled()) {
                disableTextFields(true);
                startTime = LocalTime.now();
                timerButton.setText("Stop Time");
                startTimeLabel.setText("Start Zeitpunkt: "+LocalTime.now().format(timeFormat));
            }
        } else { // Time stopped
            startTime = null;
            disableTextFields(false);
            stoppedTimeLabel.setText(formatTime());
            stoppedTimeLabel.setOpacity(1);
            projectLabel.setText(projectTextField.getText());
            paketLabel.setText(paketTextField.getText());
            employeeLabel.setText(employeeTextField.getText().toUpperCase());
            timerButton.setText("Start Time");
        }
    }

    /**
     * When the final time is clicked, it gets copied to the clipboard
     */
    @FXML
    protected void onFinishTimeClicked() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(
            projectLabel.getText() + "\t\t" +
                paketLabel.getText() + "\t\t" +
                employeeLabel.getText() + "\t\t\t" +
                LocalDate.now().format(dateFormat) + "\t\t\t" +
                formatTime()
        );
        clipboard.setContent(content);
    }

    private void updateTimer() {
        if (startTime != null) {
            hours = LocalTime.now().getHour()-startTime.getHour();
            int time = hours*60 + LocalTime.now().getMinute() - startTime.getMinute();
            minutes = (((time+8) % 60) / 15) * 15;
            timeLabel.setText(formatTime());
        } else {
            timeLabel.setText(LocalTime.now().format(timeFormat));
        }
    }

    /**
     * Checks if TextFields are filled correctly.
     * Also Displays an Error Message.
     */
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
            errorLabel.setText("Bitte Angaben korrigieren.");
            return false;
        }
    }

    /**
     * Controls the Text Fields.
     * @param disable true disables, false enables the TextFields.
     */
    private void disableTextFields(boolean disable) {
            projectTextField.setDisable(disable);
            paketTextField.setDisable(disable);
            employeeTextField.setDisable(disable);
    }

    private String formatTime() {
        return String.format("%02d,%02d",hours, minutes);
    }

}