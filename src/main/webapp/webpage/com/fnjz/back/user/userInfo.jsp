<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
 <head>
  <title>用户信息</title>
  <t:base type="jquery,easyui,tools,DatePicker"></t:base>
 </head>
 <body style="overflow-y: " scroll="yes">
  <t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="userInfoController.do?save">
			<input id="id" name="id" type="hidden" value="${userInfoPage.id }">
			<table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
				<tr>
					<td align="right">
						<label class="Validform_label">
							昵称:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="nickName" name="nickName" ignore="ignore"  value="${userInfoPage.nickName}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							手机:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="mobile" name="mobile" ignore="ignore"  value="${userInfoPage.mobile}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<%--<tr>
					<td align="right">
						<label class="Validform_label">
							邮箱:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="email" name="email" ignore="ignore"  value="${userInfoPage.email}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>--%>
				<tr>
					<td align="right">
						<label class="Validform_label">
							性别:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="sex" name="sex" ignore="ignore"  value="${userInfoPage.sex}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							出生年月日:
						</label>
					</td>
					<td class="value">
						<input class="Wdate" onClick="WdatePicker()"  style="width: 150px" id="birthday" name="birthday" ignore="ignore"  value="<fmt:formatDate value='${userInfoPage.birthday}' type="date" pattern="yyyy-MM-dd"/>" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<%--<tr>
					<td align="right">
						<label class="Validform_label">
							密码:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="password" name="password"   value="${userInfoPage.password}" datatype="*" />
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
						<input class="inputxt" id="gesturePw" name="gesturePw" ignore="ignore"  value="${userInfoPage.gesturePw}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							手势密码打开关闭状态 0关闭  1打开:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="gesturePwType" name="gesturePwType" ignore="ignore"  value="${userInfoPage.gesturePwType}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							微信授权token:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="wechatAuth" name="wechatAuth" ignore="ignore"  value="${userInfoPage.wechatAuth}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							微博授权token:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="weiboAuth" name="weiboAuth" ignore="ignore"  value="${userInfoPage.weiboAuth}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>--%>
				<%--<tr>
					<td align="right">
						<label class="Validform_label">
							省份_id:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="provinceId" name="provinceId" ignore="ignore"  value="${userInfoPage.provinceId}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							省份_value:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="provinceName" name="provinceName" ignore="ignore"  value="${userInfoPage.provinceName}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							城市_id:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="cityId" name="cityId" ignore="ignore"  value="${userInfoPage.cityId}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							城市_name:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="cityName" name="cityName" ignore="ignore"  value="${userInfoPage.cityName}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							区县_id:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="districtId" name="districtId" ignore="ignore"  value="${userInfoPage.districtId}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							区县_name:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="districtName" name="districtName" ignore="ignore"  value="${userInfoPage.districtName}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							账户状态:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="status" name="status" ignore="ignore"  value="${userInfoPage.status}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							用户类型,vip:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="userType" name="userType" ignore="ignore"  value="${userInfoPage.userType}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>--%>
				<tr>
					<td align="right">
						<label class="Validform_label">
							用户所属行业:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="profession" name="profession" ignore="ignore"  value="${userInfoPage.profession}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							用户职位,基层/中层/高层:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="position" name="position" ignore="ignore"  value="${userInfoPage.position}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							年龄:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="age" name="age" ignore="ignore"  value="${userInfoPage.age}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							星座:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="constellation" name="constellation" ignore="ignore"  value="${userInfoPage.constellation}" datatype="n" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							终端系统:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="mobileSystem" name="mobileSystem" ignore="ignore"  value="${userInfoPage.mobileSystem}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<%--<tr>
					<td align="right">
						<label class="Validform_label">
							终端系统版本:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="mobileSystemVersion" name="mobileSystemVersion" ignore="ignore"  value="${userInfoPage.mobileSystemVersion}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							终端厂商:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="mobileManufacturer" name="mobileManufacturer" ignore="ignore"  value="${userInfoPage.mobileManufacturer}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							终端设备号:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="mobileDevice" name="mobileDevice" ignore="ignore"  value="${userInfoPage.mobileDevice}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							ios_token标识:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="iosToken" name="iosToken" ignore="ignore"  value="${userInfoPage.iosToken}" />
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
						<input class="inputxt" id="loginIp" name="loginIp" ignore="ignore"  value="${userInfoPage.loginIp}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">
							用户头像:
						</label>
					</td>
					<td class="value">
						<input class="inputxt" id="avatarUrl" name="avatarUrl" ignore="ignore"  value="${userInfoPage.avatarUrl}" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>--%>
				<tr>
					<td align="right">
						<label class="Validform_label">
							注册时间:
						</label>
					</td>
					<td class="value">
						<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  style="width: 150px" id="registerDate" name="registerDate"     value="<fmt:formatDate value='${userInfoPage.registerDate}' type="date" pattern="yyyy-MM-dd hh:mm:ss"/>" datatype="*" />
						<span class="Validform_checktip"></span>
					</td>
				</tr>
			</table>
		</t:formvalid>
 </body>