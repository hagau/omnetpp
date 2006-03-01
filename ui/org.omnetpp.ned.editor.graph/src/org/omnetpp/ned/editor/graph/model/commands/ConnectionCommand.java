package org.omnetpp.ned.editor.graph.model.commands;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.gef.commands.Command;
import org.omnetpp.ned.editor.graph.misc.MessageFactory;
import org.omnetpp.ned.editor.graph.model.ConnectionNodeEx;
import org.omnetpp.ned.editor.graph.model.INedNode;

/**
 * (Re)assigns a wire object to source/target submodule gates
 * @author rhornig
 *
 */
public class ConnectionCommand extends Command {

    protected INedNode oldSource;
    protected String oldSourceTerminal;
    protected INedNode oldTarget;
    protected String oldTargetTerminal;
    protected INedNode source;
    protected String sourceGate;
    protected INedNode target;
    protected String targetGate;
    protected ConnectionNodeEx wire;

    public ConnectionCommand() {
        super(MessageFactory.ConnectionCommand_Label);
    }

    
    /**
     * Input output gate config consistency can be checked here
     */
    public boolean canExecute() {
//        if (target != null) {
//            Vector conns = target.getConnections();
//            Iterator i = conns.iterator();
//            while (i.hasNext()) {
//                WireModel conn = (WireModel) i.next();
//                if (targetGate != null && conn.getTargetGate() != null)
//                    if (conn.getTargetGate().equals(targetGate) && conn.getTarget().equals(target))
//                        return false;
//            }
//        }
        return true;
    }

    public void execute() {
        if (source != null) {
            wire.setSrcModuleRef(source);
//            wire.setSourceGate(sourceGate);
        }
        if (target != null) {
//            wire.detachTarget();
            wire.setDestModuleRef(target);
//            wire.setTargetGate(targetGate);
        }
        if (source == null && target == null) {
            wire.setDestModuleRef(null);
            wire.setSrcModuleRef(null);
//            wire.setTargetGate(null);
//            wire.setSourceGate(null);
        }
    }

    public String getLabel() {
        return MessageFactory.ConnectionCommand_Description;
    }

    public INedNode getSource() {
        return source;
    }

    public java.lang.String getSourceGate() {
        return sourceGate;
    }

    public INedNode getTarget() {
        return target;
    }

    public String getTargetGate() {
        return targetGate;
    }

    public ConnectionNodeEx getWire() {
        return wire;
    }

    public void redo() {
        execute();
    }

    public void setSource(INedNode newSource) {
        source = newSource;
    }

    public void setSourceGate(String newSourceGate) {
        sourceGate = newSourceGate;
    }

    public void setTarget(INedNode newTarget) {
        target = newTarget;
    }

    public void setTargetGate(String newTargetGate) {
        targetGate = newTargetGate;
    }

    public void setWire(ConnectionNodeEx w) {
        wire = w;
        oldSource = w.getSrcModuleRef();
        oldTarget = w.getDestModuleRef();
        // TODO implement gate handling
//        oldSourceTerminal = w.getSourceGate();
//        oldTargetTerminal = w.getTargetGate();
    }

    public void undo() {
        source = wire.getSrcModuleRef();
        target = wire.getDestModuleRef();
//        sourceGate = wire.getSourceGate();
//        targetGate = wire.getTargetGate();

//        wire.detachSource();
//        wire.detachTarget();

        wire.setSrcModuleRef(oldSource);
        wire.setDestModuleRef(oldTarget);
//        wire.setSourceGate(oldSourceTerminal);
//        wire.setTargetGate(oldTargetTerminal);

    }

}
