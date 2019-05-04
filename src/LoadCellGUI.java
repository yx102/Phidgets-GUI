import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.DeviceClass;
import com.phidget22.DeviceID;
import com.phidget22.ErrorEvent;
import com.phidget22.ErrorListener;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioInputVoltageRatioChangeEvent;
import com.phidget22.VoltageRatioInputVoltageRatioChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class LoadCellGUI {

	private JFrame frame;
	private JTextField averageDisplay;
	private JTextField slopeDisplay;

	private String channel0 = "0";
	private String channel1 = "1";
	private String channel2 = "2";
	private String channel3 = "3";
	// The size of the ball
	private double ballWeight = 67.0;
    // The intercept and the slope from calibration of the load cells
	private double channelBase[] = {-0.000017561460,0.000039793628,0.000091985162,0.000001661583};   
    private double channelSlope[] = {188655.848542119, 179354.42750085, 188486.3242865455, 182422.7917694195};
	
	private double voltage;
	private double slope;
	private int selected  = 0;
	private ArrayList<Double> voltageList = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoadCellGUI window = new LoadCellGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoadCellGUI() {
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblAverage = new JLabel("Average Voltage");
		lblAverage.setBounds(64, 56, 127, 16);
		frame.getContentPane().add(lblAverage);
		// Window to display the average voltage		
		averageDisplay = new JTextField();
		averageDisplay.setEditable(false);
		averageDisplay.setBounds(215, 50, 134, 28);
		frame.getContentPane().add(averageDisplay);
		averageDisplay.setColumns(10);
		// Window to display the average slope
		// This slope is not the same as the field channelSlope, in here the slope refers to the slope of the surface to be measureed
		slopeDisplay = new JTextField();
		slopeDisplay.setEditable(false);
		slopeDisplay.setBounds(215, 84, 134, 28);
		frame.getContentPane().add(slopeDisplay);
		slopeDisplay.setColumns(10);
		// Remind the user about what are the previous comments
		// The exception information from the load cell will be presented in the status display
		JTextArea statusDisplay = new JTextArea();
		JScrollPane scrollBar = new JScrollPane(statusDisplay);
		statusDisplay.setEditable(false);
		scrollBar.setBounds(60, 246, 330, 126);
	
		frame.getContentPane().add(scrollBar);
		
		JLabel lblSlope = new JLabel("Slope");
		lblSlope.setBounds(64, 90, 61, 16);
		frame.getContentPane().add(lblSlope);
		
		JRadioButton rdbtnChannel0 = new JRadioButton("Channel 0(+Y)");
		rdbtnChannel0.setBounds(62, 124, 141, 23);
		frame.getContentPane().add(rdbtnChannel0);
		rdbtnChannel0.setActionCommand(channel0);
		
		JRadioButton rdbtnChannel1 = new JRadioButton("Channel 1(+X)");
		rdbtnChannel1.setBounds(62, 150, 141, 23);
		frame.getContentPane().add(rdbtnChannel1);
		rdbtnChannel1.setActionCommand(channel1);
		
		JRadioButton rdbtnChannel2 = new JRadioButton("Channel 2(-Y)");
		rdbtnChannel2.setBounds(62, 176, 141, 23);
		frame.getContentPane().add(rdbtnChannel2);
		rdbtnChannel2.setActionCommand(channel2);
		
		JRadioButton rdbtnChannel3 = new JRadioButton("Channel 3(-X)");
		rdbtnChannel3.setBounds(62, 203, 141, 23);
		frame.getContentPane().add(rdbtnChannel3);
		rdbtnChannel3.setActionCommand(channel3);
		
		ActionListener al2 = new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	// Set the channel for data collection
	          	selected = Integer.parseInt(e.getActionCommand());
	        }
		};
				
		rdbtnChannel0.addActionListener(al2);
		rdbtnChannel1.addActionListener(al2);
		rdbtnChannel2.addActionListener(al2);
		rdbtnChannel3.addActionListener(al2);
		
        // Button group for channel selection
		ButtonGroup group = new ButtonGroup();
        group.add(rdbtnChannel0);
        group.add(rdbtnChannel1);
        group.add(rdbtnChannel2);
        group.add(rdbtnChannel3);
        
		ActionListener al1 = new ActionListener() {
	        @Override
	        // Collect data from selected channel, the channel would be open for five second
	        // The average value collected in these five second would be output
	        public void actionPerformed(ActionEvent e) {
	        	    VoltageRatioInput ch = null;
				try {
					ch = new VoltageRatioInput();
				} catch (PhidgetException e2) {
					e2.printStackTrace();
				}
	            
				voltageList = new ArrayList<>();
	            ch.addAttachListener(new AttachListener() {
	    			public void onAttach(AttachEvent ae) {
	    				VoltageRatioInput phid = (VoltageRatioInput) ae.getSource();
	    				try {
	    					if(phid.getDeviceClass() != DeviceClass.VINT){
	    						System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " attached");
	    					}
	    					else{
	    						System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " hub port " + phid.getHubPort() + " attached");
	    					}
	    				} catch (PhidgetException ex) {
	    					System.out.println(ex.getDescription());
	    				}
	    			}
	            });

	            ch.addDetachListener(new DetachListener() {
	    			public void onDetach(DetachEvent de) {
	    				VoltageRatioInput phid = (VoltageRatioInput) de.getSource();
	    				try {
	    					if (phid.getDeviceClass() != DeviceClass.VINT) {
	    						System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " detached");
	    					} else {
	    						System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " hub port " + phid.getHubPort() + " detached");
	    					}
	    				} catch (PhidgetException ex) {
	    					System.out.println(ex.getDescription());
	    				}
	    			}
	            });

	            ch.addErrorListener(new ErrorListener() {
	            	public void onError(ErrorEvent ee) {
	    				System.out.println("Error: " + ee.getDescription());
	    			}
	            });

	            ch.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
	    			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent e) {
	    				System.out.println(e.getVoltageRatio());
	    				voltageList.add(e.getVoltageRatio());
	    			}
	            });
	            
	            try {      
	            	// Check if the selected channel is attached to the computer
	                ch.setChannel(selected);
	                System.out.println("Opening and waiting 5 seconds for attachment...");
	                ch.open(5000);	                
	                if(ch.getDeviceID() == DeviceID.PN_1046){
	                    System.out.println("Setting bridge enabled");
	                    ch.setBridgeEnabled(true);
	                }
	                                
	                System.out.println("\n\nGathering data for 5 seconds\n\n");
	                try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
	        
	                ch.close();
	                ch = null;
	                System.out.println("\nClosed Voltage Ratio Input");
	                double total = 0.0;
	                // Calculate the average of the voltages collected in the previous five second
	                for (double elem: voltageList) {
	                	    total += elem;
	                }
	                voltage = total/voltageList.size();
	                // Base on the channel, use the corresponding intercept and slope to translate the voltage into weight in gram
	                // Then, output the ratio between the gram with the weight of the ball, the negative signs are from the direction of the load cells
	                if (selected == 0)
	            	        slope = -(voltage - channelBase[selected])*channelSlope[selected]/ballWeight;
	                else if (selected == 1)
	            	        slope = -(voltage - channelBase[selected])*channelSlope[selected]/ballWeight;
	                else if (selected == 2)
	            	        slope = (voltage - channelBase[selected])*channelSlope[selected]/ballWeight;
	                else if (selected == 3)
	              	    slope = (voltage - channelBase[selected])*channelSlope[selected]/ballWeight;
	                // Output the first 12 digit of the voltage and ratio separately.
	                String averageStr = String.format("%.12f", voltage);
	                String slopeStr = String.format("%.12f",slope);
	                averageDisplay.setText(averageStr);
	                slopeDisplay.setText(slopeStr);
	                statusDisplay.append("Collecting from Channel " + selected + " is complete!" + "\n");	                          
	            } catch (PhidgetException ex) {
	                System.out.println(ex.getDescription());
	                // Show the error information from the exception on the status display
	                statusDisplay.append(ex.getDescription() + "\n");
	            }
	        }
		};
		// Button for collecting data
		JButton btnCollect = new JButton("Collect");
		btnCollect.setBounds(232, 124, 117, 29);
		frame.getContentPane().add(btnCollect);
		btnCollect.addActionListener(al1);		
		
		JLabel lblNewLabel = new JLabel("mV");
		lblNewLabel.setBounds(361, 56, 61, 16);
		frame.getContentPane().add(lblNewLabel);
		// Change the intercept of the selected channel
		JButton btnZero = new JButton("Zero");
		btnZero.setBounds(232, 160, 117, 29);
		frame.getContentPane().add(btnZero);
		// Clear the contends in the status display 
		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(232, 196, 117, 29);
		frame.getContentPane().add(btnClear);
				
		btnZero.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	VoltageRatioInput ch = null;
				try {
					ch = new VoltageRatioInput();
				} catch (PhidgetException e2) {
					e2.printStackTrace();
				}	            
	            voltageList = new ArrayList<>();
	            ch.addAttachListener(new AttachListener() {
	    			public void onAttach(AttachEvent ae) {
	    				VoltageRatioInput phid = (VoltageRatioInput) ae.getSource();
	    				try {
	    					if(phid.getDeviceClass() != DeviceClass.VINT){
	    						System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " attached");
	    					}
	    					else{
	    						System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " hub port " + phid.getHubPort() + " attached");
	    					}
	    				} catch (PhidgetException ex) {
	    					System.out.println(ex.getDescription());
	    				}
	    			}
	            });

	            ch.addDetachListener(new DetachListener() {
	    			public void onDetach(DetachEvent de) {
	    				VoltageRatioInput phid = (VoltageRatioInput) de.getSource();
	    				try {
	    					if (phid.getDeviceClass() != DeviceClass.VINT) {
	    						System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " detached");
	    					} else {
	    						System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " hub port " + phid.getHubPort() + " detached");
	    					}
	    				} catch (PhidgetException ex) {
	    					System.out.println(ex.getDescription());
	    				}
	    			}
	            });

	            ch.addErrorListener(new ErrorListener() {
	            	public void onError(ErrorEvent ee) {
	    				System.out.println("Error: " + ee.getDescription());
	    			}
	            });

	            ch.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
	    			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent e) {
	    				System.out.println(e.getVoltageRatio());
	    				voltageList.add(e.getVoltageRatio());
	    			}
	            });	            
	            try {            
	                ch.setChannel(selected);
	                System.out.println("Opening and waiting 5 seconds for attachment...");
	                ch.open(5000);
	                
	                if(ch.getDeviceID() == DeviceID.PN_1046){
	                    System.out.println("Setting bridge enabled");
	                    ch.setBridgeEnabled(true);
	                }	                                
	                System.out.println("\n\nGathering data for 5 seconds\n\n");
	                try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
	        
	                ch.close();
	                ch = null;
	                System.out.println("\nClosed Voltage Ratio Input");
	                double total = 0.0;
	                for (double elem: voltageList) {
	                	    total += elem;
	                }
	                voltage = total/voltageList.size();
	                // Change the intercept
	                channelBase[selected] = voltage;
	                String averageStr = String.format("%.12f", voltage);
	                averageDisplay.setText(averageStr);
	                statusDisplay.append("Channel " + selected + " has been zeroed!" + "\n");
	                
	            } catch (PhidgetException ex) {
	                System.out.println(ex.getDescription());
	                statusDisplay.append(ex.getDescription() + "\n");
	            }
	        }
		});
		
		btnClear.addActionListener(new ActionListener() {
			@Override
	        public void actionPerformed(ActionEvent e) {
				// Clear the status display
			     statusDisplay.setText(null);
			}
		});

	}
}
