package interpret;

public class OutcomeImpl implements Outcome{
	   int num;
	   String rep;
	   public OutcomeImpl(String rep,int num)
	   {
		   this.num=num;
		   this.rep=rep;
	   }
	   @Override
	   public String getRep()
	   {
		   return rep;
	   }
	   @Override
	   public int getNum()
	   {
		   return num;
	   }
}
