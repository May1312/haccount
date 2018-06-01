<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
 <head>
  <title>App版本升级</title>
  <t:base type="jquery,easyui,tools,DatePicker"></t:base>
 </head>
 <body style="overflow-y: hidden" scroll="yes">
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
						<input class="inputxt" id="version" name="version" ignore="ignore"  value="${appVersionPage.version}" />
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
						<input class="inputxt" id="mobileSystem" name="mobileSystem" ignore="ignore"  value="${appVersionPage.mobileSystem}" datatype="n" />
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
						<input class="inputxt" id="installStatus" name="installStatus" ignore="ignore"  value="${appVersionPage.installStatus}" datatype="n" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							app状态,是否有效:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="appStatus" name="appStatus" ignore="ignore"  value="${appVersionPage.appStatus}" datatype="n" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
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
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							app路径:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="url" name="url" ignore="ignore"  value="${appVersionPage.url}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							app大小:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="size" name="size" ignore="ignore"  value="${appVersionPage.size}" datatype="n" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							更新日志:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="updateLog" name="updateLog" ignore="ignore"  value="${appVersionPage.updateLog}" />
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
						<input class="inputxt" id="delflag" name="delflag" ignore="ignore"  value="${appVersionPage.delflag}" datatype="n" />
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
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="delDate" name="delDate" ignore="ignore"    value="<fmt:formatDate value='${appVersionPage.delDate}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
			</table>
		</t:formvalid>
 </body>