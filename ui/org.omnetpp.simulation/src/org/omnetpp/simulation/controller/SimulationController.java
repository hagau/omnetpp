package org.omnetpp.simulation.controller;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.omnetpp.common.engine.BigDecimal;
import org.omnetpp.common.util.DisplayUtils;
import org.omnetpp.simulation.SimulationPlugin;
import org.omnetpp.simulation.controller.Simulation.RunMode;
import org.omnetpp.simulation.controller.Simulation.SimState;
import org.omnetpp.simulation.liveanimation.LiveAnimationController;

/**
 * TODO
 *
 * @author Andras
 */
//TODO introduce BUSY state in Cmdenv? it would be active during setting up a network and calling finish
//TODO Stop gomb resetelje a running state-et!!! (cancel animation, etc!!!)
//TODO ha animacio kozben vagyunk, Step/Run/Stop etc allitsa le az animaciot!!!
public class SimulationController implements ISimulationCallback {
    private Simulation simulation;
    private LiveAnimationController liveAnimationController;  //TODO should probably be done via listeners, and not by storing LiveAnimationController reference here!

//    private SimState state;  //TODO keep in sync with Simulation's state
    private boolean lastEventAnimationDone = false;
    private Simulation.RunMode currentRunMode = RunMode.NOTRUNNING;
    private BigDecimal runUntilSimTime;
    private long runUntilEventNumber;
    private boolean stopRequested;
    private ListenerList simulationListeners = new ListenerList(); // listeners we have to notify on changes

    public SimulationController(Simulation simulation) {
        this.simulation = simulation;
        simulation.setSimulationCallback(this);
    }

    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * The state of the simulation. NOTE: this is not always the same as the
     * state of the simulation process! While running, the UI runs the
     * simulation in chunks of n events (or seconds) and queries the process
     * state after each chunk; naturally the process will report state==READY
     * between the chunks, while UI is obviously in state==RUNNING.
     */
    public SimState getUIState() {
        SimState state = simulation.getState();
        return (state == SimState.READY && currentRunMode != RunMode.NOTRUNNING) ? SimState.RUNNING : state;
    }

    public long getEventNumber() {
        return isLastEventAnimationDone() ? simulation.getNextEventNumber() : simulation.getLastEventNumber();
    }

    public BigDecimal getSimulationTime() {
        return isLastEventAnimationDone() ? simulation.getNextEventSimulationTimeGuess() : simulation.getLastEventSimulationTime();
    }

    //XXX we'd need:
    // module ptr, ID, fullPath, nedType  (ptr is not enough, because module itself may have been deleted already)
    // msg ptr, ID, name, className (ptr is not enough, because message itself may have been deleted already)
    //TODO group eventnum,simtime,module,msg into an EventInfo class?

    public int getEventModuleId() { 
        return isLastEventAnimationDone() ? simulation.getNextEventModuleIdGuess() : 0 /*XXX no getLastEventModuleId*/;
    }

    public long getEventMessageId() {
        return isLastEventAnimationDone() ? simulation.getNextEventMessageIdGuess() : 0 /*XXX no getLastEventMessageId*/;
    }

    public boolean isNetworkPresent() {
        return getUIState() != SimState.DISCONNECTED && getUIState() != SimState.NONETWORK;
    }

    public boolean isSimulationOK() {
        return getUIState() == SimState.READY || getUIState() == SimState.RUNNING;
    }

    public boolean isRunning() {
        return getUIState() == SimState.RUNNING;
    }

    public Simulation.RunMode getCurrentRunMode() {
        return currentRunMode;
    }

    public boolean isRunUntilActive() {
        return isRunning() && runUntilSimTime != null && runUntilEventNumber  > 0;
    }

    public boolean isLastEventAnimationDone() {
        return lastEventAnimationDone;
    }

    public void setLiveAnimationController(LiveAnimationController liveAnimationController) {
        this.liveAnimationController = liveAnimationController;
    }

    public LiveAnimationController getLiveAnimationController() {
        return liveAnimationController;
    }

