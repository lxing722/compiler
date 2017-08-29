import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a Moo program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode {
    
    // every subclass must provide an unparse operation
    public void unparse(PrintWriter p, int indent) {
    }

    // this method can be used by the unparse methods to do indenting
    protected void doIndent(PrintWriter p, int indent) {
        for (int k=0; k<indent; k++) p.print(" ");
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    // TO COMPLETE
    public DeclListNode dec;
    public ProgramNode(DeclListNode dec){
        this.dec = dec;
    }
    public void unparse(PrintWriter p, int indent) {
        dec.unparse(p, indent);
    }
}

class DeclListNode extends ASTnode {
    // TO COMPLETE
    public LinkedList<DeclNode> list;
    public DeclListNode(LinkedList<DeclNode> list){
        this.list = list;
    }
    public void unparse(PrintWriter p, int indent){
        for(Iterator<DeclNode> iter = list.iterator(); iter.hasNext();)
            iter.next().unparse(p, indent);
    }
}

class FormalsListNode extends ASTnode {
    // TO COMPLETE
    public LinkedList<FormalDeclNode> list;
    public FormalsListNode(LinkedList<FormalDeclNode> list){
        this.list = list;
    }
    public void unparse(PrintWriter p, int indent){
        for(Iterator<FormalDeclNode> iter = list.iterator(); iter.hasNext();){
            iter.next().unparse(p, indent);
            if(iter.hasNext()){
                p.print(", ");
            }
        }
    }
}

class FnBodyNode extends ASTnode {
    // TO COMPLETE
    public DeclListNode decl;
    public StmtListNode stmt;
    public FnBodyNode(DeclListNode decl, StmtListNode stmt){
        this.decl = decl;
        this.stmt = stmt;
    }
    public void unparse(PrintWriter p, int indent){
        p.print("{\n");
        decl.unparse(p, indent+4);
        stmt.unparse(p, indent+4);
        p.print("}");
    }
}

class StmtListNode extends ASTnode {
    // TO COMPLETE
    public LinkedList<StmtNode> list;
    public StmtListNode(LinkedList<StmtNode> list){
        this.list = list;
    }
    public void unparse(PrintWriter p, int indent){
        for(Iterator<StmtNode> iter = list.iterator(); iter.hasNext();)
            iter.next().unparse(p, indent);
    }
}

class ExpListNode extends ASTnode {
    // TO COMPLETE
    public LinkedList<ExpNode> list;
    public ExpListNode(LinkedList<ExpNode> list){
        this.list = list;
    }
    public void unparse(PrintWriter p, int indent){
        for(Iterator<ExpNode> iter = list.iterator(); iter.hasNext();){
            iter.next().unparse(p, indent);
            if(iter.hasNext()){
                p.print(", ");
            }
        }
    }
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
}

class VarDeclNode extends DeclNode {
    // TO COMPLETE
    public TypeNode type;
    public IdNode id;
    public int flag;
    public VarDeclNode(TypeNode type, IdNode id, int flag){
        this.type = type;
        this.id = id;
        this.flag = flag;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        this.type.unparse(p, indent);
        doIndent(p, 1);
        this.id.unparse(p, indent);
        p.print(";\n");
    }
    ///// DO NOT CHANGE THIS PART /////
    private int mySize;  // use value NOT_STRUCT if this is not a struct type
    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    // TO COMPLETE
    public TypeNode type;
    public IdNode id;
    public FormalsListNode fl;
    public FnBodyNode fn;
    public FnDeclNode(TypeNode type, IdNode id, FormalsListNode fl, FnBodyNode fn){
        this.type = type;
        this.id = id;
        this.fl = fl;
        this.fn = fn;
    }
    public void unparse(PrintWriter p ,int indent){
        type.unparse(p, indent);
        doIndent(p, 1);
        id.unparse(p, indent);
        p.print("(");
        fl.unparse(p, indent);
        p.print(")");
        doIndent(p, 1);
        fn.unparse(p, indent);
        p.print("\n");
    }
}

class FormalDeclNode extends DeclNode {
    // TO COMPLETE
    public TypeNode type;
    public IdNode id;
    public FormalDeclNode(TypeNode type, IdNode id){
        this.type = type;
        this.id = id;
    }
    public void unparse(PrintWriter p, int indent){
        type.unparse(p, indent);
        doIndent(p, 1);
        id.unparse(p, indent);
    }
}

class StructDeclNode extends DeclNode {
    // TO COMPLETE
    public IdNode id;
    public DeclListNode list;
    public StructDeclNode(IdNode id, DeclListNode list){
        this.id = id;
        this.list = list;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        p.print("struct");
        doIndent(p, 1);
        id.unparse(p, indent);
        doIndent(p, 1);
        p.print("{\n");
        list.unparse(p, indent+4);
        doIndent(p, indent);
        p.print("};\n");
    }
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
}

class IntNode extends TypeNode {
    public IntNode(){}
    public void unparse(PrintWriter p, int indent){
        p.print("int");
    }
    // TO COMPLETE
}

class BoolNode extends TypeNode {
    // TO COMPLETE
    public BoolNode(){}
    public void unparse(PrintWriter p, int indent){
        p.print("bool");
    }
}

class VoidNode extends TypeNode {
    // TO COMPLETE
    public VoidNode(){}
    public void unparse(PrintWriter p, int indent){
        p.print("void");
    }
}

class StructNode extends TypeNode {
    // TO COMPLETE
    public IdNode id;
    public StructNode(IdNode id){
        this.id = id;
    }
    public void unparse(PrintWriter p, int indent){
        p.print("struct");
        doIndent(p, 1);
        id.unparse(p, indent);
    }
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
}

class AssignStmtNode extends StmtNode {
    // TO COMPLETE
    public AssignNode assign;
    public AssignStmtNode(AssignNode assign){
        this.assign = assign;
    }
    public void unparse(PrintWriter p ,int indent){
        doIndent(p, indent);
        assign.unparse(p, indent);
        p.print(";\n");
    }
}

class PostIncStmtNode extends StmtNode {
    // TO COMPLETE
    public ExpNode exp;
    public PostIncStmtNode(ExpNode exp){
        this.exp = exp;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        exp.unparse(p, indent);
        p.print("++;\n");
    }
}

class PostDecStmtNode extends StmtNode {
    // TO COMPLETE
    public ExpNode exp;
    public PostDecStmtNode(ExpNode exp){
        this.exp = exp;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        exp.unparse(p, indent);
        p.print("--;\n");
    }
}

class ReadStmtNode extends StmtNode {
    // TO COMPLETE
    public ExpNode exp;
    public ReadStmtNode(ExpNode exp){
        this.exp = exp;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        p.print("cin >> ");
        exp.unparse(p, indent);
        p.print(";\n");
    }
}

class WriteStmtNode extends StmtNode {
    // TO COMPLETE
    public ExpNode exp;
    public WriteStmtNode(ExpNode exp){
        this.exp = exp;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        p.print("cout << ");
        exp.unparse(p, indent);
        p.print(";\n");
    }
}

class IfStmtNode extends StmtNode {
    // TO COMPLETE
    public ExpNode exp;
    public DeclListNode decl; 
    public StmtListNode stmt;
    public IfStmtNode(ExpNode exp, DeclListNode decl, StmtListNode stmt){
        this.exp = exp;
        this.decl = decl;
        this.stmt = stmt;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        p.print("if (");
        exp.unparse(p, indent);
        p.print(") {\n");
        decl.unparse(p, indent+4);
        stmt.unparse(p, indent+4);
        doIndent(p, indent);
        p.print("}\n");
    }
}

class IfElseStmtNode extends StmtNode {
    // TO COMPLETE
    public ExpNode exp;
    public DeclListNode d1, d2;
    public StmtListNode s1, s2;
    public IfElseStmtNode(ExpNode exp, DeclListNode d1, StmtListNode s1, DeclListNode d2, StmtListNode s2){
        this.exp = exp;
        this.d1 = d1;
        this.d2 = d2;
        this.s1 = s1;
        this.s2 = s2;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        p.print("if (");
        exp.unparse(p, indent);
        p.print(") {\n");
        d1.unparse(p, indent+4);
        s1.unparse(p, indent+4);
        doIndent(p, indent);
        p.print("}\n");
        doIndent(p, indent);
        p.print("else {\n");
        d2.unparse(p, indent+4);
        s2.unparse(p, indent+4);
        doIndent(p, indent);
        p.print("}\n");
    }
}

class WhileStmtNode extends StmtNode {
    // TO COMPLETE
    public ExpNode exp;
    public DeclListNode decl;
    public StmtListNode stmt;
    public WhileStmtNode(ExpNode exp, DeclListNode decl, StmtListNode stmt){
        this.exp = exp;
        this.decl = decl;
        this.stmt = stmt;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        p.print("while (");
        exp.unparse(p, indent);
        p.print(") {\n");
        decl.unparse(p, indent+4);
        stmt.unparse(p, indent+4);
        doIndent(p, indent);
        p.print("}\n");
    }
}

class CallStmtNode extends StmtNode {
    // TO COMPLETE
    public CallExpNode call;
    public CallStmtNode(CallExpNode call){
        this.call = call;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        call.unparse(p, indent);
        p.print(";\n");
    }
}

class ReturnStmtNode extends StmtNode {
    // TO COMPLETE
    public ExpNode exp;
    public ReturnStmtNode(ExpNode exp){
        this.exp = exp;
    }
    public void unparse(PrintWriter p, int indent){
        doIndent(p, indent);
        p.print("return ");
        if(exp != null)
            exp.unparse(p, indent);
        p.print(";\n");
    }
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
}

class IntLitNode extends ExpNode {
    // TO COMPLETE
    public int lineNum, charNum, intVal;
    public IntLitNode(int lineNum, int charNum, int intVal){
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.intVal = intVal;
    }
    public void unparse(PrintWriter p, int indent){
        p.print(intVal);
    }
}

class StringLitNode extends ExpNode {
    // TO COMPLETE
    public int lineNum, charNum;
    public String strVal;
    public StringLitNode(int lineNum, int charNum, String strVal){
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.strVal = strVal;
    }
    public void unparse(PrintWriter p, int indent){
        p.print(strVal);
    }
}

class TrueNode extends ExpNode {
    // TO COMPLETE
    public int lineNum, charNum;
    public TrueNode(int lineNum, int charNum){
        this.lineNum = lineNum;
        this.charNum = charNum;
    }
    public void unparse(PrintWriter p, int indent){
        p.print("true");
    }
}

class FalseNode extends ExpNode {
    // TO COMPLETE
    public int lineNum, charNum;
    public FalseNode(int lineNum, int charNum){
        this.lineNum = lineNum;
        this.charNum = charNum;
    }
    public void unparse(PrintWriter p, int indent){
        p.print("false");
    }
}

class IdNode extends ExpNode {
    public int lineNum;
    public int charNum;
    public String idVal;
    public IdNode(int lineNum, int charNum, String idVal){
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.idVal = idVal;
    }
    public void unparse(PrintWriter p, int indent){
        p.print(idVal);
    }
    // TO COMPLETE
}

class DotAccessExpNode extends ExpNode {
    // TO COMPLETE
    public ExpNode exp;
    public IdNode id;
    public DotAccessExpNode(ExpNode exp, IdNode id){
        this.exp = exp;
        this.id = id;
    }
    public void unparse(PrintWriter p, int indent){
        exp.unparse(p, indent);
        p.print(".");
        id.unparse(p, indent);
    }
}

class AssignNode extends ExpNode {
    // TO COMPLETE
    public ExpNode exp1, exp2;
    public AssignNode(ExpNode exp1, ExpNode exp2){
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    public void unparse(PrintWriter p, int indent){
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("=");
        doIndent(p, 1);
        exp2.unparse(p, indent);
    }
}

class CallExpNode extends ExpNode {
    // TO COMPLETE
    public IdNode id;
    public ExpListNode el;
    public CallExpNode(IdNode id, ExpListNode el){
        this.id = id;
        this.el = el;
    }
    public void unparse(PrintWriter p, int indent){
        id.unparse(p, indent);
        p.print("(");
        el.unparse(p, indent);
        p.print(")");
    }
}

abstract class UnaryExpNode extends ExpNode {
    // TO COMPLETE
    public ExpNode exp;
    public UnaryExpNode(ExpNode exp){
        this.exp = exp;
    }
}

abstract class BinaryExpNode extends ExpNode {
    // TO COMPLETE
    public ExpNode exp1, exp2;
    public BinaryExpNode(ExpNode exp1, ExpNode exp2){
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    // TO COMPLETE
    public UnaryMinusNode(ExpNode exp){
        super(exp);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("-");
        exp.unparse(p, indent);
    }
}

class NotNode extends UnaryExpNode {
    // TO COMPLETE
    public NotNode(ExpNode exp){
        super(exp);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("!");
        exp.unparse(p, indent);
    } 
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    // TO COMPLETE
    public PlusNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("+");
        doIndent(p, 1);
        exp2.unparse(p, indent);
    }
}

class MinusNode extends BinaryExpNode {
    // TO COMPLETE
    public MinusNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("-");
        doIndent(p, 1);
        exp2.unparse(p, indent);
    }
}

class TimesNode extends BinaryExpNode {
    // TO COMPLETE
    public TimesNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("*");
        doIndent(p, 1);
        exp2.unparse(p, indent);
    }
}

