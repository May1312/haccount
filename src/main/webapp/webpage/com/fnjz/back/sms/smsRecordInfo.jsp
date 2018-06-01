<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
 <head>
  <title>发送记录</title>
  <t:base type="jquery,easyui,tools,DatePicker"></t:base>
 </head>
 <body style="overflow-y: " scroll="yes">
  <t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="smsRecordInfoController.do?save">
			<input id="id" name="id" type="hidden" value="${smsRecordInfoPage.id }">
			<table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
				<tr>
					<td align="right">
						<label class="Validform_label">
							创建发送记录id:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="smsrecordid" name="smsrecordid" ignore="ignore"  value="${smsRecordInfoPage.smsrecordid}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							发送内容:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="sendcontent" name="sendcontent" ignore="ignore"  value="${smsRecordInfoPage.sendcontent}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							发送状态:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="sendstate" name="sendstate" ignore="ignore"  value="${smsRecordInfoPage.sendstate}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							终端类型:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="terminaltype" name="terminaltype" ignore="ignore"  value="${smsRecordInfoPage.terminaltype}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							发送手机号:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="sendmobile" name="sendmobile" ignore="ignore"  value="${smsRecordInfoPage.sendmobile}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							发送模板id:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="sendtemplatecode" name="sendtemplatecode" ignore="ignore"  value="${smsRecordInfoPage.sendtemplatecode}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							发送时间:
						</label>
					</td>
					<td class="value">
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="sendtime" name="sendtime" ignore="ignore"    value="<fmt:formatDate value='${smsRecordInfoPage.sendtime}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							返回时间:
						</label>
					</td>
					<td class="value">
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="returntime" name="returntime" ignore="ignore"    value="<fmt:formatDate value='${smsRecordInfoPage.returntime}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
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
						<input class="inputxt" id="delFalg" name="delFalg" ignore="ignore"  value="${smsRecordInfoPage.delFalg}" datatype="n" />
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
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="delDate" name="delDate" ignore="ignore"    value="<fmt:formatDate value='${smsRecordInfoPage.delDate}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
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
						<input class="inputxt" id="remark" name="remark" ignore="ignore"  value="${smsRecordInfoPage.remark}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							备用字段:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="tag" name="tag" ignore="ignore"  value="${smsRecordInfoPage.tag}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>--%>
			</table>
		</t:formvalid>
 </body>