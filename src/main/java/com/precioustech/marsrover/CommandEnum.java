package com.precioustech.marsrover;

public enum CommandEnum {

	GO_STRAIGHT('s'),
	GO_RIGHT('r'),
	GO_LEFT('l'),
	TAKE_SAMPLE('S');

	private final char commandChar;

	private CommandEnum(char commandChar) {
		this.commandChar = commandChar;
	}

	public char getCommandChar() {
		return this.commandChar;
	}

	public static CommandEnum deriveCommand(char command) {
		for (CommandEnum cmd : CommandEnum.values()) {
			if (cmd.getCommandChar() == command) {
				return cmd;
			}
		}
		return null;
	}
}
