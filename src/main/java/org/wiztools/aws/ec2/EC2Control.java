package org.wiztools.aws.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author subhash
 */
public class EC2Control {

    private final AmazonEC2 client;
    private final List<String> instances;
    
    public EC2Control(AmazonEC2 client, List<String> instances) {
        this.client = client;
        this.instances = Collections.unmodifiableList(instances);
    }
    
    public void start() {
        StartInstancesRequest req = new StartInstancesRequest(instances);
        StartInstancesResult res = client.startInstances(req);
        System.out.println("Start command issued to instances: " + instances);
    }
    
    public void stop() {
        StopInstancesRequest req = new StopInstancesRequest(instances);
        StopInstancesResult res = client.stopInstances(req);
        System.out.println("Stop command issued to instances: " + instances);
    }
    
    public void terminate() {
        TerminateInstancesRequest req = new TerminateInstancesRequest(instances);
        TerminateInstancesResult res = client.terminateInstances(req);
        System.out.println("Terminate command issued to instances: " + instances);
    }
}
