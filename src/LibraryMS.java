
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

public class LibraryMS extends JFrame implements ActionListener
{
	Container cp;
	JLabel lblid,lblname,lblauthor,lblprice;
	JTextField txtid,txtname,txtauthor,txtprice;
	JButton btninsert,btnupdate,btndelete,btnshowall,btnsearch,btnexit;
	String url="jdbc:mysql://localhost:3306/sdb";
	String uname="root", pass="mind";
	Connection con;
	Statement st;
	PreparedStatement ps;
	String q;
	
	
	public LibraryMS(String t) throws ClassNotFoundException, SQLException
	{
		super(t);
		cp=getContentPane();
		cp.setLayout(new GridLayout(8,2));
		
		createConnection();
		
		lblid=new JLabel("\tEnter Book Id ");
		txtid=new JTextField();
		
		lblname=new JLabel("\tEnter Book Name ");
		txtname=new JTextField();
		
		lblauthor=new JLabel("\tEnter Book Author ");
		txtauthor=new JTextField();
		
		lblprice=new JLabel("\tEnter Book Price ");
		txtprice=new JTextField();
		
		btninsert=new JButton("Insert");
		btninsert.addActionListener(this);
		
		btnupdate=new JButton("Update");
		btnupdate.addActionListener(this);
		
		btndelete=new JButton("Delete");
		btndelete.addActionListener(this);
		
		btnshowall=new JButton("Showall");
		btnshowall.addActionListener(this);
		
		btnsearch=new JButton("Search");
		btnsearch.addActionListener(this);
		
		btnexit=new JButton("Exit");
		btnexit.addActionListener(this);
		
		cp.add(lblid);  cp.add(txtid);
		cp.add(lblname);  cp.add(txtname);
		cp.add(lblauthor);  cp.add(txtauthor);
		cp.add(lblprice);  cp.add(txtprice);
		cp.add(btninsert);  cp.add(btnupdate);
		cp.add(btnshowall); cp.add(btnsearch);
		cp.add(btndelete);  cp.add(btnexit);
		
		setSize(550,550);
		setVisible(true);
	}
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException 
	{
		
		LibraryMS lm=new LibraryMS("Library Management System");

	}
	
	public void createConnection() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");
		con=DriverManager.getConnection(url,uname,pass);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		try {
		if(e.getSource()==btninsert)
			
				insertBook();
		else
			if(e.getSource()==btnupdate)
					updateBook();
			else
				if(e.getSource()==btndelete)
						deleteBook();
				else
					if(e.getSource()==btnshowall)
						showallBook();
					else
						if(e.getSource()==btnsearch)
							serchBook();
					
				else 
					if(e.getSource()==btnexit)
						this.dispose();
	}
		 catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	
	private void showallBook() 
	{
		JFrame f=new JFrame("All Books");
		
		DefaultTableModel model=new DefaultTableModel();
		String colnames[]= {"Id","Name","Author","Price"};
		model.setColumnIdentifiers(colnames);
		JTable table=new JTable(model);
		
		JScrollPane scroll=new JScrollPane(table);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		try {
			st=con.createStatement();
			ResultSet rs=st.executeQuery("select * from Books");
			while(rs.next())
			{
				int id=rs.getInt(1);
				String name=rs.getString(2);
				String auth=rs.getString(3);
				Double pr=rs.getDouble(4);
				
				model.addRow(new Object[] {id,name,auth,pr});
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		f.add(scroll);
		f.setSize(400,300);
		f.setVisible(true);
	}


	private void serchBook() 
	{
		JFrame f=new JFrame("Your searched book details: ");
		
		DefaultTableModel model= new DefaultTableModel();
		
		String colname[]= {"Id","Name","Author","Price"};
		model.setColumnIdentifiers(colname);
		
		JTable table=new JTable(model);
		
		JScrollPane scroll=new JScrollPane(table);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		try
		{
			int i=Integer.parseInt(txtid.getText());
			String q="select * from Books where Bid= ?";
			PreparedStatement ps=con.prepareStatement(q);
			ps.setInt(1, i);
			ResultSet rs=ps.executeQuery();
			if(rs.next())
			{
				int id=rs.getInt(1);
				String name=rs.getString(2);
				String au=rs.getString(3);
				double pr=rs.getDouble(4);
				model.addRow(new Object[] {id,name,au,pr} );
				f.add(scroll);
				f.setSize(400,400);
				f.setVisible(true);
			}
			else
			{
					JOptionPane.showMessageDialog(this, "Record not found... ");
			}
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		
	}


	public void insertBook() throws SQLException 
	{
		q="insert into Books values(?,?,?,?)";
		ps=con.prepareStatement(q);
		ps.setInt(1, Integer.parseInt(txtid.getText()));
		ps.setString(2, txtname.getText());
		ps.setString(3, txtauthor.getText());
		ps.setDouble(4, Double.parseDouble(txtprice.getText()));
		
		int row=ps.executeUpdate();
		if(row>0)
			JOptionPane.showMessageDialog(this, row+ " book added in library...");
		showallBook();
	}
	
	private void updateBook() throws SQLException 
	{
		q="update Books set price=? where Bid=?";
		ps=con.prepareStatement(q);
		ps.setDouble(1, Double.parseDouble(txtprice.getText()));
		ps.setInt(2, Integer.parseInt(txtid.getText()));
		
		int rows=ps.executeUpdate();
		if(rows>0)
		JOptionPane.showMessageDialog(this, rows+ " book price updated...");
		else
			JOptionPane.showMessageDialog(this, "Record not found... ");
		showallBook();
	}
	
	private void deleteBook() throws SQLException 
	{
		q="delete from Books where Bid=?";
		
		ps=con.prepareStatement(q);
		ps.setInt(1, Integer.parseInt(txtid.getText()));
		
		int rows=ps.executeUpdate();
		if(rows>0)
			JOptionPane.showMessageDialog(this, rows +" book removed from library...");
		else
			JOptionPane.showMessageDialog(this, "Record not found...");
		showallBook();
		
	}


}
