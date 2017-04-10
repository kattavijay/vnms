 
/* 
 * This class defines a node
 */
package com.javamix.vnms.vnms;
public class Node {
	public String ip;
	public String hostname;
	public boolean resolved;
	public int x;
	public int y;
	
	// two nodes are equal if they have same ip address
	public boolean equals(Node node2)
	{
		return (ip.compareTo(node2.ip) == 0); 
	}
	
	public Node(String anIP)
	{
		ip = anIP;
		hostname = "";
		resolved = false;
	}
	
	public void resolveIPtoHost()
	{
		new NodeResolveHostName(this).start();
	}
}
