package de.haukerehfeld.hlisp;

import java.util.*;
import java.io.*;

import de.haukerehfeld.hlisp.parser.*;

public class Parser {
	public AstRoot parse(InputStream src) throws ParseException {
		HLispParser parser = new HLispParser(src, "UTF-8");
		AstRoot root = parser.Start();
		return root;
	}

}