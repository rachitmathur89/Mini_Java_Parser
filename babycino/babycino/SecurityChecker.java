package babycino;

import org.antlr.v4.runtime.ParserRuleContext;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;

// Check that methods whose names begin with "h" or "l" are noninterfering.
// Treat variable names beginning with "h" as being High; everything else is Low.
public class SecurityChecker extends MiniJavaBaseListener {

    // Symbol Table for the program being type-checked.
    private SymbolTable sym;
    // Class currently being type-checked.
    private Class current;
    // Method currently being type-checked.
    private Method method;
    // Flag: Have any errors occurred so far?
    private boolean errors;
    // Flag: Are we checking security types in this method?
    private boolean secure;

    // Stack of unprocessed security types, corresponding to checked subexpressions.
    private Stack<Level> types;
    
    public SecurityChecker(SymbolTable sym) {
        this.sym = sym;
        this.method = null;
        this.errors = false;
        this.secure = false;
        this.types = new Stack<Level>();
    }

    // ------------------------------------------------------------------------
    // Track what the current class/method is.

    @Override
    public void enterMainClass(MiniJavaParser.MainClassContext ctx) {
        this.current = sym.get(ctx.identifier(0).getText());
        // Set a dummy method with no variables to avoid null-pointer errors later.
        this.method = new Method("main", null, this.current, null);
    }

