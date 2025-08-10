grammar AGENT_GRAMMAR;

//--- LEXER: ---
COMMENT: ('//' .*? ('\r' | '\n' | EOF) | '/*' .*? '*/') -> skip;
WS: [ \t\r\n]+ -> skip;

PERIOD: '.';
COMMA: ',';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
ASSIGNMENT: '=';
PLUS: '+';
MIN: '-';
MUL: '*';
DIV: '/';
REMAINDER: '%';
POW: '^';
BRACKET_OPEN: '(';
BRACKET_CLOSE: ')';
QUESTION_MARK: '?';

EQUAL_TO: '==';
NOT_EQUAL_TO: '!=' | '<>';
GREATER_THAN: '>';
SMALLER_THAN: '<';
GREATER_OR_EQUAL_THAN: '>=';
SMALLER_OR_EQUAL_THAN: '<=';

NOT: '!';
AND: '&&';
OR: '||';

FUNCTION_NAME: [a-z] [A-Za-z]*;
VALUE_NAME: [A-Z] [A-Za-z]*;
INTEGER: [0-9]+;
NUMBER: INTEGER [.,] [0-9]+;


//--- PARSER: ---
agent: line* EOF;

line: valueIdentifier ASSIGNMENT operation SEMICOLON;

operation
    : BRACKET_OPEN operation BRACKET_CLOSE #bracketOperation
    | NOT operation #notOperation
    | operation POW operation#powerOperation
    | operation (MUL | DIV | REMAINDER) operation #multiplicativeOperation
    | operation (PLUS | MIN) operation #additiveOperation
    | operation (GREATER_OR_EQUAL_THAN | GREATER_THAN | SMALLER_OR_EQUAL_THAN | SMALLER_THAN) operation #relationalOperation
    | operation (EQUAL_TO | NOT_EQUAL_TO) operation #equalityOperation
    | operation AND operation #andOperation
    | operation OR operation #orOperation
    | operation QUESTION_MARK operation COLON operation #ternaryOperation
    | (valueReference | number | function) #terminalOperation
    ;

number: (PLUS | MIN)? (INTEGER | NUMBER);

function: FUNCTION_NAME BRACKET_OPEN functionArguments? BRACKET_CLOSE;
functionArguments: operation (COMMA operation)*;

valueReference: valueIdentifier valueRange?;
valueIdentifier: (VALUE_NAME PERIOD)? VALUE_NAME;
valueRange: BOX_BRACKET_OPEN number (COLON number?)? BOX_BRACKET_CLOSE;
