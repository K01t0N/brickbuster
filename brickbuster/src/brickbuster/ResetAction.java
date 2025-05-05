package minesweeper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResetAction implements ActionListener {
	
	Brickbuster B;
	
	public ResetAction (Brickbuster B) {this.B = B;}

	@Override
	public void actionPerformed(ActionEvent e) {this.B.newGame();}
}
