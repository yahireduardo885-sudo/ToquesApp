package com.example.touchgame;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView challengeTextView, scoreTextView, instructionsTextView;
    private Button startButton;
    private View mainLayout;

    private int screenWidth;
    private int screenHeight;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int currentChallenge;
    private boolean gameActive = false;
    private final Random random = new Random();
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Vinculación de Vistas
        challengeTextView = findViewById(R.id.challengeTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);
        startButton = findViewById(R.id.startButton);
        mainLayout = findViewById(android.R.id.content);

        // 2. Obtener dimensiones de la pantalla (RF01)
        Rect boundsScreen = getWindowManager().getCurrentWindowMetrics().getBounds();
        screenHeight = boundsScreen.height();
        screenWidth = boundsScreen.width();

        // 3. Configurar el detector de gestos para el doble toque (RF12)
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (gameActive) {
                    endGame();
                    return true;
                }
                return false;
            }
        });

        // 4. Configurar el botón de inicio
        startButton.setOnClickListener(v -> startGame());
    }

    private void startGame() {
        gameActive = true;
        correctAnswers = 0;
        wrongAnswers = 0;

        // Ocultar vistas de inicio y mostrar las del juego
        startButton.setVisibility(View.INVISIBLE);
        instructionsTextView.setVisibility(View.INVISIBLE);
        challengeTextView.setVisibility(View.VISIBLE);
        updateScore();
        generateNewChallenge();
    }

    private void endGame() {
        gameActive = false;
        Toast.makeText(this, "Juego finalizado. Puntuación final: " + correctAnswers + " aciertos.", Toast.LENGTH_LONG).show();

        // Restaurar la vista inicial
        startButton.setVisibility(View.VISIBLE);
        instructionsTextView.setVisibility(View.VISIBLE);
        challengeTextView.setVisibility(View.INVISIBLE);
        scoreTextView.setText("Aciertos: 0 - Errores: 0");
    }

    // 5. Manejo del evento de toque en la pantalla (RF06)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameActive) {
            return super.onTouchEvent(event);
        }

        // Pasar el evento al detector de gestos
        gestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int touchedQuadrant = 0;
            if (x < screenWidth / 2 && y < screenHeight / 2) {
                touchedQuadrant = 1; // Arriba-izquierda
            } else if (x > screenWidth / 2 && y < screenHeight / 2) {
                touchedQuadrant = 2; // Arriba-derecha
            } else if (x < screenWidth / 2 && y > screenHeight / 2) {
                touchedQuadrant = 3; // Abajo-izquierda
            } else if (x > screenWidth / 2 && y > screenHeight / 2) {
                touchedQuadrant = 4; // Abajo-derecha
            }

            validateAnswer(touchedQuadrant);
        }
        return super.onTouchEvent(event);
    }

    // 6. Validación de la respuesta (RF07, RF08)
    private void validateAnswer(int quadrant) {
        if (quadrant == currentChallenge) {
            correctAnswers++;
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
            flashFeedback(Color.GREEN);
        } else {
            wrongAnswers++;
            Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show();
            flashFeedback(Color.RED);
        }
        updateScore();
        generateNewChallenge(); // (RF11)
    }

    // 7. Generación de número aleatorio (RF03, RF04)
    private void generateNewChallenge() {
        currentChallenge = random.nextInt(4) + 1; // Números del 1 al 4
        challengeTextView.setText(String.valueOf(currentChallenge));
    }

    // 8. Actualización de la puntuación (RF10)
    private void updateScore() {
        scoreTextView.setText("Aciertos: " + correctAnswers + " - Errores: " + wrongAnswers);
    }

    // Retroalimentación visual
    private void flashFeedback(int color) {
        mainLayout.setBackgroundColor(color);
        new Handler(Looper.getMainLooper()).postDelayed(() -> mainLayout.setBackgroundColor(Color.WHITE), 200); // Vuelve a blanco después de 200ms
    }
}
