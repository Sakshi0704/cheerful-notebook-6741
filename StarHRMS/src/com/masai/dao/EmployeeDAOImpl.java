package com.masai.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.masai.dto.DepartmentDTO;
import com.masai.dto.DepartmentDTOImpl;
import com.masai.dto.EmployeeDTO;
import com.masai.dto.EmployeeDTOImpl;
import com.masai.dto.LeaveDTO;
import com.masai.dto.LeaveDTOImpl;
import com.masai.exception.NoSuchRecordFoundException;
import com.masai.exception.SomthingWentWrongException;
import com.masai.exception.WrongCredentialsException;

public class EmployeeDAOImpl implements EmployeeDAO {
	
	@Override
	public List<String> empLogIn(String emailId, String password) throws WrongCredentialsException, SomthingWentWrongException {
		// get connection to database....
		Connection conn = null;
		List<String> list = new ArrayList<>();
		try {
			conn = DBUtility.getConnectionToDataBase();
			
			String query = "select eId,ename from Employee where email=? && password=?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, emailId);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if(DBUtility.isResultSetEmpty(rs)) {
				throw new WrongCredentialsException("Wrong Login Credentials");
			}
		  while(rs.next()) {
			  list.add(rs.getInt(1)+"");
			  list.add(rs.getString(2));
		  }
			
		} catch (ClassNotFoundException |SQLException e) {
			//throw new SomthingWentWrongException("Unable to connection with database! please try again");
			throw new SomthingWentWrongException(e.getMessage());
		}finally{
			try {
				DBUtility.closeConnectionToDataBase(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	   return list;
	}

	@Override
	public EmployeeDTO viewYourProfile(int empId) throws NoSuchRecordFoundException, SomthingWentWrongException {
		
		// get connection to database....
		   Connection conn = null;
		   EmployeeDTO employee = null;
		   try {
				conn = DBUtility.getConnectionToDataBase();
				// need to work.................................//
				String query = "select e.empId,e.ename,e.email,e.empAddress,e.Salary_Per_Month,e.date_of_joining,"
						+ "d.deptID,d.deptName from Employee e INNER JOIN dept d ON (d.did = e.did AND e.eId = ? AND e.is_delete = 0)";
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setInt(1, empId);
				ResultSet rs = ps.executeQuery();
				if(DBUtility.isResultSetEmpty(rs)) {
					throw new NoSuchRecordFoundException("There is no such record avaiable");
				}
			  if(rs.next()) {
				  employee = new EmployeeDTOImpl(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),"xxxxxxx",rs.getDouble(5),
						  		rs.getDate(6).toLocalDate(), new DepartmentDTOImpl(rs.getString(7),rs.getString(8)));
				  return employee;
			  }
			  
				
			} catch (ClassNotFoundException |SQLException e) {
				//throw new SomthingWentWrongException("Unable to connection with database! please try again");
				throw new SomthingWentWrongException(e.getMessage());
			}finally{
				try {
					DBUtility.closeConnectionToDataBase(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		  
		   return employee;
		
	}

	@Override
	public void changePassword(String oldPassword,String updatedPassword, int empId)
			throws NoSuchRecordFoundException, SomthingWentWrongException {
			Connection conn = null;
		   try {
				conn = DBUtility.getConnectionToDataBase();
				
				String query = "update employee set password = ? where (password = ? & eId = ?) AND is_delete = 0";
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, oldPassword);
				ps.setString(2, updatedPassword);
				ps.setInt(3,empId);
				
				int rs = ps.executeUpdate();
				if(rs==0) {
					throw new NoSuchRecordFoundException("Invalid Details");
				}
				
			} catch (ClassNotFoundException | SQLException e) {
				//throw new SomthingWentWrongException("Unable to connection with database! please try again");
				throw new SomthingWentWrongException(e.getMessage());
			}finally{
				try {
					DBUtility.closeConnectionToDataBase(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
	}

	@Override
	public void applyForLeave(LeaveDTO leave, int empId) throws SomthingWentWrongException {
		
		Connection conn = null;
		try {
			
			conn = DBUtility.getConnectionToDataBase();
			
			String query = "insert into empleave"
					+ "(days_of_leave,type,reason,eid,date_of_leave)"
					+ "values"
					+ "(?,?,?,?,?)";
	
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, leave.getDays_of_leave());
			ps.setInt(2, leave.getType());
			ps.setString(3, leave.getReason());
			ps.setInt(4, empId);
			ps.setDate(5, Date.valueOf(LocalDate.now()));
			
		  int n = ps.executeUpdate();
		  if(n==0) {
			  throw new SomthingWentWrongException("Unable to add Input is Wrong");
		  }

		}catch(ClassNotFoundException | SQLException e) {
			throw new SomthingWentWrongException(e.getMessage());
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public List<LeaveDTO> leaveStatus(int empId) throws SomthingWentWrongException, NoSuchRecordFoundException {
		
		 Connection conn = null;
		   List<LeaveDTO> list = new ArrayList<>();  
		   try {
				conn = DBUtility.getConnectionToDataBase();
				// need to work.................................//
				//String query = "select days_of_leave,type,reason,date_of_leave,status from empleave where eId=? order by date_of_leave desc limit 2";
				
				String query = "select days_of_leave,type,reason,date_of_leave,status,e.empId,e.ename "
						+ "from empleave el LEfT JOIN employee e where eId = ?"
						+ " ON el.eId = e.eId order by date_of_leave desc limit 2";
				
				
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setInt(1, empId);
				ResultSet rs = ps.executeQuery();
				if(DBUtility.isResultSetEmpty(rs)) {
					throw new NoSuchRecordFoundException("There is no such record avaiable");
				}
				while(rs.next()) {
					list.add(new LeaveDTOImpl(rs.getInt(2),rs.getInt(3)
							  ,rs.getString(4),rs.getDate(5).toLocalDate(),rs.getString(6),rs.getString(7),rs.getString(8)));
				}
				
			} catch (ClassNotFoundException |SQLException e) {
				//throw new SomthingWentWrongException("Unable to connection with database! please try again");
				throw new SomthingWentWrongException(e.getMessage());
			}finally{
				try {
					DBUtility.closeConnectionToDataBase(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		  
		   return list;
		   
	}

	@Override
	public List<LeaveDTO> recordOfLeave(int empId) throws SomthingWentWrongException, NoSuchRecordFoundException {
		 Connection conn = null;
		   List<LeaveDTO> list = new ArrayList<>();  
		   try {
				conn = DBUtility.getConnectionToDataBase();
				// need to work.................................//
				//String query = "select days_of_leave,type,reason,date_of_leave,status from empleave where eId=? order by date_of_leave desc";

				String query = "select days_of_leave,type,reason,date_of_leave,status,e.empId,e.ename "
						+ "from empleave el LEfT JOIN employee e where eId = ?"
						+ " ON el.eId = e.eId order by date_of_leave desc limit 2";
				
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setInt(1, empId);
				ResultSet rs = ps.executeQuery();
				if(DBUtility.isResultSetEmpty(rs)) {
					throw new NoSuchRecordFoundException("There is no such record avaiable");
				}
				while(rs.next()) {
					list.add(new LeaveDTOImpl(rs.getInt(2),rs.getInt(3)
							  ,rs.getString(4),rs.getDate(5).toLocalDate(),rs.getString(6),rs.getString(7),rs.getString(8)));
				}
				
			} catch (ClassNotFoundException |SQLException e) {
				//throw new SomthingWentWrongException("Unable to connection with database! please try again");
				throw new SomthingWentWrongException(e.getMessage());
			}finally{
				try {
					DBUtility.closeConnectionToDataBase(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		  
		   return list;
	}

	@Override
	public double totalSalaryOfMonth(LocalDate startDate,LocalDate endDate,int empId) throws SomthingWentWrongException {
		
		Connection conn = null;
		double salary = 0;
		int total_leave = 0;
		try {
			conn = DBUtility.getConnectionToDataBase();
			// need to work.................................//
			String query = "select count(*) from empleave where eId = ? AND status = 'Accepted' AND type = 3 "
					+ "AND date_of_leave BETWEEN ? AND ? ";
							
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, empId);
			ps.setDate(2, Date.valueOf(startDate));
			ps.setDate(3,Date.valueOf(endDate));
			
			ResultSet rs = ps.executeQuery();
			if(DBUtility.isResultSetEmpty(rs)) {
				PreparedStatement ps1 = conn.prepareStatement("select Salary_Per_Month from employee where eId = ? AND is_delete = 0");
				ps1.setInt(1, empId);
				
				ResultSet rs2 = ps.executeQuery();
				if(DBUtility.isResultSetEmpty(rs2)){
					throw new SomthingWentWrongException("Somthing went wrong! Please try again after some time ");
				}
				salary = rs2.getDouble(1);
			}
			while(rs.next()) {
				total_leave = rs.getInt(1);
			  }
			
			PreparedStatement ps2 = conn.prepareStatement("select round(Salary_Per_Month-(Salary_Per_Month/30*?) , 2) where eId = ?");
			ps2.setInt(1, total_leave);
			ps2.setInt(2,empId);
			
			ResultSet rs2=ps2.executeQuery();
			if(DBUtility.isResultSetEmpty(rs2)) {
				throw new SomthingWentWrongException(" Something went Wrong !! please try after some time ");
			}
			while(rs2.next()) {
				salary=rs2.getDouble(1);
			}
		} catch (ClassNotFoundException |SQLException e) {
			//throw new SomthingWentWrongException("Unable to connection with database! please try again");
			throw new SomthingWentWrongException(e.getMessage());
		}finally{
			try {
				DBUtility.closeConnectionToDataBase(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return salary;
	}

	@Override
	public double totalSalaryAnnualy(int empId) throws SomthingWentWrongException {
		
		Connection conn = null;
		double salary = 0;
		try {
			conn = DBUtility.getConnectionToDataBase();
			
			PreparedStatement ps = conn.prepareStatement("select round(Salary_Per_Month*12) , 2) from employee where eId = ?");
			
			ps.setInt(1,empId);
			ResultSet rs = ps.executeQuery();
			if(DBUtility.isResultSetEmpty(rs)) {
				throw new SomthingWentWrongException("Something went Wrong !! please try after some time");
			}
			while(rs.next()) {
				salary=rs.getDouble(1);
			}
			
		} catch (ClassNotFoundException | SQLException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return salary;
	}
	
	
	
}