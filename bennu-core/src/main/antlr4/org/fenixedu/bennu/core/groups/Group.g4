grammar Group;

parse
    : expression EOF
    ;

expression
    : minus
    ;

minus
    : or ('-' or)*
    ;

or
    : and ('|' and)*
    ;

and
    : not ('&' not)*
    ;

not
    : '!' atom
    |  atom
    ;

atom
    : link
    | function
    | '(' expression ')'
    ;

link
    : '#' IDENTIFIER
    ;

function
    : IDENTIFIER ('(' argument (',' argument)* ')')?
    ;

argument
    : (IDENTIFIER '=')? (value | '[' (value (',' value)*)? ']')
    ;

value
    : IDENTIFIER
    | STRING
    ;

STRING : '\'' (ESC | ~[\'\\])* '\'' ;

fragment ESC :   '\\' ([\'\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;

IDENTIFIER : ('a'..'z'|'A'..'Z'|'_'|'0'..'9')+;

WS : [ \t\r\n]+ -> skip ;
