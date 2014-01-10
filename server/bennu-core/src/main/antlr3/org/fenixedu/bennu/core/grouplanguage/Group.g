grammar Group;

options {
    output=AST;
    ASTLabelType=CommonTree;
}

tokens {
	USERS; USERNAME; EXPRESSION; ANONYMOUS; ANYONE; NOBODY; LOGGED; CUSTOM; DYNAMIC;
}

@parser::header {
package org.fenixedu.bennu.core.grouplanguage;
}

@lexer::header {
package org.fenixedu.bennu.core.grouplanguage;
}

definition
	:	group EOF!
	;

group
	:	'anonymous' -> ANONYMOUS
	|	'anyone' -> ANYONE
	|	'nobody' -> NOBODY
	|	'logged' -> LOGGED
	|	users
	|	composition
	|	negation
	|	custom
	|	dynamic
	;

composition
	:	'('! g+=group (('&'^ | '|'^ | '-'^) g+=group)+ ')'!
	;
	
negation
	:	'!'^ g=group
	;

custom
	:	op=IDENTIFIER('(' arg+=IDENTIFIER (',' arg+=IDENTIFIER)* ')')?
	->	^(CUSTOM $op $arg*)
	;

dynamic
	:	'#' name+=IDENTIFIER
	->	^(DYNAMIC $name+)
	;

users
	:	'U(' u+=username (',' u+=username)* ')'
	->	^(USERS $u+)
	;

username
	:	IDENTIFIER
	->	^(USERNAME IDENTIFIER)
	;

IDENTIFIER
    :   ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')+
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;
