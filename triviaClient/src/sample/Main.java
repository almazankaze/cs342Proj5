package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Main extends Application {


    public void playMusic(String fileLocation) {
        try {
            File musicPath = new File(fileLocation);

            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            }
            else {
                System.out.println("could not find the music file");
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



    public Parent setup() {

        BorderPane mainLayout = new BorderPane();
        Button on = new Button("play");

        mainLayout.setCenter(on);

        on.setOnAction(e->{

            playMusic("kdaSong.wav");


        });

        return mainLayout;
    }






    @Override
    public void start(Stage primaryStage) throws Exception{


        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(setup(), 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
