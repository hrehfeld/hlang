import java.io.*;

public class Root {
	private java.lang.String[] args;

	public Root(java.lang.String[] args) {
		this.args = args;
	}
	

	public Void _hlisp_run() {
		List l = new List();
		for (java.lang.String arg: args) {
			l._hlisp_escape_plusequal(String(arg))._hlisp_run();
		}

		MyNamespace()._hlisp_run()._hlisp_reserved_main(Int(5))._hlisp_run();
		return null;
	}

	public static void main(java.lang.String[] args) {
		new Root(args)._hlisp_run();
	}

	private class Void {
	}

	private interface Function<R> {
		public R _hlisp_run();
	}

	public List List() { return new List(); }
	class List implements Function<List> {
		private java.util.ArrayList list = new java.util.ArrayList();

		public List _hlisp_run() {
			return this;
		}

		public _hlisp_escape_plusequal _hlisp_escape_plusequal(java.lang.Object element) { return new _hlisp_escape_plusequal(element); }
		class _hlisp_escape_plusequal implements Function<List> {
			private java.lang.Object element;

			public _hlisp_escape_plusequal(java.lang.Object element) {
				this.element = element;
			}

			public List _hlisp_run() {
				list.add(element);
				return List.this;
			}
		}

		public get get(Int i) { return new get(i); }
		class get implements Function<java.lang.Object> {
			private Int i;

			public get(Int i) {
				this.i = i;
			}

			public java.lang.Object _hlisp_run() {
				return list.get(i.toInt()._hlisp_run());
			}
		}
		
	}

	public _hlisp_reserved_while _hlisp_reserved_while(Function<Boolean> condition, Function<Void> body) { return new _hlisp_reserved_while(condition, body); }
	public class _hlisp_reserved_while implements Function<Void> {
		private Function<Boolean> condition;
		private Function<Void> body;

		public _hlisp_reserved_while(Function<Boolean> condition, Function<Void> body) {
			this.condition = condition;
			this.body = body;
		}

		public Void _hlisp_run() {
			while (condition._hlisp_run()) { body._hlisp_run(); }
			return null;
		}
	}

	public Int Int(int value) { return new Int(value); }
	public class Int implements Function<Int> {
		private int value;
		
		public Int(int value) {
			this.value = value;
		}

		public Int _hlisp_run() {
			return this;
		}

		public _hlisp_escape_plus _hlisp_escape_plus(Int other) { return new _hlisp_escape_plus(other); }
		class _hlisp_escape_plus implements Function<Int> {
			private Int other;

			public _hlisp_escape_plus(Int other) {
				this.other = other;
			}

			public Int _hlisp_run() {
				return Int(value + other.value);
			}
		}
		public _hlisp_escape_star _hlisp_escape_star(Int other) { return new _hlisp_escape_star(other); }
		class _hlisp_escape_star implements Function<Int> {
			private Int other;

			public _hlisp_escape_star(Int other) {
				this.other = other;
			}

			public Int _hlisp_run() {
				return Int(value * other.value);
			}
		}


		public _hlisp_escape_plusplus _hlisp_escape_plusplus() { return new _hlisp_escape_plusplus(); }
		public class _hlisp_escape_plusplus implements Function<Void> {
			public Void _hlisp_run() {
				value++;
				return null;
			}
		}

		public _hlisp_escape_smallerthan _hlisp_escape_smallerthan(Int other) { return new _hlisp_escape_smallerthan(other); }
		public class _hlisp_escape_smallerthan implements Function<Boolean> {
			private final Int other;

			public _hlisp_escape_smallerthan(Int other) {
				this.other = other;
			}
			
			public Boolean _hlisp_run() {
				return value < other.value;
			}
		}

		public toInt toInt() { return new toInt(); }
		class toInt implements Function<java.lang.Integer> {
			public java.lang.Integer _hlisp_run() {
				return value;
			}
		}
		public _hlisp_reserved_toString _hlisp_reserved_toString() { return new _hlisp_reserved_toString(); }
		class _hlisp_reserved_toString implements Function<String> {
			public String _hlisp_run() {
				return String(java.lang.String.valueOf(value));
			}
		}
	}

