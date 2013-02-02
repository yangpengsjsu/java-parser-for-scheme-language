public class SchemeBranch  {
		
		private SchemeExpression espr1;
		private SchemeExpression espr2;
		
		//ctor
		public SchemeBranch (SchemeExpression espr1_, SchemeExpression espr2_)
		{
			espr1 = espr1_;
			espr2 = espr2_;
		}
		
		public void stampa()
    	{
    	 	System.out.print("SCHEME_BRANCH( ");
    	 	espr1.stampa();
    	 	System.out.print(" ");
    	 	espr2.stampa();
    	 	System.out.print(" )");
    	 	 
        }

}		