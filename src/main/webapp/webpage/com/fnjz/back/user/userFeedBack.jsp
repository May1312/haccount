<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户反馈</title>
    <t:base type="jquery,easyui,tools,DatePicker"></t:base>
</head>
<body style="overflow-y: hidden" scroll="no">
<t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="userFeedBackController.do?save">
    <input id="id" name="id" type="hidden" value="${userFeedBackPage.id }">
    <table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
        <tr>
            <td align="right">
                <label class="Validform_label">
                    用户详情id:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="userInfoId" name="userInfoId" ignore="ignore"
                       value="${userFeedBackPage.userInfoId}" datatype="n"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    反馈内容:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="content" name="content" ignore="ignore" value="${userFeedBackPage.content}"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    反馈图片:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="url" name="url" ignore="ignore" value="${userFeedBackPage.url}"/>
                <img src="${userFeedBackPage.url}" id="show"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    状态
                </label>
            </td>
            <td class="value">
                    <%--<input class="inputxt" id="status" name="status" ignore="ignore"  value="${userFeedBackPage.status}" />:0_未处理   1_已查看  2_已解决:--%>
                <select class="selectxt" id="status" name="status" ignore="ignore"
                        value="${userFeedBackPage.status}">
                    <option value="0"
                            <c:if test="${'0' == userFeedBackPage.status}">selected="selected"</c:if> >未处理
                    </option>
                    <option value="1" <c:if test="${'1' == userFeedBackPage.status}">selected="selected"</c:if>>已查看
                    </option>
                    <option value="2" <c:if test="${'2' == userFeedBackPage.status}">selected="selected"</c:if>>已解决
                    </option>


                </select>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
            <%--<tr>
                <td align="right">
                    <label class="Validform_label">
                        删除标记:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="delflag" name="delflag" ignore="ignore"  value="${userFeedBackPage.delflag}" datatype="n" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <label class="Validform_label">
                        删除时间:
                    </label>
                </td>
                <td class="value">
                    <input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="delDate" name="delDate" ignore="ignore"    value="<fmt:formatDate value='${userFeedBackPage.delDate}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>--%>
    </table>
</t:formvalid>
</body>