    public void addSimulationStateListener(ISimulationStateListener listener) {
        simulationListeners.add(listener);
    }

    public void removeSimulationStateListener(ISimulationStateListener listener) {
        simulationListeners.remove(listener);
    }

    public List<ConfigDescription> getConfigDescriptions() throws IOException {
        return simulation.getConfigDescriptions();
    }

    public void setupRun(String configName, int runNumber) throws IOException {
        simulation.sendSetupRunCommand(configName, runNumber);
        refreshUntil(SimState.READY); //TODO in background thread, plus callback in the end?
    }

    public void setupNetwork(String networkName) throws IOException {
        simulation.sendSetupNetworkCommand(networkName);
        refreshUntil(SimState.READY); //TODO in background thread, plus callback in the end?
    }

    public void rebuildNetwork() throws IOException {
        simulation.sendRebuildNetworkCommand();
        refreshUntil(SimState.READY); //TODO in background thread, plus callback in the end?
    }

    public void step() throws IOException {
        Assert.isTrue(simulation.getState() == SimState.READY);
        if (currentRunMode == RunMode.NOTRUNNING) {
//            animationPlaybackController.jumpToEnd();

            currentRunMode = RunMode.STEP;
            notifyListeners(); // because currentRunMode has changed
            simulation.sendStepCommand();
            lastEventAnimationDone = false;
            refreshUntil(SimState.READY);

            // animate it
//            animationPlaybackController.play();
            EventEntry lastEvent = getSimulation().getLogBuffer().getLastEventEntry();
            liveAnimationController.startAnimatingLastEvent(lastEvent);
            // Note: currentRunMode=RunMode.NOTRUNNING and lastEventAnimationDone=false will be done in animationStopped()!
        }
        else {
            stop(); // if already running, just stop it
        }
    }

    public void run() throws IOException {
        run(RunMode.NORMAL);
    }

    public void fastRun() throws IOException {
        run(RunMode.FAST);
    }

    public void expressRun() throws IOException {
        run(RunMode.EXPRESS);
    }

    public void run(RunMode mode) throws IOException {
        runUntil(mode, null, 0);
    }

    public void runUntil(RunMode mode, BigDecimal simTime, long eventNumber) throws IOException {
        runUntilSimTime = simTime;  //TODO if time/event already passed, don't do anything
        runUntilEventNumber = eventNumber;

//        animationPlaybackController.jumpToEnd();

        if (currentRunMode == RunMode.NOTRUNNING) {
            Assert.isTrue(simulation.getState() == SimState.READY);
            stopRequested = false;
            currentRunMode = mode;
            notifyListeners(); // because currentRunMode has changed
            doRun();
        }
        else {
            // asyncExec() already scheduled, just change the runMode for it
            currentRunMode = mode;
        }
    }

