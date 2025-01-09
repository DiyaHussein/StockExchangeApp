package upt.cebp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private Stage primaryStage;
    private Scene loginScene, registrationScene, stockScene;

    // In-memory database to store usernames and passwords
    private Map<String, String> userDatabase = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create login screen and registration screen
        createLoginScreen();
        createRegistrationScreen();

        // Set the initial scene to login
        primaryStage.setTitle("Stock Exchange");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void createLoginScreen() {
        // Username and password fields
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Check credentials in the user database
            if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                // Successful login, switch to stock exchange scene
                createStockScene();
                primaryStage.setScene(stockScene);
            } else {
                // Show error message
                showError("Invalid username or password!");
            }
        });

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            // Switch to registration scene
            primaryStage.setScene(registrationScene);
        });

        // Layout for login screen
        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, registerButton);

        loginScene = new Scene(loginLayout, 300, 250);
    }

    private void createRegistrationScreen() {
        // Username and password fields
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Username or password cannot be empty!");
                return;
            }

            if (userDatabase.containsKey(username)) {
                showError("Username already exists!");
            } else {
                // Save the user in the database
                userDatabase.put(username, password);
                showInfo("Registration successful! Please log in.");
                primaryStage.setScene(loginScene);
            }
        });

        // Back to Login button
        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> primaryStage.setScene(loginScene));

        // Layout for registration screen
        VBox registrationLayout = new VBox(10);
        registrationLayout.setAlignment(Pos.CENTER);
        registrationLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, registerButton, backButton);

        registrationScene = new Scene(registrationLayout, 300, 250);
    }

    private void createStockScene() {
        // Stock data
        String[] stockNames = {"Apple", "Tesla", "Amazon", "Google"};
        double[] stockPrices = {150.25, 700.45, 3200.75, 2800.60};

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        for (int i = 0; i < stockNames.length; i++) {
            final String stockName = stockNames[i];
            final double stockPrice = stockPrices[i];

            Label stockLabel = new Label(stockName + " - $" + stockPrice);
            Button buyButton = new Button("Buy");
            Button sellButton = new Button("Sell");

            buyButton.setOnAction(e -> {
                System.out.println("Buying " + stockName);
            });

            sellButton.setOnAction(e -> {
                System.out.println("Selling " + stockName);
            });

            vbox.getChildren().addAll(stockLabel, buyButton, sellButton);
        }

        stockScene = new Scene(vbox, 400, 400);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
