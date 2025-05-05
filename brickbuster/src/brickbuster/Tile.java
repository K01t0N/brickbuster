package minesweeper;

import java.awt.Color;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class Tile extends JButton { // class [name] implements ActionListener
	
	int row;
	int column;
	boolean hasBomb;
	boolean isMined;
	boolean isFlagged;
	int surroundingBombs;
	
	public Tile(int r, int c) {
		// constructor
		this.row = r;
		this.column = c;
		this.setBounds(50*r+50, 50*c+50, 50, 50); // x, y, width, height
		this.setBackground(Color.gray);
		this.setFont(new Font("Arial", Font.PLAIN, 32));
		this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		this.doClick();
	}
	
	public void newGame (Brickbuster B) {
		// set hasBomb to true if tile has bomb
		
		this.hasBomb = false;
		
		for (int i=0; i<B.bombs.length; i++ ) {
			if (row == Math.floorDiv(B.bombs[i], B.divider) && column == B.bombs[i] % B.divider) {
				this.hasBomb = true;
			}
		}
		
		// reset isMined to false and text to blank
		this.isMined = false;
		this.isFlagged = false;
		this.setText("");
		this.setBackground(Color.gray);
		
	}
	
	public void leftClick (Brickbuster B) {
		
		// don't do anything if game is over or space is flagged
		if (B.gameOver == true || this.isFlagged == true) {return;}
		
		B.checkWin(); // check win
		
		if (this.hasBomb == true) {this.gameOver(B);} // game over
		
		else {
			if (this.isMined == false) {this.isMined = true;}
			mine(B, this.row, this.column); // do not use "this.mine"
		}
	}
	
	public void rightClick (Brickbuster B) {
		
		if (B.gameOver == false) {
		
			if (this.isFlagged == false) {
				
				this.setBackground(Color.yellow);
				this.setForeground(Color.black);
				this.setText("!");
				this.isFlagged = true;
				
			} else {
				
				this.isFlagged = false;
				if (isMined == true) {
					format(B, this.row, this.column);
					
				} else {
					this.setBackground(Color.gray);
					this.setText("");
				}
			}
		}
	}
	
	public void win () {
		if (this.hasBomb == true) {
			this.setBackground(Color.GREEN);
			this.setText("");
		}
	}
	
	void gameOver (Brickbuster B) {

		for (int i=0; i < B.rowSize; i++) {
			for (int j=0; j < B.colSize; j++) {
				if (B.tiles[i][j].hasBomb == true) {
					B.tiles[i][j].setBackground(Color.red);
					B.tiles[i][j].setText("");
				}
			}
		}
		B.label.setText("Game Over!");
		B.gameOver = true;
	}
	
	void mine (Brickbuster B, int row, int col) {
		
		format(B, row, col);
		B.checkWin();

		if (B.tiles[row][col].surroundingBombs == 0) {

			// go through each surrounding tile
			for (int i=-1; i < 2; i++) {
				for (int j=-1; j < 2; j++) {
					
					// if a tile is valid, set isMined to true and format it
					if (validate (B, row, col, i, j) == true) {
						B.tiles[row+i][col+j].isMined = true;
						format(B, row+i, col+j);
						
						// if tile has 0 surrounding bombs, trigger recursion
						if (B.tiles[row+i][col+j].surroundingBombs == 0) {
							B.checkWin();
							mine(B, row+i, col+j);
						}
					}
				}
			}
		}
	}
	
	boolean validate (Brickbuster B, int row, int col, int i, int j) {
		if (row + i >= 0 && row + i < B.rowSize && // stay in row bounds
			col + j >= 0 && col + j < B.colSize && // stay in column bounds
			(i != 0 || j != 0) && // don't re-mine the center tile
			B.tiles[row+i][col+j].isMined == false) { // only mine tiles that have already been mined
			return true;
			} else {
				return false;
			}
	}
	
	void format (Brickbuster B, int row, int col) {
		
		Tile t = B.tiles[row][col];
		int s = t.surroundingBombs;

		// zero surrounding bombs
		if (s == 0) {
			t.setText("");
			t.setBackground(Color.white);
			
		} else { // 1+ surrounding bombs
			t.setText(String.valueOf(s));
			t.setBackground(Color.darkGray);
			
			if (s == 1) {t.setForeground(Color.red);}
			else if (s == 2) {t.setForeground(Color.green);}
			else if (s >= 3) {t.setForeground(Color.blue);}
		}
	}
	
	int bombCheck (Brickbuster M, int row, int col) {
		
		int counter = 0;
		for (int i=-1; i < 2; i++) {		
			for (int j=-1; j < 2; j++) {
				if (row + i >= 0 && row + i < M.rowSize &&
					col + j >= 0 && col + j < M.colSize) { // stay in bounds
					if (i != 0 || j != 0) { // don't count center tile
						if (M.tiles[row+i][col+j].hasBomb == true) {counter += 1;}
					}
				}
			}
		}
		return counter;
	}
	
	boolean in (int num, int[] arr) {
		for (int i=0; i < arr.length; i++) {
			if (num == arr[i]) {return true;}
		}
		return false;
	}
	
	int search (int num, int[] arr) {
		for (int i=0; i < arr.length; i++) {
			if (arr[i] == num) {return i;}
		}
		return (-1);
	}

}
