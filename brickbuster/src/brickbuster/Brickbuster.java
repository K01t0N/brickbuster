package minesweeper;

import java.awt.*; // java.awt.color
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class Brickbuster {
	
	// object variables
	int rowSize = 10; // default row size
	int colSize = 10; // default col size
	int numBombs = 10; // default bombs
	int divider = rowSize; // can be either rowSize or colSize
	
	boolean gameOver;
	int[] bombs;
	Tile[][] tiles;
	JFrame frame;
	JPanel gridPanel;
	JLabel label;
	JSlider sliderRows;
	JSlider sliderCols;
	JButton reset;
	SpinnerModel spinnerModel;
	
	public void init () {
		
		frame = new JFrame(); // frame
		frame.setLayout(new BorderLayout());
		frame.setSize(500, 600);
		frame.setBackground(Color.black);
		frame.setVisible(true);
		
		gridPanel = new JPanel(); // grid panel for tiles
		gridPanel.setLayout(new GridLayout(rowSize, colSize));
		gridPanel.setBackground(Color.darkGray);		
		frame.add(gridPanel, BorderLayout.CENTER);

		label = new JLabel("Minesweeper", SwingConstants.CENTER); // label
		label.setOpaque(true);
		label.setFont(new Font("Arial", Font.PLAIN, 32));
		gridPanel.setForeground(Color.black);	
		frame.add(label, BorderLayout.NORTH);

		reset = new JButton(); // new game button
		reset.setText("New Game");
		reset.setFont(new Font("Arial", Font.PLAIN, 32));
		reset.addActionListener(new ResetAction(this));
		frame.add(reset, BorderLayout.SOUTH);

		// create an array for the tiles
		tiles = new Tile[rowSize][colSize];
			
		// create the tiles
		for (int i=0; i < rowSize; i++) {
			for (int j=0; j < colSize; j++) {
				tiles[i][j] = new Tile(i, j);
				tiles[i][j].addActionListener(new TileAction(this, i, j));
				tiles[i][j].addMouseListener(new NewMouseListener(this, i, j));
				gridPanel.add(tiles[i][j]);
			}
		}
		newGame(); // start a new game
	}

	public void newGame () {
		
		// selecting bomb locations
		bombs = shuffle(rowSize * colSize, numBombs);
		label.setText("Bombs: " + numBombs);
		gameOver = false;

		// restart tiles
		for (int i=0; i < rowSize; i++) {
			for (int j=0; j < colSize; j++) {
				tiles[i][j].newGame(this);
			}
		}
		// check surrounding bombs for each tile (this must be on a separate loop)
		for (int i=0; i < rowSize; i++) {
			for (int j=0; j < colSize; j++) {
				tiles[i][j].surroundingBombs = tiles[i][j].bombCheck(this, i, j);
			}
		}
	}
	
	int[] shuffle(int length, int partition) {
		// shuffles integers 0 to length then returns the first p shuffled integers
		ArrayList<Integer> localList = new ArrayList<Integer>();
		for (int i=0; i<length; i++) {localList.add(i);}
		Collections.shuffle(localList);
		Object[] resultObj = localList.toArray();
		int[] result = new int[partition];
		for (int j = 0; j < partition; j++) {result[j] = (int) resultObj[j];}
		return result;
	}
	
	void checkWin() {

		// return early if tiles without bombs are still unrevealed
		for (int i=0; i < this.rowSize; i++) {
			for (int j=0; j < this.colSize; j++) {
				if (tiles[i][j].hasBomb == false && 
					tiles[i][j].isMined == false) {return;}
			}
		}
		// make all tiles green
		for (int i=0; i < this.rowSize; i++) {
			for (int j=0; j < this.colSize; j++) {
					tiles[i][j].win();
			}
		}
		
		label.setText("You Win!");
		gameOver = true;
	}
	
}
