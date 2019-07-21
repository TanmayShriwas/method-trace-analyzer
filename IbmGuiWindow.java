package ibmcodes;
import ibmcodes.Sync;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.SwingConstants;
import java.awt.SystemColor;

public class IbmGuiWindow {
	static String passingClass;   //strings to accept the file names.
	static String failingClass;
	private JFrame frame;
	private JTextField file1;
	private JTextField file2;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IbmGuiWindow window = new IbmGuiWindow();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/*code to access the file names*/
	
	public String getPassingFile() {
		passingClass=file1.getText();
		return passingClass;
	}
	
	public String getFailingFile() {
	   failingClass=file2.getText();
		return failingClass;
	}

	/**
	 * Create the application.
	 */
	public IbmGuiWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setForeground(SystemColor.windowBorder);
		frame.getContentPane().setBackground(new Color(245, 245, 245));
		frame.setBounds(200, 400, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblFilename = new JLabel("Passing Class");
		lblFilename.setHorizontalAlignment(SwingConstants.LEFT);
		lblFilename.setForeground(new Color(34, 139, 34));
		lblFilename.setFont(new Font("Book Antiqua", Font.BOLD | Font.ITALIC, 24));
		lblFilename.setBounds(25, 90, 173, 30);
		frame.getContentPane().add(lblFilename);
		
		file1 = new JTextField();
		file1.setHorizontalAlignment(SwingConstants.RIGHT);
		file1.setBackground(new Color(220, 220, 220));
		file1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				passingClass = file1.getText();
				
			}
		});
		file1.setBounds(246, 90, 156, 30);
		frame.getContentPane().add(file1);
		file1.setColumns(10);
		
		
		file2 = new JTextField();
		file2.setHorizontalAlignment(SwingConstants.RIGHT);
		file2.setBackground(new Color(220, 220, 220));
		file2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				failingClass = file2.getText();
				
			}
		});
		
		file2.setColumns(10);
		file2.setBounds(246, 150, 156, 30);
		frame.getContentPane().add(file2);
		
		
		JButton btnSubmit = new JButton("SUBMIT\r\n");
		btnSubmit.setVerticalAlignment(SwingConstants.BOTTOM);
		btnSubmit.setFont(new Font("Book Antiqua", Font.BOLD | Font.ITALIC, 15));
		btnSubmit.setForeground(new Color(199, 21, 133));
		btnSubmit.setBackground(new Color(219, 112, 147));
		
		 btnSubmit.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent arg0) {
				passingClass = file1.getText();
				failingClass = file2.getText();
				try {
					File file = new File("filenames.txt");
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(passingClass);
					fileWriter.write(" ");
					fileWriter.write(failingClass);
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
				
					String[] command =
					    {
					        "cmd",
					    };
					    Process p;
	
					try {
						p = Runtime.getRuntime().exec(command);
					        new Thread(new Sync(p.getErrorStream(), System.err)).start();
				                new Thread(new Sync(p.getInputStream(), System.out)).start();
				                PrintWriter stdin = new PrintWriter(p.getOutputStream());
				                stdin.println("javac ibmcodes/LogAnalysis.java");
				                stdin.println("java ibmcodes.LogAnalysis");
				                stdin.close();
				                p.waitFor();
				    	} 
					catch (Exception e) {
				 		e.printStackTrace();
				 		}
				}
			
			});
	
		
		btnSubmit.setBounds(171, 223, 108, 27);
		frame.getContentPane().add(btnSubmit);
		
		JLabel lblMethodTracerGui = new JLabel("Trace Analyzer\r\n");
		lblMethodTracerGui.setHorizontalAlignment(SwingConstants.CENTER);
		lblMethodTracerGui.setForeground(new Color(178, 34, 34));
		lblMethodTracerGui.setFont(new Font("Book Antiqua", Font.BOLD | Font.ITALIC, 32));
		lblMethodTracerGui.setBounds(59, 11, 317, 44);
		frame.getContentPane().add(lblMethodTracerGui);
		
		JLabel lblEnterPassingClass = new JLabel("Failing Class");
		lblEnterPassingClass.setHorizontalAlignment(SwingConstants.LEFT);
		lblEnterPassingClass.setForeground(new Color(220, 20, 60));
		lblEnterPassingClass.setFont(new Font("Book Antiqua", Font.BOLD | Font.ITALIC, 24));
		lblEnterPassingClass.setBounds(25, 150, 182, 30);
		frame.getContentPane().add(lblEnterPassingClass);
		
		
	}
}
