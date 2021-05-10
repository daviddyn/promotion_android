package edu.neu.promotion.enties;

import java.io.Serializable;

public class AdminRoleGroupNode implements Serializable {

    public String adminRoleGroupId;
    public String roleId;
    public String groupId;
    public String adminId;
    public String checkState;
    public GroupNode groupObj;
    public RoleNode roleObj;
    public DictionaryItemNode checkStateObj;
    public AdminNode adminObj;
    public String submitState;
    public RoleNode[] roleDic;
    public GroupNode[] groupDic;
    public AdminNode[] adminDic;
    public DictionaryItemNode[] checkStateDic;
    public Boolean canCheck = false;
}
