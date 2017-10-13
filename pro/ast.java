import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a Mini program.
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
    abstract public void unparse(PrintWriter p, int indent);

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
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    /**
     * nameAnalysis
     * Creates an empty symbol table for the outermost scope, then processes
     * all of the globals, struct defintions, and functions in the program.
     */
    public void nameAnalysis() {
        SymTable symTab = new SymTable();
        myDeclList.nameAnalysis(symTab);
        if (symTab.lookupGlobal("main") == null) {
        	ErrMsg.fatal(0, 0,
                    "No main function");
        	
        }
    }
    
    /**
     * typeCheck
     */
    public void typeCheck() {
        myDeclList.typeCheck();
    }
    
    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }
    
    public void codeGen() {
		myDeclList.codeGen();
	}

    // 1 kid
    private DeclListNode myDeclList;
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, process all of the decls in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        nameAnalysis(symTab, symTab);
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab and a global symbol table globalTab
     * (for processing struct names in variable decls), process all of the 
     * decls in the list.
     */    
    public void nameAnalysis(SymTable symTab, SymTable globalTab) {
        for (DeclNode node : myDecls) {
            if (node instanceof VarDeclNode) {
                ((VarDeclNode)node).nameAnalysis(symTab, globalTab);
            } else {
                node.nameAnalysis(symTab);
            }
        }
    }    
    
    /**
     * typeCheck
     */
    public void typeCheck() {
        for (DeclNode node : myDecls) {
            node.typeCheck();
        }
    }
    
    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }
    
    public void codeGen() {
		for (DeclNode node : myDecls) {
            node.codeGen();
        }
	}
    
    public List<DeclNode> getList() {
    	return myDecls;
    	
    }

    // list of kids (DeclNodes)
    private List<DeclNode> myDecls;
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * for each formal decl in the list
     *     process the formal decl
     *     if there was no error, add type of formal decl to list
     */
    public List<Type> nameAnalysis(SymTable symTab) {
        int offset = 0; //Default offsets
        List<Type> typeList = new LinkedList<Type>();
        for (FormalDeclNode node : myFormals) {
            SymInfo sym = node.nameAnalysis(symTab);
            if (sym != null) {
                typeList.add(sym.getType());
                
                sym.setOffset(offset);
                offset+=4;
            }
        }
        return typeList;
    }    
    
    /**
     * Return the number of formals in this list.
     */
    public int length() {
        return myFormals.size();
    }
    
    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }
    
    public void codeGen(){
        for(FormalDeclNode formal : myFormals){
            formal.codeGen();
        }
    } 

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the declaration list
     * - process the statement list
     */
    public void nameAnalysis(SymTable symTab, int offset) {
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab, offset);
    }    
 
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        myStmtList.typeCheck(retType);
    }    
          
    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }
    
    public void codeGen(String label) {
        myStmtList.codeGen(label);
    }
    


	public List<DeclNode> getDeclList() {
		return myDeclList.getList();
	}
    

    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, process each statement in the list.
     */
    public void nameAnalysis(SymTable symTab, int offset) {
        for (StmtNode node : myStmts) {
            node.nameAnalysis(symTab, offset);
        }
    }    
    
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        for(StmtNode node : myStmts) {
            node.typeCheck(retType);
        }
    }
    
    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }
    
    public void codeGen(String label) {
		for(StmtNode node : myStmts) {
            node.codeGen(label);
        }
	}

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }
    
    public int size() {
        return myExps.size();
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, process each exp in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        for (ExpNode node : myExps) {
            node.nameAnalysis(symTab);
        }
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(List<Type> typeList) {
        int k = 0;
        try {
            for (ExpNode node : myExps) {
                Type actualType = node.typeCheck();     // actual type of arg
                
                if (!actualType.isErrorType()) {        // if this is not an error
                    Type formalType = typeList.get(k);  // get the formal type
                    if (!formalType.equals(actualType)) {
                        ErrMsg.fatal(node.lineNum(), node.charNum(),
                                     "Type of actual does not match type of formal");
                    }
                }
                k++;
            }
        } catch (NoSuchElementException e) {
            System.err.println("unexpected NoSuchElementException in ExpListNode.typeCheck");
            System.exit(-1);
        }
    }
    
    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }
    
    public void codeGen() {
    	for (ExpNode node : myExps) {
            node.codeGen();
        }
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    /**
     * Note: a formal decl needs to return a sym
     */
    abstract public SymInfo nameAnalysis(SymTable symTab);

    // default version of typeCheck for non-function decls
    public void typeCheck() { }
    
    public void codeGen() {}
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    /**
     * nameAnalysis (overloaded)
     * Given a symbol table symTab, do:
     * if this name is declared void, then error
     * else if the declaration is of a struct type, 
     *     lookup type name (globally)
     *     if type name doesn't exist, then error
     * if no errors so far,
     *     if name has already been declared in this scope, then error
     *     else add name to local symbol table     
     *
     * symTab is local symbol table (say, for struct field decls)
     * globalTab is global symbol table (for struct type names)
     * symTab and globalTab can be the same
     */
    public SymInfo nameAnalysis(SymTable symTab) {
        return nameAnalysis(symTab, symTab);
    }
    
    public SymInfo nameAnalysis(SymTable symTab, SymTable globalTab) {
        boolean badDecl = false;
        String name = myId.name();
        SymInfo sym = null;
        IdNode structId = null;

        if (myType instanceof VoidNode) {  // check for void type
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), 
                         "Non-function declared void");
            badDecl = true;        
        }
        
        else if (myType instanceof StructNode) {
            structId = ((StructNode)myType).idNode();
            sym = globalTab.lookupGlobal(structId.name());
            
            // if the name for the struct type is not found, 
            // or is not a struct type
            if (sym == null || !(sym instanceof StructDefInfo)) {
                ErrMsg.fatal(structId.lineNum(), structId.charNum(), 
                             "Invalid name of struct type");
                badDecl = true;
            }
            else {
                structId.link(sym);
            }
        }
        
        if (symTab.lookupLocal(name) != null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), 
                         "Multiply declared identifier");
            badDecl = true;            
        }
        
        if (!badDecl) {  // insert into symbol table
            try {
                if (myType instanceof StructNode) {
                    sym = new StructInfo(structId); //TODO: Add struct offset to sym
                }
                else {
                    sym = new SymInfo(myType.type());
                }
                symTab.addDecl(name, sym);
                
                if(symTab.size() == 1){
                        sym.setGlobal(true);
                }
                
                myId.link(sym);
            } catch (DuplicateSymException ex) {
                System.err.println("Unexpected DuplicateSymException " +
                                   " in VarDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in VarDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }
        
        return sym;
    }    
    
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.name());
        p.println(";");
    }
    
    public void codeGen(){

        if(this.myId.sym().isGlobal()) {
            Codegen.generate(".data");
            Codegen.generate(".align 2");
            Codegen.generateLabeled("_" + myId.name(), ".space 4", "");
        }
    }
    
    public IdNode getMyId() {
    	return myId;
    }

    // 3 kids
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
        this.paramsOffset = 0;
        this.localsOffset = 0;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this name has already been declared in this scope, then error
     * else add name to local symbol table
     * in any case, do the following:
     *     enter new scope
     *     process the formals
     *     if this function is not multiply declared,
     *         update symbol table entry with types of formals
     *     process the body of the function
     *     exit scope
     */
    public SymInfo nameAnalysis(SymTable symTab) {
        String name = myId.name();
        FnInfo sym = null;
        
        if (symTab.lookupLocal(name) != null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Multiply declared identifier");
        }
        
        else { // add function name to local symbol table
            try {
                sym = new FnInfo(myType.type(), myFormalsList.length());
                symTab.addDecl(name, sym);
                myId.link(sym);
            } catch (DuplicateSymException ex) {
                System.err.println("Unexpected DuplicateSymException " +
                                   " in FnDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in FnDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }
        
        symTab.addScope();  // add a new scope for locals and params
        
        // process the formals
        List<Type> typeList = myFormalsList.nameAnalysis(symTab);
        if (sym != null) {
            sym.addFormals(typeList);
            sym.setOffset(4*this.myFormalsList.length()); //Offset of params
            this.paramsOffset = sym.getOffset();
        }
        int offset = 8 + this.paramsOffset; 
		
        myBody.nameAnalysis(symTab, offset + 4*myBody.getDeclList().size()); 
        // process the function body
        
        for (DeclNode node : myBody.getDeclList()) {
			if (node instanceof VarDeclNode) {
				((VarDeclNode)node).getMyId().sym().setOffset(offset);
				offset += 4;			
			}
		}
        
        this.localsOffset = offset - 8 - this.paramsOffset; 
        
        try {
            symTab.removeScope();  // exit scope
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in FnDeclNode.nameAnalysis");
            System.exit(-1);
        }
        
        return null;
    } 
       
    /**
     * typeCheck
     */
    public void typeCheck() {
        myBody.typeCheck(myType.type());
    }
        
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.name());
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }
    
    //Generates the function prologue, 
    //epilogue for function, uses offsets from name analysis
    public void codeGen() {
		Codegen.p.print("\t.text\n");
		//if main
		if(this.myId.name().equals("main")) {
			Codegen.p.print("\t.globl main\n");
			Codegen.genLabel(this.myId.name());
			Codegen.genLabel("__start");
		}
		else {
			Codegen.p.print("\t_" + this.myId.name() + ":\n");
		}
		
		String exitLabel = "exit_" + this.myId.name();
	
		//Prologue generation
		Codegen.generateIndexed("sw", Codegen.RA,Codegen.SP,0,
				"#Start of Prologue for "+
						this.myId.name()+"\n");
		Codegen.generate("subu",Codegen.SP,Codegen.SP,4);
		Codegen.generateIndexed("sw", Codegen.FP,Codegen.SP,0);
		Codegen.generate("subu",Codegen.SP,Codegen.SP,4);
		Codegen.generate("addu", 
			Codegen.FP,Codegen.SP,paramsOffset+8);
		Codegen.generate("subu",
			Codegen.SP,Codegen.SP,localsOffset);
	
		
		//Body
		this.myBody.codeGen(exitLabel);
		
		
		//Function exit
		Codegen.genLabel(exitLabel); //label for return
		Codegen.generateIndexed("lw", 
			Codegen.RA,Codegen.FP,(-paramsOffset),
			"#Start of Epilogue for "+
					this.myId.name()+"\n");
		Codegen.generate("move",Codegen.T0,Codegen.FP);
		Codegen.generateIndexed("lw", 
			Codegen.FP,Codegen.FP,-(paramsOffset+4),
			"Restore frame pointer");
		Codegen.generate("move", Codegen.SP,Codegen.T0);
		
		if(this.myId.name().equals("main")) {
			Codegen.generate("li",Codegen.V0,"10");
			Codegen.generate("syscall");
		}
		else {
			Codegen.generateWithComment("jr","Return jump",
					Codegen.RA);
		}
		

	}

    // 4 kids
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
    private int localsOffset;
    private int paramsOffset;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this formal is declared void, then error
     * else if this formal is already in the local symble table,
     *     then issue multiply declared error message and return null
     * else add a new entry to the symbol table and return that Sym
     */
    public SymInfo nameAnalysis(SymTable symTab) {
        String name = myId.name();
        boolean badDecl = false;
        SymInfo sym = null;
        
        if (myType instanceof VoidNode) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), 
                         "Non-function declared void");
            badDecl = true;        
        }
        
        if (symTab.lookupLocal(name) != null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), 
                         "Multiply declared identifier");
            badDecl = true;
        }
        
        if (!badDecl) {  // insert into symbol table
            try {
                sym = new SymInfo(myType.type());
                symTab.addDecl(name, sym);
                
                
                
                myId.link(sym);
            } catch (DuplicateSymException ex) {
                System.err.println("Unexpected DuplicateSymException " +
                                   " in VarDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in VarDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }
        
        return sym;
    }    
    
    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.name());
    }
    
    public void codeGen() {
	}

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this name is already in the symbol table,
     *     then multiply declared error (don't add to symbol table)
     * create a new symbol table for this struct definition
     * process the decl list
     * if no errors
     *     add a new entry to symbol table for this struct
     */
    public SymInfo nameAnalysis(SymTable symTab) {
        String name = myId.name();
        boolean badDecl = false;
        
        if (symTab.lookupLocal(name) != null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), 
                         "Multiply declared identifier");
            badDecl = true;            
        }

        SymTable structInfoTab = new SymTable();
        
        // process the fields of the struct
        myDeclList.nameAnalysis(structInfoTab, symTab);
        
        if (!badDecl) {
            try {   // add entry to symbol table
                StructDefInfo sym = new StructDefInfo(structInfoTab);
                symTab.addDecl(name, sym);
                myId.link(sym);
            } catch (DuplicateSymException ex) {
                System.err.println("Unexpected DuplicateSymException " +
                                   " in StructDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in StructDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }
        
        return null;
    }    
    
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct ");
        p.print(myId.name());
        p.println("{");
        myDeclList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("};\n");

    }

    // 2 kids
    private IdNode myId;
    private DeclListNode myDeclList;
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    /* all subclasses must provide a type method */
    abstract public Type type();
    public void codeGen() {}
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    /**
     * type
     */
    public Type type() {
        return new IntType();
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    /**
     * type
     */
    public Type type() {
        return new BoolType();
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }
    
    /**
     * type
     */
    public Type type() {
        return new VoidType();
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }

    public IdNode idNode() {
        return myId;
    }
    
    /**
     * type
     */
    public Type type() {
        return new StructType(myId);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        p.print(myId.name());
    }
    
    // 1 kid
    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
    abstract public void nameAnalysis(SymTable symTab);
    public void nameAnalysis(SymTable symTab, int offset) {
			nameAnalysis(symTab);
	}
    abstract public void typeCheck(Type retType);
    public void codeGen(String label) {}
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myAssign.nameAnalysis(symTab);
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        myAssign.typeCheck();
    }
        
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }
    
    public void codeGen(String label) {
		this.myAssign.codeGen();
        Codegen.genPop(Codegen.T0);
	}

    // 1 kid
    private AssignNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();
        
        if (!type.isErrorType() && !type.isIntType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Arithmetic operator applied to non-numeric operand");
        }
    }
        
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }
    
    public void codeGen(String label) {
		if (myExp instanceof IdNode) {
			IdNode idExp = (IdNode)myExp;
			
			
			idExp.codeGen();
			idExp.genAddr();
			Codegen.genPop(Codegen.T1);
			Codegen.genPop(Codegen.T0);
			
			Codegen.generate("add", Codegen.T0, Codegen.T0, 1);
			
			
			Codegen.generateIndexed("sw", Codegen.T0, Codegen.T1, 0);
			
		}
	}

    // 1 kid
    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();
        
        if (!type.isErrorType() && !type.isIntType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Arithmetic operator applied to non-numeric operand");
        }
    }
        
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }
    
    public void codeGen(String label) {
		if (myExp instanceof IdNode) {
			IdNode idExp = (IdNode)myExp;
			
			
			idExp.codeGen();
			idExp.genAddr();
			Codegen.genPop(Codegen.T1);
			Codegen.genPop(Codegen.T0);
			
			Codegen.generate("sub", Codegen.T0, Codegen.T0, "1");
			
			Codegen.generateIndexed("sw", Codegen.T0, Codegen.T1, 0);		
		}
	}
    
    // 1 kid
    private ExpNode myExp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }    
 
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();
        
        if (type.isFnType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to read a function");
        }
        
        if (type.isStructDefType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to read a struct name");
        }
        
        if (type.isStructType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to read a struct variable");
        }
    }
    
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }
    
    public void codeGen(String label){

        if(myExp instanceof IdNode){
            ((IdNode) myExp).genAddr();
			Codegen.genPop(Codegen.T0);
			Codegen.generate("li", Codegen.V0, 5);
			Codegen.generate("syscall");
			Codegen.generateIndexed("sw", Codegen.V0, Codegen.T0, 0 );
			
		}
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();
        
        if (type.isFnType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to write a function");
        }
        
        if (type.isStructDefType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to write a struct name");
        }
        
        if (type.isStructType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to write a struct variable");
        }
        
        if (type.isVoidType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to write void");
        }
    }
        
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }
    
    public void codeGen(String label) {
		myExp.codeGen();
		Codegen.genPop(Codegen.A0);
		
        if(myExp instanceof StringLitNode){
			Codegen.generate("li", Codegen.V0, 4);
        }
        
        else {
			Codegen.generate("li", Codegen.V0, 1);
        }
        
       	Codegen.generate("syscall");	

       
	}
    

    // 1 kid
    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
    
    public void nameAnalysis(SymTable symTab) {
		System.out.println("IFSTMT: This should NOT BE CALLED");
	}
    
    public void nameAnalysis(SymTable symTab, int offset) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();



		
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab, offset + 4*myDeclList.getList().size());
      
        this.offset = 0;
        for (DeclNode d : myDeclList.getList()) {
			if (d instanceof VarDeclNode) {
				((VarDeclNode)d).getMyId().sym().setOffset(offset + this.offset);
				this.offset += 4;
				
			}
		}
        
        
        
        try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in IfStmtNode.nameAnalysis");
            System.exit(-1);        
        }
    }
    
     /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();
        
        if (!type.isErrorType() && !type.isBoolType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Non-bool expression used as an if condition");        
        }
        
        myStmtList.typeCheck(retType);
    }
       
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }
    
    public void codeGen(String label) {
		String label1 = Codegen.nextLabel();
		String label2 = Codegen.nextLabel();
		
		myExp.codeGen();
		Codegen.genPop(Codegen.T0);
		
		Codegen.generate("bne", Codegen.T0, "1", label2);
		
		Codegen.generate("subu", Codegen.SP, Codegen.SP, this.offset);
		
		myStmtList.codeGen(label);
		
		Codegen.generate("addu", Codegen.SP, Codegen.SP, this.offset);
		
		Codegen.genLabel(label2);
	}
    // 4 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
    private int offset;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }
    
    public void nameAnalysis(SymTable s) {
		System.out.println("This should not occur");
	}
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts of then
     * - exit the scope
     * - enter a new scope
     * - process the decls and stmts of else
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTab, int offset) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
       
       
        myThenDeclList.nameAnalysis(symTab);
        myThenStmtList.nameAnalysis(symTab, offset + 4*myThenDeclList.getList().size());
        
        this.offsetThen = 0;
        for (DeclNode d : myThenDeclList.getList()) {
			if (d instanceof VarDeclNode) {
				((VarDeclNode)d).getMyId().sym().setOffset(offset + this.offsetThen);
				this.offsetThen += 4;
				
			}
		}
        
       try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in IfStmtNode.nameAnalysis");
            System.exit(-1);        
        }
        symTab.addScope();
        
        
        myElseDeclList.nameAnalysis(symTab);
        myElseStmtList.nameAnalysis(symTab, offset + 4*myElseDeclList.getList().size());
        
        this.offsetElse = 0;
        for (DeclNode d : myElseDeclList.getList()) {
			if (d instanceof VarDeclNode) {
				((VarDeclNode)d).getMyId().sym().setOffset(offset + this.offsetElse);
				this.offsetElse += 4;
				
			}
		}
        
        try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in IfStmtNode.nameAnalysis");
            System.exit(-1);        
        }
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();
        
        if (!type.isErrorType() && !type.isBoolType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Non-bool expression used as an if condition");        
        }
        
        myThenStmtList.typeCheck(retType);
        myElseStmtList.typeCheck(retType);
    }
        
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
        doIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");        
    }
    
    public void codeGen(String label) {
		String label1 = Codegen.nextLabel();
		String label2 = Codegen.nextLabel();
		
		myExp.codeGen();
		Codegen.genPop(Codegen.T0);
		
		Codegen.generate("bne", Codegen.T0, "1", label1);
		
		Codegen.generate("subu", Codegen.SP, Codegen.SP, this.offsetThen);
		myThenStmtList.codeGen(label);
		Codegen.generate("addu", Codegen.SP, Codegen.SP, this.offsetThen);
		
		Codegen.generate("b", label2);	
		
		Codegen.genLabel(label1);
		Codegen.generate("subu", Codegen.SP, Codegen.SP, this.offsetElse);
		myElseStmtList.codeGen(label);
		Codegen.generate("addu", Codegen.SP, Codegen.SP, this.offsetElse);
		
		Codegen.genLabel(label2);
		
	}

    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
    private int offsetThen;
    private int offsetElse;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
     
    public void nameAnalysis(SymTable symTab) {
			System.out.println("This should not run: WHile loop");
	} 
     
    public void nameAnalysis(SymTable symTab, int offset) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
        
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab, offset + 4*myDeclList.getList().size());
        
        this.offset = 0;
        for (DeclNode d : myDeclList.getList()) {
			if (d instanceof VarDeclNode) {
				((VarDeclNode)d).getMyId().sym().setOffset(offset + this.offset);
				this.offset += 4;
				
			}
		}
        
		
		
        try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in IfStmtNode.nameAnalysis");
            System.exit(-1);        
        }
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();
        
        if (!type.isErrorType() && !type.isBoolType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Non-bool expression used as a while condition");        
        }
        
        myStmtList.typeCheck(retType);
    }
        
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }
    
    public void codeGen(String label) {
		String label1 = Codegen.nextLabel();
		String label2 = Codegen.nextLabel();
		
		Codegen.genLabel(label1);
		myExp.codeGen();
		
		Codegen.genPop(Codegen.T0);
		
		Codegen.generate("bne", Codegen.T0, "1", label2);
		Codegen.generate("subu", Codegen.SP, Codegen.SP, this.offset);
		
		myStmtList.codeGen(label);
		
		Codegen.generate("addu", Codegen.SP, Codegen.SP, this.offset);
		Codegen.generate("j", label1);
		
		Codegen.genLabel(label2);
	}

    // 4 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
    private int offset;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myCall.nameAnalysis(symTab);
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        myCall.typeCheck();
    }
    
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }
    
    public void codeGen(String label){
        this.myCall.codeGen();
        Codegen.genPop(Codegen.T0);
    }

    // 1 kid
    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public ReturnStmtNode(ExpNode exp, int charnum, int linenum) {
        myExp = exp;
        myCharnum = charnum;
        myLinenum = linenum;
    }
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child,
     * if it has one
     */
    public void nameAnalysis(SymTable symTab) {
        if (myExp != null) {
            myExp.nameAnalysis(symTab);
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        if (myExp != null) {  // return value given
            Type type = myExp.typeCheck();
            
            if (retType.isVoidType()) {
                ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                             "Return with a value in a void function");                
            }
            
            else if (!retType.isErrorType() && !type.isErrorType() && !retType.equals(type)){
                ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                             "Bad return value");
            }
        }
        
        else {  // no return value given -- ok if this is a void function
            if (!retType.isVoidType()) {
                ErrMsg.fatal(0, 0, "Missing return value");                
            }
        }
        
    }
    
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }
    
    //Given the return label, jumps to function exit codes
    public void codeGen(String exitLabel) {
		if (myExp != null) {
			myExp.codeGen();
			Codegen.genPop(Codegen.V0);
		}
		
		Codegen.generate("b", exitLabel);
	}

    // 3 kid
    private ExpNode myExp; // possibly null
    private int myCharnum;
    private int myLinenum;
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
    /**
     * Default version for nodes with no names
     */
    public void nameAnalysis(SymTable symTab) { }
    
    abstract public Type typeCheck();
    abstract public int lineNum();
    abstract public int charNum();
    public void codeGen() {}
    public static Hashtable<String, String> stringTable = new Hashtable<String, String>();
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }
    
    /**
     * Return the line number for this literal.
     */
    public int lineNum() {
        return myLineNum;
    }
    
    /**
     * Return the char number for this literal.
     */
    public int charNum() {
        return myCharNum;
    }
        
    /**
     * typeCheck
     */
    public Type typeCheck() {
        return new IntType();
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }
    
    public void codeGen() {
		Codegen.generate("li", Codegen.T0, this.myIntVal);
        Codegen.genPush(Codegen.T0);
	}

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }
    
    /**
     * Return the line number for this literal.
     */
    public int lineNum() {
        return myLineNum;
    }
    
    /**
     * Return the char number for this literal.
     */
    public int charNum() {
        return myCharNum;
    }
    
    /**
     * typeCheck
     */
    public Type typeCheck() {
        return new StringType();
    }
        
    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }
    
    public void codeGen() {
    	String label = stringTable.get(this.myStrVal);
    	if (label == null) {
    		String newLabel = Codegen.nextLabel();
    		stringTable.put(this.myStrVal, newLabel);
    		Codegen.p.println("\t.data");
    		Codegen.generateLabeled(newLabel, ".asciiz " + myStrVal, "");
    		Codegen.p.println("\t.text");
    		Codegen.generate("la", Codegen.T0, newLabel);
    		Codegen.genPush(Codegen.T0);
    	}
    	else{
    		Codegen.p.println("\t.text");
    		Codegen.generate("la", Codegen.T0, label);
    		Codegen.genPush(Codegen.T0);
    	}
    	
    	
    	
    }
    
    

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    /**
     * Return the line number for this literal.
     */
    public int lineNum() {
        return myLineNum;
    }
    
    /**
     * Return the char number for this literal.
     */
    public int charNum() {
        return myCharNum;
    }
    
    /**
     * typeCheck
     */
    public Type typeCheck() {
        return new BoolType();
    }
        
    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }
    
    public void codeGen() {
		Codegen.generate("li", Codegen.T0, 1);
        Codegen.genPush(Codegen.T0);
	}

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    /**
     * Return the line number for this literal.
     */
    public int lineNum() {
        return myLineNum;
    }
    
    /**
     * Return the char number for this literal.
     */
    public int charNum() {
        return myCharNum;
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        return new BoolType();
    }
        
    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }
    
    public void codeGen() {
		Codegen.generate("li", Codegen.T0, 0);
        Codegen.genPush(Codegen.T0);
	}

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    /**
     * Link the given symbol to this ID.
     */
    public void link(SymInfo sym) {
        mySym = sym;
    }
    
    /**
     * Return the name of this ID.
     */
    public String name() {
        return myStrVal;
    }
    
    /**
     * Return the symbol associated with this ID.
     */
    public SymInfo sym() {
        return mySym;
    }
    
    /**
     * Return the line number for this ID.
     */
    public int lineNum() {
        return myLineNum;
    }
    
    /**
     * Return the char number for this ID.
     */
    public int charNum() {
        return myCharNum;
    }    
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - check for use of undeclared name
     * - if ok, link to symbol table entry
     */
    public void nameAnalysis(SymTable symTab) {
        SymInfo sym = symTab.lookupGlobal(myStrVal);
        if (sym == null) {
            ErrMsg.fatal(myLineNum, myCharNum, "Undeclared identifier");
        } else {
            link(sym);
        }
    }
 
    /**
     * typeCheck
     */
    public Type typeCheck() {
        if (mySym != null) {
            return mySym.getType();
        } 
        else {
            System.err.println("ID with null sym field in IdNode.typeCheck");
            System.exit(-1);
        }
        return null;
    }
           
    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
        if (mySym != null) {
            p.print("(" + mySym + ")");
        }
    }
    
    public void genJumpAndLink() {
		if (this.myStrVal.equals("main")) {
			Codegen.generate("jal", this.myStrVal);
		}
		Codegen.generate("jal", "_"+this.myStrVal);
	}
	
	public void codeGen() {
		if(this.sym().isGlobal()) {
            Codegen.generate("lw", Codegen.T0, "_" + this.myStrVal);
            Codegen.genPush(Codegen.T0);
        } else {
            Codegen.generateIndexed("lw", Codegen.T0, Codegen.FP, 
            		-this.sym().getOffset());
            Codegen.genPush(Codegen.T0);
        }
	}
	
	public void genAddr() {
		if(this.sym().isGlobal()) {
            Codegen.generate("la", Codegen.T0, "_" + this.myStrVal);
            Codegen.genPush(Codegen.T0);
            System.out.println("global");
        } else {
            Codegen.generateIndexed("la", Codegen.T0, Codegen.FP, 
            		-this.sym().getOffset(), "Generate Address");
            Codegen.genPush(Codegen.T0);
        }
		
	}
    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private SymInfo mySym;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;    
        myId = id;
        mySym = null;
    }

    /**
     * Return the symbol associated with this dot-access node.
     */
    public SymInfo sym() {
        return mySym;
    }    
    
    /**
     * Return the line number for this dot-access node. 
     * The line number is the one corresponding to the RHS of the dot-access.
     */
    public int lineNum() {
        return myId.lineNum();
    }
    
    /**
     * Return the char number for this dot-access node.
     * The char number is the one corresponding to the RHS of the dot-access.
     */
    public int charNum() {
        return myId.charNum();
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the LHS of the dot-access
     * - process the RHS of the dot-access
     * - if the RHS is of a struct type, set the sym for this node so that
     *   a dot-access "higher up" in the AST can get access to the symbol
     *   table for the appropriate struct definition
     */
    public void nameAnalysis(SymTable symTab) {
        badAccess = false;
        SymTable structInfoTab = null; // to lookup RHS of dot-access
        SymInfo sym = null;
        
        myLoc.nameAnalysis(symTab);  // do name analysis on LHS
        
        // if myLoc is really an ID, then sym will be a link to the ID's symbol
        if (myLoc instanceof IdNode) {
            IdNode id = (IdNode)myLoc;
            sym = id.sym();
            
            // check ID has been declared to be of a struct type
            
            if (sym == null) { // ID was undeclared
                badAccess = true;
            }
            else if (sym instanceof StructInfo) { 
                // get symbol table for struct type
                SymInfo tempSym = ((StructInfo)sym).getStructType().sym();
                structInfoTab = ((StructDefInfo)tempSym).getSymTable();
            } 
            else {  // LHS is not a struct type
                ErrMsg.fatal(id.lineNum(), id.charNum(), 
                             "Dot-access of non-struct type");
                badAccess = true;
            }
        }
        
        // if myLoc is really a dot-access (i.e., myLoc was of the form
        // LHSloc.RHSid), then sym will either be
        // null - indicating RHSid is not of a struct type, or
        // a link to the Sym for the struct type RHSid was declared to be
        else if (myLoc instanceof DotAccessExpNode) {
            DotAccessExpNode loc = (DotAccessExpNode)myLoc;
            
            if (loc.badAccess) {  // if errors in processing myLoc
                badAccess = true; // don't continue proccessing this dot-access
            }
            else { //  no errors in processing myLoc
                sym = loc.sym();

                if (sym == null) {  // no struct in which to look up RHS
                    ErrMsg.fatal(loc.lineNum(), loc.charNum(), 
                                 "Dot-access of non-struct type");
                    badAccess = true;
                }
                else {  // get the struct's symbol table in which to lookup RHS
                    if (sym instanceof StructDefInfo) {
                        structInfoTab = ((StructDefInfo)sym).getSymTable();
                    }
                    else {
                        System.err.println("Unexpected Sym type in DotAccessExpNode");
                        System.exit(-1);
                    }
                }
            }

        }
        
        else { // don't know what kind of thing myLoc is
            System.err.println("Unexpected node type in LHS of dot-access");
            System.exit(-1);
        }
        
        // do name analysis on RHS of dot-access in the struct's symbol table
        if (!badAccess) {
        
            sym = structInfoTab.lookupGlobal(myId.name()); // lookup
            if (sym == null) { // not found - RHS is not a valid field name
                ErrMsg.fatal(myId.lineNum(), myId.charNum(), 
                             "Invalid struct field name");
                badAccess = true;
            }
            
            else {
                myId.link(sym);  // link the symbol
                // if RHS is itself as struct type, link the symbol for its struct 
                // type to this dot-access node (to allow chained dot-access)
                if (sym instanceof StructInfo) {
                    mySym = ((StructInfo)sym).getStructType().sym();
                }
            }
        }
    }    
 
    /**
     * typeCheck
     */
    public Type typeCheck() {
        return myId.typeCheck();
    }
    
    public void unparse(PrintWriter p, int indent) {
        myLoc.unparse(p, 0);
        p.print(".");
        myId.unparse(p, 0);
    }

    // 4 kids
    private ExpNode myLoc;    
    private IdNode myId;
    private SymInfo mySym;          // link to Sym for struct type
    private boolean badAccess;  // to prevent multiple, cascading errors
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode rhs) {
        myLhs = lhs;
        myRhs = rhs;
    }
    
    /**
     * Return the line number for this assignment node. 
     * The line number is the one corresponding to the left operand.
     */
    public int lineNum() {
        return myLhs.lineNum();
    }
    
    /**
     * Return the char number for this assignment node.
     * The char number is the one corresponding to the left operand.
     */
    public int charNum() {
        return myLhs.charNum();
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's 
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myLhs.nameAnalysis(symTab);
        myRhs.nameAnalysis(symTab);
    }
 
    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type typeLhs = myLhs.typeCheck();
        Type typeRhs = myRhs.typeCheck();
        Type retType = typeLhs;
        
        if (typeLhs.isFnType() && typeRhs.isFnType()) {
            ErrMsg.fatal(lineNum(), charNum(), "Function assignment");
            retType = new ErrorType();
        }
        
        if (typeLhs.isStructDefType() && typeRhs.isStructDefType()) {
            ErrMsg.fatal(lineNum(), charNum(), "Struct name assignment");
            retType = new ErrorType();
        }
        
        if (typeLhs.isStructType() && typeRhs.isStructType()) {
            ErrMsg.fatal(lineNum(), charNum(), "Struct variable assignment");
            retType = new ErrorType();
        }        
        
        if (!typeLhs.equals(typeRhs) && !typeLhs.isErrorType() && !typeRhs.isErrorType()) {
            ErrMsg.fatal(lineNum(), charNum(), "Type mismatch");
            retType = new ErrorType();
        }
        
        if (typeLhs.isErrorType() || typeRhs.isErrorType()) {
            retType = new ErrorType();
        }
        
        return retType;
    }
    
    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myRhs.unparse(p, 0);
        if (indent != -1)  p.print(")");
    }
    
    public void codeGen(){


        if(myLhs instanceof IdNode && myRhs instanceof IdNode){
                      
			((IdNode)myLhs).genAddr();  
            ((IdNode)myRhs).codeGen();
            
            Codegen.genPop(Codegen.T1); 
            Codegen.genPop(Codegen.T0);
            
            Codegen.generateIndexed("sw", Codegen.T1, Codegen.T0, 0); 
            Codegen.genPush(Codegen.T1); 

        }

        else if (myLhs instanceof IdNode) {
                       
			((IdNode)myLhs).genAddr();  
            myRhs.codeGen();
            Codegen.genPop(Codegen.T1); 
            Codegen.genPop(Codegen.T0);
            
            Codegen.generateIndexed("sw", Codegen.T1, Codegen.T0, 0); 
            Codegen.genPush(Codegen.T1); 
        }
    }


    // 2 kids
    private ExpNode myLhs;
    private ExpNode myRhs;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    /**
     * Return the line number for this call node. 
     * The line number is the one corresponding to the function name.
     */
    public int lineNum() {
        return myId.lineNum();
    }
    
    /**
     * Return the char number for this call node.
     * The char number is the one corresponding to the function name.
     */
    public int charNum() {
        return myId.charNum();
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's 
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myId.nameAnalysis(symTab);
        myExpList.nameAnalysis(symTab);
    }  
      
    /**
     * typeCheck
     */
    public Type typeCheck() {
        if (!myId.typeCheck().isFnType()) {  
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), 
                         "Attempt to call a non-function");
            return new ErrorType();
        }
        
        FnInfo fnInfo = (FnInfo)(myId.sym());
        
        if (fnInfo == null) {
            System.err.println("null sym for Id in CallExpNode.typeCheck");
            System.exit(-1);
        }
        
        if (myExpList.size() != fnInfo.getNumParams()) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), 
                         "Function call with wrong number of args");
            return fnInfo.getReturnType();
        }
        
        myExpList.typeCheck(fnInfo.getParamTypes());
        return fnInfo.getReturnType();
    }
        
    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }
    
    public void codeGen(){
        this.myExpList.codeGen();
        this.myId.genJumpAndLink();
        Codegen.genPush(Codegen.V0);
    }

    // 2 kids
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }
    
    /**
     * Return the line number for this unary expression node. 
     * The line number is the one corresponding to the  operand.
     */
    public int lineNum() {
        return myExp.lineNum();
    }
    
    /**
     * Return the char number for this unary expression node.
     * The char number is the one corresponding to the  operand.
     */
    public int charNum() {
        return myExp.charNum();
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }
    
    // one child
    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }
    
    /**
     * Return the line number for this binary expression node. 
     * The line number is the one corresponding to the left operand.
     */
    public int lineNum() {
        return myExp1.lineNum();
    }
    
    /**
     * Return the char number for this binary expression node.
     * The char number is the one corresponding to the left operand.
     */
    public int charNum() {
        return myExp1.charNum();
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's 
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myExp1.nameAnalysis(symTab);
        myExp2.nameAnalysis(symTab);
    }
    
    // two kids
    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type = myExp.typeCheck();
        Type retType = new IntType();
        
        if (!type.isErrorType() && !type.isIntType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Arithmetic operator applied to non-numeric operand");
            retType = new ErrorType();
        }
        
        if (type.isErrorType()) {
            retType = new ErrorType();
        }
        
        return retType;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
		this.myExp.codeGen();
        Codegen.genPop(Codegen.T0);

        Codegen.generate("li", Codegen.T1,0);
		Codegen.generate("sub",Codegen.T0, Codegen.T1, Codegen.T0);
        Codegen.genPush(Codegen.T0);
	}
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type = myExp.typeCheck();
        Type retType = new BoolType();
        
        if (!type.isErrorType() && !type.isBoolType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Logical operator applied to non-bool operand");
            retType = new ErrorType();
        }
        
        if (type.isErrorType()) {
            retType = new ErrorType();
        }
        
        return retType;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
		this.myExp.codeGen();
        Codegen.genPop(Codegen.T0);

		Codegen.generate("seq",Codegen.T0, Codegen.T0, "0");
        Codegen.genPush(Codegen.T0);
	}
    
    
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

