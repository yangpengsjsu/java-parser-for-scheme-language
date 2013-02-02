import java.util.List;

interface SchemeFactory {

    public SchemeDefinition createDefinition(String name, SchemeExpression expr);
    
    public SchemeBranch createBranch(SchemeExpression test, SchemeExpression e);
    
    public SchemeExpression createApplyExpression(List exprs);
    public SchemeExpression createLambdaExpression(List params, SchemeExpression expr);
    public SchemeExpression createAndExpression(List exprs);
    public SchemeExpression createOrExpression(List exprs);
    public SchemeExpression createCondExpression(List branches, SchemeExpression e);
    public SchemeExpression createLocalExpression(List bindings, SchemeExpression e);
    public SchemeExpression createIdExpression(String id);
    public SchemeExpression createBoolExpression(boolean v);
    public SchemeExpression createIntExpression(int v);
    public SchemeExpression createStringExpression(String v);
    public SchemeExpression createSymbolExpression(String v);
}
