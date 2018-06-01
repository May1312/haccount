<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
 <head>
  <title>用户注册登录信息</title>
  <t:base type="jquery,easyui,tools,DatePicker"></t:base>
 </head>
 <body style="overflow-y: " scroll="yes">
  <t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="userLoginController.do?save">
			<input id="id" name="id" type="hidden" value="${userLoginPage.id }">
			<table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
				<tr>
					<td align="right">
						<label class="Validform_label">
							手机:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="mobile" name="mobile" ignore="ignore"  value="${userLoginPage.mobile}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							邮箱:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="email" name="email" ignore="ignore"  value="${userLoginPage.email}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							密码:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="password" name="password" ignore="ignore"  value="${userLoginPage.password}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							手势密码:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="gesturePw" name="gesturePw" ignore="ignore"  value="${userLoginPage.gesturePw}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							手势密码状态 0关闭   1打开:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="gesturePwType" name="gesturePwType" ignore="ignore"  value="${userLoginPage.gesturePwType}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							登录ip:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="loginIp" name="loginIp" ignore="ignore"  value="${userLoginPage.loginIp}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							微信授权:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="wechatAuth" name="wechatAuth" ignore="ignore"  value="${userLoginPage.wechatAuth}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							注册时间:
						</label>
					</td>
					<td class="value">
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="registerDate" name="registerDate" ignore="ignore"    value="<fmt:formatDate value='${userLoginPage.registerDate}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							用户详情id:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="userInfoId" name="userInfoId"   value="${userLoginPage.userInfoId}" datatype="*" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
			</table>
		</t:formvalid>
 </body>