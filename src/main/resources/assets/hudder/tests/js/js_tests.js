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



|TEST:

@name=No Access Class
@mode=normal

input:
	function topleft() {
		return get("JavaTestNoAccess");
	}
end_input;

output:
	undefined
end_output;

END_TEST;



|TEST:

@name=No Access Inherited Class
@mode=normal

input:
	function topleft() {
		return get("JavaTestInheritedNoAccess");
	}
end_input;

output:
	undefined
end_output;

END_TEST;



|TEST:

@name=No Access Field
@mode=normal

input:
	function topleft() {
		return get("JavaTestPartialNoAccess").hiddenField;
	}
end_input;

output:
	undefined
end_output;

END_TEST;



|TEST:

@name=No Access Method
@mode=error
@exception_type=ExecutionException

input:
	function topleft() {
		return get("JavaTestPartialNoAccess").hiddenMethod();
	}
end_input;

END_TEST;



|TEST:

@name=No Access Visible Members
@mode=normal

input:
	function topleft() {
		let obj = get("JavaTestPartialNoAccess");
		return obj.visibleField + " " + obj.visibleMethod();
	}
end_input;

output:
	visible visible
end_output;

END_TEST;



|TEST:

@name=No Access Field Value
@mode=normal

input:
	function topleft() {
		return get("JavaTestPartialNoAccess").noAccessField;
	}
end_input;

output:
	undefined
end_output;

END_TEST;



|TEST:

@name=No Access Return Value
@mode=normal

input:
	function topleft() {
		return get("JavaTestPartialNoAccess").getNoAccessObject();
	}
end_input;

output:
	undefined
end_output;

END_TEST;
