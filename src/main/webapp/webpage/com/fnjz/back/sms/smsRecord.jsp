<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
 <head>
  <title>短信发送记录</title>
  <t:base type="jquery,easyui,tools,DatePicker"></t:base>
 </head>
 <body style="overflow-y: hidden" scroll="no">
  <t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="smsRecordController.do?save">
			<input id="id" name="id" type="hidden" value="${smsRecordPage.id }">
			<table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
				<%--<tr>
					<td align="right">
						<label class="Validform_label">
							发送状态:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="sendstate" name="sendstate" ignore="ignore"  value="${smsRecordPage.sendstate}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>--%>
				<tr>
					<td align="right">
						<label class="Validform_label">
							终端类型:
						</label>
					</td>
					<td class="value">
						<%--<input class="inputxt" id="terminaltype" name="terminaltype" ignore="ignore"  value="${smsRecordPage.terminaltype}" />--%>

						<select class="selectxt" id="terminaltype" name="terminaltype" ignore="ignore" value="${smsRecordPage.terminaltype}">
							<option value="" >不限</option>
							<option value="android" <c:if test="${smsRecordPage.terminaltype eq 'android'}"> selected="selected"</c:if>>android</option>
							<option value="ios" <c:if test="${smsRecordPage.terminaltype eq 'ios'}"> selected="selected"</c:if>>ios</option>
							<option value="all" <c:if test="${smsRecordPage.terminaltype eq 'all'}"> selected="selected"</c:if>>全部</option>
						</select>

						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label" >
							发送手机号:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="sendmobile" name="sendmobile" ignore="ignore"  value="${smsRecordPage.sendmobile}" />
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
						<input class="inputxt" id="sendcontent" name="sendcontent"  value="${smsRecordPage.sendcontent}" datatype="s"  />
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
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'%y-%M-{%d+1}'})"  style="width: 150px" id="sendtime" name="sendtime" datatype="*"    value="<fmt:formatDate value='${smsRecordPage.sendtime}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<%--<tr>
					<td align="right">
						<label class="Validform_label">
							返回时间:
						</label>
					</td>
					<td class="value">
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="returntime" name="returntime" ignore="ignore"    value="<fmt:formatDate value='${smsRecordPage.returntime}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
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
						<input class="inputxt" id="delFalg" name="delFalg" ignore="ignore"  value="${smsRecordPage.delFalg}" datatype="n" />
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
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="delDate" name="delDate" ignore="ignore"    value="<fmt:formatDate value='${smsRecordPage.delDate}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
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
						<input class="inputxt" id="remark" name="remark" ignore="ignore"  value="${smsRecordPage.remark}" />
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
						<input class="inputxt" id="tag" name="tag" ignore="ignore"  value="${smsRecordPage.tag}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>--%>
			</table>
		</t:formvalid>
 </body>

 <script type="text/javascript">

     $('input[name=sendmobile]').change(function() {
         var sendmobile = $("#sendmobile").val();
		 if (sendmobile != null && sendmobile != ""){
             $("#terminaltype").attr("disabled","disabled");
		 }else {
             $("#terminaltype").attr("disabled",false);
		 }
     });

     $(document).ready(function(){
         $("#terminaltype").change(function(){
             var selected=$(this).children('option:selected').val()
             if(selected !=null && selected != ""){
                 $("#sendmobile").attr("disabled","disabled");
             }else {
                 $("#sendmobile").attr("disabled",false);
			 }
         });
     });
 </script>