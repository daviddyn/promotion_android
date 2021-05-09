package edu.neu.promotion.enties;

import java.util.Date;

public class ProjectNode {

    public String projectId;

    public String projectGroup;

    public String projectName;

    public String projectState;

    public String projectAdmin;

    public String projectMakeDate;

    public String projectAlterDate;

    public String projectDescription;

    public GroupNode projectGroupObj;

    public AdminNode projectAdminObj;

    public DictionaryItemNode projectStateObj;

    public AdminNode[] projectAdminList;

    public String projectMakeDateStr;

    public String projectAlterDateStr;

    public Boolean canCheck = false;

    public Boolean canEdit = false;

    public Boolean canDelete = false;

    public Boolean canDownloadApproval = false;

    public Boolean canDownloadRecord = false;

    public String operateAdmin;

    public String submitState;
}