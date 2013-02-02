import java.util.List;

class SchemeFactoryImpl implements SchemeFactory {
	
	public SchemeDefinition createDefinition(String name, SchemeExpression expr)
    {
    	return new SchemeDefinition(name, expr);
    } 
   
    public SchemeBranch createBranch(SchemeExpression test, SchemeExpression e)
    {
    	return new SchemeBranch(test, e);
    }
    
    public SchemeExpression createApplyExpression(List exprs)
    { 
      	return new SchemeExpression(Costanti.LIST, exprs); 
    }
    
    public SchemeExpression createLambdaExpression(List params, SchemeExpression expr)
    {
 		return new SchemeExpression(params, expr, Costanti.LAMBDA, 0);
 	}
 	
 	public SchemeExpression createAndExpression(List exprs)
    {
    	return new SchemeExpression(Costanti.AND, exprs);
    }
    
    public SchemeExpression createOrExpression(List exprs)
    {
    	return new SchemeExpression(Costanti.OR, exprs);
    }
    
    public SchemeExpression createCondExpression(List branches, SchemeExpression e)
    {
    	return new SchemeExpression(branches, e);
    }
    
    public SchemeExpression createLocalExpression(List bindings, SchemeExpression e)
    {
    	return new SchemeExpression(bindings, e, Costanti.LOCAL);
    }
    
    public SchemeExpression createIdExpression(String id)
    {
    	return new SchemeExpression(Costanti.ID, id);
    }
    
    public SchemeExpression createBoolExpression(boolean v)
    {
    	return new SchemeExpression(v);
    
    }
    
    public SchemeExpression createIntExpression(int v)
    {
    	return new SchemeExpression(Costanti.INT, v);
    }
    
    public SchemeExpression createStringExpression(String v)
    {
    	return new SchemeExpression(Costanti.STRING, v);
    }
    
    public SchemeExpression createSymbolExpression(String v) {return new SchemeExpression(0, 0);}
}