	public String String(java.lang.String value) { return new String(value); }
	class String implements Function<String> {
		private java.lang.String value;

		public String(java.lang.String value) {
			this.value = value;
		}

		public String _hlisp_run() {
			return this;
		}

		public _hlisp_escape_plus _hlisp_escape_plus(String other) { return new _hlisp_escape_plus(other);	}
		class _hlisp_escape_plus implements Function<String> {
			private String other;

			public _hlisp_escape_plus(String other) {
				this.other = other;
			}

			public String _hlisp_run() {
				return String(value + other.value);
			}
		}

		public print print() { return new print(); }
		public class print implements Function<Void> {
			public Void _hlisp_run() {
				System.out.println(value);
				return null;
			}
		}

		public println println() { return new println(); }
		public class println implements Function<Void> {
			public Void _hlisp_run() {
				return _hlisp_escape_plus(String("\n"))._hlisp_run()
				    .print()._hlisp_run();
			}
		}
	}

	public MyNamespace MyNamespace() { return new MyNamespace(); }
	public class MyNamespace implements Function<MyNamespace> {
		public MyNamespace _hlisp_run() {
			return this;
		}
		
		public HelloWorldClass HelloWorldClass() { return new HelloWorldClass(); }
		class HelloWorldClass implements Function<HelloWorldClass> {
			private Int count = Int(5);

			public HelloWorldClass _hlisp_run() {
				return this;
			}

			public run run() {
				return new run();
			}
			class run implements Function<Void> {
				public Void _hlisp_run() {
					return String("Hello World!")._hlisp_escape_plus(count._hlisp_reserved_toString()._hlisp_run())._hlisp_run()
					    .print()._hlisp_run();
				}
			}
			public function function(Int a, Int b) { return new function(a, b); }
			class function implements Function<Int> {
				private Int a;
				private Int b;

				public function(Int a, Int b) {
					this.a = a;
					this.b = b;
				}

				public Int _hlisp_run() {
					return a._hlisp_escape_plus(b)._hlisp_run();
				}
			}
		}
		
		public _hlisp_reserved_main _hlisp_reserved_main(Int param) { return new _hlisp_reserved_main(param); }
		private class _hlisp_reserved_main implements Function<Void> {
			private final Int param;

			public _hlisp_reserved_main(Int param) {
				this.param = param;
			}
			
			@Override public Void _hlisp_run() {
				_hlisp_reserved_while(new Function<Boolean>() {
				        public Boolean _hlisp_run() {
							return i._hlisp_escape_smallerthan(param)._hlisp_run();
						}
				    },
				    new Function<Void>() {
						public Void _hlisp_run() {
							String("iteration").println()._hlisp_run();
							i._hlisp_escape_plusplus()._hlisp_run();
							return null;
						}
					})
				    ._hlisp_run();
				return null;
			}

			
			private Int i = Int(0);
			private Int eval = Int(5)._hlisp_escape_plus(new Function<Int>() {
			        @Override public Int _hlisp_run() {
						return i._hlisp_escape_star(new Int(4))._hlisp_run();
					}
			    }._hlisp_run())._hlisp_run();
			public error error() { return new error(); }
			public class error implements Function<Int> {
				@Override public Int _hlisp_run() {
					return Int(0);
				}
			}
			private List k = List();
			private final List list = List()._hlisp_run()
			    ._hlisp_escape_plusequal("some")._hlisp_run()
			    ._hlisp_escape_plusequal("elements")._hlisp_run();

			public evalFun evalFun() { return new evalFun(); }
			class evalFun implements Function<Int> {
				public Int _hlisp_run() {
					return Int(5)._hlisp_escape_plus(i._hlisp_escape_star(Int(4))._hlisp_run())._hlisp_run();
				}
			}

			private final HelloWorldClass helloClass = HelloWorldClass();

			public helloFun helloFun(Int a) { return new helloFun(a); }
			public class helloFun implements Function<HelloWorldClass.function>{
				private Int a;

				public helloFun(Int a) {
					this.a = a;
				}
				
				@Override public HelloWorldClass.function _hlisp_run() {
					return helloClass.function(a, Int(2));
				}
			}
		}
	}
}