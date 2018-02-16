package view.loading;

import java.awt.Color;
import java.awt.Graphics2D;

import model.data.Point;

public class LoadingLine {

	int xS, yS, xE, yE;
	double percent;
	Color col1, col2;

	public LoadingLine(int xS, int yS, int xE, int yE, Color col1, Color col2) {
		this.xS = xS;
		this.yS = yS;
		this.xE = xE;
		this.yE = yE;
		this.col1 = col1;
		this.col2 = col2;
	}

	public void setPercent(double percent) {
		double per = percent;
		if ( per > 1 ) per = 1;
		this.percent = per;
	}

	public double percent() {
		return percent;
	}

	public void setColorOne(Color col) {
		col1 = col;
	}

	public void setColorTwo(Color col) {
		col2 = col;
	}

	public Color colorOne() {
		return col1;
	}

	public Color colorTwo() {
		return col2;
	}

	public Point getStart() {
		return new Point(xS, yS);
	}

	public Point getEnd() {
		return new Point(xE, yE);
	}

	public Point getEndProgress() {
		int xEP = xS + (int) ((xE - xS) * percent);
		int yEP = yS + (int) ((yE - yS) * percent);

		return new Point(xEP, yEP);
	}

	public static void drawLoadingLine(Graphics2D g2d, LoadingLine l) {
		if ( l.colorOne() != null ) {
			g2d.setColor(l.colorOne());
			g2d.drawLine((int) l.getStart().getX(), (int) l.getStart().getY(), (int) l.getEnd().getX(), (int) l.getEnd().getY());
		}
		if ( l.percent() > 0 ) {
			g2d.setColor(l.colorTwo());
			g2d.drawLine((int) l.getStart().getX(), (int) l.getStart().getY(), (int) l.getEndProgress().getX(), (int) l.getEndProgress().getY());
		}
	}
}
