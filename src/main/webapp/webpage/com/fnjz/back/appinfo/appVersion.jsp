<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>App版本升级</title>
    <t:base type="jquery,easyui,tools,DatePicker"></t:base>
</head>
<body style="overflow-y: scroll" scroll="yes">
<t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="appVersionController.do?save">
    <input id="id" name="id" type="hidden" value="${appVersionPage.id }">
    <table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
        <tr>
            <td align="right">
                <label class="Validform_label">
                    app版本号:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="version" name="version" ignore="ignore" value="${appVersionPage.version}"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    终端系统标识:
                </label>
            </td>
            <td class="value">
                    <%--<input class="inputxt" id="mobileSystem" name="mobileSystem" ignore="ignore"
                           value="${appVersionPage.mobileSystem}" />--%>
                Android<input type="radio" name="mobileSystem" ignore="ignore"
                              <c:if test="${'1' !=   appVersionPage.mobileSystem }">checked="checked"</c:if> value="0"/>
                ios<input type="radio" name="mobileSystem" ignore="ignore"
                          <c:if test="${'1' ==   appVersionPage.mobileSystem }">checked="checked"</c:if> value="1"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    app状态,是否强制升级:
                </label>
            </td>
            <td class="value">
                    <%--<input class="inputxt" id="installStatus" name="installStatus" ignore="ignore"
                           value="${appVersionPage.installStatus}" datatype="n"/>--%>
                否<input type="radio" name="installStatus" ignore="ignore"
                        <c:if test="${'1' !=   appVersionPage.installStatus }">checked="checked"</c:if> value="0"/>
                是<input type="radio" name="installStatus" ignore="ignore"
                        <c:if test="${'1' ==   appVersionPage.installStatus }">checked="checked"</c:if> value="1"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    app状态,是否生效:
                </label>
            </td>
            <td class="value">
                    <%--<input class="inputxt" id="appStatus" name="appStatus" ignore="ignore"
                           value="${appVersionPage.appStatus}" datatype="n"/>--%>
                否<input type="radio" name="appStatus" ignore="ignore"
                        <c:if test="${'1' !=   appVersionPage.appStatus }">checked="checked"</c:if> value="0"/>
                是<input type="radio" name="appStatus" ignore="ignore"
                        <c:if test="${'1' ==   appVersionPage.appStatus }">checked="checked"</c:if> value="1"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
            <%--<tr>
                <td align="right">
                    <label class="Validform_label">
                        系统适配范围max:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="systemMax" name="systemMax" ignore="ignore"  value="${appVersionPage.systemMax}" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <label class="Validform_label">
                        系统适配范围min:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="systemMin" name="systemMin" ignore="ignore"  value="${appVersionPage.systemMin}" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>--%>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    app下载地址:
                </label>
            </td>
            <td class="value">
                    <%--<input class="inputxt" id="url" name="url" ignore="ignore"  value="${appVersionPage.url}" />
                    <input id="fileupload" type="file" alt="url" name="files[]" data-url="appVersionController.do?upload" multiple>

                    <div id="progress" class="progress">
                        <div class="bar" style="width: 0%;"></div>
                    </div>--%>
                <%--<c:if test="${not empty appVersionPage.url}">--%>
                    <input class="inputxt" id="url" name="url" ignore="ignore" value="${appVersionPage.url}" style="width:100%"/>
                <%--</c:if>--%>

                <t:webUploader name="url" fileSingleSizeLimit="500" buttonStyle="btn-green btn-M mb20"
                               fileNumLimit="1"></t:webUploader>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
            <%--<tr>
                <td align="right">
                    <label class="Validform_label">
                        app大小:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="size" name="size" ignore="ignore"  value="${appVersionPage.size}" datatype="n" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>--%>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    更新内容:
                </label>
            </td>
            <td class="value">
                <%--<input class="inputxt" id="updateLog" name="updateLog" ignore="ignore"
                       value="${appVersionPage.updateLog}"/>--%>
                <textarea name="updateLog" id="updateLog" cols="60" rows="10">${appVersionPage.updateLog}</textarea>
                <span class="Validform_checktip"></span>
            </td>
        </tr>

    </table>
</t:formvalid>
</body>