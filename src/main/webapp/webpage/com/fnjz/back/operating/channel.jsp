<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>渠道</title>
    <t:base type="jquery,easyui,tools,DatePicker"></t:base>
</head>
<body style="overflow-y: hidden" scroll="no">
<t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="channelController.do?save">
    <input id="id" name="id" type="hidden" value="${channelPage.id }">
    <table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
        <tr>
            <td align="right">
                <label class="Validform_label">
                    渠道类型:
                </label>
            </td>
            <td class="value">
                    <%--<input class="inputxt" id="channeltype" name="channeltype" ignore="ignore"  value="${channelPage.channeltype}" />--%>
                <span class="Validform_checktip"></span>


                <select class="selectxt" id="channeltype" name="channeltype" ignore="ignore"
                        value="${channelPage.channeltype}">
                    <option value="0"
                            <c:if test="${'0' == channelPage.channeltype}">selected="selected"</c:if> >普通h5
                    </option>
                    <option value="1" <c:if test="${'1' == channelPage.channeltype}">selected="selected"</c:if>>微信公众号
                    </option>
                    <option value="2" <c:if test="${'2' == channelPage.channeltype}">selected="selected"</c:if>>小程序
                    </option>
                </select>


            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    渠道标识:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="channelflag" name="channelflag" ignore="ignore"
                       value="${channelPage.channelflag}"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    渠道名称:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="channelname" name="channelname" ignore="ignore"
                       value="${channelPage.channelname}"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    渠道短链:
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="channelurl" name="channelurl" ignore="ignore"
                       value="${channelPage.channelurl}"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
            <%--<tr>
                <td align="right">
                    <label class="Validform_label">
                        注册量:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="registrations" name="registrations" ignore="ignore"  value="${channelPage.registrations}" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <label class="Validform_label">
                        登陆量:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="loginnumber" name="loginnumber" ignore="ignore"  value="${channelPage.loginnumber}" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <label class="Validform_label">
                        状态:0_未生效   1_已生效:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="status" name="status" ignore="ignore"  value="${channelPage.status}" />
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
                    <input class="inputxt" id="delflag" name="delflag" ignore="ignore"  value="${channelPage.delflag}" datatype="n" />
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
                    <input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="delDate" name="delDate" ignore="ignore"    value="<fmt:formatDate value='${channelPage.delDate}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <label class="Validform_label">
                        备注说明:
                    </label>
                </td>
                <td class="value">
                    <input class="inputxt" id="remark" name="remark" ignore="ignore"  value="${channelPage.remark}" />
                    <span class="Validform_checktip"></span>
                </td>
            </tr>--%>
    </table>
</t:formvalid>
</body>