    protected void doRun() throws IOException {
        if (stopRequested) {
            stopRequested = false;
            currentRunMode = RunMode.NOTRUNNING;
            notifyListeners(); // because currentRunMode has changed
            return;
        }

        //TODO exit if "until" limit has been reached

        long eventDelta = -1;
        switch (currentRunMode) {
            case NORMAL: eventDelta = 1; break;
            case FAST: eventDelta = 10; break;
            case EXPRESS: eventDelta = 1000; break;  //TODO: express should rather use wall-clock seconds as limit!!!
            default: Assert.isTrue(false);
        }

        long untilEvent = runUntilEventNumber == 0 ? simulation.getLastEventNumber()+eventDelta : Math.min(runUntilEventNumber, simulation.getLastEventNumber()+eventDelta);
        simulation.sendRunUntilCommand(currentRunMode, runUntilSimTime, untilEvent);
        if (currentRunMode == RunMode.NORMAL)
            lastEventAnimationDone = false;

        refreshUntil(SimState.READY);

        if (simulation.getState() != SimState.READY)
            return;

        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    if (currentRunMode == RunMode.FAST || currentRunMode == RunMode.EXPRESS) {
//                        animationPlaybackController.stop();  // in case it was running
//                        animationPlaybackController.jumpToEnd(); // show current state on animation canvas
                        doRun();
                    }
                    else if (currentRunMode == RunMode.NORMAL) {
                        // animate it
//                        animationPlaybackController.play();
                        EventEntry lastEvent = getSimulation().getLogBuffer().getLastEventEntry();
                        liveAnimationController.startAnimatingLastEvent(lastEvent);
                        // Note: next doRun() will be called from animationStopped()!
                    }
                }
                catch (IOException e) {
                    MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "An error occurred: " + e.getMessage());
                    SimulationPlugin.logError("Error running simulation", e);
                }
            }
        });
    }

    public void stop() throws IOException {
        stopRequested = true;
        if (simulation.getState() == SimState.RUNNING) {
            try {
                simulation.sendStopCommand();
            } finally {
                refreshUntil(SimState.READY);
            }
        }
    }

    public void callFinish() throws IOException {
        // strictly speaking, we shouldn't allow callFinish() after SIM_ERROR but it comes handy in practice...
        SimState state = simulation.getState();
        Assert.isTrue(state == SimState.READY || state == SimState.TERMINATED || state == SimState.ERROR);
        simulation.sendCallFinishCommand();
        refreshUntil(SimState.FINISHCALLED); //TODO in background thread, plus callback in the end?
    }

    /**
     * Repeatedly issue refreshStatus() until the state reported by simulation process becomes 
     * the expected state, or a quasi terminal state (TERMINATED, ERROR or DISCONNECTED).
     * The caller should check which one occurred.
     */
    public void refreshUntil(SimState expectedState) throws IOException {
        //TODO this method is called after sending commands for potentially long-running operations 
        // in the simulation process, e.g. setting up a network, calling finish, or processing and event.
        // Then HTTP (or this loop) will keep up the UI thread and make the IDE nonresponsive.
        // So, this whole thing should be probably done in a background thread, and report completion 
        // via a callback...
        //TODO or, at least bring up a cancellable progress dialog after a few seconds

        for (int retries = 0; true; retries++) {
            simulation.refreshStatus();  // note: this will trigger at least one but potentially several statusRefreshed() callbacks
            SimState state = simulation.getState();
            if (state == expectedState || state == SimState.TERMINATED || state == SimState.ERROR || state == SimState.DISCONNECTED)
                return;
            if (retries > 1)
                try { Thread.sleep(retries <= 5 ? 100 : 500); } catch (InterruptedException e) {}
        }
    }

    @Override
    public void statusRefreshed() {
        notifyListeners();
    }

    @Override
    public void socketError(SocketException e) {
        notifyListeners();
    }

    @Override
    public void simulationProcessExited() {
        // this comes is a background thread, but listeners are typically UI related
        DisplayUtils.runNowOrAsyncInUIThread(new Runnable() {
            @Override
            public void run() {
                notifyListeners();
            }
        });
    }

    // XXX called from outside
    public void animationStopped() {
        if (currentRunMode == RunMode.STEP) {
            currentRunMode = RunMode.NOTRUNNING;
            lastEventAnimationDone = true;
            notifyListeners();
        }
        else if (currentRunMode == RunMode.NORMAL) {
            try {
                lastEventAnimationDone = true;
                notifyListeners();
                doRun();
            }
            catch (Exception e) {
                MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "An error occurred: " + e.getMessage());
                SimulationPlugin.logError("Error running simulation", e);
            }
        }
    }

    protected void notifyListeners() {
        for (final Object listener : simulationListeners.getListeners()) {
            SafeRunner.run(new ISafeRunnable() {
                @Override
                public void run() throws Exception {
                    ((ISimulationStateListener) listener).simulationStateChanged(SimulationController.this);
                }

                @Override
                public void handleException(Throwable e) {
                    SimulationPlugin.logError(e);
                }
            });
        }
    }

    public void dispose() {
        simulation.dispose();
        //TODO cancel timers, etc.
    }

}
