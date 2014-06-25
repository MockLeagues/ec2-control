package org.wiztools.aws.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author subhash
 */
public class EC2Control {

    private final AmazonEC2 client;
    private final EC2ControlConfig config;
    private final List<String> instances;
    
    private enum State {running, stopped, terminated};
    
    public EC2Control(AmazonEC2 client, EC2ControlConfig config, List<String> instances) {
        this.client = client;
        this.config = config;
        this.instances = Collections.unmodifiableList(instances);
    }
    
    private void waitTillState(List<InstanceStateChange> iscs, State expState) {
        final String expSt = expState.name();
        for(InstanceStateChange isc: iscs) {
            while(true) {
                final InstanceState state = isc.getCurrentState();
                if(state.getName().equals(expSt)) {
                    System.out.printf("Instance %s is %s.",
                            isc.getInstanceId(), expSt);
                    break;
                }
                // Sleep now:
                try {
                    TimeUnit.SECONDS.sleep(1);
                }
                catch(InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    
    private StartInstancesResult startInstances(List<String> insts) {
        StartInstancesRequest req = new StartInstancesRequest(insts);
        return client.startInstances(req);
    }
    
    private StartInstancesResult startInstances(String inst) {
        return startInstances(Arrays.asList(new String[]{inst}));
    }
    
    public void start() {
        if(config.isSequential()) {
            for(String instance: instances) {
                StartInstancesResult res = startInstances(instance);
                waitTillState(res.getStartingInstances(), State.running);
            }
        }
        else {
            StartInstancesResult res = startInstances(instances);
            System.out.println("Start command issued to instances: " + instances);
        }
    }
    
    private StopInstancesResult stopInstances(List<String> insts) {
        StopInstancesRequest req = new StopInstancesRequest(insts);
        return client.stopInstances(req);
    }
    
    private StopInstancesResult stopInstances(String inst) {
        return stopInstances(Arrays.asList(new String[]{inst}));
    }
    
    public void stop() {
        if(config.isSequential()) {
            for(String instance: instances) {
                StopInstancesResult res = stopInstances(instance);
                waitTillState(res.getStoppingInstances(), State.stopped);
            }
        }
        else {
            StopInstancesResult res = stopInstances(instances);
            System.out.println("Stop command issued to instances: " + instances);
        }
    }
    
    private TerminateInstancesResult terminateInstances(List<String> insts) {
        TerminateInstancesRequest req = new TerminateInstancesRequest(insts);
        return client.terminateInstances(req);
    }
    
    private TerminateInstancesResult terminateInstances(String inst) {
        return terminateInstances(Arrays.asList(new String[]{inst}));
    }
    
    public void terminate() {
        if(config.isSequential()) {
            for(String instance: instances) {
                TerminateInstancesResult res = terminateInstances(instance);
                waitTillState(res.getTerminatingInstances(), State.terminated);
            }
        }
        else {
            TerminateInstancesResult res = terminateInstances(instances);
            System.out.println("Terminate command issued to instances: " + instances);
        }
    }
}
