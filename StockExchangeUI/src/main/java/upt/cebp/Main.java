package upt.cebp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private Scene loginScene, stockScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create the login screen
        createLoginScreen();

        // Set the scene to the login screen
        primaryStage.setTitle("Stock Exchange - Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void createLoginScreen() {
        // Create username and password fields
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        // Create login button
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            // Simple login validation (username = "user" and password = "password")
            if (usernameField.getText().equals("user") && passwordField.getText().equals("password")) {
                // Successfully logged in, switch to stock exchange UI
                createStockScene();
                primaryStage.setScene(stockScene);
            } else {
                // Show error message if login fails
                Label errorLabel = new Label("Invalid username or password!");
                VBox errorBox = new VBox(10, errorLabel);
                errorBox.setAlignment(Pos.CENTER);
                Scene errorScene = new Scene(errorBox, 300, 200);
                primaryStage.setScene(errorScene);
            }
        });

        // Create a layout for the login page
        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        // Create the login scene
        loginScene = new Scene(loginLayout, 300, 200);
    }

    private void createStockScene() {
        // Stock data
        String[] stockNames = {"Apple", "Tesla", "Amazon", "Google"};
        double[] stockPrices = {150.25, 700.45, 3200.75, 2800.60};

        // Create labels and buttons for each stock
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        for (int i = 0; i < stockNames.length; i++) {
            final String stockName = stockNames[i];  // Capture stock name
            final double stockPrice = stockPrices[i];  // Capture stock price

            Label stockLabel = new Label(stockName + " - $" + stockPrice);
            Button buyButton = new Button("Buy");
            Button sellButton = new Button("Sell");

            buyButton.setOnAction(e -> {
                System.out.println("Buying " + stockName);
                // Add your buy functionality here
            });

            sellButton.setOnAction(e -> {
                System.out.println("Selling " + stockName);
                // Add your sell functionality here
            });

            vbox.getChildren().addAll(stockLabel, buyButton, sellButton);
        }

        // Create a scene with the stock info
        stockScene = new Scene(vbox, 400, 400);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
