<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
  <div region="center" style="padding:0px;border:0px">
  <t:datagrid name="userLoginList" title="用户注册登录信息" actionUrl="userLoginController.do?datagrid" idField="id" fit="true">
   <t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
   <t:dgCol title="手机" field="mobile"   width="120"></t:dgCol>
   <t:dgCol title="邮箱" field="email"   width="120"></t:dgCol>
   <t:dgCol title="密码" field="password"   width="120"></t:dgCol>
   <t:dgCol title="手势密码" field="gesturePw"   width="120"></t:dgCol>
   <t:dgCol title="手势密码状态 0关闭   1打开" field="gesturePwType"   width="120"></t:dgCol>
   <t:dgCol title="登录ip" field="loginIp"   width="120"></t:dgCol>
   <t:dgCol title="微信授权" field="wechatAuth"   width="120"></t:dgCol>
   <t:dgCol title="注册时间" field="registerDate" formatter="yyyy-MM-dd hh:mm:ss"  width="120"></t:dgCol>
   <t:dgCol title="用户详情id" field="userInfoId"   width="120"></t:dgCol>
   <t:dgCol title="操作" field="opt" width="100"></t:dgCol>
   <t:dgDelOpt title="删除" url="userLoginController.do?del&id={id}" urlclass="ace_button"  urlfont="fa-trash-o"/>
   <t:dgToolBar title="录入" icon="icon-add" url="userLoginController.do?addorupdate" funname="add"></t:dgToolBar>
   <t:dgToolBar title="编辑" icon="icon-edit" url="userLoginController.do?addorupdate" funname="update"></t:dgToolBar>
   <t:dgToolBar title="查看" icon="icon-search" url="userLoginController.do?addorupdate" funname="detail"></t:dgToolBar>
  </t:datagrid>
  </div>
 </div>