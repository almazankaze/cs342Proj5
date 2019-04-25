package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
public class Main extends Application {

    BorderPane mainLayout, gameLayout;
    Image lobbyBackground, gameBackground;
    Button connect, exit;
    Button[] answerButton;
    HBox lobbyButtons, gameButtons;
    Stage stage;
    Scene scene1, scene2;


    public Parent setupGUI() {

        //lobby background setup
        lobbyBackground = new Image("triviaLobby.png");
        BackgroundImage backGround1 = new BackgroundImage(lobbyBackground,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        //game screen  background setup
        gameBackground = new Image("triviaGameScreen.png");
        BackgroundImage backGround2 = new BackgroundImage(gameBackground,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        //connect button setup
        connect = new Button("connect");
        connect.setMinSize(135, 60);
        connect.getStylesheets().add("lobbyButtons.css");

        //exit button setup
        exit = new Button("exit");
        exit.setMinSize(135, 60);
        exit.getStylesheets().add("lobbyButtons.css");
        lobbyButtons = new HBox(10);
        lobbyButtons.getChildren().addAll(connect,exit);


        //answer buttons setup
        answerButton = new Button[4];
        gameButtons = new HBox(80);
        for(int i=0; i<4; i++) {
            answerButton[i] = new Button();
            answerButton[i].setMinSize(125, 45);
            answerButton[i].getStylesheets().add("lobbyButtons.css");
            gameButtons.getChildren().add(answerButton[i]);
        }
        answerButton[0].setText("A");
        answerButton[1].setText("B");
        answerButton[2].setText("C");
        answerButton[3].setText("D");

        // adding elements to lobby layout
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(25));
        mainLayout.setBottom(lobbyButtons);
        mainLayout.setBackground(new Background(backGround1));


        //adding elements to the game layout
        gameLayout = new BorderPane();
        gameLayout.setPadding(new Insets(25));
        gameLayout.setBottom(gameButtons);
        gameLayout.setBackground(new Background(backGround2));

        //scene2 setup
        scene2 = new Scene(gameLayout, 790, 440);

        // event handlers for lobby buttons
        connect.setOnAction(e -> {
            stage.setScene(scene2);
        });
        return mainLayout;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        stage.setTitle("Trivia");
        scene1 = new Scene(setupGUI(), 795, 445);
        stage.setScene(scene1);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
