package edu.utep.cs.cs4330.sudoku;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.sudoku.model.Board;

/**
 * HW1 template for developing an app to play simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(), numberClicked(int) and squareSelected(int,int).
 * Feel free to improved the given UI or design your own.
 *
 * <p>
 *  This template uses Java 8 notations. Enable Java 8 for your project
 *  by adding the following two lines to build.gradle (Module: app).
 * </p>
 *
 * <pre>
 *  compileOptions {
 *  sourceCompatibility JavaVersion.VERSION_1_8
 *  targetCompatibility JavaVersion.VERSION_1_8
 *  }
 * </pre>
 *
 * @author Yoonsik Cheon, Marina Chong, Ana Garcia
 */
public class MainActivity extends AppCompatActivity {

    private String myDeviceIPAddress = "192.168.1.0";
    private int myDevicePort = 8080;
    private Board board;

    private BoardView boardView;

    /** All the number buttons. */
    private List<View> numberButtons;
    private static final int[] numberIds = new int[] {
            R.id.n0, R.id.n1, R.id.n2, R.id.n3, R.id.n4,
            R.id.n5, R.id.n6, R.id.n7, R.id.n8, R.id.n9
    };

    /** Width of number buttons automatically calculated from the screen size. */
    private static int buttonWidth;

    private int squareX;
    private int squareY;
    public int size = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        board = new Board(size,1);
        boardView = findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.addSelectionListener(this::squareSelected);
        numberButtons = new ArrayList<>(numberIds.length);
        for (int i = 0; i < numberIds.length; i++) {
            final int number = i; // 0 for delete button
            View button = findViewById(numberIds[i]);
            button.setOnClickListener(e -> numberClicked(number));
            numberButtons.add(button);
            setButtonWidth(button);
        }


    }

    //Enable buttons depending in the size of the array
    public void enableButtons(){
        if(size == 4){
            for (int i = 5 ; i < numberIds.length; i++){
                View button = findViewById(numberIds[i]);
                button.setEnabled(false);
            }
        }
    }

    //create the 3 dots
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //determine which item's selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Solve menu item
        if(id == R.id.action_solve){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to give up?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SudokuSolver solver = new SudokuSolver(board);
                    solver.solve();
                    if(!board.solvable)
                        toast("Board can't be solved.");
                    boardView.postInvalidate();

                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        //Check menu item
        else if(id == R.id.action_check){
            Board copyBoard = board.copyBoard(new Board(board.size, board.difficulty));
            SudokuSolver solver = new SudokuSolver(board);
            solver.solve();
            if(!copyBoard.solvable)
                toast("Game is not solvable");
            else
                toast("Game is solvable!");
        }
        //4x4 board size option
        else if(id == R.id.small){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(4,1);
                size = 4;
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                enableButtons();
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }
        //9x9 board size option
        else if(id == R.id.large){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(9,1);
                size = 9;
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }
        //Easy level option
        else if(id == R.id.easy){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(size, 1);
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }
        //Medium level option
        else if(id == R.id.medium){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(size, 2);
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }
        //Hard level option
        else if(id == R.id.hard){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(size, 3);
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }


        return super.onOptionsItemSelected(item);
    }


    /** Callback to be invoked when the new button is tapped. */
    public void newClicked(View view) {

        //Create notification to ask user if they are sure about creating new game
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to start a new game?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Restart Activity
                //recreate();
                if(board.size==4){
                    board = new Board(4,board.difficulty);
                    boardView = findViewById(R.id.boardView);
                    boardView.setBoard(board);
                    boardView.setSelectedX(-1);
                    boardView.setSelectedY(-1);
                    boardView.postInvalidate();
                } else{
                    board = new Board(9,board.difficulty);
                    boardView = findViewById(R.id.boardView);
                    boardView.setBoard(board);
                    boardView.setSelectedX(-1);
                    boardView.setSelectedY(-1);
                    boardView.postInvalidate();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /*public Dialog onConnectClicked(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Get the layour inflater
        LayoutInflater inflater = this.getLayoutInflater();



        builder.setView(inflater.inflate(R.layout.dialog_connect, null));
        builder.setPositiveButton("Pair", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int option){
                toast("Pair clicked!");

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int option){
                dialog.cancel();
            }
        });
        return builder.create();
    }*/

    public void connectClicked(View view){
        //Create a pop up dialog to ask the user for data if he/she wants to connect
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(R.layout.dialog_connect);
        alertDialogBuilder.setMessage("My Device IPAddress: " + myDeviceIPAddress + "\r\n" + "My Device Port: " +
        myDevicePort);

        alertDialogBuilder.setPositiveButton("Pair", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int option){

                toast("Pair clicked!");
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int option){
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();

    }


    /** Callback to be invoked when a number button is tapped.
     *
     * @param n Number represented by the tapped button
     *          or 0 for the delete button.
     */
    public void numberClicked(int n) {
        //Deletes number in square selected
        if(n==0 && board.getSquare(squareX,squareY).getValue()!=0){
            board.insertZero(squareX,squareY);
            boardView.postInvalidate();
        }
        //Insert number in square selected
        else if(board.getSquare(squareX,squareY).getValue()==0 && n!=0){
            board.insertNumber(squareX, squareY, n);
            //if game is won, display winning message
            if(board.win){
                boardView.win = true;
                toast("YOU WIN!");
            }
            boardView.postInvalidate();
        }else{
            if(board.getSquare(squareX,squareY).getPrefilled())
                toast("Can't delete prefilled value");
            else {
                toast("Space is taken.");
            }
        }
    }


    /**
     * Callback to be invoked when a square is selected in the board view.
     *
     * @param x 0-based column index of the selected square.
     * @param x 0-based row index of the selected square.
     */
    private void squareSelected(int x, int y) {
        //Get coordinates of square
        squareX = x;
        squareY = y;

          /*Update selected x,y coordinates in BoardView
        to draw a red border around the selected cell.
         */
        boardView.setSelectedX(x);
        boardView.setSelectedY(y);
        //force the screen to redraw upon selection
        boardView.postInvalidate();
        disableButtons();
        board.permittedNums(x,y);
       //  toast(String.format("Square selected: (%d, %d)", x, y));
    }

    //Disable buttons for numbers that are not permitted for the selected square and for the prefilled values
    public void disableButtons(){
        for(int i = 1 ; i <= board.size; i++){
            View button = findViewById(numberIds[i]);
            if(!board.isValidNumber(squareX,squareY, i) && !board.getSquare(squareX,squareY).getPrefilled()){
                button.setEnabled(false);
            } else if(board.getSquare(squareX,squareY).getPrefilled() || board.getSquare(squareX,squareY).getValue()!=0){
                button.setEnabled(false);
            } else
                button.setEnabled(true);
        }
    }

    /** Show a toast message. */
    private void toast(String msg) {
        Toast toast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,-7,130);
        toast.show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to exit?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /** Set the width of the given button calculated from the screen size. */
    private void setButtonWidth(View view) {
        if (buttonWidth == 0) {
            final int distance = 2;
            int screen = getResources().getDisplayMetrics().widthPixels;
            buttonWidth = (screen - ((9 + 1) * distance)) / 9; // 9 (1-9)  buttons in a row
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = buttonWidth;
        view.setLayoutParams(params);
    }
}