class DivideNode extends BinaryExpNode {
    // TO COMPLETE
    public DivideNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("/");
        doIndent(p, 1);
        exp2.unparse(p, indent);
    }
}

class AndNode extends BinaryExpNode {
    // TO COMPLETE
    public AndNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("(");
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("&&");
        doIndent(p, 1);
        exp2.unparse(p, indent);
        p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    // TO COMPLETE
    public OrNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("(");
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("||");
        doIndent(p, 1);
        exp2.unparse(p, indent);
        p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    // TO COMPLETE
    public EqualsNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("(");
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("==");
        doIndent(p, 1);
        exp2.unparse(p, indent);
        p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    // TO COMPLETE
    public NotEqualsNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("(");
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("!=");
        doIndent(p, 1);
        exp2.unparse(p, indent);
        p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    // TO COMPLETE
    public LessNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("(");
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("<");
        doIndent(p, 1);
        exp2.unparse(p, indent);
        p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    // TO COMPLETE
    public GreaterNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("(");
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print(">");
        doIndent(p, 1);
        exp2.unparse(p, indent);
        p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    // TO COMPLETE
    public LessEqNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("(");
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print("<=");
        doIndent(p, 1);
        exp2.unparse(p, indent);
        p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    // TO COMPLETE
    public GreaterEqNode(ExpNode exp1,  ExpNode exp2){
        super(exp1, exp2);
    }
    public void unparse(PrintWriter p, int indent){
        p.print("(");
        exp1.unparse(p, indent);
        doIndent(p, 1);
        p.print(">=");
        doIndent(p, 1);
        exp2.unparse(p, indent);
        p.print(")");
    }
}
