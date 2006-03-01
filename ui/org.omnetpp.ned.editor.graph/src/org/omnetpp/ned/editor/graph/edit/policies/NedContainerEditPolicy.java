package org.omnetpp.ned.editor.graph.edit.policies;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.omnetpp.ned.editor.graph.misc.MessageFactory;
import org.omnetpp.ned.editor.graph.model.INedContainer;
import org.omnetpp.ned.editor.graph.model.INedNode;
import org.omnetpp.ned.editor.graph.model.commands.OrphanChildCommand;

public class NedContainerEditPolicy extends ContainerEditPolicy {

    protected Command getCreateCommand(CreateRequest request) {
        return null;
    }

    public Command getOrphanChildrenCommand(GroupRequest request) {
        List parts = request.getEditParts();
        CompoundCommand result = new CompoundCommand(
                MessageFactory.LogicContainerEditPolicy_OrphanCommandLabelText);
        for (int i = 0; i < parts.size(); i++) {
            OrphanChildCommand orphan = new OrphanChildCommand();
            orphan.setChild((INedNode) ((EditPart) parts.get(i)).getModel());
            orphan.setParent((INedContainer) getHost().getModel());
            orphan.setLabel(MessageFactory.LogicElementEditPolicy_OrphanCommandLabelText);
            result.add(orphan);
        }
        return result.unwrap();
    }

}