package de.haukerehfeld.hlisp;

import java.util.*;
import java.io.*;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.semantics.RootType;
import de.haukerehfeld.hlisp.semantics.TypeDefiner;
import de.haukerehfeld.hlisp.semantics.BodyResolver;
import de.haukerehfeld.hlisp.semantics.TypeResolver;

public class Lisp {
	public static void main(String[] args) throws Exception {
		Lisp l = new Lisp();
		l.run(args);
		
	}

	public void run(String[] args) throws Exception {
		List<File> sourcefiles = checkFileArgs(Arrays.asList(args));
		if (sourcefiles.size() < 1) {
			System.err.println("No input files given.");
			System.exit(1);
		}

		List<AstRoot> rootnodes = parse(sourcefiles);
		RootType rootType = new RootType();
		

		PrintAstVisitor printer = new PrintAstVisitor();
		for (AstRoot rootnode: rootnodes) {
			rootnode.dump("");
			
			System.out.println("Defining Types...");
			TypeDefiner definer = new TypeDefiner(rootType);
			rootnode.jjtAccept(definer, null);
		}

		TypeResolver s = new TypeResolver();
		s.solve((RootType) rootType);

		new BodyResolver().resolve(rootType);

		String output = rootType.emit(new JavaEmitter());
		System.out.println(output);

		File file = new File("../tmp/de/haukerehfeld/hlisp/Root.java");
		try{
			file.getParentFile().mkdirs();
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Written " + file.getAbsolutePath());
	}

	private List<File> checkFileArgs(List<String> args) {
		List<File> sourcefiles = new ArrayList<File>();
		for (String file: args) {
			File f = new File(file);
			if (!f.exists() || !f.canRead()) {
				System.err.println("Cannot read '" + f + "'");
				continue;
			}
			sourcefiles.add(f);
		}
		return sourcefiles;
	}

	private List<AstRoot> parse(List<File> sourcefiles) throws
		FileNotFoundException,
		ParseException {
		Parser p = new Parser();
		List<AstRoot> rootnodes = new ArrayList<AstRoot>(sourcefiles.size());
		for (File file: sourcefiles) {
			InputStream src = new BufferedInputStream(new FileInputStream(file));
			rootnodes.add(p.parse(src));
		}
		return rootnodes;
	}

/**
 * Fetch the entire contents of a text file, and return it in a String.
 * This style of implementation does not throw Exceptions to the caller.
 *
 * @param aFile is a file which already exists and can be read.
 */
	static public String getContents(File aFile) {
		StringBuilder contents = new StringBuilder();
		
		try {
//use buffering, reading one line at a time
//FileReader always assumes default encoding is OK!
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null;
				while (( line = input.readLine()) != null){
					contents.append(line);
					contents.append("\n");
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		
		return contents.toString();
	}	
}