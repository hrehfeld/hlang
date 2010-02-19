import java.io.*;

public class Root {
	private class Void {
	}

	private interface Function<R> {
		public R _hlisp_run();
	}

	private class Int {
		private Integer value;
		
		public Int(int value) {
			this.value = new Integer(value);
		}

		public _hlisp_escape_plusplus _hlisp_escape_plusplus() {
			return new _hlisp_escape_plusplus();
		}
		public class _hlisp_escape_plusplus implements Function<Void> {
			public Void _hlisp_run() {
				value++;
				return null;
			}
		}

		public _hlisp_escape_smallerthan _hlisp_escape_smallerthan(Int other) {
			return new _hlisp_escape_smallerthan(other);
		}
		public class _hlisp_escape_smallerthan implements Function<Boolean> {
			private final Int other;

			public _hlisp_escape_smallerthan(Int other) {
				this.other = other;
			}
			
			public Boolean _hlisp_run() {
				return value < other.value;
			}
		}

	}

	private class _hlisp_reserved_while implements Function<Void> {
		private final Function<Boolean> condition;
		private final Function<?> body;
		
		public _hlisp_reserved_while(Function<Boolean> condition, Function<?> body) {
			this.condition = condition;
			this.body = body;
		}
		
		@Override public Void _hlisp_run() {
			while (condition._hlisp_run()) {
				body._hlisp_run();
			}
			return null;
		}
	}

	// user code


	public main main(Int param) {
		return new main(param);
	}
	private class main implements Function<Void> {
		private final Int param;

		Int i = new Int(0);

		public main(Int param) {
			this.param = param;
		}
		
		@Override public Void _hlisp_run() {
			new _hlisp_reserved_while(i._hlisp_escape_smallerthan(param),
			                          new Function<Void>() {
			                              public Void _hlisp_run() {
											  new println(System.out, "iteration")._hlisp_run();
											  i._hlisp_escape_plusplus()._hlisp_run();
											  return null;
										  }
			})._hlisp_run();
			return null;
		}
	}

	private class HelloWorldClass implements Function<Void> {
		private Int count = new Int(5);
		
		@Override public Void _hlisp_run() {return null;}

		public class run implements Function<Void> {
			public Void _hlisp_run() {
				println(System.out, "Hello World!" + count)._hlisp_run();
				return null;
			}
		}
	}

	public _hlisp_escape_plus _hlisp_escape_plus(String lhs, String rhs) {
		return new _hlisp_escape_plus(lhs, rhs);
	}
	private class _hlisp_escape_plus implements Function<String> {
		private final String lhs;
		private final String rhs;
		
		public _hlisp_escape_plus(String lhs, String rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		@Override public String _hlisp_run() {
			return lhs.concat(rhs);
		}
	}

	public println println(PrintStream out, String s) {
		return new println(out, s);
	}
	private class println implements Function<Void> {
		private final PrintStream out;
		private final String s;
		
		public println(PrintStream out, String s) {
			this.out = out;
			this.s = s;
		}
		
		public Void _hlisp_run() {
			System.out.print(_hlisp_escape_plus(s, "\n")._hlisp_run());
			return null;
		}
	}
	public Object _hlisp_run() {
		main(new Int(5))._hlisp_run();
		
		return null;
	}

	public static void main(String[] args) {
		new Root()._hlisp_run();
	}
}