/* 
 * This class carries out operations when a packet is received
 */
package com.javamix.vnms.vnms;
import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.net.IPPacket;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;

class PacketHandler implements PacketListener {

	// references for convinence
	private VirtualWatch vpn;
	
	public PacketHandler(VirtualWatch vpn){
		// get the references for easy access
		this.vpn = vpn; 
	}

	// when a packet arrives... 
	public void packetArrived(Packet packet) {

		// we only care about IP packets
		if (packet instanceof IPPacket) {

			IPPacket ipPacket = (IPPacket) packet;
			int type = Traffic.PLAINTEXT;
			if (ipPacket.getProtocol() == 0x06) {
				// a TCP packet has arrived
				TCPPacket tcpPacket = (TCPPacket) packet;
				
				if (tcpPacket.getDestinationPort() == 443) {
					// a TCP-HTTPS packet has arrived
					type = Traffic.HTTPS;
					vpn.statisticsPanel.numHTTPS++;
				} else if (tcpPacket.getDestinationPort() == 22) {
					
					// a TCP-SSH packet has arrived
					type = Traffic.SSH;
					vpn.statisticsPanel.numSSH++;
					
				} else {
					// it is a TCP packet, but not a HTTPS nor SSH packet
					vpn.statisticsPanel.numOthers++;
				}
				
			} else if (ipPacket.getProtocol() == 0x2f) {
				// a GRE packet has arrived 
				// a GRE packet is used by a PPTP connection
				type = Traffic.PPTP;
				vpn.statisticsPanel.numPPTP++;
				
			} else if (ipPacket.getProtocol() == 0x32) {
                // an ESP packet has arrived
                // an ESP packet is used by an IPSec connection
				type = Traffic.IPSEC;
				vpn.statisticsPanel.numIPSEC++;

			} else {
                // It is not PPTP nor IPSec packet
				vpn.statisticsPanel.numOthers++;
			}

			// add the pair of the connection
			vpn.trafficPanel.AddVPNPair(
				ipPacket.getSourceAddress(),
				ipPacket.getDestinationAddress(),
				type);

			// dump the text if it is not encrypted
			if (type == Traffic.PLAINTEXT) {
				vpn.plaintextPanel.DumpAddresses(packet);
			}

		} else {

			// It is not an IP packet
			vpn.statisticsPanel.numOthers++;
		}
	}
}
