package com.example.mp3player;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MP3Controller implements Initializable {

    private File musicDirectory;
    private File[] filesMusic;

    private int speedRate;

    private Media media;
    private MediaPlayer mediaPlayer;
    private ArrayList<File> playlist;
    private int songNumber = 0;
    private double[] speed = {0.5, 1, 1.5, 2};
    //private Media media;
    private Timer timer;
    private TimerTask timerTask;
    private boolean run;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label songTitleLabel;
    @FXML
    private ProgressBar songTimeBar;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Button resetButton, pauseButton, playButton, nextButton, previousButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playlist = new ArrayList<File>();
        musicDirectory = new File("src/MusicFolder");
        filesMusic = musicDirectory.listFiles();
        if(filesMusic != null) {
            for(File file : filesMusic) {
                playlist.add(file);
                System.out.println(file);
            }
        }
        songTitleLabel.setText(playlist.get(songNumber).getName());
        for(int i = 0; i < speed.length; i++){
            speedBox.getItems().add(Double.toString(speed[i]));
        }
        speedBox.setOnAction(this::changeSpeedSong);

        media = new Media(playlist.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
    }

    public void resetSong(){
        songTimeBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
        pauseSong();
    }

    public void pauseSong(){
        cancelTimer();
        mediaPlayer.pause();
    }

    public void playSong(){
        startTimer();
        changeSpeedSong(null);
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }

    public void previousSong(){
        if(songNumber > 0){
            songNumber--;
            mediaPlayer.stop();
            if(run){
                cancelTimer();
            }
            media = new Media(playlist.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songTitleLabel.setText(playlist.get(songNumber).getName());
            playSong();
        }
        else {
            songNumber = playlist.size() - 1;
            mediaPlayer.stop();
            if(run){
                cancelTimer();
            }
            media = new Media(playlist.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songTitleLabel.setText(playlist.get(songNumber).getName());
            playSong();
        }
    }

    public void nextSong(){
        if(songNumber < playlist.size() - 1){
            songNumber++;
            mediaPlayer.stop();
            media = new Media(playlist.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songTitleLabel.setText(playlist.get(songNumber).getName());
            playSong();
        }
        else {
            songNumber = 0;
            mediaPlayer.stop();
            media = new Media(playlist.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songTitleLabel.setText(playlist.get(songNumber).getName());
            playSong();
        }

    }

    public void changeSpeedSong(ActionEvent event){
        if(speedBox.getValue() == null) {
            mediaPlayer.setRate(1);
        }
        else {
            mediaPlayer.setRate(Double.parseDouble(speedBox.getValue()));
        }

    }

    public void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                run = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songTimeBar.setProgress(current/end);
                if(current/end == 1) {
                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    public void cancelTimer(){
        run = false;
        timer.cancel();
    }
}