abstract class ArithmeticExpNode extends BinaryExpNode {
    public ArithmeticExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type1 = myExp1.typeCheck();
        Type type2 = myExp2.typeCheck();
        Type retType = new IntType();
        
        if (!type1.isErrorType() && !type1.isIntType()) {
            ErrMsg.fatal(myExp1.lineNum(), myExp1.charNum(),
                         "Arithmetic operator applied to non-numeric operand");
            retType = new ErrorType();
        }
        
        if (!type2.isErrorType() && !type2.isIntType()) {
            ErrMsg.fatal(myExp2.lineNum(), myExp2.charNum(),
                         "Arithmetic operator applied to non-numeric operand");
            retType = new ErrorType();
        }
        
        if (type1.isErrorType() || type2.isErrorType()) {
            retType = new ErrorType();
        }
        
        return retType;
    }
}

abstract class LogicalExpNode extends BinaryExpNode {
    public LogicalExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type1 = myExp1.typeCheck();
        Type type2 = myExp2.typeCheck();
        Type retType = new BoolType();
        
        if (!type1.isErrorType() && !type1.isBoolType()) {
            ErrMsg.fatal(myExp1.lineNum(), myExp1.charNum(),
                         "Logical operator applied to non-bool operand");
            retType = new ErrorType();
        }
        
