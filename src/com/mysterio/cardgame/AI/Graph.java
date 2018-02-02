package com.mysterio.cardgame.AI;

import java.util.ArrayList;
import java.util.List;

import com.mysterio.cardgame.model.Card;

public class Graph {
	
	private Vertex[] mVertexList;
	private Card[][] mAdjMatrix;
	private int mVertices;
	
	public Graph() {
		mVertexList = new Vertex[8];
		mAdjMatrix = new Card[8][4];
		
		for (int i = 0; i < 8; i++) {
			for(int j = 0; j < 4; j++){
				mAdjMatrix[i][j] = null;
			}
		}
	}
	
	public void addCard(final Card card){
		/*mVertexList[mVertices++] = */
	}

	private class Vertex{
		boolean wasVisited;
		char mLabel;
		
		Vertex(final char label) {
			mLabel = label;
		}
	}
	
	private final class Stack {
		
		private List<Card> mStackList;
		private int mCounter;
		
		private Stack() {
			mStackList = new ArrayList<Card>();
			mCounter = -1;
		}
		
		private void push(final Card card){
			mStackList.add(card);
			mCounter++;
		}
		
		private void pop(){
			mStackList.remove(mCounter--);
		}
		
		private boolean isEmpty(){
			return mCounter == -1;
		}
	}
}
