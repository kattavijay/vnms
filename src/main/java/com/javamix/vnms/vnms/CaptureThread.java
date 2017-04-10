/* 
 * 
 * This class is the thread that captures packets
 * 
 */
 package com.javamix.vnms.vnms;
import net.sourceforge.jpcap.capture.PacketCapture;

public class CaptureThread extends Thread {

	/* references */
	private VirtualWatch vpn;

	private static final int INFINITY = -1;
	private static final int PACKET_COUNT = INFINITY;

	private PacketCapture pcap;

	private int count;

	public CaptureThread(VirtualWatch vpn) {
		super();
		this.vpn = vpn;
		count = 0;
	}

	public void run() {

		try {
			vpn.startButton.setEnabled(false);
			vpn.stopButton.setEnabled(true);

			// make sure that the plaintext panel has a new table
			//vpn.plaintextPanel.getNewModel();

			pcap = new PacketCapture();

			String usingDevice =
				(String) vpn.deviceCombo.getSelectedItem();

			//System.out.println("Using device: " + usingDevice);
			vpn.statusBar.setStatus(
				"Capturing on "
					+ usingDevice
					+ ": "
					+ count
					+ " packets received");
			pcap.open(usingDevice, 1500, true, 1000); // 1500 is the MTU
			final String FILTER =
			""; // no filter
			pcap.setFilter(FILTER, true);
			pcap.addPacketListener(new PacketHandler(vpn));

			while (vpn.captureThreadisStopped == false) {
				pcap.capture(1);
				count++;
				vpn.statusBar.setStatus(
					"Capturing on "
						+ usingDevice
						+ ": "
						+ count
						+ " packets received");
			}

			vpn.startButton.setEnabled(true);
			vpn.stopButton.setEnabled(false);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	} /* end of run */
} /* end of class */
