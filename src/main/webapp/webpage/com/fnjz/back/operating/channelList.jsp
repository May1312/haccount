<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
  <div region="center" style="padding:0px;border:0px">
  <t:datagrid name="channelList" title="渠道" actionUrl="channelController.do?datagrid" idField="id" fit="true" queryMode="group">
   <t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
   <t:dgCol title="渠道类型" field="channeltype"   width="120" replace="普通h5_0,微信公众号_1,小程序_2" query="true"></t:dgCol>
   <t:dgCol title="渠道标识" field="channelflag"   width="120"></t:dgCol>
  <%-- <t:dgCol title="渠道名称" field="channelname"   width="120"></t:dgCol>
   <t:dgCol title="渠道短链" field="channelurl"   width="120"></t:dgCol>--%>
   <t:dgCol title="注册量" field="registrations"   width="120"></t:dgCol>
   <t:dgCol title="登陆量" field="loginnumber"   width="120"></t:dgCol>
  <%-- <t:dgCol title="状态:0_未生效   1_已生效" field="status"   width="120"></t:dgCol>
   <t:dgCol title="删除标记" field="delflag"   width="120"></t:dgCol>
   <t:dgCol title="删除时间" field="delDate" formatter="yyyy-MM-dd hh:mm:ss"  width="120"></t:dgCol>
   <t:dgCol title="备注说明" field="remark"   width="120"></t:dgCol>--%>
   <t:dgCol title="操作" field="opt" width="100"></t:dgCol>
   <t:dgDelOpt title="删除" url="channelController.do?del&id={id}" urlclass="ace_button"  urlfont="fa-trash-o"/>
   <t:dgToolBar title="录入" icon="icon-add" url="channelController.do?addorupdate" funname="add"></t:dgToolBar>
   <t:dgToolBar title="编辑" icon="icon-edit" url="channelController.do?addorupdate" funname="update"></t:dgToolBar>
   <t:dgToolBar title="查看" icon="icon-search" url="channelController.do?addorupdate" funname="detail"></t:dgToolBar>
  </t:datagrid>
  </div>
 </div>