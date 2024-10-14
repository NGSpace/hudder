package io.github.ngspace.hudder.util;

/**
 * Thrown when people think they are smarter than me and try to break the game, the user's computer or any other
 * possible security vulnerability.
 */
public class UnauthorizedFileIOException extends RuntimeException {
	public UnauthorizedFileIOException(String message) {super(message);}
	private static final long serialVersionUID = -9219393341700318452L;
}
