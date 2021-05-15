package edu.neu.promotion;

import com.davidsoft.http.HttpContentBytesReceiver;
import com.davidsoft.http.HttpContentJsonProvider;
import com.davidsoft.http.HttpContentJsonReceiver;
import com.davidsoft.utils.JsonNode;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.ServerRequestNode;
import edu.neu.promotion.enties.ServerResponseNode;

public final class ServerInterfaces {

    public static final int RESULT_CODE_SUCCESS = 200;
    public static final int RESULT_CODE_UNKNOWN_ERROR = 301;
    public static final int RESULT_CODE_USER_NOT_EXISTS = 501;
    public static final int RESULT_CODE_USER_ALREADY_EXISTS = 502;
    public static final int RESULT_CODE_WRONG_PASSWORD = 502;
    public static final int RESULT_CODE_USER_HAS_BEEN_FREEZE = 503;
    public static final int RESULT_CODE_VERIFY_CODE_ERROR = 509;

    private static final String baseUrl = "https://apps.neu.edu.qizhiqiang.com/promotion";
    //private static final String baseUrl = "http://192.168.3.3/promotion";

    public static ServerResponseNode analyseCommonContent(ServerInvoker.InvokeResult invokeResult) {
        return JsonNode.toObject(((ServerInvoker.InvokeResult) invokeResult).getContent(), ServerResponseNode.class);
    }

    public static ServerResponseNode analyseCommonListContent(ServerInvoker.InvokeResult invokeResult) {
        ServerResponseNode responseNode = analyseCommonContent(invokeResult);
        responseNode.object = responseNode.object.getField("items");
        return JsonNode.toObject(((ServerInvoker.InvokeResult) invokeResult).getContent(), ServerResponseNode.class);
    }

    public static ServerInvoker verifyCode() {
        HashMap<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        return new ServerInvoker(
                baseUrl + "/verifyCode",
                null,
                new HttpContentBytesReceiver(),
                extraHeaders
        );
    }

    public static ServerInvoker adminSign(String adminAccount, String adminPasswordMD5, String adminName, String adminSex, String adminPhone, String adminCollege, String adminPosition, String adminDegree, String adminIdentityNumber, String code, String AESCode) {
        JsonNode admin = JsonNode.createEmptyObject();
        admin.setField("adminAccount", adminAccount);
        admin.setField("adminPassword", adminPasswordMD5);
        admin.setField("adminPasswordAgn", adminPasswordMD5);
        admin.setField("adminName", adminName);
        admin.setField("adminSex", adminSex);
        admin.setField("adminPhone", adminPhone);
        admin.setField("adminCollege", adminCollege);
        admin.setField("adminPosition", adminPosition);
        admin.setField("adminDegree", adminDegree);
        admin.setField("adminIdentityNumber", adminIdentityNumber);
        admin.setField("code", code);
        admin.setField("AESCode", AESCode);
        ServerRequestNode requestNode = new ServerRequestNode();
        requestNode.params = JsonNode.createEmptyObject();
        requestNode.params.setField("admin", admin);
        return new ServerInvoker(
                baseUrl + "/adminSign",
                new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                new HttpContentJsonReceiver(),
                null
        );
    }

    public static ServerInvoker adminLogin(String adminAccount, String adminPasswordMD5) {
        JsonNode admin = JsonNode.createEmptyObject();
        admin.setField("adminAccount", adminAccount);
        admin.setField("adminPassword", adminPasswordMD5);
        ServerRequestNode requestNode = new ServerRequestNode();
        requestNode.params = JsonNode.createEmptyObject();
        requestNode.params.setField("admin", admin);
        return new ServerInvoker(
                baseUrl + "/adminLoginApp",
                new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                new HttpContentJsonReceiver(),
                null
        );
    }

    public static ServerInvoker getMyselfMessage(String token) {
        HashMap<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("Token", token);
        ServerRequestNode requestNode = new ServerRequestNode();
        requestNode.params = JsonNode.createEmptyObject();
        return new ServerInvoker(
                baseUrl + "/getMyselfMessage",
                new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                new HttpContentJsonReceiver(),
                extraHeaders
        );
    }

    public static final class Role {

