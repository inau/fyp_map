package view.loading;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class LoadingLineList {

	private final ArrayList<LoadingLine> list = new ArrayList<LoadingLine>();

	private Color col1, col2;
	private int lastX, lastY;
	private double progress = 0;
	private int N;

	public LoadingLineList(Color col1, Color col2) {
		this.col1 = col1;
		this.col2 = col2;
		this.lastX = 0;
		this.lastY = 0;
	}
	
	public LoadingLineList(Color col1, Color col2, int startX, int startY) {
		this.col1 = col1;
		this.col2 = col2;
		this.lastX = startX;
		this.lastY = startY;
	}

	// Adding lines for LoadingLineList

	public void addLine(LoadingLine line) {
		line.setColorOne(col1);
		line.setColorTwo(col2);
		list.add(line);
		setLast(line.xE, line.yE);
		N++;
	}

	public void addLine(int xE, int yE) {
		list.add(new LoadingLine(lastX, lastY, xE, yE, col1, col2));
		setLast(xE, yE);
		N++;
	}
	
	public void addLine(int xS, int yS, int xE, int yE) {
		list.add(new LoadingLine(xS, yS, xE, yE, col1, col2));
		setLast(xE, yE);
		N++;
	}
	
	public void addLineRel(int xRel, int yRel) {
		list.add(new LoadingLine(lastX, lastY, lastX + xRel, lastY + yRel, col1, col2));
		setLast(lastX + xRel, lastY + yRel);
		N++;
	}
	
	public void addLineRel(int xS, int yS, int xRel, int yRel) {
		list.add(new LoadingLine(xS, yS, xS + xRel, yS + yRel, col1, col2));
		setLast(xS + xRel, yS + yRel);
		N++;
	}

	// Set lastX and lastY
	private void setLast(int x, int y) {
		lastX = x;
		lastY = y;
	}
	
	public Point getLast() {
		return new Point(lastX, lastY);
	}

	// Set progress
	public void setProgress(double percent) {
		progress = percent;
		for (int i = 0; i < N; i ++) {
			double per = ((double) N * progress) - (double) i;
			if ( per > 0 )
				list.get(i).setPercent( per );
		}
	}

	public void drawLoadingList(Graphics2D g2d) {
		for (int i = 0; i < N; i ++) {
			LoadingLine.drawLoadingLine(g2d, list.get(i));
		}
	}

}
