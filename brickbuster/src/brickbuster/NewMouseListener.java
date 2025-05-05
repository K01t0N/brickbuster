package minesweeper;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class NewMouseListener implements MouseListener {

	Brickbuster B;
	int row;
	int column;
	
	public NewMouseListener (Brickbuster B, int row, int column) {
		this.B = B;
		this.row = row;
		this.column = column;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			B.tiles[this.row][this.column].rightClick(B);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
