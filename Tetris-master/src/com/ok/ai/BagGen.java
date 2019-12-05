package com.ok.ai;


import java.util.*;

public class BagGen implements PieceGenerator
{
	private static int LEN = Tetris.PIECE_TYPES;
	
	Random rng;
	int[] bag;
	int bagPos;
	
	BagGen()
	{
		bag = new int[LEN];
		rng = new Random();
		refreshBag();
	}
	
	private void refreshBag()
	{
		bagPos = 0;
		
		for (int i = 0; i < LEN; i++)
			bag[i] = i + 1;
		
		for (int i = 0; i < LEN; i++)
		{
			int ind = rng.nextInt(LEN - i) + i;
			int swap = bag[ind];
			bag[ind] = bag[i];
			bag[i] = swap;
		}
	}
	
	public void newGame()
	{
		refreshBag();
	}
	
	public int nextPiece()
	{
		if (bagPos == LEN)
			refreshBag();
		
		return bag[bagPos++];
	}
}
