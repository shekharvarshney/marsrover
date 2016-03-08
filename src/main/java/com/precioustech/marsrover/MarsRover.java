package com.precioustech.marsrover;

import static com.precioustech.marsrover.PositionMarkerEnum.CURR_POS;
import static com.precioustech.marsrover.PositionMarkerEnum.EW_PATH;
import static com.precioustech.marsrover.PositionMarkerEnum.NS_PATH;
import static com.precioustech.marsrover.PositionMarkerEnum.START_POS;
import static com.precioustech.marsrover.PositionMarkerEnum.TURNING_PT;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The Mars rover is programmed to drive around Mars. Its programming is very
 * simple. The commands are the following:
 * <dl>
 * <dt>s</dt>
 * <dd>drive in a straight line</dd>
 * <dt>r</dt>
 * <dd>turn right</dd>
 * <dt>l</dt>
 * <dd>turn left</dd>
 * </dl>
 *
 * Note that the Mars rover always land at the <code>X</code> and starts by
 * facing east.
 * 
 * The Mars rover can send a 2D string representation of its path back to
 * Mission Control. The following character are used with the following
 * meanings:
 * <dl>
 * <dt>X</dt>
 * <dd>where the Mars rover landed</dd>
 * <dt>*</dt>
 * <dd>current position of the Mars rover</dd>
 * <dt>-</dt>
 * <dd>path in the west-east direction</dd>
 * <dt>|</dt>
 * <dd>path in the north-south direction</dd>
 * <dt>+</dt>
 * <dd>a place where the Mars rover turned or a crossroad</dd>
 * <dt>S</dt>
 * <dd>a place where a sample was taken</dd>
 * </dl>
 */

// Class not thread safe
public class MarsRover {

	// private static final char START_POS = 'X';
	// private static final char CURR_POS = '*';
	// private static final char EW_PATH = '-';
	// private static final char NS_PATH = '|';
	// private static final char TURNING_PT = '+';
	private static final char SPACE = ' ';
	private List<int[]> coordinatesVisited = new ArrayList<int[]>();

	// private Set<String> sampleCoords = new HashSet<String>();
	private int currX = 0;
	private int currY = 0;
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private Stack<Character> moveStack = new Stack<Character>();

	private boolean isFacingEast = true;
	private boolean isFacingWest = false;//
	private boolean isFacingNorth = false;
	private boolean isFacingSouth = false;

	public MarsRover(String operations) {
		coordinatesVisited.add(new int[] { currX, currY });
		moveStack.push(START_POS.getPositionMarketChar());
		if (operations != null && operations.length() > 0) {
			final int len = operations.length();
			for (int pos = 0; pos < len; pos++) {
				char c = operations.charAt(pos);
				CommandEnum cmdEnum = CommandEnum.deriveCommand(c);
				switch (cmdEnum) {
				case GO_LEFT:
					turnLeft();
					break;
				case GO_RIGHT:
					turnRight();
					break;
				case GO_STRAIGHT:
					moveForward();
					break;
				case TAKE_SAMPLE:
					takeSample();
					break;
				default:
					throw new IllegalArgumentException("Unrecognised operation:" + c);
				}
			}
		}

	}

	public String path() {
		List<int[]> boundary = deriveBoundary();

		final int minX = boundary.get(0)[0];
		final int maxX = boundary.get(1)[0];
		final int minY = boundary.get(0)[1];
		final int maxY = boundary.get(3)[1];

		int lenX = maxX - minX + 1;
		int lenY = maxY - minY + 1;
		char matrix[][] = new char[lenY][lenX];
		for (int i = 0; i < lenY; i++) {
			for (int j = 0; j < lenX; j++) {
				matrix[i][j] = SPACE;
			}
		}


		@SuppressWarnings("unchecked")
		Stack<Character> copyStack = (Stack<Character>) this.moveStack.clone();
		copyStack.pop();
		copyStack.push(CURR_POS.getPositionMarketChar());

		for (int i = this.coordinatesVisited.size() - 1; i >= 0; i--) {
			int[] XY = this.coordinatesVisited.get(i);
			markInMatrix(matrix, copyStack.pop(), XY, minX, maxY);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lenY; i++) {
			for (int j = 0; j < lenX; j++) {
				sb.append(matrix[i][j]);
			}
			sb.append(LINE_SEPARATOR);
		}
		return sb.toString();
	}

