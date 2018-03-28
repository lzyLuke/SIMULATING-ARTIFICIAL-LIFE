package interpret;

import java.util.Iterator;
import java.util.LinkedList;

import ast.BinaryCondition;
import ast.BinaryExpression;
import ast.Command;
import ast.Condition;
import ast.Expr;
import ast.MiscellExpr;
import ast.Node;
import ast.Program;
import ast.Relation;
import ast.Rule;
import ast.UnaryExpr;
import exceptions.NodeTypeError;
import simulate.Critter;

public class InterpreterImpl implements Interpreter{
	Critter critter;
	
	 public InterpreterImpl(Critter critter)
	 {this.critter=critter;}
	
	@Override
	public Outcome interpret(Program p)
	  {
		  LinkedList<Node> ruleList;
		  Iterator<Node> it;
		  Outcome outcome=null;
		  ruleList=p.getRuleList();
		  Rule rule;
		  Command command;
		  Condition condition;
		  Boolean hasTrue=false;			//notes that it find a action that can be stored into outcome
		  int pass=1;
		  // current world constant MAX_RULES_PER_TURN = 999
		  while(critter.getMem(5)<critter.myworld.getMaxRulesPerTurn())
		  {
			  it=ruleList.iterator();
			  while(it.hasNext())
			  {
				  rule=(Rule)(it.next());
				  condition=rule.getCondition();
				  if(condition.accept(this))		//If it accept this condition for this rule
				  {
					  hasTrue=true;				
					  command=rule.getCommand();
					  outcome=command.accept(this);
					  critter.lastExe=rule;		//Since we find a action ,the rule will be stopped here
				  }
				  critter.changeMem(5, pass++);	//this rule has been passed
				  if(outcome!=null && !outcome.getRep().equals(""))
					 {
					  return outcome;
					  }
				 
			  }
			  
			  if(hasTrue==false)
			  return new OutcomeImpl("wait",0);  
		  }
		  
		  return new OutcomeImpl("wait",0); 
	  }
	@Override
	  public boolean eval(Condition c)
	  {
		  if(c instanceof BinaryCondition)				//There are two kinds of nodes for eval condition
			  return ((BinaryCondition)c).accept(this);
		  else if(c instanceof Relation)
			  return ((Relation)c).accept(this);
		  try {
		  throw new NodeTypeError();
		  }catch(Exception e)
		  {
			  System.out.println("Wrong node type in eval Condition");
			  e.printStackTrace();
		  }
		  
		  return false;
		  
	  }
	 
	@Override
	  public int eval(Expr e)		//There are four kinds of nodes for expression eval.
	  {
		  if(e instanceof ast.Number)
				return ((ast.Number)e).accept(this);
				
				else if(e instanceof BinaryExpression)
				return ((BinaryExpression)e).accept(this);
				
				else if(e instanceof MiscellExpr)
					return ((MiscellExpr)e).accept(this);
				
				else if(e instanceof UnaryExpr)
					return ((UnaryExpr)e).accept(this);
		
		  try {
			  throw new NodeTypeError();
			  }catch(Exception n)
			  {
				  System.out.println("Wrong node type in eval Expression");
				  n.printStackTrace();
			  }
		  return 0;
	  }
	 
	@Override
	  public Critter getCritter()
	  {
		  return critter;
	  }
	  
	  
}
