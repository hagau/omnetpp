//=========================================================================
//  FILTERNODES.CC - part of
//                  OMNeT++/OMNEST
//           Discrete System Simulation in C++
//
//=========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992-2005 Andras Varga

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#ifdef _MSC_VER
#pragma warning(disable:4786)
#endif

#include <math.h>
#include "channel.h"
#include "customfilter.h"



/**
 * Resolves variables (x, y) and functions (sin, fabs, etc) in
 * ExpressionFilterNode expressions.
 */
class Resolver : public Expression::Resolver
{
  private:
    ExpressionFilterNode *hostnode;
  public:
    Resolver(ExpressionFilterNode *node) {hostnode = node;}
    virtual ~Resolver() {};
    virtual Expression::Functor *resolveVariable(const char *varname);
    virtual Expression::Functor *resolveFunction(const char *funcname, int argcount);
};

Expression::Functor *Resolver::resolveVariable(const char *varname)
{
    if (strcmp(varname, "x")==0 || strcmp(varname, "y")==0)
        return new ExpressionFilterNode::NodeVar(hostnode, varname);
    else
        throw opp_runtime_error("Unrecognized variable: %s", varname);
}

Expression::Functor *Resolver::resolveFunction(const char *funcname, int argcount)
{
    //FIXME check argcount!
    if (MathFunction::supports(funcname))
        return new MathFunction(funcname);
    else
        throw opp_runtime_error("Unrecognized function: %s()", funcname);
}

ExpressionFilterNode::ExpressionFilterNode(const char *text)
{
    expr = new Expression();
    Resolver resolver(this);
    expr->parse(text, &resolver);
}

ExpressionFilterNode::~ExpressionFilterNode()
{
    delete expr;
}

bool ExpressionFilterNode::isReady() const
{
    return in()->length()>0;
}

void ExpressionFilterNode::process()
{
    int n = in()->length();
    for (int i=0; i<n; i++)
    {
        Datum d;
        in()->read(&currentDatum,1);
        currentDatum.y = expr->doubleValue();
        out()->write(&currentDatum,1);
    }
}

double ExpressionFilterNode::getVariable(const char *varname)
{
    if (varname[0]=='x' && varname[1]==0)
        return currentDatum.x;
    else if (varname[0]=='y' && varname[1]==0)
        return currentDatum.y;
    else
        return 0.0;  // cannot happen, as validation has already taken place in Resolver
}

//--

const char *ExpressionFilterNodeType::description() const
{
    return "Evaluates an arbitrary expression";
}

void ExpressionFilterNodeType::getAttributes(StringMap& attrs) const
{
    attrs["expression"] = "The expression to evaluate. Use x for time, and y for value."; //FIXME use "t" and "x" instead?
}

Node *ExpressionFilterNodeType::create(DataflowManager *mgr, StringMap& attrs) const
{
    checkAttrNames(attrs);

    Node *node = new ExpressionFilterNode(attrs["expression"].c_str());
    node->setNodeType(this);
    mgr->addNode(node);
    return node;
}


