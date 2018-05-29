<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
  <div region="center" style="padding:0px;border:0px">
  <t:datagrid name="appVersionList" title="App版本升级" actionUrl="appVersionController.do?datagrid" idField="id" fit="true">
   <t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
   <t:dgCol title="app版本号" field="version"   width="120"></t:dgCol>
   <t:dgCol title="终端系统标识" field="mobileSystem"   width="120"></t:dgCol>
   <t:dgCol title="app状态,是否强制升级" field="installStatus"   width="120"></t:dgCol>
   <t:dgCol title="app状态,是否有效" field="appStatus"   width="120"></t:dgCol>
   <t:dgCol title="系统适配范围max" field="systemMax"   width="120"></t:dgCol>
   <t:dgCol title="系统适配范围min" field="systemMin"   width="120"></t:dgCol>
   <t:dgCol title="app路径" field="url"   width="120"></t:dgCol>
   <t:dgCol title="app大小" field="size"   width="120"></t:dgCol>
   <t:dgCol title="更新日志" field="updateLog"   width="120"></t:dgCol>
   <t:dgCol title="删除标记" field="delflag"   width="120"></t:dgCol>
   <t:dgCol title="删除时间" field="delDate" formatter="yyyy-MM-dd hh:mm:ss"  width="120"></t:dgCol>
   <t:dgCol title="操作" field="opt" width="100"></t:dgCol>
   <t:dgDelOpt title="删除" url="appVersionController.do?del&id={id}" urlclass="ace_button"  urlfont="fa-trash-o"/>
   <t:dgToolBar title="录入" icon="icon-add" url="appVersionController.do?addorupdate" funname="add"></t:dgToolBar>
   <t:dgToolBar title="编辑" icon="icon-edit" url="appVersionController.do?addorupdate" funname="update"></t:dgToolBar>
   <t:dgToolBar title="查看" icon="icon-search" url="appVersionController.do?addorupdate" funname="detail"></t:dgToolBar>
  </t:datagrid>
  </div>
 </div>