        if (!type2.isErrorType() && !type2.isBoolType()) {
            ErrMsg.fatal(myExp2.lineNum(), myExp2.charNum(),
                         "Logical operator applied to non-bool operand");
            retType = new ErrorType();
        }
        
        if (type1.isErrorType() || type2.isErrorType()) {
            retType = new ErrorType();
        }
        
        return retType;
    }
}

abstract class EqualityExpNode extends BinaryExpNode {
    public EqualityExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type1 = myExp1.typeCheck();
        Type type2 = myExp2.typeCheck();
        Type retType = new BoolType();
        
        if (type1.isVoidType() && type2.isVoidType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Equality operator applied to void functions");
            retType = new ErrorType();
        }
        
        if (type1.isFnType() && type2.isFnType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Equality operator applied to functions");
            retType = new ErrorType();
        }
        
        if (type1.isStructDefType() && type2.isStructDefType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Equality operator applied to struct names");
            retType = new ErrorType();
        }
        
        if (type1.isStructType() && type2.isStructType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Equality operator applied to struct variables");
            retType = new ErrorType();
        }        
        
        if (!type1.equals(type2) && !type1.isErrorType() && !type2.isErrorType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Type mismatch");
            retType = new ErrorType();
        }
        
        if (type1.isErrorType() || type2.isErrorType()) {
            retType = new ErrorType();
        }
        
