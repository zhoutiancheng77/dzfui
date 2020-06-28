grammar TaxFormula;

options {
    language = Java;
}

// 测试表达式：
// 1+2*3
// 'abc' + "def" + 12 + 1.3
// 'abc' + "def" + (12 - 1.3)
// qcLines.get(splitString(R1C1,2)).get("sl") + report.vsoccrecode
// mi(c('zz'), d.e(f.xx, h(1)))

// 表达式
expr: expr OP1 expr // 先乘除，后加减
    | expr OP2 expr
    | expr1
    | '('expr')';
OP1: '*'|'/';
OP2: '+'|'-'|'&';

/*
cobj: INT // 整数
    | NUM       // 浮点数
    | STR       // 字符串
    | CELL      // 单元格
    | ID        // 上下文中的对象或预置对象，如：report、corp、qcLines、this等
    | method        // 内置函数（如iif()、trim()、splitString()等）
    // 支持属性或方法的级联调用，如：a.b.c、a.b.f()、a.e().c、a.e().f()
    | cobj'.'method  // 对象的属性
    | cobj'.'field   // 对象的方法
    ;
*/

// “单个”表达式
expr1: INT // 整数
    | NUM       // 浮点数
    | STR       // 字符串
    | sobj      // 一般对象
    | cobj      // 计算对象
    ;

// 一般对象。包括：单元格、上下文中的对象或预置对象
sobj: CELL      // 单元格
    | ID        // 上下文中的对象或预置对象，如：report、corp、qcLines、this等
    ;

// 计算对象。包括：对象的属性、对象的方法调用。暂不包含简单类型如INT、STR等
cobj: method    // 内置函数（如iif()、trim()、splitString()等）
    | sobj'.'method
    | sobj'.'field
    // 支持属性或方法的级联调用，如：a.b.c、a.b.f()、a.e().c、a.e().f()
    | cobj'.'method
    | cobj'.'field
;


field: ID;
method: funcName'('params')';
funcName: ID;
params: (param(','param)*)?; // 支持函数嵌套，即参数可以是其他复合表达式，如其他函数调用、对象的属性、方法等。例如：ab(c('a'), d.e(f.xx, h(1)))
param: expr;
CELL: [Rr](INT|[a-zA-Z])[Cc](INT|[a-zA-Z]); // 如：r10c2、rxc3、r1cy
INT: [0-9]+;
NUM: [0-9]*'.'[0-9]*;
STR: '"'(~('"'))*'"'|'\''(~('\''))*'\'';
//STR2: '\'' ('\'\'' | ~('\''))* '\'';
ID: [a-zA-Z0-9|'_']+;
WS: [ \t]+ -> skip;
