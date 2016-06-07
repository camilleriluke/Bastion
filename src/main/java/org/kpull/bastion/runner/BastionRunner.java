package org.kpull.bastion.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.kpull.bastion.core.BastionListener;
import org.kpull.bastion.core.BastionListenerRegistrar;

public class BastionRunner extends BlockJUnit4ClassRunner implements BastionListener {

    public BastionRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        BastionListenerRegistrar.getDefaultBastionListenerRegistrar().registerListener(this);
    }

    @Override
    public Description getDescription() {
        return null;
    }

    @Override
    public void run(RunNotifier runNotifier) {

    }

    @Override
    public void callStarted() {

    }

    @Override
    public void callFinished() {

    }

    @Override
    public void callFailed() {

    }

    @Override
    public void callError() {

    }

}