        return retType;
    }
}

abstract class RelationalExpNode extends BinaryExpNode {
    public RelationalExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type1 = myExp1.typeCheck();
        Type type2 = myExp2.typeCheck();
        Type retType = new BoolType();
        
        if (!type1.isErrorType() && !type1.isIntType()) {
            ErrMsg.fatal(myExp1.lineNum(), myExp1.charNum(),
                         "Relational operator applied to non-numeric operand");
            retType = new ErrorType();
        }
        
        if (!type2.isErrorType() && !type2.isIntType()) {
            ErrMsg.fatal(myExp2.lineNum(), myExp2.charNum(),
                         "Relational operator applied to non-numeric operand");
            retType = new ErrorType();
        }
        
        if (type1.isErrorType() || type2.isErrorType()) {
            retType = new ErrorType();
        }
        
        return retType;
    }
}

class PlusNode extends ArithmeticExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    public void codeGen() {
    	this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);

        Codegen.generate("add",Codegen.T0, Codegen.T0, Codegen.T1);

        Codegen.genPush(Codegen.T0);
	}
}

class MinusNode extends ArithmeticExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
    	this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);

        Codegen.generate("sub", Codegen.T0, Codegen.T0, Codegen.T1);

        Codegen.genPush(Codegen.T0);
	}
}

