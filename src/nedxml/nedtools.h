//==========================================================================
// nedtools.h -
//
//                     OMNeT++/OMNEST
//            Discrete System Simulation in C++
//
//==========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 2002-2017 Andras Varga
  Copyright (C) 2006-2017 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/


#ifndef __OMNETPP_NEDXML_NEDTOOLS_H
#define __OMNETPP_NEDXML_NEDTOOLS_H

#include "nedelements.h"

namespace omnetpp {
namespace nedxml {

/**
 * @brief Misc NED utilities
 *
 * @ingroup Validation
 */
class NEDXML_API NedTools
{
  public:
    /**
     * Tries to makes an ASTNode tree comply with the DTD by gradually
     * discarding elements that fail DTD validation. Success is not guaranteed.
     */
    static void repairASTNodeTree(ASTNode *tree);

    /**
     * Splits each NedFile under FilesElement to several files, to place
     * every module or interface into its own file.
     */
    static void splitToFiles(FilesElement *tree);
};

} // namespace nedxml
}  // namespace omnetpp


#endif

