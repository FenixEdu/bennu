grammar Group;

options {
    output=AST;
    ASTLabelType=CommonTree;
}

tokens {
	PEOPLE; USERNAME; EXPRESSION; ANONYMOUS; ANYONE; NOBODY; LOGGED; CUSTOM;
}

@parser::header {
package pt.ist.bennu.core.grouplanguage;
}

@lexer::header {
package pt.ist.bennu.core.grouplanguage;
}

definition
	:	group EOF!
	;

group
	:	'anonymous' -> ANONYMOUS
	|	'anyone' -> ANYONE
	|	'nobody' -> NOBODY
	|	'logged' -> LOGGED
	|	people
	|	composition
	|	negation
	|	custom
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

people
	:	'P(' u+=username (',' u+=username)* ')'
	->	^(PEOPLE $u+)
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