class TimesNode extends ArithmeticExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
    	this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);

        Codegen.generate("mult", Codegen.T0, Codegen.T1);
        Codegen.generate("mflo", Codegen.T0);

        Codegen.genPush(Codegen.T0);
	}
}

class DivideNode extends ArithmeticExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
    	this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);

		//mult uses special register
        Codegen.generate("div", Codegen.T0, Codegen.T0, Codegen.T1);

        Codegen.genPush(Codegen.T0);
	}
}

class AndNode extends LogicalExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
		String shortLabel = Codegen.nextLabel();
		String exitLabel = Codegen.nextLabel();
		
		this.myExp1.codeGen();
        Codegen.genPop(Codegen.T0);
        Codegen.generate("bne",Codegen.T0, "0", shortLabel); //If not false, use RHS as solution
		
		Codegen.genPush(Codegen.T0); //Uses LHS as solution
		Codegen.generate("b", exitLabel);
		
        Codegen.genLabel(shortLabel);
        this.myExp2.codeGen(); //Just leave RHS on stack as solution
        Codegen.genLabel(exitLabel);
	}
	
}

class OrNode extends LogicalExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
		String shortLabel = Codegen.nextLabel();
		String exitLabel = Codegen.nextLabel();
		
		this.myExp1.codeGen();
        Codegen.genPop(Codegen.T0);
        Codegen.generate("bne",Codegen.T0, "1", shortLabel); //If not true, use RHS as solution
		
		Codegen.genPush(Codegen.T0); //Uses LHS as solution
		Codegen.generate("b", exitLabel);
		
        Codegen.genLabel(shortLabel);
        this.myExp2.codeGen(); //Just leave RHS on stack as solution
        Codegen.genLabel(exitLabel);	
	}
}

