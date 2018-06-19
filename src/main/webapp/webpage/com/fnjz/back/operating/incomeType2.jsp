<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>收入标签管理</title>
    <t:base type="jquery,easyui,tools,DatePicker"></t:base>
</head>
<body style="overflow-y: hidden" scroll="no">
<t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="incomeTypeController.do?save">
    <input id="id" name="id" type="hidden" value="${incomeTypePage.id }">
    <table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
        <tr>
            <td align="right">
                <label class="Validform_label">
                    收入类目名称:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="incomeName" name="incomeName" ignore="ignore"
                       value="${incomeTypePage.incomeName}"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
            <%--<tr>
                <td align="right">
                    <label class="Validform_label">
                        收入父级类目:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="parentId" name="parentId" ignore="ignore"  value="${incomeTypePage.parentId}" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <label class="Validform_label">
                        图标:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="icon" name="icon" ignore="ignore"  value="${incomeTypePage.icon}" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>--%>
            <%--<tr>
                <td align="right">
                    <label class="Validform_label">
                        状态(0:下线,1:上线):
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="status" name="status"   value="${incomeTypePage.status}" datatype="*" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>--%>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    优先级:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="priority" name="priority" value="${incomeTypePage.priority}" datatype="n"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
            <%--<tr>
                <td align="right">
                    <label class="Validform_label">
                        常用标记,0:不常用,1:常用:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="mark" name="mark" ignore="ignore"  value="${incomeTypePage.mark}" datatype="n" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <label class="Validform_label">
                        删除标记:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="delflag" name="delflag" ignore="ignore"  value="${incomeTypePage.delflag}" datatype="n" />
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
                    <input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="delDate" name="delDate" ignore="ignore"    value="<fmt:formatDate value='${incomeTypePage.delDate}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>--%>
    </table>
</t:formvalid>
</body>