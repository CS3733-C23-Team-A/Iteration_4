package edu.wpi.cs3733.C23.teamA;

import edu.wpi.cs3733.C23.teamA.enums.UrgencyLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class ServiceRequestEntities {
  @Getter @Setter private String name;
  @Getter @Setter private String IDNum;
  @Getter @Setter private String location;
  @Getter @Setter private String description;
  @Getter @Setter private int requestType;

  @Getter @Setter private UrgencyLevel ul;

  public ServiceRequestEntities(
      String name, String IDNum, String location, String description, String ul, int requestType) {
    this.name = name;
    this.IDNum = IDNum;
    this.location = location;
    this.description = description;
    this.requestType = requestType;
    // this.urgencyLevel = UrgencyLevel.LOW;

    switch (ul) {
      case "Low":
        this.ul = UrgencyLevel.LOW;
      case "Medium":
        this.ul = UrgencyLevel.MEDIUM;
      case "High":
        this.ul = UrgencyLevel.HIGH;
      case "Extremely Urgent":
        this.ul = UrgencyLevel.EXTREMELY_URGENT;
      default:
        this.ul = UrgencyLevel.LOW; // CHECK WHAT IS THE DEFAULT CASE
    }
  }
}
