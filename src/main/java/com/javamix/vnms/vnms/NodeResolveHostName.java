 
/*
 * This class defines how to resolve the ip address to hostname
 */
package com.javamix.vnms.vnms;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeResolveHostName extends Thread {
	
	private Node node;
	
	public NodeResolveHostName(Node aNode)
	{
		node = aNode;
	}
	
	public void run()
	{
		try
		{
			InetAddress addr = InetAddress.getByName(node.ip);
			node.hostname = addr.getHostName();
			node.resolved = true;
			//System.out.println(node.ip+" has hostname = "+node.hostname);
		} catch (UnknownHostException e)
		{ 
		}
	}
}