class EqualsNode extends EqualityExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen(){
    	String trueLabel = Codegen.nextLabel();
		String exitLabel = Codegen.nextLabel();
    	
		this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);
        
        Codegen.generate("beq",Codegen.T0, Codegen.T1, trueLabel); 
       
        //Set false
        Codegen.generate("li",Codegen.T0, 0); 
        Codegen.generate("b",exitLabel); 	
        
        Codegen.genLabel(trueLabel);
        Codegen.generate("li",Codegen.T0, 1);
       
		Codegen.genLabel(exitLabel);
		Codegen.genPush(Codegen.T0);	
	}
}

class NotEqualsNode extends EqualityExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" != ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen(){
    	String trueLabel = Codegen.nextLabel();
		String exitLabel = Codegen.nextLabel();
    	
    	
		this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);
        
        Codegen.generate("bne",Codegen.T0, Codegen.T1, trueLabel); 
       
        //Set false
        Codegen.generate("li",Codegen.T0, 0); 
        Codegen.generate("b",exitLabel); 	
        
        Codegen.genLabel(trueLabel);
        Codegen.generate("li",Codegen.T0, 1);
       
		Codegen.genLabel(exitLabel);
		Codegen.genPush(Codegen.T0);	
	}
}

class LessNode extends RelationalExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
		String falseLabel = Codegen.nextLabel();
		String exitLabel = Codegen.nextLabel();
		
		this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);
        
        //T0 > T1 -> !(0 <= T1-T0)
        Codegen.generate("sub",Codegen.T0, Codegen.T0, Codegen.T1); 
        Codegen.generate("bgez",Codegen.T0, falseLabel); 
       
       //Set false
        Codegen.generate("li",Codegen.T0, 1); 
        Codegen.generate("b",exitLabel); 	
        
        Codegen.genLabel(falseLabel);
        Codegen.generate("li",Codegen.T0, 0);
       
		Codegen.genLabel(exitLabel);
		Codegen.genPush(Codegen.T0);
		
	}
}

