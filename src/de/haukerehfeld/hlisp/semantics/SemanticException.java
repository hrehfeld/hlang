package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import java.util.*;

public class SemanticException extends Exception {
	public SemanticException(String msg) {
		super(msg);
	}
}