        public static ServerInvoker getApplicableRole(String token) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            return new ServerInvoker(
                    baseUrl + "/role/getApplicableRole",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker getAdminRole(String token) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            return new ServerInvoker(
                    baseUrl + "/role/getAdminRoleApp",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker applyRole(String token, String roleId, String groupId) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode adminRoleGroup = JsonNode.createEmptyObject();
            adminRoleGroup.setField("roleId", roleId);
            adminRoleGroup.setField("groupId", groupId);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("adminRoleGroup", adminRoleGroup);
            return new ServerInvoker(
                    baseUrl + "/role/applyRole",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker accessRole(String token, String adminRoleGroupId) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode adminRoleGroup = JsonNode.createEmptyObject();
            adminRoleGroup.setField("adminRoleGroupId", adminRoleGroupId);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("adminRoleGroup", adminRoleGroup);
            return new ServerInvoker(
                    baseUrl + "/role/accessRoleApp",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker cancelRole(String token, String adminRoleGroupId) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode adminRoleGroup = JsonNode.createEmptyObject();
            adminRoleGroup.setField("adminRoleGroupId", adminRoleGroupId);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("adminRoleGroup", adminRoleGroup);
            return new ServerInvoker(
                    baseUrl + "/role/cancelRole",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker getAdminRoleGroupBySearch(String token, AdminNode adminInfo, String checkState, int currentPage, int itemsPerPage) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode admin;
            if (adminInfo == null) {
                admin = JsonNode.createEmptyObject();
            }
            else {
                admin = JsonNode.valueOf(adminInfo);
            }
            admin.setField("checkState", checkState);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("admin", admin);
            requestNode.params.setField("currentPage", currentPage);
            requestNode.params.setField("showCount", itemsPerPage);
            return new ServerInvoker(
                    baseUrl + "/role/getAdminRoleGroupBySearch",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker getNoCheckAdminRoleGroupBySearch(String token, int currentPage, int itemsPerPage) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode admin = JsonNode.createEmptyObject();
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("admin", admin);
            requestNode.params.setField("currentPage", currentPage);
            requestNode.params.setField("showCount", itemsPerPage);
            return new ServerInvoker(
                    baseUrl + "/role/getNoCheckAdminRoleGroupBySearch",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker getAdminRoleTimeList(String token, String adminRoleGroupId) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode adminRoleGroup = JsonNode.createEmptyObject();
            adminRoleGroup.setField("adminRoleGroupId", adminRoleGroupId);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("adminRoleGroup", adminRoleGroup);
            return new ServerInvoker(
                    baseUrl + "/role/getAdminRoleTimeList",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker checkRole(String token, String adminRoleGroupId) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode adminRoleGroup = JsonNode.createEmptyObject();
            adminRoleGroup.setField("adminRoleGroupId", adminRoleGroupId);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("adminRoleGroup", adminRoleGroup);
            return new ServerInvoker(
                    baseUrl + "/role/checkRole",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker uncheckRole(String token, String adminRoleGroupId) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode adminRoleGroup = JsonNode.createEmptyObject();
            adminRoleGroup.setField("adminRoleGroupId", adminRoleGroupId);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("adminRoleGroup", adminRoleGroup);
            return new ServerInvoker(
                    baseUrl + "/role/unCheckRole",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

    }

    public static final class Group {

        public static ServerInvoker getAllGroup(String token) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            return new ServerInvoker(
                    baseUrl + "/group/getAllGroup",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static ServerInvoker getAllGroupByToken(String token) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            return new ServerInvoker(
                    baseUrl + "/group/getAllGroupByToken",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

    }

    public static final class Dictionary {

        public static final String DICTIONARY_TYPE_COLLEGE = "COLLEGE";
        public static final String DICTIONARY_TYPE_ADMIN_CHECK_STATE = "ADMIN_CHECK_STATE";

        public static ServerInvoker getDictionaryItems(String dictionaryType) {
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("dictionaryType", dictionaryType);
            return new ServerInvoker(
                    baseUrl + "/dictionary/getDictionaryItems",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    null
            );
        }

    }

    public static final class Project {

        public static ServerInvoker getProjectBySearch(String token, String projectAdmin, String projectGroup, String projectName, String projectState, int currentPage, int itemsPerPage) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            JsonNode project = JsonNode.createEmptyObject();
            project.setField("projectAdmin", projectAdmin);
            project.setField("projectGroup", projectGroup);
            project.setField("projectName", projectName);
            project.setField("projectState", projectState);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("project", project);
            requestNode.params.setField("currentPage", currentPage);
            requestNode.params.setField("showCount", itemsPerPage);
            return new ServerInvoker(
                    baseUrl + "/project/getProjectBySearch",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

        public static final String STATE_FILTER_ALL = "all";
        public static final String STATE_FILTER_AVAILABLE = "available";
        public static final String STATE_FILTER_NOT_DRAFT = "notDraft";

        public static ServerInvoker getProjectByPreset(String token, boolean isMyProject, boolean isMyJoinProject, String stateFilter, int currentPage, int itemsPerPage) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            requestNode.params.setField("isMyProject", isMyProject);
            requestNode.params.setField("isMyJoinProject", isMyJoinProject);
            requestNode.params.setField("stateFilter", stateFilter);
            requestNode.params.setField("currentPage", currentPage);
            requestNode.params.setField("showCount", itemsPerPage);
            return new ServerInvoker(
                    baseUrl + "/project/getProjectBySearchApp",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }

    }

    public static final class Power {

        public static ServerInvoker getPowerList(String token) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Token", token);
            ServerRequestNode requestNode = new ServerRequestNode();
            requestNode.params = JsonNode.createEmptyObject();
            return new ServerInvoker(
                    baseUrl + "/power/getPowerListApp",
                    new HttpContentJsonProvider(JsonNode.valueOf(requestNode)),
                    new HttpContentJsonReceiver(),
                    extraHeaders
            );
        }
    }
}
