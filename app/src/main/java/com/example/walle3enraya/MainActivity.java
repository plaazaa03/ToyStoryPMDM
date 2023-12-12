package com.example.walle3enraya;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private ImageButton[][] panel = new ImageButton[3][3];
    private Player player;
    private RadioButton easyRadioButton;
    private RadioButton advancedRadioButton;
    private ImageView turnImageView;
    private TextView turnTextView;
    private ToggleButton toggleButton;
    private Button restartBtn;
    private int humanImgPath = R.mipmap.human;
    private int robotImgPath = R.mipmap.robot;
    private boolean humanTurn;
    private int turnCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Sounds.playBackgroundMusic(this);

        assignIds();
        addListeners();
        restartGame();

        if (toggleButton.isChecked()) {
            startGame();
            realizarJugadaMaquina(); // Realizar la primera jugada de la máquina
        }
    }

    private void realizarJugadaMaquina() {
        if (easyRadioButton.isChecked()) {
            // Modo fácil: jugar de forma aleatoria
            realizarJugadaAleatoria();
        } else if (advancedRadioButton.isChecked()) {
            // Modo avanzado: jugar de forma estratégica
            realizarJugadaEstrategica();
        }
    }

    public void assignIds()
    {
        easyRadioButton = findViewById(R.id.easyRadioButton);
        advancedRadioButton = findViewById(R.id.advancedRadioButton);
        turnImageView = findViewById(R.id.turnImageView);
        turnTextView = findViewById(R.id.turnTextView);

        panel[0][0] = findViewById(R.id.imageButton1);
        panel[0][1] = findViewById(R.id.imageButton2);
        panel[0][2] = findViewById(R.id.imageButton3);
        panel[1][0] = findViewById(R.id.imageButton4);
        panel[1][1] = findViewById(R.id.imageButton5);
        panel[1][2] = findViewById(R.id.imageButton6);
        panel[2][0] = findViewById(R.id.imageButton7);
        panel[2][1] = findViewById(R.id.imageButton8);
        panel[2][2] = findViewById(R.id.imageButton9);

        toggleButton = findViewById(R.id.toggleButton);
        restartBtn = findViewById(R.id.restartBtn);
    }

    // Configure elements by default
    public void configureElements()
    {
        // Set Disable all the Image Buttons
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                final int row = i;
                final int col = j;

                panel[i][j].setEnabled(false);
            }
        }

        // Set Radio Buttons for difficulty enabled
        easyRadioButton.setEnabled(true);
        advancedRadioButton.setEnabled(true);

        // Clean the turn image
        turnImageView.setImageResource(0);

        // Set the default toggle button state
        toggleButton.setChecked(false);
    }

    // Method to restart the game
    public void restartGame()
    {
        // Create Player object
        player = new Player(humanImgPath);

        // Clean all the panel
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                final int row = i;
                final int col = j;

                panel[row][col].setImageResource(0);
                panel[row][col].setTag(0);
            }
        }

        configureElements();
        humanTurn = true;
        turnCount = 0;
    }

    public void addListeners()
    {
        // Listener for the start button
        toggleButton.setOnClickListener(v -> {
            if(toggleButton.isChecked())
            {
                Toast.makeText(MainActivity.this, "The game is running...", Toast.LENGTH_SHORT).show();
                startGame();

                turnImageView.setImageResource(humanImgPath);
            }
            else
            {
                Toast.makeText(MainActivity.this, "The game is stopped", Toast.LENGTH_SHORT).show();
                stopGame();

                turnImageView.setImageResource(robotImgPath);
            }
        });

        // Listener for the restart button
        restartBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Estás seguro de que quieres reinciar el juego?").setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Accept the restart
                    restartGame();
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Reject the restart
                }
            });

            AlertDialog mDialog = builder.create();
            mDialog.show();
        });

        // Listener for the Image Buttons
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                final int row = i;
                final int col = j;

                panel[i][j].setOnClickListener(v ->
                {
                    onImageButtonClicked(row, col);
                });
            }
        }
    }

    public void onImageButtonClicked(int row, int col) {
        // Square already selected
        if (panel[row][col].getDrawable() != null) {
            Toast.makeText(MainActivity.this, "Casilla ya seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }
        // Human turn
        if (humanTurn) {
            player.makeMovement(panel, row, col);
            turnImageView.setImageResource(robotImgPath);
            turnTextView.setText("Turno: CPU");
            humanTurn = false;
            turnCount++;
        }

        // Verificar si hay un ganador o empate después de la jugada del humano
        if (checkForWin()) {
            Toast.makeText(MainActivity.this, "¡Gana el Jugador!", Toast.LENGTH_SHORT).show();
        } else if (turnCount == 9) {
            Toast.makeText(MainActivity.this, "Empate", Toast.LENGTH_SHORT).show();
        } else { // CPU turn
            realizarJugadaMaquina(); // Realizar la jugada de la máquina

            // Verificar si hay un ganador o empate después de la jugada de la CPU
            if (checkForWin()) {
                Toast.makeText(MainActivity.this, "¡Gana la CPU!", Toast.LENGTH_SHORT).show();
            } else if (turnCount == 9) {
                Toast.makeText(MainActivity.this, "Empate", Toast.LENGTH_SHORT).show();
            }
        }

        // Cambiar el turno después de la jugada del humano o la CPU
        humanTurn = true;
        turnCount++;
        turnImageView.setImageResource(humanImgPath);
        turnTextView.setText("Turno: Jugador");
    }

    private void realizarJugadaEstrategica() {
        // Primero, verifica si hay una oportunidad para ganar en la próxima jugada
        if (buscarOportunidadGanar()) {
            // Realiza la jugada estratégica para ganar
        } else if (bloquearOponente()) {
            // Si no hay oportunidad para ganar, intenta bloquear al oponente
        } else {
            // Si no hay oportunidades para ganar ni bloquear, realiza una jugada aleatoria
            realizarJugadaAleatoria();
        }
    }

    private boolean buscarOportunidadGanar() {
        // Verificar filas
        for (int i = 0; i < 3; i++) {
            if (verificarFila(i, panel)) {
                return true;
            }
        }

        // Verificar columnas
        for (int i = 0; i < 3; i++) {
            if (verificarColumna(i, panel)) {
                return true;
            }
        }

        // Verificar diagonales
        if (verificarDiagonalPrincipal(panel) || verificarDiagonalSecundaria(panel)) {
            return true;
        }

        return false;
    }

    private boolean bloquearOponente() {
        // Verificar filas
        for (int i = 0; i < 3; i++) {
            if (verificarFila(i, panel, true)) {
                return true;
            }
        }

        // Verificar columnas
        for (int i = 0; i < 3; i++) {
            if (verificarColumna(i, panel, true)) {
                return true;
            }
        }

        // Verificar diagonales
        if (verificarDiagonalPrincipal(panel, true) || verificarDiagonalSecundaria(panel, true)) {
            return true;
        }

        return false;
    }

    private boolean verificarFila(int fila, ImageButton[][] panel) {
        Drawable drawable0 = panel[fila][0].getDrawable();
        Drawable drawable1 = panel[fila][1].getDrawable();
        Drawable drawable2 = panel[fila][2].getDrawable();

        return drawable0 != null &&
                drawable0.getConstantState() != null && drawable1 != null &&
                drawable1.getConstantState() != null && drawable2 != null &&
                drawable2.getConstantState() != null &&
                drawable0.getConstantState().equals(drawable1.getConstantState()) &&
                drawable1.getConstantState().equals(drawable2.getConstantState());
    }


    private boolean verificarColumna(int columna, ImageButton[][] panel) {
        Drawable drawable0 = panel[0][columna].getDrawable();
        Drawable drawable1 = panel[1][columna].getDrawable();
        Drawable drawable2 = panel[2][columna].getDrawable();

        return drawable0 != null &&
                drawable0.getConstantState() != null && drawable1 != null &&
                drawable1.getConstantState() != null && drawable2 != null &&
                drawable2.getConstantState() != null &&
                drawable0.getConstantState().equals(drawable1.getConstantState()) &&
                drawable1.getConstantState().equals(drawable2.getConstantState());
    }

    private boolean verificarDiagonalPrincipal(ImageButton[][] panel) {
        Drawable drawable00 = panel[0][0].getDrawable();
        Drawable drawable11 = panel[1][1].getDrawable();
        Drawable drawable22 = panel[2][2].getDrawable();

        return drawable00 != null &&
                drawable00.getConstantState() != null && drawable11 != null &&
                drawable11.getConstantState() != null && drawable22 != null &&
                drawable22.getConstantState() != null &&
                drawable00.getConstantState().equals(drawable11.getConstantState()) &&
                drawable11.getConstantState().equals(drawable22.getConstantState());
    }


    private boolean verificarDiagonalSecundaria(ImageButton[][] panel) {
        Drawable drawable02 = panel[0][2].getDrawable();
        Drawable drawable11 = panel[1][1].getDrawable();
        Drawable drawable20 = panel[2][0].getDrawable();

        return drawable02 != null &&
                drawable02.getConstantState() != null && drawable11 != null &&
                drawable11.getConstantState() != null && drawable20 != null &&
                drawable20.getConstantState() != null &&
                drawable02.getConstantState().equals(drawable11.getConstantState()) &&
                drawable11.getConstantState().equals(drawable20.getConstantState());
    }


    // Sobrecarga de métodos para la versión que bloquea al oponente
    private boolean verificarFila(int fila, ImageButton[][] panel, boolean bloquear) {
        Drawable drawable01 = panel[fila][0].getDrawable();
        Drawable drawable11 = panel[fila][1].getDrawable();
        Drawable drawable21 = panel[fila][2].getDrawable();

        return bloquear && drawable01 == null &&
                drawable11 != null && drawable21 != null &&
                drawable11.getConstantState() != null &&
                drawable21.getConstantState() != null &&
                drawable11.getConstantState().equals(drawable21.getConstantState());
    }

    private boolean verificarColumna(int columna, ImageButton[][] panel, boolean bloquear) {
        Drawable drawable10 = panel[0][columna].getDrawable();
        Drawable drawable11 = panel[1][columna].getDrawable();
        Drawable drawable12 = panel[2][columna].getDrawable();

        return bloquear && drawable10 == null &&
                drawable11 != null && drawable12 != null &&
                drawable11.getConstantState() != null &&
                drawable12.getConstantState() != null &&
                drawable11.getConstantState().equals(drawable12.getConstantState());
    }

    private boolean verificarDiagonalPrincipal(ImageButton[][] panel, boolean bloquear) {
        Drawable drawable00 = panel[0][0].getDrawable();
        Drawable drawable11 = panel[1][1].getDrawable();
        Drawable drawable22 = panel[2][2].getDrawable();

        return bloquear && drawable00 == null &&
                drawable11 != null && drawable22 != null &&
                drawable11.getConstantState() != null &&
                drawable22.getConstantState() != null &&
                drawable11.getConstantState().equals(drawable22.getConstantState());
    }

    private boolean verificarDiagonalSecundaria(ImageButton[][] panel, boolean bloquear) {
        Drawable drawable02 = panel[0][2].getDrawable();
        Drawable drawable11 = panel[1][1].getDrawable();
        Drawable drawable20 = panel[2][0].getDrawable();

        return bloquear && drawable02 == null &&
                drawable11 != null && drawable20 != null &&
                drawable11.getConstantState() != null &&
                drawable20.getConstantState() != null &&
                drawable11.getConstantState().equals(drawable20.getConstantState());
    }




    private void realizarJugadaAleatoria() {
        // Obtener la lista de botones disponibles
        ArrayList<ImageButton> botonesDisponibles = obtenerBotonesDisponibles();

        // Verificar si hay botones disponibles
        if (!botonesDisponibles.isEmpty()) {
            // Seleccionar un botón aleatorio
            Random random = new Random();
            int indiceAleatorio = random.nextInt(botonesDisponibles.size());
            ImageButton botonSeleccionado = botonesDisponibles.get(indiceAleatorio);

            // Realizar la jugada en el botón seleccionado
            botonSeleccionado.setImageResource(robotImgPath);
            botonSeleccionado.setEnabled(false); // Deshabilitar el botón

            // Eliminar el botón de la lista de botones disponibles
            botonesDisponibles.remove(botonSeleccionado);
        }
    }

    private ArrayList<ImageButton> obtenerBotonesDisponibles() {
        ArrayList<ImageButton> botonesDisponibles = new ArrayList<>();

        // Recorrer la matriz de botones y agregar los botones disponibles a la lista
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (panel[i][j].getDrawable() == null) {
                    botonesDisponibles.add(panel[i][j]);
                }
            }
        }

        return botonesDisponibles;
    }


    // Method to start the game
    public void startGame()
    {
        // Set Radio Buttons for difficulty disabled
        easyRadioButton.setEnabled(false);
        advancedRadioButton.setEnabled(false);

        // Set Enable all the Image Buttons
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                final int row = i;
                final int col = j;

                panel[i][j].setEnabled(true);
            }
        }
    }

    // Method to stop the game
    public void stopGame()
    {
        // Set Disable all the Image Buttons
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                final int row = i;
                final int col = j;

                panel[i][j].setEnabled(false);
            }
        }
    }

    public boolean checkForWin()
    {
        // Verify rows
        for (int i = 0; i < 3; i++)
        {
            if (checkRowCol(panel[i][0], panel[i][1], panel[i][2]))
                return true;
        }
        // Verify columns
        for (int i = 0; i < 3; i++)
        {
            if (checkRowCol(panel[0][i], panel[1][i], panel[2][i]))
                return true;
        }
        // Verify diagonals
        if (checkRowCol(panel[0][0], panel[1][1], panel[2][2]) || checkRowCol(panel[0][2], panel[1][1], panel[2][0]))
            return true;

        return false;
    }

    private boolean checkRowCol(ImageButton b1, ImageButton b2, ImageButton b3)
    {
        return (b1.getDrawable() != null && b2.getDrawable() != null && b3.getDrawable() != null) &&
                b1.getDrawable().getConstantState().equals(b2.getDrawable().getConstantState()) &&
                b2.getDrawable().getConstantState().equals(b3.getDrawable().getConstantState());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sounds.stopBackgroundMusic();
    }
}