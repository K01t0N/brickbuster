package minesweeper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TileAction implements ActionListener {
	
	Brickbuster B;
	int row;
	int column;
	
	public TileAction (	Brickbuster B, int row, int column) {
		this.B = B;
		this.row = row;
		this.column = column;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.B.tiles[this.row][this.column].leftClick(B); // apparently M is null
	}
}
