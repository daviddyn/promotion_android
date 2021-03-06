package edu.neu.promotion.enties;

import java.io.Serializable;

public class AdminNode implements Serializable {

    public String adminId;
    public String adminAccount;
    public String adminPassword;
    public String adminName;
    public String adminSex;
    public String adminPhone;
    public String adminCollege;
    public DictionaryItemNode adminCollegeObj;
    public String adminPosition;
    public String adminDegree;
    public String adminIdentityNumber;
    public String adminUseful;
    public String adminText;
    public String adminPasswordAgn;
    public String code;
    public String aescode;

    public boolean isStudent() {
        return "学生".equals(adminPosition);
    }

    public boolean isFemale() {
        return "女".equals(adminSex);
    }
}
