//=========================================================================
//  DATAFLOWMANAGER.H - part of
//                          OMNeT++
//           Discrete System Simulation in C++
//
//=========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992-2003 Andras Varga

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#ifndef _DATAFLOWMANAGER_H_
#define _DATAFLOWMANAGER_H_

#include <vector>
#include "node.h"
#include "channel.h"


/**
 * Controls execution of the data flow network.
 *
 * @see Node, Channel
 */
class DataflowManager
{
    protected:
        std::vector<Node *> nodes;
        std::vector<Channel *> channels;
        Node *selectNode();
        int threshold; // channel buffer upper limit
        int lastnode; // for round robin
    public:
        DataflowManager();
        ~DataflowManager();
        void addNode(Node *node);
        void addChannel(Channel *channel);
        void connect(Port *src, Port *dest);
        void execute();
};


#endif


