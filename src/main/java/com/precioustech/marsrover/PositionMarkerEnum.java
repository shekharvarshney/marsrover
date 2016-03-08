package com.precioustech.marsrover;

public enum PositionMarkerEnum {

	START_POS('X'),
	CURR_POS('*'),
	EW_PATH('-'),
	NS_PATH('|'),
	TURNING_PT('+');

	private final char markerChar;

	private PositionMarkerEnum(char markerChar) {
		this.markerChar = markerChar;
	}

	public char getPositionMarketChar() {
		return this.markerChar;
	}
}
