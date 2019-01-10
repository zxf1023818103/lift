package experiment.lift.gui.controller;

import experiment.lift.*;
import experiment.lift.gui.LiftUpdateTask;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class LiftController {

    public Label upLabel;
    public Label downLabel;
    public Label floorLabel;
    public Rectangle leftDoor;
    public Rectangle rightDoor;
    public Label currentPeopleNumberLabel;
    public Button startButton;
    public Button pauseButton;
    public Timeline openDoorAnimation;
    public volatile Boolean paused = false;
    public boolean init = false;
    public volatile boolean upArrowFlicker = false;
    public volatile boolean downArrowFlicker = false;

    public void start(MouseEvent event) throws FileNotFoundException {
        if (!init) {
            /// 初始化动画
            KeyValue leftDoorCloseWidth = new KeyValue(leftDoor.layoutXProperty(), 150);
            KeyValue leftDoorOpenWidth = new KeyValue(leftDoor.layoutXProperty(), 0);
            KeyValue rightDoorCloseWidth = new KeyValue(rightDoor.layoutXProperty(), 300);
            KeyValue rightDoorOpenWidth = new KeyValue(rightDoor.layoutXProperty(), 450);
            KeyFrame openDoorKeyFrame = new KeyFrame(new Duration(1000), leftDoorOpenWidth, rightDoorOpenWidth);
            KeyFrame closeDoorKeyFrame = new KeyFrame(new Duration(1000), leftDoorCloseWidth, rightDoorCloseWidth);
            openDoorAnimation = new Timeline(openDoorKeyFrame);
            Timeline closeDoorAnimation = new Timeline(closeDoorKeyFrame);
            openDoorAnimation.setOnFinished(e -> closeDoorAnimation.play());
            closeDoorAnimation.setOnFinished(e -> paused = false);
            init = true;
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (upArrowFlicker) {
                        upLabel.setDisable(!upLabel.isDisable());
                    }
                    if (downArrowFlicker) {
                        downLabel.setDisable(!downLabel.isDisable());
                    }
                }
            }, 0, 250);
        }

        if (paused) {
            paused = false;
        }
        else {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new LiftUpdateTask(this), 0, 500);
        }
        startButton.setDisable(true);
        pauseButton.setDisable(false);
    }

    public void flicker(LiftState state) {
        switch (state) {
            case STILL:
                upArrowFlicker = downArrowFlicker = false;
                break;
            case UP:
                upArrowFlicker = true;
                downArrowFlicker = false;
                break;
            case DOWN:
                upArrowFlicker = false;
                downArrowFlicker = true;
                break;
                default: break;
        }
    }

    public void pause(MouseEvent event) {
        pauseButton.setDisable(true);
        startButton.setDisable(false);
        paused = true;
    }

    public void exit(MouseEvent event) {
        System.exit(0);
    }

}
