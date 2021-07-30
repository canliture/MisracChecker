package kr.ac.jbnu.ssel.misrac.rulesupport;

/**
 * 
 * @author "STKIM"
 *
 */
public class MessageFactory {

	private static MessageFactory instance;

	private MessageFactory() {
	}

	public static MessageFactory getInstance() {
		if (instance == null) {
			instance = new MessageFactory();
		}

		return instance;
	}

	public String getMessage(int messageNum) {
		String msg = null;

		switch (messageNum) {
		case 1011:
			msg = "[C99] Use of '//' comment.";
			break;
		case 5126:
			msg = "The library functions abort, exit, getenv and system from library <stdlib.h> shall not be used.";
			break;
		case 750:
			msg = "A union type specifier has been defined.";
			break;
		case 759:
			msg = "An object of union type has been defined.";
			break;
		case 777:
			msg = "[U] External identifier does not differ from other identifier(s) (e.g. '%s') within the specified number of significant characters.";
			break;
		case 779:
			msg = "[U] Identifier does not differ from other identifier(s) (e.g. '%s') within the specified number of significant characters.";
			break;
		case 1506:
			msg = "The identifier '%1s' is declared as a typedef and is used elsewhere for a different kind of declaration.";
			break;
		case 1507:
			msg = "	'%1s' is used as a typedef for different types.";
			break;
		case 1508:
			msg = "The typedef '%1s' is declared in more than one location.";
			break;
		case 3116:
			msg = "Unrecognized #pragma arguments '%s' This #pragma directive has been ignored.";
			break;
		case 3448:
			msg = "	Declaration of typedef '%s' is not in a header file although it is used in a definition or declaration with external linkage.";
			break;
		case 3417:
			msg = "	The comma operator has been used outside a 'for' statement.";
			break;
		case 3418:
			msg = "	The comma operator has been used in a 'for' statement.";
			break;
		case 5124:
			msg = "	The input/output library <stdio.h> shall not be used in production code.";
			break;
		case 2050:
			msg = "	The 'int' type specifier has been omitted from a function declaration.";
			break;
		case 2051:
			msg = " The 'int' type specifier has been omitted from an object declaration.";
			break;
		case 2001:
			msg = "	A 'goto' statement has been used.";
		case 2462:
			msg = " The variable initialized in the first expression of this 'for' statement is not the variable identified as the 'loop control variable' (%s).";
			break;
		case 2463:
			msg = " The variable incremented in the third expression of this 'for' statement is not the variable identified as the 'loop control variable' (%s).";
			break;
		case 2889:
			msg = "	This function has more than one 'return' path.";
			break;
		case 5119:
			msg = "	The error indicator errno shall not be used.";
			break;
		case 5125:
			msg = " The library functions atof, atoi and atol from library <stdlib.h> shall not be used.";
			break;
		case 5120:
			msg = " The macro offsetof, in library <stddef.h>, shall not be used. ";
			break;
		case 5123:
			msg = " The signal handling facilities of <signal.h> shall not be used. ";
			break;
		case 3659:
			msg = "	Unnamed zero-width bit-field declared with a signed type.";
			break;
		case 3660:
			msg = " Named bit-field consisting of a single bit declared with a signed type.";
			break;
		case 3665:
			msg = " Unnamed bit-field consisting of a single bit declared with a signed type.";
			break;
		case 0771:
			msg = "More than one 'break' statement has been used to terminate this iteration statement.";
			break;
		case 0770:
			msg = "A 'continue' statement has been used.";
			break;
		case 2002:
			msg = "No 'default' label found in this 'switch' statement.";
			break;
		case 2009:
			msg = "This 'default' label is not the final 'case' label within the 'switch' block.";
			break;
		case 2212:
			msg = "Body of control statement is not enclosed within braces.";
			break;

		case 2214:
			msg = "Body of control statement is on the same line and is not enclosed within braces.";
			break;

		case 5013:
			msg = "Use of basic type ''{0}''.";
			break;

		case 3002:
			msg = "Defining '%s()' with an identifier list and separate parameter declarations is an obsolescent feature.";
			break;
		case 3334:
			msg = "	This declaration of '%s' hides a more global declaration.";
			break;
		case 3335:
			msg = "No function declaration. Implicit declaration inserted: 'extern int %s();'.";
			break;
		case 3450:
			msg = "Function '%s', with internal linkage, is being defined without a previous declaration.";
			break;
		case 0624:
			msg = "Function '%s' is declared using typedefs which are different to those in a previous declaration.";
			break;
		case 1331:
			msg = "Type or number of arguments doesn't match previous use of the function.";
			break;
		case 1332:
			msg = "Type or number of arguments doesn't match prototype found later.";
			break;
		case 1333:
			msg = "Type or number of arguments doesn't match function definition found later.";
			break;
		case 3320:
			msg = "Type of argument no. %s differs from its type in definition of function.";
			break;
		case 3675:
			msg = "Function parameter declared with type qualification which differs from previous declaration.";
			break;
		case 2547:
			msg = "	This declaration of tag '%s' hides a more global declaration.";
			break;
		case 2004:
			msg = "	No concluding 'else' exists in this 'if'-'else'-'if' statement.";
			break;
		case 3402:
			msg = " Braces are needed to clarify the structure of this 'if'-'if'-'else' statement.";
			break;
		case 3601:
			msg = "Trigraphs (??x) are an ISO feature.";
			break;
		case 5118:
			msg = "Dynamic heap memory allocation shall not be used.";
			break;
		case 5127:
			msg = "The time handling functions of library <time.h> shall not be used.";
			break;
		case 5122:
			msg = "The setjmp macro and the longjmp function shall not be used.";
			break;
		case 841:
			msg = "Using '#undef'.";
			break;
		case 842:
			msg = "	Using #define or #undef inside a function.";
			break;
		case 813:
			msg = "	[U] Using any of the characters ' \" or /* in '#include <%s>' gives undefined behaviour.";
			break;
		case 814:
			msg = "	[U] Using the characters ' or /* in '#include \"%s\"' gives undefined behaviour.";
			break;
		case 831:
			msg = "	[E] Use of '\\' in this '#include' line is a PC extension - this usage is non-portable.";
			break;
		case 3415:
			msg = "Right hand operand of '&&' or '||' is an expression with possible side effects.";
			break;
		case 3398:
			msg = "Extra parentheses recommended. A function call, array subscript, or member operation is the operand of a logical && or ||.";
			break;
		case 3399:
			msg = "Extra parentheses recommended. A unary operation is the operand of a logical && or ||.";
			break;
		case 341:
			msg = "Using the stringify operator '#'.";
			break;
		case 342:
			msg = "Using the glue operator '##'.";
			break;
		case 5087:
			msg = "#include statements in a file should only be preceded by other preprocessor directives or comments.";
			break;
		case 809:
			msg = "	[U] The '#include' preprocessing directive has not been followed by <h-char-sequence> or \"s-char-sequence\".";
			break;
		case 3317:
			msg = "	'#if...' not matched by '#endif' in included file. This is probably an error.";
			break;
		case 3318:
			msg = "	'#else'/'#elif'/'#endif' in included file matched '#if...' in parent file. This is probably an error.";
			break;
		case 544:
			msg = "	[U] The value of an incomplete 'union' may not be used.";
			break;
		case 545:
			msg = "	[U] The value of an incomplete 'struct' may not be used.";
			break;
		case 623:
			msg = "	[U] '%s' has incomplete type and no linkage - this is undefined.";
			break;
		case 636:
			msg = "	[U] There are no named members in this 'struct' or 'union'.";
			break;
		case 3322:
			msg = "Logical operators should not be confused with bitwise operators.";
			break;
		case 310:
			msg = "	Casting to different object pointer type.";
			break;
		case 316:
			msg = "	[I] Cast from a pointer to void to a pointer to object type.";
			break;
		case 317:
			msg = "	[I] Implicit conversion from a pointer to void to a pointer to object type.";
			break;
		case 3315:
			msg = "This 'switch' statement contains only a single path - it is redundant.";
			break;
		case 735:
			msg = "Using relational or logical operators in a 'switch' expression is usually a programming error.";
			break;
		case 2019:
			msg = "'Switch' label is located within a nested code block.";
			break;
		case 3440:
			msg = "Using the value resulting from a ++ or -- operation.";
			break;
		case 3108:
			msg = "Nested comments are not recognized in the ISO standard.";
			break;
		case 3234:
			msg = "	Declarations precede the first label in this 'switch' construct.";
			break;
		case 2469:
			msg = "	Loop control variable in this 'for' statement, %s, is modified in the body of the loop.";
			break;
		case 1520:
			msg = "	Functions are indirectly recursive.";
			break;
		case 3670:
			msg = "	Recursive call to function containing this call.";
			break;
		case 1330:
			msg = "	The parameter identifiers in this function declaration differ from those in a previous declaration.";
			break;
		case 3001:
			msg = "	Function has been declared with an empty parameter list.";
			break;
		case 3007:
			msg = "	void has been omitted when defining a function with no parameters.";
			break;
		case 1335:
			msg = " Parameter identifiers missing in function prototype declaration.";
			break;
		case 1336:
			msg = "	Parameter identifiers missing in declaration of a function type.";
			break;
		case 3307:
			msg = " The operand of 'sizeof' is an expression with implied side effects, but they will not be evaluated.";
			break;
		case 883:
			msg = "	Include file code is not protected against repeated inclusion";
			break;
		case 3221:
			msg = "	Function with external linkage declared at block scope.";
			break;
		case 1514:
			msg = "The object '%1s' is only referenced by function '%2s', in the translation unit where it is defined";
			break;
		case 3218:
			msg = "File scope static, '%s', is only accessed in one function.";
			break;
		case 2003:
			msg = "	The preceding 'switch' clause is not empty and does not end with a 'jump' statement. Execution will fall through.";
			break;
		case 2020:
			msg = "	Final 'switch' clause does not end with an explicit 'jump' statement.";
			break;
		case 780:
			msg = "Another identifier '%s' is already in scope in a different namespace.";
			break;
		case 781:
			msg ="'%s' is being used as a structure/union member as well as being a label, tag or ordinary identifier.";
			break;
		case 422:
			msg = "Function call contains fewer arguments than prototype specifies.";
			break;
		case 423:
			msg = "Function call contains more arguments than prototype specifies.";
			break;
		case 3319:
			msg = "Function called with number of arguments which differs from number of parameters in definition.";
			break;
		case 745:
			msg = " 'return;' found in '%s()', which has been defined with a non-'void' return type.";
			break;
		case 2887:
			msg = " Function 'main' ends with an implicit 'return' statement.";
			break;
		case 2888:
			msg = "	This function has been declared with a non-void 'return' type but ends with an implicit 'return ;' statement.";
			break;
		case 3113:
			msg = " 'return' statement includes no expression but function '%s()' is implicitly of type 'int'.";
			break;
		case 3114:
			msg = " Function '%s()' is implicitly of type 'int' but ends without returning a value.";
			break;
		case 3260:
			msg = "Typedef defined with more than 2 levels of indirection.";
			break;
		case 3261:
			msg = "Member of struct/union defined with more than 2 levels of indirection.";
			break;
		case 3262:
			msg = "Object defined or declared with more than 2 levels of indirection.";
			break;
		case 3263:
			msg = "Function defined or declared with a return type which has more than 2 levels of indirection.";
			break;
		case 634:
			msg = "Bit-fields in this struct/union have not been declared explicitly as unsigned or signed.";
			break;
		case 635:
			msg = "Bit-fields in this struct/union have been declared with types other than int, signed int or unsigned int.";
			break;
		case 3684:
			msg = "Array declared with unknown size.";
			break;
		case 0235:
			msg = "[U] Unknown escape sequence.";
			break;
		case 547:
			msg ="This declaration of tag '%s' conflicts with a previous declaration. ";
			break;
		case 10001:
			msg = "No identifier name should be reused.";
			break;
		}

		return msg;
	}

}
