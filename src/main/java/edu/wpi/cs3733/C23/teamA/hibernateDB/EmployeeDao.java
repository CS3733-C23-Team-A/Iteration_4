package edu.wpi.cs3733.C23.teamA.hibernateDB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class EmployeeDao {

  public static void writeEmployeeToCSV(List<EmployeeEntity> emps) throws IOException {
    File csvFile = new File("employees.csv");
    FileWriter fileWriter = new FileWriter(csvFile);
    fileWriter.write("employeeid,job,name,password,username\n");
    for (EmployeeEntity emp : emps) {
      fileWriter.write(
          emp.getEmployeeid()
              + ","
              + emp.getJob()
              + ","
              + emp.getName()
              + ","
              + emp.getPassword()
              + ","
              + emp.getUsername()
              + "\n");
    }

    fileWriter.close();
  }
}
