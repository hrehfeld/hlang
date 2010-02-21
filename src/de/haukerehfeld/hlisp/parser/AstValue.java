package de.haukerehfeld.hlisp.parser;

public interface AstValue<T> {
	/**
	 * get value
	 */
	public T getValue();
	
	/**
	 * set value
	 */
	public void setValue(T value);
}
