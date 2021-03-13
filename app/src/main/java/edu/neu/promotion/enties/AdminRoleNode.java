package edu.neu.promotion.enties;

import java.io.Serializable;

public class AdminRoleNode implements Serializable {

    public String adminRoleGroupId;
    public String roleId;
    public String groupId;
    public String adminId;
    public String checkState;

    public GroupNode groupObj;
    public RoleNode roleObj;
    public DictionaryItemNode checkStateObj;
}
