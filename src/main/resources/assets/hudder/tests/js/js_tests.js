|TEST:

@name=ValueGetter overriding all field access
@mode=normal

input:
	function topleft() {
		let value_getter = get("value_getter");
		return value_getter.original + "   " + value_getter.method1();
	}
end_input;

output:
	undefined   1
end_output;

END_TEST;