    @Override
    public void exitMainClass(MiniJavaParser.MainClassContext ctx) {
        this.current = null;

        // It is a fatal error if somehow not all types on the stack are used.
        if (!this.types.isEmpty()) {
            System.err.println("Internal error: not all types consumed during type-checking.");
            System.exit(1);
        }
    }
    
    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        this.current = this.sym.get(ctx.identifier(0).getText());
    }
    
    @Override
    public void exitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        this.current = null;
    }

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        String name = ctx.identifier(0).getText();
        this.method = this.current.getOwnMethod(name);

        // Check noninterference in methods whose names begin with "h" or "l".
        if (name.startsWith("h") || name.startsWith("l")) {
            this.secure = true;
        }
    }

    @Override
    public void exitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        if (!this.secure) {
            return;
        }

        String name = ctx.identifier(0).getText();
        Level t = this.types.pop();
        // If the method name indicates the return value is Low, check this is true.
        if (name.startsWith("l")) {
            this.check(t == Level.LOW, ctx, "Method " + this.method.getQualifiedName() + " returns High value; Low expected");
        }

        // Remove types from the stack for each top-level statement.
        for (MiniJavaParser.StatementContext s : ctx.statement()) {
            this.types.pop();
        }

        // It is a fatal error if somehow not all types on the stack are used.
        if (!this.types.isEmpty()) {
            System.err.println("Internal error: not all security types consumed during type-checking.");
            System.exit(1);
        }

        // Clear the flag, as we are no longer checking for noninterference.
        this.secure = false;
    }

    // ------------------------------------------------------------------------
    // When leaving an expression or statement:
    // firstly, pop the types of any subexpressions off the stack and check them;
    // secondly, for expressions, push the type of the expression onto the stack.

    // Statements:
    @Override
    public void exitStmtBlock(MiniJavaParser.StmtBlockContext ctx) {
        if (!this.secure) {
            return;
        }
		Level highstatement = Level.HIGH;
		Level lowstatement	= Level.LOW;
        for (MiniJavaParser.StatementContext s : ctx.statement()) {
            Level t = this.types.pop();
            if (Level.le(t, highstatement)) {
                highstatement = t;
            }
			else{
			}
			if (Level.le(t, lowstatement)) {
                lowstatement = t;
            }
			else{
				//this.types.push(lowstatement);
			}
        }
        this.types.push(highstatement);
        
        // TODO: Task 3.4
        this.check(false, ctx, "Unimplemented"); //Throws exception as expected and checks for security level for the if statements
    }

    @Override
    public void exitStmtIf(MiniJavaParser.StmtIfContext ctx) {
        if (!this.secure) {
            return;
        }
		Level hghlevel = Level.HIGH;
        for (MiniJavaParser.StatementContext s : ctx.statement()) {
            Level tlevel = this.types.pop();
            if (Level.le(tlevel, hghlevel)) {
                hghlevel = tlevel;
            }
        }
        Level expressionLevel = this.types.pop();
        if (!Level.le(expressionLevel, hghlevel)) {
            this.check(false, ctx, "If expression return High value; Low expected");
			this.types.push(expressionLevel);
        }
        
        this.secure = true;
		// TODO: Task 3.3
      
    }

    @Override
    public void exitStmtWhile(MiniJavaParser.StmtWhileContext ctx) {
        if (!this.secure) {
            return;
        }
		Level t1 = this.types.pop();
        Level expressionLevel = this.types.pop();

        if (!Level.le(expressionLevel, t1)) {
            this.check(false, ctx, "While expression return High value; Low expected");
			this.types.push(expressionLevel);
        }

        
        this.secure = true;
        // TODO: Task 3.2
        //this.check(false, ctx, "Unimplemented");  //This checks the condition for while for unsafe security level provided and throws exception as expected.
    }

    @Override
    public void exitStmtPrint(MiniJavaParser.StmtPrintContext ctx) {
        this.check(!this.secure, ctx, "Unsupported statement inside secure method: println");
    }
    
    @Override
   public void exitStmtAssign(MiniJavaParser.StmtAssignContext ctx) {
        if (!this.secure) {
            return;
        }
        Level identifierLevel = this.identifierLevel(ctx.identifier());
        Level assignmentLevel = this.types.pop();
        if (Level.lub(identifierLevel, assignmentLevel) != identifierLevel) {
            this.check(false, ctx, "Variable " + ctx.identifier().getText() + " returns High value; Low expected.");
		   
        }
        this.types.push(identifierLevel);
        this.secure = true;
    }
	// TODO: Task 3.1

    @Override
    public void exitStmtArrayAssign(MiniJavaParser.StmtArrayAssignContext ctx) {
        this.check(!this.secure, ctx, "Unsupported statement inside secure method: array assignment");
    }

    // Expressions:

    @Override
    public void exitExpConstTrue(MiniJavaParser.ExpConstTrueContext ctx) {
        if (!this.secure) {
            return;
        }
		this.types.push(Level.LOW);
		
        // TODO: Task 2.1
        //this.check(false, ctx, "Unimplemented");
    }

    @Override
    public void exitExpArrayLength(MiniJavaParser.ExpArrayLengthContext ctx) {
        this.check(!this.secure, ctx, "Unsupported expression inside secure method: array length");
    }

    @Override
    public void exitExpBinOp(MiniJavaParser.ExpBinOpContext ctx) {
        if (!this.secure) {
            return;
        }
		Level r1= this.types.pop();
		Level l1= this.types.pop();
		Level lub= Level.lub(r1, l1);
		this.types.push(lub);
		this.secure = true;
		// TODO: Task 2.3
        //this.check(false, ctx, "Unimplemented");
    
	}

    @Override
    public void exitExpConstInt(MiniJavaParser.ExpConstIntContext ctx) {
        if (!this.secure) {
            return;
        }

        this.types.push(Level.LOW);
    }

    @Override
    public void exitExpMethodCall(MiniJavaParser.ExpMethodCallContext ctx) {
        this.check(!this.secure, ctx, "Unsupported expression inside secure method: method call");
    }

    @Override
    public void exitExpConstFalse(MiniJavaParser.ExpConstFalseContext ctx) {
        if (!this.secure) {
            return;
        }
		this.types.push(Level.LOW);
        // TODO: Task 2.1
        //this.check(false, ctx, "Unimplemented");
    }

    @Override
    public void exitExpArrayIndex(MiniJavaParser.ExpArrayIndexContext ctx) {
        this.check(!this.secure, ctx, "Unsupported expression inside secure method: array index");
    }

    @Override
    public void exitExpNewObject(MiniJavaParser.ExpNewObjectContext ctx) {
        this.check(!this.secure, ctx, "Unsupported expression inside secure method: new object");
    }

    @Override
    public void exitExpNewArray(MiniJavaParser.ExpNewArrayContext ctx) {
        this.check(!this.secure, ctx, "Unsupported expression inside secure method: new array");
    }

    @Override
    public void exitExpNot(MiniJavaParser.ExpNotContext ctx) {
        if (!this.secure) {
            return;
        }
				
        // TODO: Task 2.4
        //this.check(false, ctx, "Unimplemented");
    }

    @Override
    public void exitExpGroup(MiniJavaParser.ExpGroupContext ctx) {
        if (!this.secure) {
            return;
        }
		// TODO: Task 2.4
        //this.check(false, ctx, "Unimplemented");
    }

    @Override
    public void exitExpLocalVar(MiniJavaParser.ExpLocalVarContext ctx) {
        if (!this.secure) {
            return;
        }
		Level identifierLevel = this.identifierLevel(ctx.identifier());
        this.types.push(identifierLevel);
        // TODO: Task 2.2
        //this.check(false, ctx, "Unimplemented");

    }

    @Override
    public void exitExpThis(MiniJavaParser.ExpThisContext ctx) {
        this.check(!this.secure, ctx, "Unsupported expression inside secure method: this");
    }

    // ------------------------------------------------------------------------

    // Helper method to get level of variable.
    private Level identifierLevel(MiniJavaParser.IdentifierContext ctx) {
        String id = ctx.getText();
        if (id.startsWith("h")) {
            return Level.HIGH;
        }
        return Level.LOW;
    }

    // Error logging and recording:

    // Assert condition. Print error if false. Record occurrence of error.
    private void check(boolean condition, ParserRuleContext ctx, String error) {
        if (!condition) {
            System.err.println(error);
            System.err.println("Context: " + ctx.getText());
            this.errors = true;
        }
    }

    // Assert false. Print error. Record occurrence of error.
    private void error(ParserRuleContext ctx, String error) {
        System.err.println(error);
        System.err.println("Context: " + ctx.getText());
        this.errors = true;
    }

    // Throw an exception if an error previously occurred.
    public void die() throws CompilerException {
        if (this.errors) {
            throw new CompilerException();
        }
    }

}

