package com.masai.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.masai.dto.DepartmentDTO;
import com.masai.dto.DepartmentDTOImpl;
import com.masai.dto.EmployeeDTO;
import com.masai.dto.EmployeeDTOImpl;
import com.masai.dto.LeaveDTO;
import com.masai.dto.LeaveDTOImpl;
import com.masai.exception.NoSuchRecordFoundException;
import com.masai.exception.SomthingWentWrongException;

public class AdminDAOImpl implements AdminDAO{

	@Override
	public void addNewDepartment(String deptId, String deptName) throws SomthingWentWrongException {
		 
		Connection conn = null;
		
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "insert into dept (deptID,deptName) values (?,?)";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, deptId);
			ps.setString(2, deptName);
			
			ps.executeUpdate();
		
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
	}

	@Override
	public List<DepartmentDTO> viewAllDepartment() throws SomthingWentWrongException,NoSuchRecordFoundException{
		
		Connection conn = null;
		List<DepartmentDTO> list = null;
		
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "select deptID , deptName from dept  where is_delete = 0";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			if(DBUtility.isResultSetEmpty(rs)) {
				throw new NoSuchRecordFoundException("There is no record to review");
			}
			
			list = new ArrayList<>();
			while(rs.next()) {
				list.add(new DepartmentDTOImpl(rs.getString(1),rs.getString(2)));
			}
		
		}catch(ClassNotFoundException | SQLException ex ) {
			throw new SomthingWentWrongException("Unable to get data please try again leter");
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return list;
	}

	@Override
	public void updateDepartmentAllDetails(String oldDeptID,String deptId, String deptName)
			throws SomthingWentWrongException, NoSuchRecordFoundException {
			
		Connection conn = null;
		
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "Update dept set deptID = ? , deptName = ? where deptID = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, deptId);
			ps.setString(2,deptName);
			ps.setString(3, oldDeptID);
			
			int rs = ps.executeUpdate();
			
			if(rs==0) {
				throw new NoSuchRecordFoundException("No Such Record Found");
			}
			
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
	}


	@Override
	public List<EmployeeDTO> viewAllEmployee() throws NoSuchRecordFoundException, SomthingWentWrongException {
		Connection conn = null;
		   List<EmployeeDTO> list = new ArrayList<>();  
		   try {
				conn = DBUtility.getConnectionToDataBase();
				
				String query = "select e.empId,e.ename,e.email,e.empAddress,e.Salary_Per_Month,e.date_of_joining,d.deptID,"
						+ "d.deptName from Employee e INNER JOIN Dept d  ON e.did = d.did AND e.is_delete = 0";
				
				PreparedStatement ps = conn.prepareStatement(query);
				
				ResultSet rs = ps.executeQuery();
				if(DBUtility.isResultSetEmpty(rs)) {
					throw new NoSuchRecordFoundException("There is no such record avaiable");
				}
			  while(rs.next()) {
				  list.add(new EmployeeDTOImpl(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),"xxxxxxx",rs.getDouble(5),
						  		rs.getDate(6).toLocalDate(), new DepartmentDTOImpl(rs.getString(7),rs.getString(8))));
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
	public void addNewEmployee(EmployeeDTO employee,int did) throws SomthingWentWrongException {
		 
		Connection conn = null;
		
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "";
			if(did==0) {
				query = "insert into employee empId,ename,email,empAddress,date_of_joining,salary_per_month values (?,?,?,?,?,?)";
			}
			else {
				query = "insert into employee empId,ename,email,empAddress,date_of_joining,salary_per_month,did values (?,?,?,?,?,?,?)";
			}
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, employee.getEmpId());
			ps.setString(2, employee.getEname());
			ps.setString(3,employee.getEmail());
			ps.setString(4, employee.getEmpAddress());
			ps.setDate(5,java.sql.Date.valueOf(employee.getDate()));
			ps.setDouble(6, employee.getSalary());
			
			if(did!=0) {
				ps.setInt(7, did);
			}
			
			ps.executeUpdate();
			
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
		
	}

	@Override
	public void deleteDepartment(String deptID) throws SomthingWentWrongException {
		
		Connection conn = null;
		
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "update dept set is_delete = 1 where deptID=? AND did != 1 AND is_delete = 0";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, deptID);
			int n = ps.executeUpdate();
			
			if(n==0) {
				throw new SomthingWentWrongException("Already deleted Or No such deptId is avaiable");
			}
			
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
		
	}

	@Override
	public void transferemployeetootherdepart(int eId , int did) throws SomthingWentWrongException {
		
		Connection conn = null;
		
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "update employee set did = ? where eId = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, did);
			ps.setInt(2, eId);
			
			int n = ps.executeUpdate();
			
			if(n==0) {
				throw new SomthingWentWrongException("Unable to update deparment beacause of wrong input");
			}
			
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
		
	}

	@Override
	public void fireEmployee(int eId) throws SomthingWentWrongException {
		Connection conn = null;
		
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "update employee set e.is_delete = 1, where eId = ? && is_delete = 0";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, eId);
			
			int n = ps.executeUpdate();
			
			if(n==0) {
				throw new SomthingWentWrongException("Unable to fire employee");
			}
			
			
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
		
		
	}

	@Override
	public Map<Integer, LeaveDTO> getListOfLeaveRequst() throws SomthingWentWrongException {
		
		Connection conn = null;
		Map<Integer,LeaveDTO> map = null;
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "select leaveId,days_of_leave,type,reason,date_of_leave,status,e.empId,e.ename "
					+ "from empleave el LEfT JOIN employee e"
					+ " ON el.eId = e.eId AND status = 'panding' order by desc";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			if(DBUtility.isResultSetEmpty(rs)) {
				throw new SomthingWentWrongException("No Record is panding");
			}
		 map = new LinkedHashMap<>();
//					leaveId int ,days_of_leave, type tinyint , reason varchar(75),date_of_leave date , status varchar(15) default 'panding',eId int
		   while(rs.next()) {
			  map.put(rs.getInt(1), new LeaveDTOImpl(rs.getInt(2),rs.getInt(3)
					  ,rs.getString(4),rs.getDate(5).toLocalDate(),rs.getString(6),rs.getString(7),rs.getString(8)));
		   }
			
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
		
		return map;
	}

	@Override
	public void acceptLeaveOfEmployee(int leaveId) throws SomthingWentWrongException {
		
		Connection conn = null;
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "update empleave set status = 'Accept' where leaveId = ? AND is_removed = 0";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, leaveId);
			
			ResultSet rs = ps.executeQuery();
			
			if(DBUtility.isResultSetEmpty(rs)) {
				throw new SomthingWentWrongException("Something went wrong ! please try again letter");
			}
			
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
		
	}

	@Override
	public void rejectLeaveOfEmployee(int leaveId) throws SomthingWentWrongException {
		Connection conn = null;
		try {
			conn = DBUtility.getConnectionToDataBase();
			String query = "update empleave set status = 'Raject' where leaveId = ? AND is_removed = 0";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, leaveId);
			
			ResultSet rs = ps.executeQuery();
			
			if(DBUtility.isResultSetEmpty(rs)) {
				throw new SomthingWentWrongException("Something went wrong ! please try again letter");
			}
			
		}catch(SQLException | ClassNotFoundException ex) {
			throw new SomthingWentWrongException(ex.getMessage());
			
		}finally {
			try {
				DBUtility.closeConnectionToDataBase(conn);
			}catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}
			
		}
		
	}
}