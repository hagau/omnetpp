//=========================================================================
//  FILTERNODES.H - part of
//                          OMNeT++
//           Discrete System Simulation in C++
//
//=========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992-2003 Andras Varga

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#ifndef _FILTERNODES_H_
#define _FILTERNODES_H_

#include "commonnodes.h"


/**
 * Processing node which does nothing but copying its input to its output.
 */
class NopNode : public FilterNode
{
    public:
        NopNode()  {}
        virtual ~NopNode() {}
        virtual bool isReady() const;
        virtual void process();
};

class NopNodeType : public FilterNodeType
{
    public:
        virtual const char *name() const {return "nop";}
        virtual const char *description() const;
        virtual void getAttributes(StringMap& attrs) const;
        virtual Node *create(DataflowManager *, StringMap& attrs) const;
};

//----

/**
 * Processing node which adds a constant to every value.
 */
class AdderNode : public FilterNode
{
    protected:
        double c;
    public:
        AdderNode(double c)  {this->c = c;}
        virtual ~AdderNode() {}
        virtual bool isReady() const;
        virtual void process();
};

class AdderNodeType : public FilterNodeType
{
    public:
        virtual const char *name() const {return "add";}
        virtual const char *description() const;
        virtual void getAttributes(StringMap& attrs) const;
        virtual Node *create(DataflowManager *, StringMap& attrs) const;
};

//----

/**
 * Processing node which multiplies every value by a constant.
 */
class MultiplierNode : public FilterNode
{
    protected:
        double a;
    public:
        MultiplierNode(double a)  {this->a = a;}
        virtual ~MultiplierNode() {}
        virtual bool isReady() const;
        virtual void process();
};

class MultiplierNodeType : public FilterNodeType
{
    public:
        virtual const char *name() const {return "multiply";}
        virtual const char *description() const;
        virtual void getAttributes(StringMap& attrs) const;
        virtual void getAttrDefaults(StringMap& attrs) const;
        virtual Node *create(DataflowManager *, StringMap& attrs) const;
};

//----

/**
 * Processing node which shifts every value in time.
 */
class TimeShiftNode : public FilterNode
{
    protected:
        double dt;
    public:
        TimeShiftNode(double dt)  {this->dt = dt;}
        virtual ~TimeShiftNode() {}
        virtual bool isReady() const;
        virtual void process();
};

class TimeShiftNodeType : public FilterNodeType
{
    public:
        virtual const char *name() const {return "timeshift";}
        virtual const char *description() const;
        virtual void getAttributes(StringMap& attrs) const;
        virtual Node *create(DataflowManager *, StringMap& attrs) const;
};

//----

/**
 * Processing node which adds linear trend to its input.
 */
class LinearTrendNode : public FilterNode
{
    protected:
        double a;
    public:
        LinearTrendNode(double a)  {this->a = a;}
        virtual ~LinearTrendNode() {}
        virtual bool isReady() const;
        virtual void process();
};

class LinearTrendNodeType : public FilterNodeType
{
    public:
        virtual const char *name() const {return "lineartrend";}
        virtual const char *description() const;
        virtual void getAttributes(StringMap& attrs) const;
        virtual void getAttrDefaults(StringMap& attrs) const;
        virtual Node *create(DataflowManager *, StringMap& attrs) const;
};

//----

/**
 * Processing node which deletes values outside a specified time interval.
 */
class CropNode : public FilterNode
{
    protected:
        double from, to;
    public:
        CropNode(double from, double to)  {this->from = from; this->to = to;}
        virtual ~CropNode() {}
        virtual bool isReady() const;
        virtual void process();
};

class CropNodeType : public FilterNodeType
{
    public:
        virtual const char *name() const {return "crop";}
        virtual const char *description() const;
        virtual void getAttributes(StringMap& attrs) const;
        virtual Node *create(DataflowManager *, StringMap& attrs) const;
};

//----

/**
 * Processing node which calculates mean on (0,t)
 */
class MeanNode : public FilterNode
{
    protected:
        double sum;
        long count;
    public:
        MeanNode()  {sum=0; count=0;}
        virtual ~MeanNode() {}
        virtual bool isReady() const;
        virtual void process();
};

class MeanNodeType : public FilterNodeType
{
    public:
        virtual const char *name() const {return "mean";}
        virtual const char *description() const;
        virtual void getAttributes(StringMap& attrs) const;
        virtual Node *create(DataflowManager *, StringMap& attrs) const;
};

#endif