	private void markInMatrix(char matrix[][], char c, int[] XY, int minX, int maxY) {
		int X = XY[0];
		int Y = XY[1];
		int effX = X - minX;
		int effY = maxY - Y;
		if (matrix[effY][effX] == CURR_POS.getPositionMarketChar()) {// do not
																		// overwrite
																		// curr
																		// pos
			return;
		}
		if ((matrix[effY][effX] == NS_PATH.getPositionMarketChar() && c == EW_PATH.getPositionMarketChar())
				|| (matrix[effY][effX] == EW_PATH.getPositionMarketChar() && c == NS_PATH.getPositionMarketChar())) {
			matrix[effY][effX] = TURNING_PT.getPositionMarketChar();
		} else {
			matrix[effY][effX] = c;
		}
	}

	private List<int[]> deriveBoundary() {
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (int[] coord : this.coordinatesVisited) {
			if (coord[0] < minX) {
				minX = coord[0];
			}
			if (coord[0] > maxX) {
				maxX = coord[0];
			}
			if (coord[1] < minY) {
				minY = coord[1];
			}
			if (coord[1] > maxY) {
				maxY = coord[1];
			}
		}
		List<int[]> boundary = new ArrayList<int[]>(4);
		boundary.add(new int[] { minX, minY });
		boundary.add(new int[] { maxX, minY });
		boundary.add(new int[] { maxX, maxY });
		boundary.add(new int[] { minX, maxY });
		return boundary;
	}

	public MarsRover turnLeft() {
		replaceChar(TURNING_PT.getPositionMarketChar());
		if (isFacingEast) {
			this.isFacingNorth = true;
			this.isFacingEast = false;
			this.isFacingSouth = false;
			this.isFacingWest = false;
		} else if (isFacingNorth) {
			this.isFacingNorth = false;
			this.isFacingEast = false;
			this.isFacingSouth = false;
			this.isFacingWest = true;
		} else if (isFacingWest) {
			this.isFacingNorth = false;
			this.isFacingEast = false;
			this.isFacingSouth = true;
			this.isFacingWest = false;
		} else {
			this.isFacingNorth = false;
			this.isFacingEast = true;
			this.isFacingSouth = false;
			this.isFacingWest = false;
		}
		return this;
	}

	private void replaceChar(char chr) {
		Character c = this.moveStack.pop();
		if (c != null && (c.charValue() == EW_PATH.getPositionMarketChar()
				|| c.charValue() == NS_PATH.getPositionMarketChar())) {
			this.moveStack.push(chr);
		} else {
			this.moveStack.push(c);
		}
	}

	public MarsRover turnRight() {
		replaceChar(TURNING_PT.getPositionMarketChar());
		if (isFacingEast) {
			this.isFacingNorth = false;
			this.isFacingEast = false;
			this.isFacingSouth = true;
			this.isFacingWest = false;
		} else if (isFacingNorth) {
			this.isFacingNorth = false;
			this.isFacingEast = true;
			this.isFacingSouth = false;
			this.isFacingWest = false;
		} else if (isFacingWest) {
			this.isFacingNorth = true;
			this.isFacingEast = false;
			this.isFacingSouth = false;
			this.isFacingWest = false;
		} else {
			this.isFacingNorth = false;
			this.isFacingEast = false;
			this.isFacingSouth = false;
			this.isFacingWest = true;
		}
		return this;
	}


	public MarsRover moveForward() {

		if (isFacingEast) {
			this.currX++;
			this.moveStack.push(EW_PATH.getPositionMarketChar());
		} else if (isFacingWest) {
			this.currX--;
			this.moveStack.push(EW_PATH.getPositionMarketChar());
		} else if (isFacingNorth) {
			this.currY++;
			this.moveStack.push(NS_PATH.getPositionMarketChar());
		} else {
			this.currY--;
			this.moveStack.push(NS_PATH.getPositionMarketChar());
		}
		this.coordinatesVisited.add(new int[] { this.currX, this.currY });
		return this;
	}


	public MarsRover takeSample() {
		moveStack.pop();
		moveStack.push(CommandEnum.TAKE_SAMPLE.getCommandChar());
		return this;
	}
}