class GreaterNode extends RelationalExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
		String falseLabel = Codegen.nextLabel();
		String exitLabel = Codegen.nextLabel();
		
		this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);
        
        //T0 > T1 -> !(0 <= T1-T0)
        Codegen.generate("sub",Codegen.T0, Codegen.T1, Codegen.T0); 
        Codegen.generate("bgez",Codegen.T0, falseLabel); 
       
        //Set false
        Codegen.generate("li",Codegen.T0, 1); 
        Codegen.generate("b",exitLabel); 	
        
        Codegen.genLabel(falseLabel);
        Codegen.generate("li",Codegen.T0, 0);
       
		Codegen.genLabel(exitLabel);
		Codegen.genPush(Codegen.T0);
		
	}
}

class LessEqNode extends RelationalExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
		String trueLabel = Codegen.nextLabel();
		String exitLabel = Codegen.nextLabel();
		
		this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);
        
        Codegen.generate("sub",Codegen.T0, Codegen.T1, Codegen.T0); 
        Codegen.generate("bgez",Codegen.T0, trueLabel); 
       
       //Set false
        Codegen.generate("li",Codegen.T0, 0); 
        Codegen.generate("b",exitLabel); 	
        
        Codegen.genLabel(trueLabel);
        Codegen.generate("li",Codegen.T0, 1);
       
		Codegen.genLabel(exitLabel);
		Codegen.genPush(Codegen.T0);
		
	}
}

class GreaterEqNode extends RelationalExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
    public void codeGen() {
		String trueLabel = Codegen.nextLabel();
		String exitLabel = Codegen.nextLabel();
		
		this.myExp1.codeGen();
        this.myExp2.codeGen();
        Codegen.genPop(Codegen.T1);
        Codegen.genPop(Codegen.T0);
        
        Codegen.generate("sub",Codegen.T0, Codegen.T0, Codegen.T1); 
        Codegen.generate("bgez",Codegen.T0, trueLabel); 
       
       //Set false
        Codegen.generate("li",Codegen.T0, 0); 
        Codegen.generate("b",exitLabel); 	
        
        Codegen.genLabel(trueLabel);
        Codegen.generate("li",Codegen.T0, 1);
       
		Codegen.genLabel(exitLabel);
		Codegen.genPush(Codegen.T0);		
	}		
}