package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.JavaEmitter;

public interface NativeType extends Type {
	public String getNativeName();
}