from customtkinter import *
import random

set_appearance_mode("dark")

class Minesweeper(CTk):

    def __init__(self):

        super().__init__()

        # game variables
        self.rows = 8
        self.columns = 8
        self.num_bombs = 6

        # window
        self.title("Minesweeper")
        self.geometry(f"{70*self.rows}x{70*self.columns}")
        self.minsize(70*self.rows, 70*self.columns)
        self.maxsize(70*self.rows, 70*self.columns)

        # creating buttons and coordinates
        self.buttons = [[0 for x in range(self.columns)] for y in range(self.rows)]
        self.coords = [(x, y) for x in range(self.columns) for y in range(self.rows)]

        # game tiles
        for row, column in self.coords:
            self.buttons[row][column] = CTkButton(self, font=('Arial', 30), width=50, height=50, corner_radius=4,
                                                  command= lambda row=row, column=column: self.left_click(row, column))
            self.buttons[row][column].place(relx=row/10+0.1, rely=column/10+0.15)

        # text label
        self.label = CTkLabel(self, text=f"Bombs: {self.num_bombs}", font=('Arial', 30))            
        self.label.place(relx=0.4, rely=0.05)

        # reset button
        self.reset_button = CTkButton(self, width=50, height=50, corner_radius=4,
                                      text='new game', font=('Arial', 20), command=self.new_game)
        self.reset_button.place(relx=0.05, rely=0.05)

        # flag button
        self.toggle_button = CTkButton(self, width=50, height=50, corner_radius=4, # corner_radius=4 was added
                                       text='flag', font=('Arial', 20), command=self.toggle)
        self.toggle_button.place(relx=0.9, rely=0.05)

        # initialize a new game
        self.new_game() 

        # window loop
        self.mainloop() 


    def left_click(self, row, column):

        # don't do anything if game is over or space is flagged
        text = self.buttons[row][column].cget('text')
        if (self.game_over == True) or (text == "!"):
            return

        self.check_win()

        # game over, bad ending
        if (row, column) in self.bomb_list: # this was elif

            for r, c in self.coords:
                self.format(r, c, plus_bg="#111111", zero="#222222")

            for r1, c1 in self.bomb_list:
                self.buttons[r1][c1].configure(fg_color="red")

            self.label.configure(text=(f'GAME OVER'))
            self.game_over = True
    
        else: 
            if self.tile_info[(row, column)][1] == False:
                self.tile_info[(row, column)][1] = True
            self.mine(row, column)


    def mine(self, row, column):

        num_display = self.tile_info[(row, column)][0] # [1] indicates if tile was revealed

        self.format(row, column, plus_bg="#333333", plus_fg=True, plus_text=True, zero="#AAAAAA")

        # ----------------------------------
        # select space(s) to begin recursion

        if num_display > 0:
            self.tile_info[(row, column)][1] = True
            self.check_win()
            return

        for row_2, column_2 in self.surround(row, column):

            # check for out of bounds 
            if ((row_2 < 0) or (row_2 >= self.rows) or (column_2 < 0) or (column_2 >= self.columns) or (num_display > 0)):
                continue

            # if the space isn't revealed yet, reveal that space and mine it
            elif self.tile_info[(row_2, column_2)][1] == False:
                self.tile_info[(row_2, column_2)][1] = True
                self.check_win()
                self.mine(row_2, column_2)

    def format(self, row, column, plus_bg=None, plus_fg=None, plus_text=None, zero=None, flag=False):

        button = self.buttons[row][column]
        tile = self.tile_info[(row, column)]
        num_display, tile_reveal = tile[0], tile[1]

        formatting = {1:"red", 2:"green", 3:"blue", 4:"purple",
                      5: "purple", 6: "purple", 7: "purple", 8: "purple"}

        if (flag == True) and (button.cget('text') != "!"):
            button.configure(text="!", fg_color="yellow", text_color="black")

        elif (flag == True) and (tile_reveal == False):
            button.configure(text="", fg_color="#444444")

        elif num_display > 0: # problem is here

            if plus_bg != None:
                button.configure(fg_color=plus_bg)

            if plus_fg != None:
                button.configure(text_color=formatting[num_display])

            if plus_text != None:
                button.configure(text=num_display)
    
        elif (zero != None) & num_display == 0:
            button.configure(text="", fg_color=zero)


    def toggle(self):

        if self.game_over == True:
            return

        self.label.configure(text=(f'Select a tile to flag.'))
        self.toggle_button.configure(text='mine', command=self.detoggle)

        for row, column in self.coords: # flag a tile
            self.buttons[row][column].configure(command= lambda row=row, column=column: self.format(row, column,
                                             plus_bg="#333333", plus_fg=True, plus_text=True, zero="#AAAAAA", flag=True))


    def detoggle(self):

        self.label.configure(text=(f'Bombs: {self.num_bombs}'))
        self.toggle_button.configure(text='flag', command=self.toggle)

        for row, column in self.coords:
            self.buttons[row][column].configure(command= lambda row=row, column=column: self.left_click(row, column))
    

    def check_win(self):

        # make the tiles with "False" for revealed match the bombs list to activate win
        # if bombs are the only unrevealed spaces, the player wins

        unrevealed = [x for x in self.tile_info if self.tile_info[x][1] == False]

        if set(unrevealed) != set(self.bomb_list):
            return

        self.label.configure(text=(f'You Win!'))

        for row, column in self.bomb_list:
            self.buttons[row][column].configure(text="", fg_color="green")

        self.game_over = True
        

    def new_game(self):

        self.game_over = False
        self.bomb_list = random.choices(self.coords, k=self.num_bombs)
        
        self.label.configure(text=(f'Bombs: {self.num_bombs}'))

        self.tile_info = {}

        for row, column in self.coords:
            self.tile_info[(row, column)] = self.bomb_check(row, column)
            self.buttons[row][column].configure(text="", fg_color="#444444",
                                                command= lambda row=row, column=column: self.left_click(row, column))


    def surround(self, x, y):

        north, south, east, west = (y-1), (y+1), (x+1), (x-1)

        spaces = [(west, north), (west, y), (west, south), (x, south),
                  (east, south), (east, y), (east, north), (x, north)]
    
        return [(row, column) for (row, column) in spaces]


    def bomb_check(self, row, column):

        counter = 0

        for row_1, column_1 in self.surround(row, column):
            if (row_1, column_1) in self.bomb_list:
                counter += 1
    
        # False means that the tile is not yet revealed
        return [counter, False]
    
    
Minesweeper()

"""
Bugs:

- Right clicking a tile should flag it
- Create a random_state variable

Future Ideas:

- Flagging a tile reduces bomb count by 1
  - (Likewise, unflagging a tile increases bomb count by 1)

- Timer counting up on screen (stopwatch)
- More customizable options in one place

"""